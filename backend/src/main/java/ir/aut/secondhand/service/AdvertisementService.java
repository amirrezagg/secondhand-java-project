package ir.aut.secondhand.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import ir.aut.secondhand.dto.*;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.*;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.CategoryRepository;
import ir.aut.secondhand.repository.FavoriteRepository;
import ir.aut.secondhand.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ImageStorageService imageStorageService;
    private final UserService userService;
    private final FavoriteRepository favoriteRepository;

    public AdvertisementService(AdvertisementRepository advertisementRepository,
                                CategoryRepository categoryRepository,
                                LocationRepository locationRepository,
                                UserService userService,
                                ImageStorageService imageStorageService,
                                FavoriteRepository favoriteRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.userService = userService;
        this.imageStorageService = imageStorageService;
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional
    public Advertisement createAdvertisement(CreateAdvertisementRequest request) {

        User seller = userService.getCurrentUser();
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category"));
        Location location = locationRepository.findById(request.getLocationId()).orElseThrow(() -> new ResourceNotFoundException("location"));

        if (!category.isSelectable()) {
            throw new IllegalArgumentException("This category is unselectable");
        }

        validateDynamicAttributes(category, request.getDynamicAttributes());

        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(request.getTitle());
        advertisement.setDescription(request.getDescription());
        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setLocation(location);
        advertisement.setStatus(Advertisement.AdvertisementStatus.PENDING);

        if (request.getPriceAmount() != null) {
            Price price = new Price(request.getPriceAmount(), request.getPriceCurrency());
            advertisement.setPrice(price);
        }

        advertisement.setDynamicAttributes(request.getDynamicAttributes());

        return advertisementRepository.save(advertisement);
    }

    private void validateDynamicAttributes(Category category, Map<String, Object> attributes) {

        if (category.getValidationSchema() == null || category.getValidationSchema().isEmpty()) {
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonToValidate = objectMapper.valueToTree(attributes);

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            JsonSchema schema = factory.getSchema(category.getValidationSchema());

            Set<ValidationMessage> errors = schema.validate(jsonToValidate);

            if (!errors.isEmpty()) {
                String errorMessage = errors.iterator().next().getMessage();
                throw new IllegalArgumentException(errorMessage);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<AdvertisementResponse> getMyAdvertisements() {
        User currentUser = userService.getCurrentUser();

        List<Advertisement> advertisements = advertisementRepository.findAdvertisementBySeller(currentUser);
        List<AdvertisementResponse> results = new ArrayList<>();

        for (Advertisement advertisement : advertisements) {
            if (advertisement.getStatus() != Advertisement.AdvertisementStatus.DELETED) {
                results.add(new AdvertisementResponse(advertisement));
            }
        }

        return results;
    }

    public List<AdvertisementResponse> getAdvertisementsByRole() {
        User currentUser = userService.getCurrentUser();

        List<Advertisement> advertisements;
        List<AdvertisementResponse> results = new ArrayList<>();

        switch (currentUser.getRole()) {
            case ADMIN -> advertisements = advertisementRepository.findAllByOrderByUpdatedAtDesc();
            case USER ->
                    advertisements = advertisementRepository.findByStatusOrderByUpdatedAtDesc(Advertisement.AdvertisementStatus.APPROVED);
            default -> advertisements = new ArrayList<>();
        }

        for (Advertisement advertisement : advertisements) {
            if (advertisement.getStatus() != Advertisement.AdvertisementStatus.DELETED) {
                results.add(new AdvertisementResponse(advertisement));
            }
        }

        return results;
    }

    public List<AdvertisementResponse> getPendingAdvertisements() {
        User currentUser = userService.getCurrentUser();

        List<Advertisement> advertisements;
        List<AdvertisementResponse> results = new ArrayList<>();

        if (Objects.requireNonNull(currentUser.getRole()) == User.Role.ADMIN) {
            advertisements = advertisementRepository.findByStatusOrderByUpdatedAtDesc(Advertisement.AdvertisementStatus.PENDING);
        } else {
            throw new IllegalArgumentException("Access denied");
        }

        for (Advertisement advertisement : advertisements) {
            results.add(new AdvertisementResponse(advertisement));
        }

        return results;
    }

    public AdvertisementResponse getAdvertisementDetails(long advertisementId) {
        User currentUser = userService.getCurrentUser();
        Advertisement advertisement = advertisementRepository
                .findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        if (advertisement.getStatus() == Advertisement.AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("advertisement");
        }

        boolean isUserAdvertisement = advertisement.getSeller().getId().equals(currentUser.getId());
        if (currentUser.getRole() == User.Role.ADMIN || isUserAdvertisement) {
            AdvertisementResponse response = new AdvertisementResponse(advertisement);
            if (advertisement.getAdminComments() != null) {
                response.setAdminComment(advertisement.getAdminComments());
            }
            return response;
        }

        boolean isVisibleForPublic = List.of(Advertisement.AdvertisementStatus.APPROVED, Advertisement.AdvertisementStatus.SOLD)
                .contains(advertisement.getStatus());
        if (!isVisibleForPublic) {
            throw new ResourceNotFoundException("advertisement");
        }

        AdvertisementResponse response = new AdvertisementResponse(advertisement);

        boolean isFav = favoriteRepository.existsByUserIdAndAdvertisementId(currentUser.getId(), advertisement.getId());
        response.setFavorited(isFav);

        advertisement.setViewCount(advertisement.getViewCount() + 1);
        advertisementRepository.save(advertisement);

        return response;
    }

    @Transactional
    public void saveAdvertisementImages(Long advertisementId, MultipartFile[] files, int mainImageIndex) {
        User currentUser = userService.getCurrentUser();

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        if(advertisement.getSeller().getId().equals(currentUser.getId())){
            throw new ResourceNotFoundException("advertisement");
        }

        if (files == null || files.length == 0) {
            AdvertisementImage defaultImage = new AdvertisementImage();
            defaultImage.setImageUrl("default-ad.png");
            defaultImage.setAdvertisement(advertisement);
            defaultImage.setIsPrimary(true);

            advertisement.getAdvertisementImages().add(defaultImage);
            advertisementRepository.save(advertisement);
            return;
        }

        if (mainImageIndex < 0 || mainImageIndex >= files.length) {
            mainImageIndex = 0;
        }

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String uniqueFilename = imageStorageService.storeFile(file);

            AdvertisementImage advertisementImage = new AdvertisementImage();
            advertisementImage.setImageUrl(uniqueFilename);
            advertisementImage.setAdvertisement(advertisement);
            advertisementImage.setIsPrimary(i == mainImageIndex);

            advertisement.getAdvertisementImages().add(advertisementImage);
        }

        advertisementRepository.save(advertisement);
    }

    @Transactional
    public Advertisement updateAdvertisement(Long id, UpdateAdvertisementRequest request) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        User currentUser = userService.getCurrentUser();
        boolean isOwner = advertisement.getSeller().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;

        if (advertisement.getStatus() == Advertisement.AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("advertisement");
        } else if (advertisement.getStatus() == Advertisement.AdvertisementStatus.SOLD) {
            throw new IllegalArgumentException("Cannot update sold advertisement");
        }

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You do not have permission to update this advertisement");
        }

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("location"));
            advertisement.setLocation(location);
        }

        Category category = advertisement.getCategory();
        if (request.getCategoryId() != null && !request.getCategoryId().equals(category.getId())) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("category"));

            if (!category.isSelectable()) {
                throw new IllegalArgumentException("This category is unselectable");
            }
            advertisement.setCategory(category);
        }

        if (request.getDynamicAttributes() != null) {
            validateDynamicAttributes(category, request.getDynamicAttributes());
            advertisement.setDynamicAttributes(request.getDynamicAttributes());
        }

        if (request.getTitle() != null) advertisement.setTitle(request.getTitle());
        if (request.getDescription() != null) advertisement.setDescription(request.getDescription());

        if (request.getPriceAmount() != null) {
            Price price = new Price(request.getPriceAmount(), request.getPriceCurrency());
            advertisement.setPrice(price);
        } else if (request.getPriceAmount() == null && request.getPriceCurrency() != null) {
            if (advertisement.getPrice() != null) {
                advertisement.setPrice(new Price(advertisement.getPrice().getAmount(), request.getPriceCurrency()));
            }
        }

        if (!isAdmin) {
            advertisement.setStatus(Advertisement.AdvertisementStatus.PENDING);
        }

        return advertisementRepository.save(advertisement);
    }

    @Transactional
    public void deleteAdvertisement(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        if (advertisement.getStatus() == Advertisement.AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("advertisement");
        }

        User currentUser = userService.getCurrentUser();
        boolean isOwner = advertisement.getSeller().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You do not have permission to delete this advertisement");
        }

        advertisement.setStatus(Advertisement.AdvertisementStatus.DELETED);
        advertisementRepository.save(advertisement);
    }

    @Transactional
    public AdvertisementResponse reviewAdvertisement(Long id, ReviewAdvertisementRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Access denied");
        }

        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        if (advertisement.getStatus() == Advertisement.AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("advertisement");
        }

        if (request.getStatus() == Advertisement.AdvertisementStatus.APPROVED) {
            advertisement.setStatus(Advertisement.AdvertisementStatus.APPROVED);
        } else if (request.getStatus() == Advertisement.AdvertisementStatus.REJECTED) {
            advertisement.setStatus(Advertisement.AdvertisementStatus.REJECTED);
            if (request.getRejectionReason() != null && !request.getRejectionReason().isBlank()) {
                if (advertisement.getAdminComments() == null) {
                    advertisement.setAdminComments(new ArrayList<>());
                }

                AdminComment comment = new AdminComment();
                comment.setContent(request.getRejectionReason());
                comment.setActionType(AdminComment.AdminActionType.REJECT);
                comment.setAdmin(currentUser);

                advertisement.getAdminComments().add(comment);
            }
        } else {
            throw new IllegalArgumentException("Invalid status for review. Choose APPROVED or REJECTED");
        }
        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);

        AdvertisementResponse response = new AdvertisementResponse(updatedAdvertisement);
        if (advertisement.getAdminComments() != null) {
            response.setAdminComment(advertisement.getAdminComments());
        }

        return response;
    }

    @Transactional
    public AdvertisementResponse markAsSold(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        User currentUser = userService.getCurrentUser();
        if (!advertisement.getSeller().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("advertisement");
        }

        if (advertisement.getStatus() != Advertisement.AdvertisementStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved advertisements can be marked as sold");
        }

        advertisement.setStatus(Advertisement.AdvertisementStatus.SOLD);

        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        return new AdvertisementResponse(updatedAdvertisement);
    }

    public List<AdvertisementResponse> searchAdvertisements(SearchAdvertisementRequest request) {
        List<Advertisement> advertisements = advertisementRepository.search(request);

        List<AdvertisementResponse> responses = new ArrayList<>();
        for (Advertisement ad : advertisements) {
            responses.add(new AdvertisementResponse(ad));
        }

        return responses;
    }
}


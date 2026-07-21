package ir.aut.secondhand.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import ir.aut.secondhand.dto.AdvertisementResponse;
import ir.aut.secondhand.dto.CreateAdvertisementRequest;
import ir.aut.secondhand.dto.ReviewAdvertisementRequest;
import ir.aut.secondhand.dto.SearchAdvertisementRequest;
import ir.aut.secondhand.dto.UpdateAdvertisementRequest;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.AdminComment;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.AdvertisementImage;
import ir.aut.secondhand.model.Category;
import ir.aut.secondhand.model.Location;
import ir.aut.secondhand.model.Price;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.CategoryRepository;
import ir.aut.secondhand.repository.FavoriteRepository;
import ir.aut.secondhand.repository.LocationRepository;

/**
 * Service layer component responsible for advertisement lifecycle management
 * within the secondhand marketplace domain.
 *
 * Handles create, update, delete, review, search and retrieval operations while
 * enforcing ownership, approval status, and dynamic category-specific schema
 * validation rules. Also coordinates image persistence and favorite flag checks.
 */
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

    /**
     * Create a new advertisement in PENDING state.
     *
     * Validates that the current user is the seller, that referenced category and
     * location exist, that the selected category is allowed for direct selection,
     * and that provided dynamic attributes conform to the category's JSON schema.
     *
     * @param request request payload containing advertisement details and dynamic attributes
     * @return saved Advertisement entity
     * @throws ResourceNotFoundException if category or location cannot be resolved
     * @throws IllegalArgumentException if the category is not selectable or dynamic attributes fail validation
     */
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

    /**
     * Enforces category-specific dynamic attribute constraints before persistence.
     *
     * If the category carries a JSON schema, the provided attribute map is converted
     * to JSON and validated. Any schema violations are exposed as a runtime validation failure.
     *
     * @param category category whose validationSchema defines the allowed dynamic attribute structure
     * @param attributes dynamic attribute values supplied by the client
     * @throws IllegalArgumentException if the schema is invalid or the attribute values fail validation
     */
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

    /**
     * Retrieve advertisements owned by current authenticated user.
     *
     * Excludes logically deleted entries and returns domain response DTOs.
     *
     * @return list of AdvertisementResponse for non-deleted owner advertisements
     */
    public List<AdvertisementResponse> getMyAdvertisements() {
        User currentUser = userService.getCurrentUser();

        List<Advertisement> advertisements = advertisementRepository.findAdvertisementBySeller(currentUser);
        List<AdvertisementResponse> results = new ArrayList<>();

        for (Advertisement advertisement : advertisements) {
            if (advertisement.getStatus() != Advertisement.AdvertisementStatus.DELETED) {
                // Exclude logically deleted advertisements from the user's personal listing
                results.add(new AdvertisementResponse(advertisement));
            }
        }

        return results;
    }

    /**
     * Retrieve advertisements based on current user's role.
     *
     * Admins see all advertisements, while regular users only see approved listings.
     * Deleted items are filtered out after repository retrieval.
     *
     * @return list of AdvertisementResponse respecting role visibility rules
     */
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

    /**
     * Return advertisements awaiting admin review.
     *
     * Only administrators may access this pending queue.
     *
     * @return list of pending AdvertisementResponse
     * @throws IllegalArgumentException if current user is not an admin
     */
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

    /**
     * Load advertisement details with visibility and ownership enforcement.
     *
     * Admins and the seller always may view the advertisement details including admin comments.
     * Regular users may only view approved or sold ads. This method also increments view count
     * for public access and flags favorite status for the current user.
     *
     * @param advertisementId identifier of advertisement to retrieve
     * @return AdvertisementResponse enriched with favorite status for public access
     * @throws ResourceNotFoundException if the advertisement does not exist or is not visible to the current user
     */
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
                response.setAdminComments(advertisement.getAdminComments());
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

    /**
     * Associate uploaded images with an existing advertisement.
     *
     * Only the advertisement owner may attach images. The method stores each file,
     * creates image entities, designates one as primary, and persists the updated advertisement.
     *
     * @param advertisementId target advertisement identifier
     * @param files array of uploaded image files
     * @param mainImageIndex index of file to mark as primary
     * @throws ResourceNotFoundException if advertisement is not found or current user is not the owner
     */
    @Transactional
    public void saveAdvertisementImages(Long advertisementId, MultipartFile[] files, int mainImageIndex) {
        User currentUser = userService.getCurrentUser();

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        if (!advertisement.getSeller().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("advertisement");
        }

        if (files != null && files.length != 0) {
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
        }

        advertisementRepository.save(advertisement);
    }

    /**
     * Update advertisement metadata while enforcing ownership and approval lifecycle rules.
     *
     * Sellers may update their own advertisements and the advertisement is reset to PENDING unless
     * the current user is an admin. Sold advertisements cannot be modified and deleted advertisements
     * are treated as not found. Category changes require selectable categories and dynamic attributes
     * are revalidated against the effective category schema.
     *
     * @param id identifier of advertisement to update
     * @param request payload containing fields to change
     * @return updated Advertisement entity
     * @throws ResourceNotFoundException if advertisement, category, or location cannot be resolved
     * @throws IllegalArgumentException if the user lacks permission, tries to update a sold advertisement, or category validation fails
     */
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
            // Validate against the effective category schema after any category change.
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

    /**
     * Perform logical deletion of an advertisement.
     *
     * Only the seller or an admin may delete. Deleted advertisements are not physically removed
     * but are excluded from normal listings and treated as not found for retrieval.
     *
     * @param id advertisement identifier
     * @throws ResourceNotFoundException if the advertisement is missing or already deleted
     * @throws IllegalArgumentException if the current user is neither owner nor admin
     */
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

    /**
     * Review and approve or reject an advertisement as an administrator.
     *
     * Rejected advertisements may collect admin comments explaining the decision.
     * This method does not permit review of deleted advertisements.
     *
     * @param id identifier of advertisement to review
     * @param request review details including target status and optional rejection reason
     * @return AdvertisementResponse after review
     * @throws IllegalArgumentException if current user is not an admin or provided status is invalid
     * @throws ResourceNotFoundException if advertisement is missing or deleted
     */
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

        if (null == request.getAdStatus()) {
            throw new IllegalArgumentException("Invalid status for review. Choose APPROVED or REJECTED");
        } else switch (request.getAdStatus()) {
            case APPROVED -> advertisement.setStatus(Advertisement.AdvertisementStatus.APPROVED);
            case REJECTED -> {
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
            }
            default -> throw new IllegalArgumentException("Invalid status for review. Choose APPROVED or REJECTED");
        }
        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);

        AdvertisementResponse response = new AdvertisementResponse(updatedAdvertisement);
        if (advertisement.getAdminComments() != null) {
            response.setAdminComments(advertisement.getAdminComments());
        }

        return response;
    }

    /**
     * Mark an approved advertisement as sold.
     *
     * Only the seller may perform this transition, and only from APPROVED state.
     *
     * @param id advertisement identifier
     * @return AdvertisementResponse with sold status
     * @throws ResourceNotFoundException if advertisement is missing or current user is not owner
     * @throws IllegalArgumentException if advertisement is not approved
     */
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

    /**
     * Proxy search request to repository layer and map results to response DTOs.
     *
     * Search returns the advertisements matching criteria without applying extra in-memory filters.
     *
     * @param request search criteria
     * @return list of AdvertisementResponse
     */
    public List<AdvertisementResponse> searchAdvertisements(SearchAdvertisementRequest request) {
        List<Advertisement> advertisements = advertisementRepository.search(request);

        List<AdvertisementResponse> responses = new ArrayList<>();
        for (Advertisement ad : advertisements) {
            responses.add(new AdvertisementResponse(ad));
        }

        return responses;
    }
}


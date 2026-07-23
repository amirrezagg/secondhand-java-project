package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.CreateAdvertisementRequest;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.*;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.CategoryRepository;
import ir.aut.secondhand.repository.FavoriteRepository;
import ir.aut.secondhand.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AdvertisementServiceTest {

    private AdvertisementService advertisementService;

    private UserService stubUserService;
    private AdvertisementRepository stubAdvertisementRepository;
    private CategoryRepository stubCategoryRepository;
    private LocationRepository stubLocationRepository;
    private ImageStorageService stubImageStorageService;
    private FavoriteRepository stubFavoriteRepository;

    private User currentUser;
    private User otherUser;
    private Category validCategory;
    private Category unselectableCategory;
    private Location validLocation;
    private Advertisement mockAd;

    private Advertisement lastSavedAd;

    @BeforeEach
    void setUp() {

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("sellerUser");
        currentUser.setRole(User.Role.USER);

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("buyerUser");
        otherUser.setRole(User.Role.USER);

        validCategory = new Category();
        validCategory.setId(1L);
        validCategory.setSelectable(true);
        validCategory.setValidationSchema("");

        unselectableCategory = new Category();
        unselectableCategory.setId(2L);
        unselectableCategory.setSelectable(false);

        validLocation = new Location();
        validLocation.setId(1L);

        mockAd = new Advertisement();
        mockAd.setId(10L);
        mockAd.setTitle("Test Ad");
        mockAd.setSeller(currentUser);
        mockAd.setCategory(validCategory);
        mockAd.setLocation(validLocation);
        mockAd.setStatus(Advertisement.AdvertisementStatus.APPROVED);

        Price mockPrice = new Price(java.math.BigDecimal.valueOf(100), Price.Currency.IRR);
        mockAd.setPrice(mockPrice);

        lastSavedAd = null;

        stubUserService = new UserService(null, null) {
            @Override
            public User getCurrentUser() {
                return currentUser;
            }
        };

        stubCategoryRepository = (CategoryRepository) java.lang.reflect.Proxy.newProxyInstance(
                CategoryRepository.class.getClassLoader(),
                new Class<?>[]{CategoryRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findById")) {
                        Long id = (Long) args[0];
                        if (id.equals(1L)) return Optional.of(validCategory);
                        if (id.equals(2L)) return Optional.of(unselectableCategory);
                    }
                    return Optional.empty();
                });

        stubLocationRepository = (LocationRepository) java.lang.reflect.Proxy.newProxyInstance(
                LocationRepository.class.getClassLoader(),
                new Class<?>[]{LocationRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findById") && args[0].equals(1L)) {
                        return Optional.of(validLocation);
                    }
                    return Optional.empty();
                });

        stubAdvertisementRepository = (AdvertisementRepository) java.lang.reflect.Proxy.newProxyInstance(
                AdvertisementRepository.class.getClassLoader(),
                new Class<?>[]{AdvertisementRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findById") && args[0].equals(10L)) {
                        return Optional.of(mockAd);
                    }
                    if (method.getName().equals("save")) {
                        lastSavedAd = (Advertisement) args[0];
                        return lastSavedAd;
                    }
                    return Optional.empty();
                });

        stubFavoriteRepository = (FavoriteRepository) java.lang.reflect.Proxy.newProxyInstance(
                FavoriteRepository.class.getClassLoader(),
                new Class<?>[]{FavoriteRepository.class},
                (proxy, method, args) -> false);

        stubImageStorageService = new ImageStorageService() {
            @Override
            public String storeFile(org.springframework.web.multipart.MultipartFile file) {
                return "dummy_image_url.jpg";
            }
        };

        advertisementService = new AdvertisementService(
                stubAdvertisementRepository,
                stubCategoryRepository,
                stubLocationRepository,
                stubUserService,
                stubImageStorageService,
                stubFavoriteRepository
        );
    }
    
    @Test
    void testCreateAdvertisement_Success() {
        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        request.setTitle("Laptop for Sale");
        request.setDescription("Good condition");
        request.setCategoryId(1L);
        request.setLocationId(1L);
        request.setPriceAmount(BigDecimal.valueOf(500));
        request.setPriceCurrency(Price.Currency.IRR);

        Advertisement result = advertisementService.createAdvertisement(request);

        assertNotNull(result);
        assertEquals("Laptop for Sale", result.getTitle());
        assertEquals(Advertisement.AdvertisementStatus.PENDING, result.getStatus());
        assertEquals(validCategory, result.getCategory());
        assertNotNull(lastSavedAd);
    }

    @Test
    void testCreateAdvertisement_UnselectableCategory_ThrowsException() {
        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        request.setCategoryId(2L);

        request.setLocationId(1L);

        assertThrows(IllegalArgumentException.class, () ->
                advertisementService.createAdvertisement(request)
        );
    }

    @Test
    void testMarkAsSold_NotApproved_ThrowsException() {
        mockAd.setStatus(Advertisement.AdvertisementStatus.PENDING);

        assertThrows(IllegalArgumentException.class, () ->
                advertisementService.markAsSold(10L)
        );
    }

    @Test
    void testDeleteAdvertisement_Success() {
        advertisementService.deleteAdvertisement(10L);

        assertNotNull(lastSavedAd);
        assertEquals(Advertisement.AdvertisementStatus.DELETED, lastSavedAd.getStatus());
    }

    @Test
    void testDeleteAdvertisement_AlreadyDeleted_ThrowsException() {
        mockAd.setStatus(Advertisement.AdvertisementStatus.DELETED);

        assertThrows(ResourceNotFoundException.class, () ->
                advertisementService.deleteAdvertisement(10L)
        );
    }
}
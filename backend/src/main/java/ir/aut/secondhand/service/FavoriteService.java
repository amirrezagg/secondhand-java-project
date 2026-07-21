package ir.aut.secondhand.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ir.aut.secondhand.dto.FavoriteResponse;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Favorite;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.FavoriteRepository;

/**
 * Service layer for managing user favorites in the advertisement platform.
 * Handles business logic for toggling favorite status (add/remove) and retrieving
 * a user's list of favorited advertisements, ensuring only accessible ads are returned.
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserService userService;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           AdvertisementRepository advertisementRepository,
                           UserService userService) {
        this.favoriteRepository = favoriteRepository;
        this.advertisementRepository = advertisementRepository;
        this.userService = userService;
    }

    /**
     * Toggles the favorite status of an advertisement for the currently authenticated user.
     * If the advertisement is already favorited, it is removed. Otherwise, it is added,
     * provided the advertisement is in an accessible state (APPROVED or SOLD).
     *
     * @param advertisementId the ID of the advertisement to toggle favorite for
     * @return {@code true} if the advertisement was added as a favorite,
     *         {@code false} if it was removed from favorites
     * @throws ResourceNotFoundException if the advertisement does not exist or
     *         is not in an accessible status (APPROVED or SOLD)
     */
    @Transactional
    public boolean toggleFavorite(Long advertisementId) {

        User currentUser = userService.getCurrentUser();

        // Check if the favorite relationship already exists
        boolean alreadyExists = favoriteRepository.existsByUserIdAndAdvertisementId(currentUser.getId(), advertisementId);

        if (alreadyExists) {
            // Remove from favorites if it already exists
            favoriteRepository.deleteByUserIdAndAdvertisementId(currentUser.getId(), advertisementId);
            return false;
        }
        else {
            // Verify advertisement exists and is accessible
            Advertisement advertisement = advertisementRepository.findById(advertisementId)
                    .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

            // Only approved or sold advertisements can be favorited
            boolean isApproved = advertisement.getStatus() == Advertisement.AdvertisementStatus.APPROVED;
            boolean isSold = advertisement.getStatus() == Advertisement.AdvertisementStatus.SOLD;

            if (!isApproved && !isSold) {
                throw new ResourceNotFoundException("advertisement");
            }

            // Create and persist the new favorite relationship
            Favorite favorite = new Favorite();
            favorite.setUser(currentUser);
            favorite.setAdvertisement(advertisement);

            favoriteRepository.save(favorite);
            return true;
        }
    }

    /**
     * Retrieves the list of favorited advertisements for the currently authenticated user.
     * Filters the results to include only advertisements with APPROVED or SOLD status,
     * effectively hiding any ads that are no longer accessible (e.g., pending, rejected, or deleted).
     *
     * @return a list of {@link FavoriteResponse} objects representing the user's valid favorites
     */
    public List<FavoriteResponse> getFavorites() {
        User currentUser = userService.getCurrentUser();

        // Fetch all favorites for the user
        List<Favorite> favorites = favoriteRepository.findByUserId(currentUser.getId());
        List<FavoriteResponse> responseList = new ArrayList<>();

        // Filter favorites to include only accessible advertisements
        for (Favorite favorite : favorites) {
            Advertisement advertisement = favorite.getAdvertisement();

            if (advertisement != null) {
                boolean isApproved = advertisement.getStatus() == Advertisement.AdvertisementStatus.APPROVED;
                boolean isSold = advertisement.getStatus() == Advertisement.AdvertisementStatus.SOLD;

                // Only include favorites for advertisements that are still visible to users
                if (isApproved || isSold) {
                    responseList.add(new FavoriteResponse(favorite));
                }
            }
        }

        return responseList;
    }
}
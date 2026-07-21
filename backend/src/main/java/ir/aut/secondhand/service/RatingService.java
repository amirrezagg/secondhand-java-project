package ir.aut.secondhand.service;

import org.springframework.stereotype.Service;

import ir.aut.secondhand.dto.RateUserRequest;
import ir.aut.secondhand.exception.DuplicateResourceException;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Rating;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.RatingRepository;
import ir.aut.secondhand.repository.UserRepository;

/**
 * Service layer for managing user ratings in the second-hand marketplace.
 * Handles business logic for submitting ratings for sellers based on completed
 * advertisements and retrieving average seller ratings.
 */
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public RatingService(RatingRepository ratingRepository, UserService userService, UserRepository userRepository, AdvertisementRepository advertisementRepository) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }

    /**
     * Submits a rating for a seller based on a specific advertisement.
     * Validates that the advertisement is accessible (not pending, rejected, or deleted),
     * prevents self-rating, and ensures a user cannot rate the same advertisement twice.
     *
     * @param rateRequest the request containing advertisement ID, score, and comment
     * @return the persisted {@link Rating} entity
     * @throws IllegalArgumentException if the advertisement is not found or the user attempts to rate themselves
     * @throws ResourceNotFoundException if the advertisement is in an inaccessible state
     * @throws DuplicateResourceException if the user has already rated this advertisement
     */
    public Rating rate(RateUserRequest rateRequest) {
        // Retrieve and validate the advertisement exists
        Advertisement ad = advertisementRepository.findById(rateRequest.getAdvertisementId()).orElseThrow(() -> new IllegalArgumentException("Advertisement not found"));

        // Only approved or sold advertisements can be rated, not pending/rejected/deleted ones
        if (ad.getStatus() == Advertisement.AdvertisementStatus.PENDING
                || ad.getStatus() == Advertisement.AdvertisementStatus.REJECTED
                || ad.getStatus() == Advertisement.AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("advertisement");
        }

        User seller = ad.getSeller();
        User rater = userService.getCurrentUser();

        // Business rule: users cannot rate themselves
        if (seller.equals(rater)) {
            throw new IllegalArgumentException("You can't rate yourself");
        }

        // Prevent duplicate ratings for the same advertisement by the same user
        if (ratingRepository.existsByRaterAndAdvertisement(rater, ad)) {
            throw new DuplicateResourceException("rating", "You have already submitted a rating for this advertisement");
        }

        // Build and persist the rating entity
        Rating rating = new Rating();
        rating.setAdvertisement(ad);
        rating.setRater(rater);
        rating.setSeller(seller);
        rating.setScore(rateRequest.getScore());
        rating.setComment(rateRequest.getComment());

        return ratingRepository.save(rating);
    }

    /**
     * Calculates the average rating score for a given seller.
     * Returns 0.0 if the seller has no ratings yet.
     *
     * @param sellerId the ID of the seller whose average rating is requested
     * @return the average rating score, or 0.0 if no ratings exist
     * @throws ResourceNotFoundException if the seller does not exist in the system
     */
    public Double getAverageRating(Long sellerId) {
        // Verify the seller exists before querying ratings
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("seller");
        }
        Double average = ratingRepository.getAverageRateBySellerId(sellerId);
        return average != null ? average : 0.0;
    }
}
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

    public Rating rate(RateUserRequest rateRequest) {
        Advertisement ad = advertisementRepository.findById(rateRequest.getAdvertisementId()).orElseThrow(() -> new IllegalArgumentException("Advertisement not found"));

        if (ad.getStatus() == Advertisement.AdvertisementStatus.PENDING
                || ad.getStatus() == Advertisement.AdvertisementStatus.REJECTED
                || ad.getStatus() == Advertisement.AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("advertisement");
        }

        User seller = ad.getSeller();
        User rater = userService.getCurrentUser();

        if (seller.equals(rater)) {
            throw new IllegalArgumentException("You can't rate yourself");
        }

        if (ratingRepository.existsByRaterAndAdvertisement(rater, ad)) {
            throw new DuplicateResourceException("rating", "You have already submitted a rating for this advertisement");
        }

        Rating rating = new Rating();
        rating.setAdvertisement(ad);
        rating.setRater(rater);
        rating.setSeller(seller);
        rating.setScore(rateRequest.getScore());
        rating.setComment(rateRequest.getComment());

        return ratingRepository.save(rating);
    }

    public Double getAverageRating(Long sellerId) {
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("seller");
        }
        Double average = ratingRepository.getAverageRateBySellerId(sellerId);
        return average != null ? average : 0.0;
    }
}

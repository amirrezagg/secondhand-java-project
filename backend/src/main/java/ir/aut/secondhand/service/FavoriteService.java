package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.FavoriteResponse;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Favorite;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public String toggleFavorite(Long advertisementId) {

        User currentUser = userService.getCurrentUser();

        boolean alreadyExists = favoriteRepository.existsByUserIdAndAdvertisementId(currentUser.getId(), advertisementId);

        if (alreadyExists) {
            favoriteRepository.deleteByUserIdAndAdvertisementId(currentUser.getId(), advertisementId);
            return "Removed from favorites";
        }
        else {
            Advertisement advertisement = advertisementRepository.findById(advertisementId)
                    .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

            boolean isApproved = advertisement.getStatus() == Advertisement.AdvertisementStatus.APPROVED;
            boolean isSold = advertisement.getStatus() == Advertisement.AdvertisementStatus.SOLD;

            if (!isApproved && !isSold) {
                throw new ResourceNotFoundException("advertisement");
            }

            Favorite favorite = new Favorite();
            favorite.setUser(currentUser);
            favorite.setAdvertisement(advertisement);

            favoriteRepository.save(favorite);
            return "Added to favorites";
        }
    }

    public List<FavoriteResponse> getFavorites() {
        User currentUser = userService.getCurrentUser();

        List<Favorite> favorites = favoriteRepository.findByUserId(currentUser.getId());
        List<FavoriteResponse> responseList = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Advertisement advertisement = favorite.getAdvertisement();

            if (advertisement != null) {
                boolean isApproved = advertisement.getStatus() == Advertisement.AdvertisementStatus.APPROVED;
                boolean isSold = advertisement.getStatus() == Advertisement.AdvertisementStatus.SOLD;

                if (isApproved || isSold) {
                    responseList.add(new FavoriteResponse(favorite));
                }
            }
        }

        return responseList;
    }
}
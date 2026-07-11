package ir.aut.secondhand.frontend;

import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final List<FavoriteAdvertisement> favorites = new ArrayList<>();

    public static void addFavorite(FavoriteAdvertisement advertisement){

        for (FavoriteAdvertisement favorite: favorites){
            if (favorite.getTitle().equals(advertisement.getTitle())){
                return;
            }
        }

        favorites.add(advertisement);
    }

    public static List<FavoriteAdvertisement> getFavorites() {
        return favorites;
    }

    public static void removeFavorite(String title){
        favorites.removeIf(favorite-> favorite.getTitle().equals(title));
    }
}

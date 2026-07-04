package ir.aut.secondhand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    boolean existsByUserIdAndAdvertisementId(Long userId, Long advertisementId);

    void deleteByUserIdAndAdvertisementId(Long userId, Long advertisementId);
}

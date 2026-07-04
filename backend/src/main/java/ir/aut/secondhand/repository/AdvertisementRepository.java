package ir.aut.secondhand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Advertisement.AdvertisementStatus;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    List<Advertisement> findByStatusOrderByUpdatedAtDesc(AdvertisementStatus status);

    List<Advertisement> findByCategoryIdAndStatus(Long categoryId, AdvertisementStatus status);

    List<Advertisement> findByLocationIdAndStatus(Long locationId, AdvertisementStatus status);

    List<Advertisement> findBySellerId(Long sellerId);
}

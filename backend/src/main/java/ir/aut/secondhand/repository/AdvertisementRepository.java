package ir.aut.secondhand.repository;

import java.util.List;

import ir.aut.secondhand.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Advertisement.AdvertisementStatus;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long>, AdvertisementRepositoryCustom {

    List<Advertisement> findByStatusOrderByUpdatedAtDesc(AdvertisementStatus status);

    List<Advertisement> findByCategoryIdAndStatus(Long categoryId, AdvertisementStatus status);

    List<Advertisement> findByLocationIdAndStatus(Long locationId, AdvertisementStatus status);

    List<Advertisement> findBySellerId(Long sellerId);

    List<Advertisement> findAdvertisementBySeller(User seller);

    List<Advertisement> findAdvertisementByStatus(AdvertisementStatus status);

    List<Advertisement> findAllByOrderByUpdatedAtDesc();
}

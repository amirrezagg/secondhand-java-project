package ir.aut.secondhand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Rating;
import ir.aut.secondhand.model.User;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByRaterAndAdvertisement(User rater, Advertisement advertisement);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.seller.id = :sellerId")
    Double getAverageRateBySellerId(@Param("sellerId") Long sellerId);
}

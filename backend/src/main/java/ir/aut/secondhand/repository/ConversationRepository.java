package ir.aut.secondhand.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByBuyerIdOrSellerIdOrderByLastUpdatedAtDesc(Long buyerId, Long sellerId);

    Optional<Conversation> findByBuyerIdAndAdvertisementId(Long buyerId, Long advertisementId);
}

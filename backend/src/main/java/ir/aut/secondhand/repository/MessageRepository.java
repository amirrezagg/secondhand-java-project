package ir.aut.secondhand.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    Optional<Message> findFirstByConversationIdAndStatusNotOrderByCreatedAtDesc(Long conversationId, Message.MessageStatus status);
}

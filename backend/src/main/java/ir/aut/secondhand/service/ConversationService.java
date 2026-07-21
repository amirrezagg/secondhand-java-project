package ir.aut.secondhand.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ir.aut.secondhand.dto.ConversationResponse;
import ir.aut.secondhand.dto.MessageResponse;
import ir.aut.secondhand.dto.SendMessageRequest;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Conversation;
import ir.aut.secondhand.model.Message;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.ConversationRepository;
import ir.aut.secondhand.repository.MessageRepository;

/**
 * Manages buyer-seller conversation lifecycle and message persistence for the secondhand marketplace.
 *
 * This service enforces advertisement availability rules, prevents self-conversations for sellers,
 * ensures only participants can interact with a conversation, and updates message read status
 * as recipients retrieve conversation history.
 */
@Service
public class ConversationService {

    private final UserService userService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AdvertisementRepository advertisementRepository;

    /**
     * Constructs the service with the required repository and user context dependencies.
     *
     * @param userService user context provider for the authenticated buyer or seller
     * @param conversationRepository repository used for conversation retrieval and persistence
     * @param messageRepository repository used for message persistence and status updates
     * @param advertisementRepository repository used for validating advertisement state and seller resolution
     */
    public ConversationService(UserService userService, ConversationRepository conversationRepository, MessageRepository messageRepository, AdvertisementRepository advertisementRepository) {
        this.userService = userService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.advertisementRepository = advertisementRepository;
    }

    /**
     * Retrieves an existing open conversation for a buyer and advertisement or creates a new one.
     *
     * A new conversation is only permitted when the advertisement exists and has been approved or sold.
     * A buyer cannot initiate a conversation for their own advertisement, and any existing thread is reused
     * instead of duplicating conversation records.
     *
     * @param advertisementId identifier of the advertisement associated with the conversation
     * @param buyer current authenticated user who will act as the buyer in the conversation
     * @return the existing conversation or a newly persisted OPEN conversation
     * @throws ResourceNotFoundException when the referenced advertisement does not exist or is not available for conversation
     * @throws IllegalArgumentException when the buyer is the same user as the seller of the advertisement
     */
    private Conversation getOrCreateConversation(Long advertisementId, User buyer) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        // Only approved or sold advertisements may be used to initiate buyer-seller chats.
        if(advertisement.getStatus() != Advertisement.AdvertisementStatus.APPROVED && advertisement.getStatus() != Advertisement.AdvertisementStatus.SOLD ){
            throw new ResourceNotFoundException("advertisement");
        }

        User seller = advertisement.getSeller();

        // Prevent seller from opening a conversation on their own advertisement.
        if (seller.getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You cannot start a conversation with your own advertisement");
        }

        Optional<Conversation> existingChat = conversationRepository
                .findByBuyerIdAndAdvertisementId(buyer.getId(), advertisementId);

        // Reuse an existing buyer-advertisement thread when available.
        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        Conversation newConversation = new Conversation();
        newConversation.setAdvertisement(advertisement);
        newConversation.setBuyer(buyer);
        newConversation.setSeller(seller);
        newConversation.setStatus(Conversation.ConversationStatus.OPEN);

        return conversationRepository.save(newConversation);
    }

    /**
     * Sends a message within the context of an existing conversation or starts a new one.
     *
     * When the conversationId is absent, the method establishes or reuses a conversation for the
     * given advertisement. For existing conversations, it validates that the current user is either
     * the buyer or seller and that the advertised item remains available for discussion.
     * Closed conversations are not eligible for new messages.
     *
     * @param conversationId optional identifier of the existing conversation to use
     * @param messageRequest payload containing message text and the advertisement identifier when starting a new thread
     * @return the saved message response representing the posted message
     * @throws IllegalArgumentException when the advertisementId is missing for a new conversation, when the advertisement is unavailable,
     *         or when the conversation has already been closed
     * @throws ResourceNotFoundException when the referenced conversation does not exist or the current user is not a participant
     */
    public MessageResponse sendMessage(Long conversationId, SendMessageRequest messageRequest) {
        User currentUser = userService.getCurrentUser();

        Conversation conversation;

        if (conversationId == null || conversationId == 0) {
            if (messageRequest.getAdvertisementId() == null) {
                throw new IllegalArgumentException("Advertisement ID is required to start a new conversation");
            }

            // Create or reuse a conversation only when starting a new message thread.
            conversation = getOrCreateConversation(messageRequest.getAdvertisementId(), currentUser);
        } else {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("conversation"));
            Advertisement ad = conversation.getAdvertisement();

            boolean isBuyer = conversation.getBuyer().getId().equals(currentUser.getId());
            boolean isSeller = conversation.getSeller().getId().equals(currentUser.getId());

            // Confirm current user is either buyer or seller in this conversation.
            if (!isBuyer && !isSeller) {
                throw new ResourceNotFoundException("conversation");
            }
            // Reject messages when advertisement status no longer allows discussion.
            if(ad.getStatus() != Advertisement.AdvertisementStatus.APPROVED && ad.getStatus() != Advertisement.AdvertisementStatus.SOLD) {
                throw new IllegalArgumentException("This advertisement is no longer available for discussion");
            }
        }

        // Block posting to conversations that have been intentionally closed.
        if (conversation.getStatus() == Conversation.ConversationStatus.CLOSED) {
            throw new IllegalArgumentException("This conversation has been closed");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(currentUser);
        message.setContent(messageRequest.getContent());
        message.setStatus(Message.MessageStatus.SENT);

        Message savedMessage = messageRepository.save(message);

        conversationRepository.save(conversation);

        return new MessageResponse(savedMessage);
    }


    /**
     * Retrieves active conversation summaries for the current user, including the latest visible message.
     *
     * Deleted conversations are excluded from the returned list. Each summary includes the most recent
     * non-deleted message content so the UI can render a meaningful preview for the user.
     *
     * @return list of conversation responses for the current authenticated user
     */
    public List<ConversationResponse> getConversations() {
        User currentUser = userService.getCurrentUser();

        List<Conversation> conversations = conversationRepository
                .findByBuyerIdOrSellerIdOrderByLastUpdatedAtDesc(currentUser.getId(), currentUser.getId());

        List<ConversationResponse> responseList = new ArrayList<>();

        for (Conversation conversation : conversations) {
            // Skip conversations that are logically deleted and should not appear in the user inbox.
            if (conversation.getStatus() != Conversation.ConversationStatus.DELETED) {
                ConversationResponse response = new ConversationResponse(conversation, currentUser);

                // Obtain the most recent message that has not been deleted to show as the thread preview.
                Optional<Message> messageOpt = messageRepository.findFirstByConversationIdAndStatusNotOrderByCreatedAtDesc(conversation.getId(), Message.MessageStatus.DELETED);
                String latestMessageContent;

                if (messageOpt.isPresent()) {
                    latestMessageContent = messageOpt.get().getContent();
                } else {
                    latestMessageContent = "";
                };

                response.setLastMessage(latestMessageContent);
                responseList.add(response);
            }
        }

        return responseList;
    }


    /**
     * Loads the chronological message history for a conversation and updates unread inbound messages to SEEN.
     *
     * Only participants in the conversation are permitted to retrieve messages. Messages marked as DELETED
     * are excluded from the returned history, while incoming messages in SENT state are transitioned to SEEN.
     *
     * @param conversationId identifier of the conversation whose messages will be retrieved
     * @return ordered list of visible message responses for the conversation
     * @throws ResourceNotFoundException when the conversation does not exist or the current user is not a participant
     */
    public List<MessageResponse> getMessages(Long conversationId) {
        User currentUser = userService.getCurrentUser();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        boolean isBuyer = conversation.getBuyer().getId().equals(currentUser.getId());
        boolean isSeller = conversation.getSeller().getId().equals(currentUser.getId());

        // Ensure the current user is a recognized participant before exposing messages.
        if (!isBuyer && !isSeller) {
            throw new ResourceNotFoundException("conversation");
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        List<MessageResponse> responseList = new ArrayList<>();
        List<Message> messagesToUpdate = new ArrayList<>();

        for (Message message : messages) {
            if (message.getStatus() != Message.MessageStatus.DELETED) {

                if (!message.getSender().getId().equals(currentUser.getId()) &&
                        message.getStatus() == Message.MessageStatus.SENT) {

                    // Mark unread incoming messages as SEEN when they are delivered to the recipient.
                    message.setStatus(Message.MessageStatus.SEEN);
                    messagesToUpdate.add(message);
                }

                responseList.add(new MessageResponse(message));
            }
        }

        if (!messagesToUpdate.isEmpty()) {
            messageRepository.saveAll(messagesToUpdate);
        }

        return responseList;
    }

    /**
     * Marks a message as deleted by the original sender so it no longer appears in conversation views.
     *
     * The deletion is logical: the message record remains persisted but its status changes to DELETED.
     * Only the sender of the message is authorized to perform this action.
     *
     * @param messageId identifier of the message to delete
     * @throws ResourceNotFoundException when the message does not exist
     * @throws IllegalArgumentException when the current user is not the sender of the message
     */
    public void deleteMessage(Long messageId) {
        User currentUser = userService.getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("message"));

        // Only the originating sender may mark their own message as deleted.
        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cant delete this message");
        }

        message.setStatus(Message.MessageStatus.DELETED);

        messageRepository.save(message);
    }
}

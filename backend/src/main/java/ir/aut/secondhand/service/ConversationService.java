package ir.aut.secondhand.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Conversation;
import ir.aut.secondhand.model.Message;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.ConversationRepository;
import ir.aut.secondhand.repository.MessageRepository;
import org.springframework.stereotype.Service;

import ir.aut.secondhand.dto.ConversationResponse;
import ir.aut.secondhand.dto.MessageResponse;
import ir.aut.secondhand.dto.SendMessageRequest;

@Service
public class ConversationService {

    private final UserService userService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AdvertisementRepository advertisementRepository;

    public ConversationService(UserService userService, ConversationRepository conversationRepository, MessageRepository messageRepository, AdvertisementRepository advertisementRepository) {
        this.userService = userService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.advertisementRepository = advertisementRepository;
    }

    private Conversation getOrCreateConversation(Long advertisementId, User buyer) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("advertisement"));

        User seller = advertisement.getSeller();

        if (seller.getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You cannot start a conversation with your own advertisement");
        }

        Optional<Conversation> existingChat = conversationRepository
                .findByBuyerIdAndAdvertisementId(buyer.getId(), advertisementId);

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

    public MessageResponse sendMessage(Long conversationId, SendMessageRequest messageRequest) {
        User currentUser = userService.getCurrentUser();

        Conversation conversation;

        if (conversationId == 0) {
            if (messageRequest.getAdvertisementId() == null) {
                throw new IllegalArgumentException("Advertisement ID is required to start a new conversation");
            }

            conversation = getOrCreateConversation(messageRequest.getAdvertisementId(), currentUser);
        } else {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

            boolean isBuyer = conversation.getBuyer().getId().equals(currentUser.getId());
            boolean isSeller = conversation.getSeller().getId().equals(currentUser.getId());

            if (!isBuyer && !isSeller) {
                throw new ResourceNotFoundException("conversation");
            }
        }

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


    public List<ConversationResponse> getConversations() {
        User currentUser = userService.getCurrentUser();

        List<Conversation> conversations = conversationRepository
                .findByBuyerIdOrSellerIdOrderByLastUpdatedAtDesc(currentUser.getId(), currentUser.getId());

        List<ConversationResponse> responseList = new ArrayList<>();

        for (Conversation conversation : conversations) {
            if (conversation.getStatus() != Conversation.ConversationStatus.DELETED) {
                responseList.add(new ConversationResponse(conversation, currentUser));
            }
        }

        return responseList;
    }


    public List<MessageResponse> getMessages(Long conversationId) {
        User currentUser = userService.getCurrentUser();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        boolean isBuyer = conversation.getBuyer().getId().equals(currentUser.getId());
        boolean isSeller = conversation.getSeller().getId().equals(currentUser.getId());

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

    public void deleteMessage(Long messageId) {
        User currentUser = userService.getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("message"));

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cant delete this message");
        }

        message.setStatus(Message.MessageStatus.DELETED);

        messageRepository.save(message);
    }
}

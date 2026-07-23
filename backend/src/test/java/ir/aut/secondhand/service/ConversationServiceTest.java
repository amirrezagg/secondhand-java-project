package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.MessageResponse;
import ir.aut.secondhand.dto.SendMessageRequest;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Conversation;
import ir.aut.secondhand.model.Message;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.AdvertisementRepository;
import ir.aut.secondhand.repository.ConversationRepository;
import ir.aut.secondhand.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConversationServiceTest {

    private ConversationService conversationService;
    private UserService stubUserService;
    private ConversationRepository stubConversationRepository;
    private MessageRepository stubMessageRepository;
    private AdvertisementRepository stubAdvertisementRepository;

    private User currentUser;
    private User sellerUser;
    private Advertisement mockAd;
    private Conversation mockConversation;
    private Message mockMessage;

    private Message lastSavedMessage;
    private Optional<Conversation> existingConversationResult = Optional.empty();

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("buyerUser");

        sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setUsername("sellerUser");

        mockAd = new Advertisement();
        mockAd.setId(10L);
        mockAd.setStatus(Advertisement.AdvertisementStatus.APPROVED);
        mockAd.setSeller(sellerUser);

        mockConversation = new Conversation();
        mockConversation.setId(100L);
        mockConversation.setBuyer(currentUser);
        mockConversation.setSeller(sellerUser);
        mockConversation.setAdvertisement(mockAd);
        mockConversation.setStatus(Conversation.ConversationStatus.OPEN);

        mockMessage = new Message();
        mockMessage.setId(500L);
        mockMessage.setSender(currentUser);
        mockMessage.setContent("Hello seller!");
        mockMessage.setStatus(Message.MessageStatus.SENT);
        mockMessage.setConversation(mockConversation);

        stubUserService = new UserService(null, null) {
            @Override public User getCurrentUser() { return currentUser; }
        };

        stubAdvertisementRepository = (AdvertisementRepository) java.lang.reflect.Proxy.newProxyInstance(
                AdvertisementRepository.class.getClassLoader(),
                new Class<?>[]{AdvertisementRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findById") && args[0].equals(10L)) {
                        return Optional.of(mockAd);
                    }
                    return Optional.empty();
                });

        stubConversationRepository = (ConversationRepository) java.lang.reflect.Proxy.newProxyInstance(
                ConversationRepository.class.getClassLoader(),
                new Class<?>[]{ConversationRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findByBuyerIdAndAdvertisementId")) {
                        return existingConversationResult;
                    }
                    if (method.getName().equals("findById") && args[0].equals(100L)) {
                        return Optional.of(mockConversation);
                    }
                    if (method.getName().equals("save")) {
                        return args[0];
                    }
                    return Optional.empty();
                });

        stubMessageRepository = (MessageRepository) java.lang.reflect.Proxy.newProxyInstance(
                MessageRepository.class.getClassLoader(),
                new Class<?>[]{MessageRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findById") && args[0].equals(500L)) {
                        return Optional.of(mockMessage);
                    }
                    if (method.getName().equals("save")) {
                        Message msg = (Message) args[0];
                        msg.setId(500L);
                        lastSavedMessage = msg;
                        return msg;
                    }
                    return Optional.empty();
                });

        conversationService = new ConversationService(
                stubUserService,
                stubConversationRepository,
                stubMessageRepository,
                stubAdvertisementRepository
        );

        lastSavedMessage = null;
        existingConversationResult = Optional.empty();
    }

    @Test
    void testSendMessage_NewConversation_Success() {
        SendMessageRequest request = new SendMessageRequest();
        request.setAdvertisementId(10L);
        request.setContent("Is this item still available?");

        existingConversationResult = Optional.empty();

        MessageResponse response = conversationService.sendMessage(null, request);

        assertNotNull(response);
        assertEquals("Is this item still available?", response.getContent());
        assertNotNull(lastSavedMessage);
        assertEquals(Message.MessageStatus.SENT, lastSavedMessage.getStatus());
    }

    @Test
    void testSendMessage_OnClosedConversation_ThrowsException() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Can I still buy it?");

        mockConversation.setStatus(Conversation.ConversationStatus.CLOSED);

        assertThrows(IllegalArgumentException.class, () ->
                conversationService.sendMessage(100L, request)
        );
    }

    @Test
    void testDeleteMessage_Success() {
        conversationService.deleteMessage(500L);

        assertNotNull(lastSavedMessage);
        assertEquals(Message.MessageStatus.DELETED, lastSavedMessage.getStatus());
    }

    @Test
    void testDeleteMessage_ByNonSender_ThrowsException() {
        mockMessage.setSender(sellerUser);

        assertThrows(IllegalArgumentException.class, () ->
                conversationService.deleteMessage(500L)
        );
    }
}
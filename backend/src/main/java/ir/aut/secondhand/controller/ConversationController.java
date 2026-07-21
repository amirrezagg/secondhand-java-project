package ir.aut.secondhand.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ir.aut.secondhand.dto.ConversationResponse;
import ir.aut.secondhand.dto.MessageResponse;
import ir.aut.secondhand.dto.SendMessageRequest;
import ir.aut.secondhand.service.ConversationService;
import jakarta.validation.Valid;

/**
 * REST controller that exposes conversation and messaging endpoints.
 *
 * Handles sending messages, retrieving conversation summaries, fetching message
 * history for a specific conversation, and deleting individual messages.
 * All business operations are delegated to {@link ConversationService}.
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestParam(required = false) Long conversationId,
            @Valid @RequestBody SendMessageRequest messageRequest) {

        MessageResponse response = conversationService.sendMessage(conversationId, messageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations() {
        List<ConversationResponse> conversations = conversationService.getConversations();
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
        List<MessageResponse> messages = conversationService.getMessages(conversationId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long messageId) {
        conversationService.deleteMessage(messageId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "success");

        return ResponseEntity.ok(response);
    }
}
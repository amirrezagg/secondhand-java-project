package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.ConversationResponse;
import ir.aut.secondhand.dto.MessageResponse;
import ir.aut.secondhand.dto.SendMessageRequest;
import ir.aut.secondhand.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        conversationService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
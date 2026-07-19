package ir.aut.secondhand.dto;

import java.time.LocalDateTime;

import ir.aut.secondhand.model.Message;

public class MessageResponse {

    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private Message.MessageStatus status;
    private LocalDateTime createdAt;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getFullName();
        this.content = message.getContent();
        this.status = message.getStatus();
        this.createdAt = message.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message.MessageStatus getStatus() {
        return status;
    }

    public void setStatus(Message.MessageStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

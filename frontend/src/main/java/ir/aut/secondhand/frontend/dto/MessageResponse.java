package ir.aut.secondhand.frontend.dto;

public class MessageResponse {

    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private String sentAt;
    private String messageStatus;
    private String createdAt;

    public MessageResponse() {
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public String getSentAt() {
        return sentAt;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
package ir.aut.secondhand.frontend.dto;

public class ConversationResponse {

    private Long id;
    private Long advertisementId;
    private String advertisementTitle;
    private Long contactId;
    private String contactName;
    private String conversationStatus;
    private String lastUpdatedAt;
    private String lastMessage;

    public ConversationResponse() {
    }

    public Long getId() {
        return id;
    }

    public Long getAdvertisementId() {
        return advertisementId;
    }

    public String getAdvertisementTitle() {
        return advertisementTitle;
    }

    public Long getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getConversationStatus() {
        return conversationStatus;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
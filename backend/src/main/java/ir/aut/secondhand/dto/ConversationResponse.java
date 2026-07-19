package ir.aut.secondhand.dto;

import java.time.LocalDateTime;

import ir.aut.secondhand.model.Conversation;
import ir.aut.secondhand.model.User;

public class ConversationResponse {

    private Long id;
    private Long advertisementId;
    private String advertisementTitle;
    private Long contactId;
    private String contactName;
    private Conversation.ConversationStatus status;
    private LocalDateTime lastUpdatedAt;

    public ConversationResponse(Conversation conversation, User currentUser) {
        this.id = conversation.getId();

        if (conversation.getAdvertisement() != null) {
            this.advertisementId = conversation.getAdvertisement().getId();
            this.advertisementTitle = conversation.getAdvertisement().getTitle();
        }

        User contact = conversation.getSeller().getId().equals(currentUser.getId()) ? conversation.getBuyer() : conversation.getSeller();

        this.contactId = contact.getId();
        this.contactName = contact.getFullName();

        this.status = conversation.getStatus();
        this.lastUpdatedAt = conversation.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(Long advertisementId) {
        this.advertisementId = advertisementId;
    }

    public String getAdvertisementTitle() {
        return advertisementTitle;
    }

    public void setAdvertisementTitle(String advertisementTitle) {
        this.advertisementTitle = advertisementTitle;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Conversation.ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(Conversation.ConversationStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}

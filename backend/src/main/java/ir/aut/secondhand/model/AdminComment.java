package ir.aut.secondhand.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class AdminComment {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum AdminActionType {
        APPROVE,
        REJECT,
        DELETE
    }

    public AdminComment() {
        this.createdAt = LocalDateTime.now();
    }

    public AdminComment(String content, AdminActionType actionType, User admin) {
        this.content = content;
        this.actionType = actionType;
        this.admin = admin;
        this.createdAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AdminActionType getActionType() {
        return actionType;
    }

    public void setActionType(AdminActionType actionType) {
        this.actionType = actionType;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.AdminComment;

import java.time.LocalDateTime;

public class AdminCommentResponse {
    private String reason;
    private String adminName;
    private LocalDateTime createdAt;

    public AdminCommentResponse(AdminComment comment) {
        this.reason = comment.getContent();
        this.adminName = comment.getAdmin().getFullName();
        this.createdAt = comment.getCreatedAt();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

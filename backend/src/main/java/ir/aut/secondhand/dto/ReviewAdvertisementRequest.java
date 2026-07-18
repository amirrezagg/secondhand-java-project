package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.Advertisement.AdvertisementStatus;
import jakarta.validation.constraints.NotNull;

public class ReviewAdvertisementRequest {

    @NotNull(message = "status cannot be null")
    private AdvertisementStatus status;

    private String rejectionReason;

    public AdvertisementStatus getStatus() {
        return status;
    }

    public void setStatus(AdvertisementStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
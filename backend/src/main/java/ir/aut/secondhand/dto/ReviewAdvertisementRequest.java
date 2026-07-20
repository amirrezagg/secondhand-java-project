package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.Advertisement.AdvertisementStatus;
import jakarta.validation.constraints.NotNull;

public class ReviewAdvertisementRequest {

    @NotNull(message = "status cannot be null")
    private AdvertisementStatus adStatus;

    private String rejectionReason;

    public AdvertisementStatus getAdStatus() {
        return adStatus;
    }

    public void setAdStatus(AdvertisementStatus adStatus) {
        this.adStatus = adStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
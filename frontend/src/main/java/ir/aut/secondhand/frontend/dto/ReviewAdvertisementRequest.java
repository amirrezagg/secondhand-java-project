package ir.aut.secondhand.frontend.dto;

public class ReviewAdvertisementRequest {

    private String adStatus;
    private String rejectionReason;

    public ReviewAdvertisementRequest(
            String adStatus,
            String rejectionReason
    ) {
        this.adStatus = adStatus;
        this.rejectionReason = rejectionReason;
    }

    public String getAdStatus() {
        return adStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}

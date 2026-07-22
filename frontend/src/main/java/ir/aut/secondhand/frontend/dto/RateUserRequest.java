package ir.aut.secondhand.frontend.dto;

public class RateUserRequest {

    private Long advertisementId;
    private Integer score;
    private String comment;

    public RateUserRequest() {
    }

    public RateUserRequest(
            Long advertisementId,
            Integer score,
            String comment
    ) {
        this.advertisementId = advertisementId;
        this.score = score;
        this.comment = comment;
    }

    public Long getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(Long advertisementId) {
        this.advertisementId = advertisementId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
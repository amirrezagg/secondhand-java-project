package ir.aut.secondhand.frontend.dto;

public class SendMessageRequest {

    private Long advertisementId;
    private String content;

    public SendMessageRequest() {
    }

    public SendMessageRequest(Long advertisementId, String content) {
        this.advertisementId = advertisementId;
        this.content = content;
    }

    public Long getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(Long advertisementId) {
        this.advertisementId = advertisementId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package ir.aut.secondhand.frontend.dto;

public class AverageRateResponse {

    private Long userId;
    private Double averageRate;

    public AverageRateResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public Double getAverageRate() {
        return averageRate;
    }
}
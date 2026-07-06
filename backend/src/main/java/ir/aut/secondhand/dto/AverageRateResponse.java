package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.User;

public class AverageRateResponse {

    private Long userId;
    private Double averageRate;

    public AverageRateResponse(Long userId, Double averageRate) {
        this.userId = userId;
        this.averageRate = averageRate;
    }

    public AverageRateResponse(User user, Double averageRate) {
        this.userId = user.getId();
        this.averageRate = averageRate;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAverageRate() {
        return this.averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }

}

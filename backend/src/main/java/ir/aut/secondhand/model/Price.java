package ir.aut.secondhand.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Price {

    @Column(name = "price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_currency", nullable = false)
    private Currency currency;

    public enum Currency {
        IRR("Rial"),
        IRT("Toman");

        private final String label;

        Currency(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public Price() {
    }

    public Price(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}

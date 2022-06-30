package antifraud.model.result.dto;

import javax.validation.constraints.Positive;

public class TransactionRequest {
    @Positive
    private long amount;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}

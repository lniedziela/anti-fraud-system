package antifraud.model.result.dto;

import antifraud.model.result.Result;

public class TransactionResultResponse {
    private final Result result;

    public TransactionResultResponse(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}

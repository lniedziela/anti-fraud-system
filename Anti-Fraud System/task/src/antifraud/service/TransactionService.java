package antifraud.service;

import antifraud.model.result.Result;
import org.springframework.stereotype.Service;

import static antifraud.model.result.Result.*;

@Service
public class TransactionService {
    public Result process(long amount) {
        if (amount <= 200) {
            return ALLOWED;
        } else if (amount <= 1500) {
            return MANUAL_PROCESSING;
        } else {
            return PROHIBITED;
        }
    }
}

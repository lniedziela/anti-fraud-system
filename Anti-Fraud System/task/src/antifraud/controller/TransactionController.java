package antifraud.controller;

import antifraud.model.result.dto.TransactionRequest;
import antifraud.model.result.dto.TransactionResultResponse;
import antifraud.service.TransactionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    @Secured("ROLE_MERCHANT")
    TransactionResultResponse transaction(@Valid @RequestBody TransactionRequest request) {
        var result = transactionService.process(request.getAmount());
        return new TransactionResultResponse(result);
    }
}

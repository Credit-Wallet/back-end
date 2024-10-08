package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.TransactionService;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping()
    public ApiResponse<List<Transaction>> getTransactions(@RequestHeader("Authorization") String token) {
        var result = transactionService.getTransactions(token);
        return ApiResponse.<List<Transaction>>builder()
                .result(result)
                .build();
    }
}

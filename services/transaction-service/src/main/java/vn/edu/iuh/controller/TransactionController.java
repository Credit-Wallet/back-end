package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.TransactionResponse;
import vn.edu.iuh.service.TransactionService;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping()
    public ApiResponse<Page<TransactionResponse>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Timestamp fromDate,
            @RequestParam(required = false) Timestamp toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var result = transactionService.getTransactions(token, fromDate, toDate, page, limit);
        return ApiResponse.<Page<TransactionResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> getTransactionById(@PathVariable Long id){
        var result = transactionService.getTransactionById(id);
        return ApiResponse.<TransactionResponse>builder()
                .result(result)
                .build();
    }
}

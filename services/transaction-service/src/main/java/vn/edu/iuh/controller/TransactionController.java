package vn.edu.iuh.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.request.CreateTransactionRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.TransactionService;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping()
    public ApiResponse<Transaction> createTransaction(@Valid @RequestBody CreateTransactionRequest request,
                                                      @RequestHeader("Authorization") String token) {
        var result = transactionService.createTransaction(request, token);
        return ApiResponse.<Transaction>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<Transaction> confirmTransaction(@PathVariable("id") Long id) {
        var result = transactionService.confirmTransaction(id);
        return ApiResponse.<Transaction>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}/confirm-detail")
    public ApiResponse<Transaction> confirmTransactionDetail(@PathVariable("id") Long id,
                                                             @RequestHeader("Authorization") String token) {
        var result = transactionService.confirmTransactionDetail(id, token);
        return ApiResponse.<Transaction>builder()
                .result(result)
                .build();
    }

}

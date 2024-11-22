package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.iuh.request.CreateTransactionRequest;
import vn.edu.iuh.request.UpdateTransactionRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.TransactionTransferResponse;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionClient {
    @PostMapping("transactions")
    ApiResponse<TransactionTransferResponse> createTransaction(@RequestBody CreateTransactionRequest transaction);

    @PutMapping("transactions/{id}")
    ApiResponse<TransactionTransferResponse> updateTransaction(@PathVariable Long id, @RequestBody UpdateTransactionRequest transaction) ;
}

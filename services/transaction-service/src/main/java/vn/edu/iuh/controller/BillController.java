package vn.edu.iuh.controller;

import feign.Body;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.request.CancelBillRequest;
import vn.edu.iuh.request.CreateBillRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.BillResponse;
import vn.edu.iuh.service.BillService;

import java.sql.Timestamp;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @GetMapping()
    public ApiResponse<Page<BillResponse>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Timestamp fromDate,
            @RequestParam(required = false) Timestamp toDate,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var result = billService.getBills(token, fromDate, toDate,status, page, limit);
        return ApiResponse.<Page<BillResponse>>builder()
                .result(result)
                .build();
    }

    @PostMapping()
    public ApiResponse<Bill> createTransaction(@Valid @RequestBody CreateBillRequest request,
                                               @RequestHeader("Authorization") String token) {
        var result = billService.createBill(request, token);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

    @PostMapping("/cancel/{id}")
    public ApiResponse<Bill> cancelTransaction(@PathVariable("id") Long id,
                                               @RequestHeader("Authorization") String token) {
        var result = billService.cancelBill(id, token);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<Bill> confirmTransaction(@PathVariable("id") Long id) {
        var result = billService.confirmBill(id);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}/confirm-bill-request")
    public ApiResponse<Bill> confirmTransactionDetail(@PathVariable("id") Long id,
                                                             @RequestHeader("Authorization") String token) {
        var result = billService.confirmBillRequest(id, token);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}/cancel-bill-request")
    public ApiResponse<Bill> cancelTransactionDetail(@PathVariable("id") Long id,
                                                     @RequestHeader("Authorization") String token,@RequestBody(required = false) CancelBillRequest request) {
        if (request == null) {
            request = new CancelBillRequest();
        }
        
        var result = billService.cancelBillRequest(id, token,request);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BillResponse> getBillById(@PathVariable("id") Long id) {
        var result = billService.getBillById(id);
        return ApiResponse.<BillResponse>builder()
                .result(result)
                .build();
    }

}

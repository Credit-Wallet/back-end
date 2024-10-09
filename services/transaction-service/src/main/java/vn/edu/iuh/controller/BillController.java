package vn.edu.iuh.controller;

import feign.Body;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.request.CancelBillRequest;
import vn.edu.iuh.request.CreateBillRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.BillService;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @PostMapping()
    public ApiResponse<Bill> createTransaction(@Valid @RequestBody CreateBillRequest request,
                                               @RequestHeader("Authorization") String token) {
        var result = billService.createBill(request, token);
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
                                                     @RequestHeader("Authorization") String token,@RequestBody() CancelBillRequest request) {
        var result = billService.cancelBillRequest(id, token,request);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Bill> getTransaction(@PathVariable("id") Long id) {
        var result = billService.findById(id);
        return ApiResponse.<Bill>builder()
                .result(result)
                .build();
    }

}

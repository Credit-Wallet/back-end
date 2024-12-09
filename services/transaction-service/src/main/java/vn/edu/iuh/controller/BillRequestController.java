package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.BillRequestResponse;
import vn.edu.iuh.response.BillRequestResponseV2;
import vn.edu.iuh.service.BillRequestService;

import java.sql.Timestamp;

@RestController
@RequestMapping("/bill-requests")
@RequiredArgsConstructor
public class BillRequestController {
    private final BillRequestService billRequestService;

    @GetMapping()
    public ApiResponse<Page<BillRequestResponse>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Timestamp fromDate,
            @RequestParam(required = false) Timestamp toDate,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var result = billRequestService.getBillRequests(token, fromDate, toDate,status, page, limit);
        return ApiResponse.<Page<BillRequestResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BillRequestResponse> getBillRequest(@PathVariable("id") Long id) {
        var result = billRequestService.getBillRequestById(id);
        return ApiResponse.<BillRequestResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}/ver-2")
    public ApiResponse<BillRequestResponseV2> getBillRequestVer2(@PathVariable("id") Long id) {
        var result = billRequestService.getBillRequestByIdV2(id);
        return ApiResponse.<BillRequestResponseV2>builder()
                .result(result)
                .build();
    }
}

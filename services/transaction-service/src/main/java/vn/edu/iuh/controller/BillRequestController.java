package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.BillRequestService;

import java.sql.Timestamp;

@RestController
@RequestMapping("/bill-requests")
@RequiredArgsConstructor
public class BillRequestController {
    private final BillRequestService billRequestService;

    @GetMapping()
    public ApiResponse<Page<BillRequest>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Timestamp fromDate,
            @RequestParam(required = false) Timestamp toDate,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var result = billRequestService.getBillRequests(token, fromDate, toDate,status, page, limit);
        return ApiResponse.<Page<BillRequest>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BillRequest> getBillRequest(@PathVariable("id") Long id) {
        var result = billRequestService.findById(id);
        return ApiResponse.<BillRequest>builder()
                .result(result)
                .build();
    }
}

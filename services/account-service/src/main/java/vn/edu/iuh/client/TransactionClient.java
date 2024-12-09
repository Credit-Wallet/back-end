package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.BillRequestResponse;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionClient {
    //get bill request by id
    @GetMapping("/bill-requests/{id}")
    ApiResponse<BillRequestResponse> getBillRequest(@PathVariable("id") Long id);

    @GetMapping("/bill-requests/{id}/ver-2")
    ApiResponse<BillRequestResponse> getBillRequestV2(@PathVariable("id") Long id);
}

package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.BillRequestResponse;
import vn.edu.iuh.response.NetworkResponse;

@Service
public class BillRequestMapper {

    public BillRequestResponse toBillRequestResponse(BillRequest billRequest, AccountResponse accountResponse, NetworkResponse networkResponse) {
        return BillRequestResponse.builder()
                .id(billRequest.getId())
                .accountId(billRequest.getAccountId())
                .amount(billRequest.getAmount())
                .bill(billRequest.getBill())
                .status(billRequest.getStatus())
                .description(billRequest.getDescription())
                .createdAt(billRequest.getCreatedAt().toString())
                .account(accountResponse)
                .network(networkResponse)
                .build();
    }
}

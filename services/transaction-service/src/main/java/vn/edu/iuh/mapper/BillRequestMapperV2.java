package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.BillRequestResponse;
import vn.edu.iuh.response.BillRequestResponseV2;
import vn.edu.iuh.response.NetworkResponse;

@Service
public class BillRequestMapperV2 {

    public BillRequestResponseV2 toBillRequestResponse(BillRequest billRequest) {
        return BillRequestResponseV2.builder()
                .id(billRequest.getId())
                .accountId(billRequest.getAccountId())
                .amount(billRequest.getAmount())
                .status(billRequest.getStatus())
                .description(billRequest.getDescription())
                .createdAt(billRequest.getCreatedAt().toString())
                .build();
    }
}

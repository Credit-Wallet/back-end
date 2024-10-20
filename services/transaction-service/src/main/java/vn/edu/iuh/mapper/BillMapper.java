package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.request.CreateBillRequest;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.BillRequestResponse;
import vn.edu.iuh.response.BillResponse;
import vn.edu.iuh.response.TransactionResponse;

import java.util.Set;

@Service
public class BillMapper {
    public Bill toBill(CreateBillRequest request){
        return Bill.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .networkId(request.getNetworkId())
                .build();
    }

    public BillResponse toBillResponse(Bill bill, AccountResponse accountResponse, Set<TransactionResponse> transactionResponses, Set<BillRequestResponse> billRequestResponses){
        return BillResponse.builder()
                .id(bill.getId())
                .name(bill.getName())
                .accountId(bill.getAccountId())
                .amount(bill.getAmount())
                .actualAmount(bill.getActualAmount())
                .transactions(transactionResponses)
                .billRequests(billRequestResponses)
                .networkId(bill.getNetworkId())
                .status(bill.getStatus())
                .createdAt(bill.getCreatedAt().toString())
                .updatedAt(bill.getUpdatedAt().toString())
                .account(accountResponse)
                .build();
    }
}

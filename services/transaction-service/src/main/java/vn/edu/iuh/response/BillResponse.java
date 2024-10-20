package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.model.Transaction;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private Long id;
    private String name;
    private Long accountId;
    private double amount;
    private double actualAmount;
    private Set<Transaction> transactions;
    private Set<BillRequest> billRequests;
    private Long networkId;
    private Status status;
    private String createdAt;
    private String updatedAt;
    private AccountResponse account;
}

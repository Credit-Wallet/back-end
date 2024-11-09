package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.model.Bill;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long accountId;
    private Long networkId;
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
    private Bill bill;
    private Timestamp createdAt;
    private boolean type;
    private AccountResponse fromAccount;
    private AccountResponse toAccount;
    private String hash;
}

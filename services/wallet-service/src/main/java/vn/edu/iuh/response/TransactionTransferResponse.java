package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionTransferResponse {
    private Long id;
    private Long accountId;
    private Long networkId;
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
    private Timestamp createdAt;
    private boolean type;
    private String hash;
}

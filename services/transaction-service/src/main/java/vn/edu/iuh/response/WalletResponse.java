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
public class WalletResponse {
    private Long id;
    private Long accountId;
    private double balance;
    private double debt;
    private Long networkId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String walletAddress;
    private double balanceOf;
}

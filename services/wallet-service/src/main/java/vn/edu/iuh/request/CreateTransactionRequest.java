package vn.edu.iuh.request;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateTransactionRequest {
    private Long accountId;
    private Long networkId;
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
    private boolean type;
}

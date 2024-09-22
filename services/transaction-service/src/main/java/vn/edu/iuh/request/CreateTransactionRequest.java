package vn.edu.iuh.request;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateTransactionRequest {
    private String name;
    private double amount;
    private boolean allMember;
    private boolean divideEqually;
    private Set<CreateTransactionDetailRequest> transactionDetails = new HashSet<>();
    private Long networkId;
}

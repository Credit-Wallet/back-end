package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.model.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRequestResponse {
    private Long id;
    private Long accountId;
    private double amount;
    private Status status;
    private String description;
    private String createdAt;
    private AccountResponse account;
}

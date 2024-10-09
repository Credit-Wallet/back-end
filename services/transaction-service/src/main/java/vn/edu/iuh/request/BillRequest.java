package vn.edu.iuh.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BillRequest {
    private Long accountId;
    private double amount;
}

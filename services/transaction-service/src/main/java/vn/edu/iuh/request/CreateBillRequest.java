package vn.edu.iuh.request;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateBillRequest {
    private String name;
    private double amount;
    private Set<BillRequest> billRequests = new HashSet<>();
    private Long networkId;
}

package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkResponse {
    private Long id;
    private String name;
    private double minBalance;
    private double maxBalance;
    private Long maxMember;
    private String description;
    private String walletAddress;
    private String privateKey;
}

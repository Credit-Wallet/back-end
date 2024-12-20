package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.model.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String username;
    private String email;
    private Long selectedNetworkId;
    private String urlAvatar;
    private boolean isTwoFactor;
    private String secretKey;
}

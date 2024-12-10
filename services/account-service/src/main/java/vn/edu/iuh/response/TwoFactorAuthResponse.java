package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorAuthResponse {
    private String secretKey;
    private String qrCodeBase64Image;
}

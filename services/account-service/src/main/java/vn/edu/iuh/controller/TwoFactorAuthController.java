package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Account;
import vn.edu.iuh.repository.AccountRepository;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.TwoFactorAuthResponse;
import vn.edu.iuh.service.AuthService;
import vn.edu.iuh.service.GoogleAuthenticatorService;
import vn.edu.iuh.util.GoogleAuthenticatorUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TwoFactorAuthController {
    private final GoogleAuthenticatorService googleAuthenticatorService;
    private final AuthService authService;
    private final AccountRepository accountRepository;

    // Endpoint to generate and return secret key and QR code URL
    @GetMapping("/generate-two-factor/{userName}")
    public ApiResponse<TwoFactorAuthResponse> generate2FA(
            @PathVariable("userName") String userName,
            @RequestHeader("Authorization") String token
    ) {
        String secretKey = googleAuthenticatorService.generateSecretKey();
        String barcodeUrl = "otpauth://totp/" + userName + "?secret=" + secretKey + "&issuer=FriendsPay";
        String jwtToken = token.substring(7);
        String email = authService.extractEmail(jwtToken);
        Optional<Account> account = accountRepository.findByEmail(email);
        
        if (account.isEmpty()) {
            throw new RuntimeException("Account not found");
        }

        // Generate QR code image
        BufferedImage qrCodeImage;
        try {
            qrCodeImage = GoogleAuthenticatorUtil.generateQRCodeImage(barcodeUrl, 200, 200);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }

        // Convert image to Base64 for easy display in frontend
        String base64Image = convertImageToBase64(qrCodeImage);
        
        // Save secret key to the database
        account.get().setSecretKey(secretKey);
        accountRepository.save(account.get());

        return ApiResponse.<TwoFactorAuthResponse>builder()
                .code(200)
                .result(TwoFactorAuthResponse.builder()
                        .secretKey(secretKey)
                        .qrCodeBase64Image(base64Image)
                        .build())
                .message("2FA secret key and QR code generated successfully")
                .build();
    }

    // Endpoint to validate OTP entered by the user
    @PostMapping("/validate-two-factor")
    public ApiResponse<?> validateOtp(@RequestParam String email, @RequestParam int otp) {
        Optional<Account> account = accountRepository.findByEmail(email);
        
        if (account.isEmpty()) {
            throw new RuntimeException("Account not found");
        }
        
        String secretKey = account.get().getSecretKey();
        
        return ApiResponse.builder()
                .code(200)
                .result(googleAuthenticatorService.validateOtp(secretKey, otp))
                .message("OTP validation result")
                .build();
    }

    private String convertImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error converting image to Base64", e);
        }
    }
}

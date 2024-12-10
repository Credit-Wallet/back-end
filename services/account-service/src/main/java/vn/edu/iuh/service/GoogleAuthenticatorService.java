package vn.edu.iuh.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthenticatorService {

    private final GoogleAuthenticator googleAuthenticator;

    public GoogleAuthenticatorService() {
        // Optional: Customize GoogleAuthenticator config if needed
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(30 * 1000)  // Set time step to 30 seconds
                .build();
        this.googleAuthenticator = new GoogleAuthenticator(config);
    }

    // Generate a secret key
    public String generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    // Validate the provided OTP against the stored secret key
    public boolean validateOtp(String secretKey, int otp) {
        return googleAuthenticator.authorize(secretKey, otp);
    }
}
package vn.edu.iuh.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;
    @Email(message = "Invalid email")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @Size(min = 6, message = "Confirm password must be at least 6 characters")
    private String confirmPassword;
}

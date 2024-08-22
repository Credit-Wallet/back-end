package vn.edu.iuh.request;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @ApiModelProperty(value = "Email", example = "user@gmail.com")
    private String email;
    @Size(min = 6 , message = "Password must be at least 6 characters")
    @ApiModelProperty(value = "Password", example = "123456")
    private String password;
}

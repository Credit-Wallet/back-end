package vn.edu.iuh.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.request.LoginRequest;
import vn.edu.iuh.request.RegisterRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.LoginResponse;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> authenticate(@RequestBody @Valid LoginRequest request) throws AppException {
        var result = authService.login(request);
        return ApiResponse.<LoginResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid RegisterRequest request) throws AppException {
        var result = authService.register(request);
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AccountResponse> checkMe() throws AppException {
        var result = authService.checkMe();
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .result(authService.getProfile(token))
                        .build()
        );
    }
}

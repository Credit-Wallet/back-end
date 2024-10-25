package vn.edu.iuh.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.model.NotificationMessage;
import vn.edu.iuh.request.LoginRequest;
import vn.edu.iuh.request.RegisterRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.LoginResponse;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.service.AuthService;
import vn.edu.iuh.service.FirebaseMessagingService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final FirebaseMessagingService firebaseMessagingService;

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .result(authService.getAccountById(id))
                        .build()
        );
    }
    
    //logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .result(authService.logout(token))
                        .build()
        );
    }


    //save token
    @PostMapping("/fcm-token")
    public ApiResponse<?> saveToken(@RequestBody String fcmToken, @RequestHeader("Authorization") String token) {
        return ApiResponse.builder()
                .result(authService.saveFcmToken(fcmToken, token))
                .code(201)
                .message("Token saved")
                .build();
    }
    
    //send notification
    @PostMapping("/send-notification")
    public ApiResponse<?> sendNotification(@RequestBody NotificationMessage notificationMessage) {
        return ApiResponse.builder()
                .result(firebaseMessagingService.sendNotificationByToken(notificationMessage))
                .code(201)
                .message("Notification sent")
                .build();
    }
}

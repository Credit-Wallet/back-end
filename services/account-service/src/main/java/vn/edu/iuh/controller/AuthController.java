package vn.edu.iuh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.model.NotificationMessage;
import vn.edu.iuh.producer.NotificationProducer;
import vn.edu.iuh.request.FcmTokenRequest;
import vn.edu.iuh.request.LoginRequest;
import vn.edu.iuh.request.RegisterRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.LoginResponse;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.service.AuthService;
import vn.edu.iuh.service.FileStorageService;
import vn.edu.iuh.service.FirebaseMessagingService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationProducer notificationProducer;
    private final FileStorageService fileStorageService;

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
    public ApiResponse<?> saveToken(@RequestBody FcmTokenRequest fcmTokenRequest, @RequestHeader("Authorization") String token) {
        String fcmToken = fcmTokenRequest.getToken();
        
        return ApiResponse.builder()
                .result(authService.saveFcmToken(fcmToken, token))
                .code(201)
                .message("Token saved")
                .build();
    }
    
    //send notification
    @PostMapping("/send-notification")
    public ApiResponse<?> sendNotification(@RequestBody NotificationMessage notificationMessage) throws JsonProcessingException {
        // Gửi thông báo vào hàng đợi RabbitMQ
        notificationProducer.sendToQueue(notificationMessage);

        return ApiResponse.builder()
                .code(201)
                .message("Notification sent to queue")
                .build();
    }
    
    //upload avatar
    @PostMapping("/upload-avatar")
    public ApiResponse<?> uploadAvatar(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestHeader("Authorization") String token
    ) throws IOException {
        if (file.getSize() > 1024 * 1024 * 10) {
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("File size must be less than 10MB")
                    .build();
        }

        // nén ảnh cho kích thước nhỏ hơn
        InputStream originalImage = file.getInputStream(); // Đọc file gốc
        ByteArrayOutputStream compressedImageOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .scale(1.0) // Không thay đổi kích thước (giữ nguyên)
                .outputQuality(0.8) // Giảm chất lượng ảnh xuống 80%
                .toOutputStream(compressedImageOutputStream);

        // Tạo MultipartFile mới từ ảnh đã nén
        byte[] compressedImageBytes = compressedImageOutputStream.toByteArray();
        MultipartFile compressedFile = new MockMultipartFile(
                file.getName(), // Tên trường
                file.getOriginalFilename(), // Tên file gốc
                file.getContentType(), // Loại file
                compressedImageBytes // Nội dung file đã nén
        );

        String fileName = fileStorageService.storeFile(compressedFile);
        boolean result = authService.saveAvatar(fileName, token);

        if (result) {
            return ApiResponse.builder()
                    .code(200)
                    .message("Avatar uploaded")
                    .result(fileName)
                    .build();
        } else {
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Cannot upload avatar")
                    .build();
        }
    }
    
    //update username
    @PostMapping("/update-username")
    public ApiResponse<?> updateUsername(
        @RequestBody(required = false) String username,
        @RequestHeader("Authorization") String token
    ) {
        log.info("Username: {}", username);
        boolean result = authService.updateUsername(username, token);
        
        if (result) {
            return ApiResponse.builder()
                    .code(204)
                    .message("Username updated")
                    .result(username)
                    .build();
        } else {
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Cannot update username")
                    .build();
        }
    }
    
    //update email
    @PostMapping("/update-email")
    public ApiResponse<?> updateEmail(@RequestBody String email, @RequestHeader("Authorization") String token) {
        return authService.updateEmail(email, token);
    }
    
    //update isTwoFactor
    @PostMapping("/update-is-two-factor")
    public ApiResponse<?> updateIsTwoFactor(@RequestHeader("Authorization") String token) {
        return authService.updateIsTwoFactor(token);
    }
}

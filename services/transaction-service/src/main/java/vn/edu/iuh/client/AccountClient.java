package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.request.NotificationMessageRequest;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @PostMapping("/auth/profile")
    ApiResponse<AccountResponse> getProfile(@RequestHeader("Authorization") String token);

    @GetMapping("/auth/{id}")
    ApiResponse<AccountResponse> getAccountById(@PathVariable("id") Long id);
    
    //notification
    @PostMapping("/auth/send-notification")
    ApiResponse<?> sendNotification(NotificationMessageRequest notificationMessageResponse);
}

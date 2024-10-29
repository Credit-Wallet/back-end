package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.TypeNotification;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.NotificationHistoryResponse;
import vn.edu.iuh.service.NotificationHistoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationHistoryService notificationHistoryService;
    
    @GetMapping()
    public ApiResponse<?> getNotification(
            @RequestHeader("Authorization") String token,
            @RequestParam() TypeNotification type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        System.out.println("type: " + type);
        return ApiResponse.<Page<NotificationHistoryResponse>>builder()
                .result(notificationHistoryService.getNotifications(token, type, page, limit))
                .build();
    }
    
    //read notification
    @PutMapping("/{id}/read")
    public ApiResponse<?> readNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        return notificationHistoryService.readNotification(token, id);
    }
    
    //read all
//    @PutMapping("/read-all")
//    public ApiResponse<?> readAllNotification(
//            @RequestHeader("Authorization") String token
//    ) {
//        return notificationHistoryService.readAllNotification(token);
//    }
}

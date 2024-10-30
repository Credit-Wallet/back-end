package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.NetworkClient;
import vn.edu.iuh.client.TransactionClient;
import vn.edu.iuh.mapper.NotificationHistoryMapper;
import vn.edu.iuh.model.NotificationHistory;
import vn.edu.iuh.model.NotificationMessage;
import vn.edu.iuh.model.TypeNotification;
import vn.edu.iuh.repository.NotificationHistoryRepository;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.NotificationHistoryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationHistoryService {
    private static final Logger log = LoggerFactory.getLogger(NotificationHistoryService.class);
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final AuthService authService;
    private final NotificationHistoryMapper notificationMapper;
    private final TransactionClient transactionClient;
    private final NetworkClient networkClient;

    public void saveNotificationHistory(NotificationMessage notificationMessage) {
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setTitle(notificationMessage.getTitle());
        notificationHistory.setBody(notificationMessage.getBody());
        notificationHistory.setImage(notificationMessage.getImage());
        notificationHistory.setAccountId(notificationMessage.getAccountId());
        notificationHistory.setData(notificationMessage.getData().toString());
        notificationHistory.setType(notificationMessage.getType());
        notificationHistoryRepository.save(notificationHistory);
    }
    
    //getNotification
    public Page<NotificationHistoryResponse> getNotifications(String token, TypeNotification type, int page, int limit) {
        var account = authService.getProfile(token);

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        
        var notifications = notificationHistoryRepository.findByAccountIdAndType(account.getId(), type, pageable);
        
        return notifications.map(notification -> {
            if (type == TypeNotification.BILL_REQUEST) {
                log.info("notification.getData(): " + notification.getData());
                Map<String, String> data = convertStringToMap(notification.getData());
                log.info("data: " + data);
                
                var accountResponse = authService.getAccountById(notification.getAccountId());
                var billRequestResponse = transactionClient.getBillRequest(Long.parseLong(data.get("billRequestId"))).getResult();
                var networkResponse = networkClient.getNetwork(Long.parseLong(data.get("networkId"))).getResult();
                return notificationMapper.toNotificationResponse(notification, accountResponse, billRequestResponse, networkResponse);
            } else {
                return notificationMapper.toNotificationResponse(notification, null, null, null);
            }
        });
    }

    private Map<String, String> convertStringToMap(String dataString) {
        Map<String, String> map = new HashMap<>();
        dataString = dataString.replaceAll("[{}]", ""); // Loại bỏ dấu ngoặc

        for (String entry : dataString.split(",")) {
            String[] keyValue = entry.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }
    
    //readNotification
    public ApiResponse<?> readNotification(String token, Long id) {
        var account = authService.getProfile(token);
        var notification = notificationHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        if (notification.getAccountId().equals(account.getId())) {
            notification.setRead(true);
            notificationHistoryRepository.save(notification);
            
            return ApiResponse.builder().code(200).message("Read notification successfully").build();
        } else {
            return ApiResponse.builder().code(400).message("You don't have permission to read this notification").build();
        }
    }
    
    //readAllNotification
    public ApiResponse<?> readAllNotification(String token) {
        var account = authService.getProfile(token);
        var notifications = notificationHistoryRepository.findByAccountIdAndRead(account.getId(), false);
        notifications.forEach(notification -> notification.setRead(true));
        notificationHistoryRepository.saveAll(notifications);

        return ApiResponse.builder().code(200).message("Read all notification successfully").build();
    }
}

package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.NotificationHistory;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.BillRequestResponse;
import vn.edu.iuh.response.NetworkResponse;
import vn.edu.iuh.response.NotificationHistoryResponse;

@Service
public class NotificationHistoryMapper {
    public NotificationHistoryResponse toNotificationResponse(NotificationHistory notificationHistory, AccountResponse accountResponse, BillRequestResponse billRequestResponse, NetworkResponse networkResponse) {
        return NotificationHistoryResponse.builder()
                .id(notificationHistory.getId())
                .title(notificationHistory.getTitle())
                .body(notificationHistory.getBody())
                .image(notificationHistory.getImage())
                .data(notificationHistory.getData())
                .type(notificationHistory.getType())
                .accountId(notificationHistory.getAccountId())
                .account(accountResponse)
                .billRequest(billRequestResponse)
                .network(networkResponse)
                .isRead(notificationHistory.isRead())
                .build();
    }
}

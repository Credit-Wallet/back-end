package vn.edu.iuh.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.model.TypeNotification;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistoryResponse {
    private Long id;
    private String title;
    private String body;
    private String image;
    private Long accountId;
    private String data;
    private boolean isRead;
    private TypeNotification type;
    private AccountResponse account;
    private BillRequestResponse billRequest;
    private NetworkResponse network;
}

package vn.edu.iuh.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.model.TypeNotification;

import java.util.Map;

@Data
@Builder
@Getter
@Setter
public class NotificationMessageRequest {
    private String title;
    private String body;
    private String image;
    private String recipientToken;
    private Long accountId;
    private Map<String, String> data;
    private TypeNotification type;
}

package vn.edu.iuh.model;

import lombok.Data;

import java.util.Map;

@Data
public class NotificationMessage {
    private String title;
    private String body;
    private String image;
    private Long accountId;
    private Map<String, String> data;
    private TypeNotification type;
}

package vn.edu.iuh.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdateTransactionRequest {
    private String hash;
}

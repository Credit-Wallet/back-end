package vn.edu.iuh.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.ApiResponse;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AccountService {
    AccountClient accountClient;

    public Mono<ApiResponse<AccountResponse>> validateToken(String token) {
        return accountClient.validateToken(token);
    }
}

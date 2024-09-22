package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.ApiResponse;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @PostMapping("/auth/profile")
    ApiResponse<AccountResponse> getProfile(@RequestHeader("Authorization") String token);

}

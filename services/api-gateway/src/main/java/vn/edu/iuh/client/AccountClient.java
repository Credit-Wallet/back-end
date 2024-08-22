package vn.edu.iuh.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.ApiResponse;


public interface AccountClient {
    @PostExchange(url = "/profile",contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<AccountResponse>> validateToken(@RequestHeader("Authorization") String token);
}

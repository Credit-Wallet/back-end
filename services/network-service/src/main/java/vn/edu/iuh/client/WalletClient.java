package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.response.ApiResponse;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @PostMapping("/wallets")
    ApiResponse<?> createWallet(@RequestParam("networkId") Long networkId, @RequestHeader("Authorization") String token);

}

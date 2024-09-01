package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.response.ApiResponse;

import java.util.List;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @PostMapping("/wallets")
    ApiResponse<?> createWallet(@RequestParam("networkId") Long networkId, @RequestHeader("Authorization") String token);

    @GetMapping("/wallets/network_ids")
    ApiResponse<List<Long>> getNetworkIdsByAccount(@RequestHeader("Authorization") String token);

}

package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.iuh.response.ApiResponse;

import java.util.List;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {
    @GetMapping("/wallets/account_ids/{networkId}")
    ApiResponse<List<Long>> getAccountIdsByNetwork(@PathVariable("networkId") Long networkId, @RequestHeader("Authorization") String token);
}

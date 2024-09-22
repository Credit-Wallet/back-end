package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import vn.edu.iuh.response.ApiResponse;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @GetMapping("/wallets/{accountId}/{networkId}")
    ApiResponse<?> getWallet(@PathVariable("accountId") Long accountId,
                             @PathVariable("networkId") Long networkId);

    @PutMapping("/wallets/{accountId}/{networkId}/update-balance/{amount}")
    ApiResponse<?> updateBalance(@PathVariable("accountId") Long accountId,
                                 @PathVariable("networkId") Long networkId,
                                    @PathVariable("amount") double amount);
}

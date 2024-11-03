package vn.edu.iuh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.WalletResponse;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @GetMapping("/wallets/{accountId}/{networkId}")
    ApiResponse<WalletResponse> getWallet(@PathVariable("accountId") Long accountId,
                                          @PathVariable("networkId") Long networkId);

    @PutMapping("/wallets/{accountId}/{networkId}/send-balance/{amount}")
    ApiResponse<?> sendBalance(@PathVariable("accountId") Long accountId,
                                 @PathVariable("networkId") Long networkId,
                                    @PathVariable("amount") double amount);

    @PutMapping("/wallets/{accountId}/{networkId}/receive-balance/{amount}")
    ApiResponse<?> receiveBalance(@PathVariable("accountId") Long accountId,
                                 @PathVariable("networkId") Long networkId,
                                 @PathVariable("amount") double amount);

    @PostMapping("/wallets/transfer")
    ApiResponse<?> transfer(@RequestParam("fromId") Long fromId, @RequestParam("toId") Long toId,
                            @RequestParam("networkId") Long networkId, @RequestParam("amount") double amount);
}

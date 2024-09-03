package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.client.WalletClient;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final WalletClient walletClient;
    private final AccountService accountService;

    @GetMapping("/{networkId}")
    public ApiResponse<?> getAccounts(@PathVariable("networkId") Long networkId, @RequestHeader("Authorization") String token) {
        var accountIds = walletClient.getAccountIdsByNetwork(networkId, token);
        return ApiResponse.builder()
                .result(accountService.getAccounts(accountIds.getResult()))
                .build();
    }

    @PutMapping("/update-selected-network/{networkId}")
    public ApiResponse<?> updateSelectedNetwork(@PathVariable("networkId") Long networkId) {
        return ApiResponse.builder()
                .result(accountService.updateSelectedNetwork(networkId))
                .build();
    }


}

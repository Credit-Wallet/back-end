package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping()
    public ApiResponse<Wallet> getWallet(@RequestHeader("Authorization") String token) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.getWallet(token))
                .build();
    }

    @PostMapping()
    public ApiResponse<Wallet> createWallet(@RequestParam("networkId") Long networkId, @RequestHeader("Authorization") String token) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.createWallet(networkId, token))
                .build();
    }

    @GetMapping("/{accountId}/{networkId}")
    public ApiResponse<Wallet> getWallet(@PathVariable("accountId") Long accountId,
                                         @PathVariable("networkId") Long networkId) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.getWallet(accountId, networkId))
                .build();
    }

    @PutMapping("/{accountId}/{networkId}/update-balance/{amount}")
    public ApiResponse<Wallet> updateBalance(@PathVariable("accountId") Long accountId,
                                             @PathVariable("networkId") Long networkId,
                                             @PathVariable("amount") double amount) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.updateBalance(accountId, networkId, amount))
                .build();
    }

    @GetMapping("/network_ids")
    public ApiResponse<List<Long>> getNetworkIdsByAccount(@RequestHeader("Authorization") String token) {
        return ApiResponse.<List<Long>>builder()
                .result(walletService.getNetworkIdsByAccount(token))
                .build();
    }

    @GetMapping("/account_ids/{networkId}")
    public ApiResponse<List<Long>> getAccountIdsByNetwork(@PathVariable("networkId") Long networkId,@RequestHeader("Authorization") String token) {
        return ApiResponse.<List<Long>>builder()
                .result(walletService.getAccountIdsByNetwork(networkId,token))
                .build();
    }
}

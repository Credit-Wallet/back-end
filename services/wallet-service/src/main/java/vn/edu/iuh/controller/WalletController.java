package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.WalletResponse;
import vn.edu.iuh.service.WalletService;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/transfer")
    public ApiResponse<?> transfer(@RequestParam("fromId") Long fromId, @RequestParam("toId") Long toId,
                               @RequestParam("networkId") Long networkId, @RequestParam("amount") double amount) throws Exception {
        return ApiResponse.builder()
                .result(walletService.transfer(fromId, toId,networkId, amount))
                .build();
    }

    @GetMapping()
    public ApiResponse<WalletResponse> getWallet(@RequestHeader("Authorization") String token) throws IOException {
        return ApiResponse.<WalletResponse>builder()
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

    @PutMapping("/{accountId}/{networkId}/send-balance/{amount}")
    public ApiResponse<Wallet> sendBalance(@PathVariable("accountId") Long accountId,
                                             @PathVariable("networkId") Long networkId,
                                             @PathVariable("amount") double amount) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.sendBalance(accountId, networkId, amount))
                .build();
    }

    @PutMapping("/{accountId}/{networkId}/receive-balance/{amount}")
    public ApiResponse<Wallet> receiveBalance(@PathVariable("accountId") Long accountId,
                                             @PathVariable("networkId") Long networkId,
                                             @PathVariable("amount") double amount) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.receiveBalance(accountId, networkId, amount))
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

    @PostMapping("/transfer-money")
    public ApiResponse<?> transferMoney(@RequestHeader("Authorization") String token, @RequestParam("toId") Long toId,
                               @RequestParam("amount") double amount) throws Exception {
        return ApiResponse.builder()
                .result(walletService.transferMoney(token, toId, amount))
                .build();
    }

    @DeleteMapping("/leave")
    public boolean leaveNetwork(@RequestParam("accountId") Long accountId, @RequestParam("networkId") Long networkId){
        return walletService.leaveNetwork(accountId, networkId);
    }
}

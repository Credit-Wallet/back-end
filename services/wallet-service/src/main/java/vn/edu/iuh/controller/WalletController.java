package vn.edu.iuh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.WalletService;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;
    @PostMapping()
    public ApiResponse<Wallet> createWallet(@RequestParam("networkId") Long networkId, @RequestHeader("Authorization") String token) {
        return ApiResponse.<Wallet>builder()
                .result(walletService.createWallet(networkId, token))
                .build();
    }
}

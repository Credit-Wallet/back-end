package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.repository.WalletRepository;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final AccountClient accountClient;

    public Wallet createWallet(Long networkId,String token) {
        var account = accountClient.getProfile(token).getResult();
        var wallet = Wallet.builder()
                .accountId(account.getId())
                .networkId(networkId)
                .balance(0)
                .build();
        return walletRepository.save(wallet);
    }

    public Wallet getWallet(Long accountId, Long networkId) {
        return walletRepository.findByAccountIdAndNetworkId(accountId, networkId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public Wallet updateBalance(Long accountId, Long networkId, double amount) {
        var wallet = getWallet(accountId, networkId);
        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }
}

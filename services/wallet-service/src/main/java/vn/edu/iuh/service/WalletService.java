package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.repository.WalletRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final AccountClient accountClient;

    public Wallet getWallet(String token) {
        var account = accountClient.getProfile(token).getResult();
        return walletRepository.findByAccountIdAndNetworkId(account.getId(), account.getSelectedNetworkId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public Wallet createWallet(Long networkId,String token) {
        var account = accountClient.getProfile(token).getResult();
        var wallet = Wallet.builder()
                .accountId(account.getId())
                .networkId(networkId)
                .balance(0)
                .build();
        if (walletRepository.existsByAccountIdAndNetworkId(wallet.getAccountId(), wallet.getNetworkId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
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

    public List<Long> getNetworkIdsByAccount(String token) {
        var account = accountClient.getProfile(token).getResult();
        return walletRepository.findByAccountId(account.getId())
                .stream()
                .map(Wallet::getNetworkId)
                .collect(Collectors.toList());
    }

    public List<Long> getAccountIdsByNetwork(Long networkId,String token) {
        var account = accountClient.getProfile(token).getResult();
        return walletRepository.findByNetworkId(networkId)
                .stream().map(Wallet::getAccountId)
                .filter(accountId -> !accountId.equals(account.getId()))
                .collect(Collectors.toList());
    }
}

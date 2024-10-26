package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.mapper.AccountMapper;
import vn.edu.iuh.model.Account;
import vn.edu.iuh.model.FcmToken;
import vn.edu.iuh.repository.AccountRepository;
import vn.edu.iuh.response.AccountResponse;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuthService authService;

    public List<AccountResponse> getAccounts(List<Long> accountIds) {
        return accountRepository.findAllById(accountIds)
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }
    public AccountResponse updateSelectedNetwork(Long networkId) {
        var account = authService.getAccountLogin();
        account.setSelectedNetworkId(networkId);
        return accountMapper.toAccountResponse(accountRepository.save(account));
    }
    
    //get fcm_tokens by accountId
    public List<FcmToken> getFcmTokens(Long accountIds) {
        return accountRepository.findById(accountIds)
                .map(Account::getFcmTokens)
                .orElse(List.of());
    }
}

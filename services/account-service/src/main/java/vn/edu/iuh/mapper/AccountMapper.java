package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.Account;
import vn.edu.iuh.request.RegisterRequest;
import vn.edu.iuh.response.AccountResponse;

@Service
public class AccountMapper {

    public Account toAccount(RegisterRequest request) {
        return Account.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
    }
    public AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .selectedNetworkId(account.getSelectedNetworkId())
                .urlAvatar(account.getUrlAvatar())
                .isTwoFactor(account.isTwoFactor())
                .build();
    }
}

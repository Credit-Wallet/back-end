package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.repository.TransactionRepository;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    public List<Transaction> getTransactions(String token) {
        var account = accountClient.getProfile(token).getResult();
        return transactionRepository.findByAccountIdAndNetworkId(account.getId(), account.getSelectedNetworkId());
    }
}

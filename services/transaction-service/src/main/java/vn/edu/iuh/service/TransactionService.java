package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<Transaction> getTransactions(String token, Timestamp fromDate, Timestamp toDate, int page, int limit) {
        var account = accountClient.getProfile(token).getResult();

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        return transactionRepository.findByAccountIdAndNetworkIdAndTimestampBetween(
                account.getId(),
                account.getSelectedNetworkId(),
                fromDate,
                toDate,
                pageable
        );
    }
}

package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.TransactionMapper;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.repository.TransactionRepository;
import vn.edu.iuh.request.CreateTransactionRequest;
import vn.edu.iuh.request.UpdateTransactionRequest;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.TransactionResponse;
import vn.edu.iuh.response.TransactionTransferResponse;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final TransactionMapper transactionMapper;

    public Page<TransactionResponse> getTransactions(String token, Timestamp fromDate, Timestamp toDate, int page, int limit) {
        var account = accountClient.getProfile(token).getResult();

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        var transactions = transactionRepository.findByAccountIdAndNetworkIdAndTimestampBetween(
                account.getId(),
                account.getSelectedNetworkId(),
                fromDate,
                toDate,
                pageable
        );
        return transactions.map(transaction -> {
            AccountResponse fromAccount = accountClient.getAccountById(transaction
                    .getFromAccountId()).getResult();
            AccountResponse toAccount = accountClient.getAccountById(transaction
                    .getToAccountId()).getResult();
            return transactionMapper.toResponse(transaction, fromAccount, toAccount);
        });
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND)
        );
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = findById(id);
        AccountResponse fromAccount = accountClient.getAccountById(transaction.getFromAccountId()).getResult();
        AccountResponse toAccount = accountClient.getAccountById(transaction.getToAccountId()).getResult();
        return transactionMapper.toResponse(transaction, fromAccount, toAccount);
    }

    public TransactionTransferResponse createTransaction(CreateTransactionRequest transaction) {
        Transaction savedTran = transactionRepository.save(Transaction.builder()
                .accountId(transaction.getAccountId())
                .networkId(transaction.getNetworkId())
                .fromAccountId(transaction.getFromAccountId())
                .toAccountId(transaction.getToAccountId())
                .amount(transaction.getAmount())
                .type(transaction.isType())
                .build());
        return TransactionTransferResponse.builder()
                .id(savedTran.getId())
                .accountId(savedTran.getAccountId())
                .networkId(savedTran.getNetworkId())
                .fromAccountId(savedTran.getFromAccountId())
                .toAccountId(savedTran.getToAccountId())
                .amount(savedTran.getAmount())
                .createdAt(savedTran.getCreatedAt())
                .type(savedTran.isType())
                .hash(savedTran.getHash())
                .build();
    }

    public TransactionTransferResponse updateTransaction(Long id, UpdateTransactionRequest transaction) {
        Transaction transactionToUpdate = findById(id);
        transactionToUpdate.setHash(transaction.getHash());
        Transaction savedTran = transactionRepository.save(transactionToUpdate);
        return TransactionTransferResponse.builder()
                .id(savedTran.getId())
                .accountId(savedTran.getAccountId())
                .networkId(savedTran.getNetworkId())
                .fromAccountId(savedTran.getFromAccountId())
                .toAccountId(savedTran.getToAccountId())
                .amount(savedTran.getAmount())
                .createdAt(savedTran.getCreatedAt())
                .type(savedTran.isType())
                .hash(savedTran.getHash())
                .build();
    }
}

package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.client.WalletClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.TransactionMapper;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.model.TransactionDetail;
import vn.edu.iuh.repository.TransactionDetailRepository;
import vn.edu.iuh.repository.TransactionRepository;
import vn.edu.iuh.request.CreateTransactionDetailRequest;
import vn.edu.iuh.request.CreateTransactionRequest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final TransactionMapper transactionMapper;
    private final AccountClient accountClient;
    private final WalletClient walletClient;

    public Transaction createTransaction(CreateTransactionRequest request,String token){
        var account = accountClient.getProfile(token).getResult();

        Transaction transaction = transactionMapper.toTransaction(request);
        Set<TransactionDetail> transactionDetails = new HashSet<>();
        for (CreateTransactionDetailRequest transactionDetail : request.getTransactionDetails()) {
            transactionDetails.add(
                    TransactionDetail.builder()
                            .accountId(transactionDetail.getAccountId())
                            .amount(transactionDetail.getAmount())
                            .confirmed(transaction.getAccountId().equals(transactionDetail.getAccountId()))
                            .transaction(transaction)
                            .build()
            );
        }
        transaction.setTransactionDetails(transactionDetails);
        transaction.setAccountId(account.getId());
        transaction.setStatus(Status.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public Transaction confirmTransaction(Long id){
        Transaction transaction = findById(id);
        processTransaction(transaction);
        transaction.setStatus(Status.COMPLETED);
        return transactionRepository.save(transaction);
    }

    public Transaction confirmTransactionDetail(Long id, String token){
        Transaction transaction = findById(id);
        var account = accountClient.getProfile(token).getResult();
        TransactionDetail transactionDetail = transactionDetailRepository
                .findByTransactionAndAccountId(transaction, account.getId()).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_FOUND)
                );
        transactionDetail.setConfirmed(true);
        transactionDetailRepository.save(transactionDetail);
        return transactionRepository.save(transaction);
    }

    public Transaction findById(Long id){
        return transactionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private void processTransaction(Transaction transaction){
        walletClient.updateBalance(transaction.getAccountId(), transaction.getNetworkId(), transaction.getAmount());
        for (TransactionDetail transactionDetail : transaction.getTransactionDetails()) {
            if (transactionDetail.isConfirmed()) {
                walletClient.updateBalance(transactionDetail.getAccountId(), transaction.getNetworkId(), -transactionDetail.getAmount());
            }
        }
    }
}

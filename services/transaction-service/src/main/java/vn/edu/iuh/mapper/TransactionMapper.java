package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.TransactionResponse;

@Service
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction, AccountResponse fromAccount, AccountResponse toAccount) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .networkId(transaction.getNetworkId())
                .fromAccountId(transaction.getFromAccountId())
                .toAccountId(transaction.getToAccountId())
                .amount(transaction.getAmount())
                .bill(transaction.getBill())
                .createdAt(transaction.getCreatedAt())
                .type(transaction.isType())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .build();
    }
}

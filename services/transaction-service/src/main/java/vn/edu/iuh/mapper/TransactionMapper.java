package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.request.CreateTransactionRequest;

@Service
public class TransactionMapper {
    public Transaction toTransaction(CreateTransactionRequest request){
        return Transaction.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .allMember(request.isAllMember())
                .divideEqually(request.isDivideEqually())
                .networkId(request.getNetworkId())
                .build();
    }
}

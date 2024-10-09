package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.client.WalletClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.BillMapper;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.repository.BillRepository;
import vn.edu.iuh.repository.BillRequestRepository;
import vn.edu.iuh.repository.TransactionRepository;
import vn.edu.iuh.request.CancelBillRequest;
import vn.edu.iuh.request.CreateBillRequest;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BillService {
    private final TransactionRepository transactionRepository;
    private final BillMapper billMapper;
    private final AccountClient accountClient;
    private final BillRepository billRepository;
    private final BillRequestRepository billRequestRepository;
    private final WalletClient walletClient;

    public Bill createBill(CreateBillRequest request, String token){
        var account = accountClient.getProfile(token).getResult();
        Bill bill = billMapper.toBill(request);
        bill.setAccountId(account.getId());
        bill.setStatus(Status.PENDING);
        Set<BillRequest> billRequests = new HashSet<>();
        for (vn.edu.iuh.request.BillRequest billRequest : request.getBillRequests()) {
            billRequests.add(
                    BillRequest.builder()
                            .accountId(billRequest.getAccountId())
                            .amount(billRequest.getAmount())
                            .status(bill.getAccountId().equals(billRequest.getAccountId()) ? Status.COMPLETED : Status.PENDING)
                            .bill(bill)
                            .build()
            );
        }
        bill.setBillRequests(billRequests);
        return billRepository.save(bill);
    }

    public Bill confirmBill(Long id){
        Bill bill = findById(id);
        double actualAmount = processBill(bill);
        bill.setActualAmount(actualAmount);
        bill.setStatus(Status.COMPLETED);
        return billRepository.save(bill);
    }

    public Bill confirmBillRequest(Long id, String token){
        Bill bill = findById(id);
        var account = accountClient.getProfile(token).getResult();
        BillRequest billRequest = billRequestRepository
                .findByBillAndAccountId(bill, account.getId()).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_FOUND)
                );
        billRequest.setStatus(Status.COMPLETED);
        billRequestRepository.save(billRequest);
        Transaction transactionOut = Transaction.builder()
                .accountId(account.getId())
                .fromAccountId(account.getId())
                .networkId(bill.getNetworkId())
                .toAccountId(bill.getAccountId())
                .type(false)
                .amount(billRequest.getAmount())
                .bill(bill)
                .build();
        Transaction transactionIn = Transaction.builder()
                .accountId(bill.getAccountId())
                .fromAccountId(account.getId())
                .networkId(bill.getNetworkId())
                .toAccountId(bill.getAccountId())
                .type(true)
                .amount(billRequest.getAmount())
                .bill(bill)
                .build();
        transactionRepository.save(transactionOut);
        transactionRepository.save(transactionIn);
        bill.getTransactions().add(transactionOut);
        bill.getTransactions().add(transactionIn);
        return billRepository.save(bill);
    }

    public Bill cancelBillRequest(Long id, String token, CancelBillRequest request){
        Bill bill = findById(id);
        var account = accountClient.getProfile(token).getResult();
        BillRequest billRequest = billRequestRepository
                .findByBillAndAccountId(bill, account.getId()).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_FOUND)
                );
        if(!billRequest.getStatus().equals(Status.PENDING)){
            throw new AppException(ErrorCode.BILL_REQUEST_NOT_PENDING);
        }
        billRequest.setDescription(request.getDescription());
        billRequest.setStatus(Status.CANCELLED);
        billRequestRepository.save(billRequest);
        return bill;
    }

    public Bill findById(Long id){
        return billRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private double processBill(Bill bill){
        double actualAmount = 0;
        for (BillRequest billRequest : bill.getBillRequests()) {
            if(billRequest.getStatus().equals(Status.PENDING)){
                throw new AppException(ErrorCode.BILL_REQUEST_NOT_PROCESSED);
            }
            if (billRequest.getStatus().equals(Status.COMPLETED) && !billRequest.getAccountId().equals(bill.getAccountId())) {
                actualAmount += billRequest.getAmount();
                walletClient.updateBalance(bill.getAccountId(), bill.getNetworkId(), billRequest.getAmount());
                walletClient.updateBalance(billRequest.getAccountId(), bill.getNetworkId(), -billRequest.getAmount());
            }
        }
        return actualAmount;
    }
}

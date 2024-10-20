package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.client.WalletClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.BillMapper;
import vn.edu.iuh.mapper.BillRequestMapper;
import vn.edu.iuh.mapper.TransactionMapper;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.repository.BillRepository;
import vn.edu.iuh.repository.BillRequestRepository;
import vn.edu.iuh.repository.TransactionRepository;
import vn.edu.iuh.request.CancelBillRequest;
import vn.edu.iuh.request.CreateBillRequest;
import vn.edu.iuh.response.BillResponse;
import vn.edu.iuh.response.TransactionResponse;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BillService {
    private final TransactionRepository transactionRepository;
    private final BillMapper billMapper;
    private final AccountClient accountClient;
    private final BillRepository billRepository;
    private final BillRequestRepository billRequestRepository;
    private final WalletClient walletClient;
    private final BillRequestMapper billRequestMapper;
    private final TransactionMapper transactionMapper;

    public Page<BillResponse> getBills(String token, Timestamp fromDate, Timestamp toDate,Status status, int page, int limit) {
        var account = accountClient.getProfile(token).getResult();

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        var bills = billRepository.findByAccountIdAndNetworkIdAndTimestampBetween(
                account.getId(),
                account.getSelectedNetworkId(),
                status,
                fromDate,
                toDate,
                pageable
        );
        return bills.map(bill -> {
            var accountResponse = accountClient.getAccountById(bill.getAccountId()).getResult();
            var billRequestResponses = bill.getBillRequests().stream().map(billRequest -> {
                var accountRequest = accountClient.getAccountById(billRequest.getAccountId()).getResult();
                return billRequestMapper.toBillRequestResponse(billRequest, accountRequest);
            });
            var transactionResponses = bill.getTransactions().stream().map(transaction -> {
                var fromAccount = accountClient.getAccountById(transaction.getFromAccountId()).getResult();
                var toAccount = accountClient.getAccountById(transaction.getToAccountId()).getResult();
                return transactionMapper.toResponse(transaction, fromAccount, toAccount);
            });
            return billMapper.toBillResponse(bill, accountResponse, transactionResponses.collect(Collectors.toSet()), billRequestResponses.collect(Collectors.toSet()));
        });
    }

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

    public Bill cancelBill(Long id,String token){
        var account = accountClient.getProfile(token).getResult();
        Bill bill = findById(id);
        if(!bill.getAccountId().equals(account.getId())){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        if(!bill.getStatus().equals(Status.PENDING)){
            throw new AppException(ErrorCode.BILL_NOT_PENDING);
        }
        bill.setStatus(Status.CANCELLED);
        bill.getBillRequests().forEach(billRequest -> {
            billRequest.setStatus(Status.CANCELLED);
            billRequestRepository.save(billRequest);
        });
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

    public BillResponse getBillById(Long id){
        Bill bill = findById(id);
        var account = accountClient.getAccountById(bill.getAccountId()).getResult();
        var billRequestResponses = bill.getBillRequests().stream().map(billRequest -> {
            var accountRequest = accountClient.getAccountById(billRequest.getAccountId()).getResult();
            return billRequestMapper.toBillRequestResponse(billRequest, accountRequest);
        });
        var transactionResponses = bill.getTransactions().stream().map(transaction -> {
            var fromAccount = accountClient.getAccountById(transaction.getFromAccountId()).getResult();
            var toAccount = accountClient.getAccountById(transaction.getToAccountId()).getResult();
            return transactionMapper.toResponse(transaction, fromAccount, toAccount);
        });
        return billMapper.toBillResponse(bill, account,transactionResponses.collect(Collectors.toSet()), billRequestResponses.collect(Collectors.toSet()));
    }

    private double processBill(Bill bill){
        double actualAmount = 0;
        for (BillRequest billRequest : bill.getBillRequests()) {
            if(billRequest.getStatus().equals(Status.PENDING)){
                billRequest.setStatus(Status.CANCELLED);
                billRequestRepository.save(billRequest);
            }
            if (billRequest.getStatus().equals(Status.COMPLETED) && !billRequest.getAccountId().equals(bill.getAccountId())) {
                Transaction transactionOut = Transaction.builder()
                        .accountId(billRequest.getAccountId())
                        .fromAccountId(billRequest.getAccountId())
                        .networkId(bill.getNetworkId())
                        .toAccountId(bill.getAccountId())
                        .type(false)
                        .amount(billRequest.getAmount())
                        .bill(bill)
                        .build();
                Transaction transactionIn = Transaction.builder()
                        .accountId(bill.getAccountId())
                        .fromAccountId(billRequest.getAccountId())
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
                actualAmount += billRequest.getAmount();
                walletClient.updateBalance(bill.getAccountId(), bill.getNetworkId(), billRequest.getAmount());
                walletClient.updateBalance(billRequest.getAccountId(), bill.getNetworkId(), -billRequest.getAmount());
            }
        }
        return actualAmount;
    }
}

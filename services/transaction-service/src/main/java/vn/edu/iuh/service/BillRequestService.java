package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.client.NetworkClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.BillRequestMapper;
import vn.edu.iuh.model.BillRequest;
import vn.edu.iuh.model.Status;
import vn.edu.iuh.repository.BillRequestRepository;
import vn.edu.iuh.response.BillRequestResponse;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Service
public class BillRequestService {
    private final BillRequestRepository billRequestRepository;
    private final AccountClient accountClient;
    private final BillRequestMapper billRequestMapper;
    private final NetworkClient networkClient;

    public Page<BillRequestResponse> getBillRequests(String token, Timestamp fromDate, Timestamp toDate, Status status, int page, int limit) {
        var account = accountClient.getProfile(token).getResult();

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        var billRequests = billRequestRepository.findByAccountIdAndNetworkIdAndTimestampBetween(
                account.getId(),
                account.getSelectedNetworkId(),
                status,
                fromDate,
                toDate,
                pageable
        );
        return billRequests.map(billRequest -> {
            var accountResponse = accountClient.getAccountById(billRequest.getAccountId()).getResult();
            return billRequestMapper.toBillRequestResponse(billRequest, accountResponse, null);
        });
    }

    public BillRequest findById(Long id) {
        return billRequestRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public BillRequestResponse getBillRequestById(Long id) {
        var billRequest = findById(id);
        var accountResponse = accountClient.getAccountById(billRequest.getAccountId()).getResult();
        var networkResponse = networkClient.getNetworkById(billRequest.getBill().getNetworkId()).getResult();
        return billRequestMapper.toBillRequestResponse(billRequest, accountResponse, networkResponse);
    }

}

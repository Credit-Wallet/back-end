package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.BillRequest;

import java.util.Optional;

public interface BillRequestRepository extends JpaRepository<BillRequest,Long> {
    Optional<BillRequest> findByBillAndAccountId(Bill bill, Long accountId);
}

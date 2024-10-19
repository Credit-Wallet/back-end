package vn.edu.iuh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.model.Status;

import java.sql.Timestamp;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT b FROM Bill b WHERE b.accountId = :accountId AND b.networkId = :networkId" +
            " AND (:fromDate IS NULL OR b.createdAt >= :fromDate)" +
            " AND (:toDate IS NULL OR b.createdAt <= :toDate)" +
            " AND (:status IS NULL OR b.status = :status)")
    Page<Bill> findByAccountIdAndNetworkIdAndTimestampBetween(@Param("accountId") Long accountId,
                                                              @Param("networkId") Long networkId,
                                                              @Param("status") Status status,
                                                              @Param("fromDate") Timestamp fromDate,
                                                              @Param("toDate") Timestamp toDate,
                                                              Pageable pageable);
}

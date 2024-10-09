package vn.edu.iuh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.model.Transaction;

import java.sql.Timestamp;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.networkId = :networkId" +
            " AND (:fromDate IS NULL OR t.createdAt >= :fromDate)" +
            " AND (:toDate IS NULL OR t.createdAt <= :toDate)")
    Page<Transaction> findByAccountIdAndNetworkIdAndTimestampBetween(
            @Param("accountId") Long accountId,
            @Param("networkId") Long networkId,
            @Param("fromDate") Timestamp fromDate,
            @Param("toDate") Timestamp toDate,
            Pageable pageable);
}

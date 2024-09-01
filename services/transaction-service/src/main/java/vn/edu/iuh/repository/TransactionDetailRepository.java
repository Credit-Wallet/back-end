package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.Transaction;
import vn.edu.iuh.model.TransactionDetail;

import java.util.Optional;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetail,Long> {
    Optional<TransactionDetail> findByTransactionAndAccountId(Transaction transaction, Long accountId);
}

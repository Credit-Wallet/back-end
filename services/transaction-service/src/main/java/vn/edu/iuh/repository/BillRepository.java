package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}

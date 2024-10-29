package vn.edu.iuh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.iuh.model.NotificationHistory;
import vn.edu.iuh.model.TypeNotification;

import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    Page<NotificationHistory> findByAccountIdAndType(Long accountId, TypeNotification type, Pageable pageable);
    
    @Query("SELECT n FROM NotificationHistory n WHERE n.accountId = ?1 AND n.isRead = ?2")
    List<NotificationHistory> findByAccountIdAndRead(Long accountId, boolean isRead);
}

package vn.edu.iuh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.NotificationHistory;
import vn.edu.iuh.model.TypeNotification;

import java.util.List;
import java.util.Optional;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    
    Page<NotificationHistory> findByAccountIdAndType(Long accountId, TypeNotification type, Pageable pageable);
    
    //findByAccountIdAndIsRead
//    List<NotificationHistory> findByAccountIdAndReadIs(Long accountId, boolean isRead);
}

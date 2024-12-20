package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.FcmToken;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByFcmToken(String fcmToken);
}
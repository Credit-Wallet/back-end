package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    boolean existsByAccountIdAndNetworkId(Long accountId, Long networkId);
    Optional<Wallet> findByAccountIdAndNetworkId(Long accountId, Long networkId);
}

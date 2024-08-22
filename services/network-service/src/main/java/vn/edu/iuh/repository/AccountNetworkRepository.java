package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.AccountNetwork;

public interface AccountNetworkRepository extends JpaRepository<AccountNetwork, Long> {
    boolean existsByAccountIdAndNetworkId(Long accountId, Long networkId);
}

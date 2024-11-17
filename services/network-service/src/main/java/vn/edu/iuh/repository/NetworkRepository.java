package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.Network;

import java.util.Optional;

public interface NetworkRepository extends JpaRepository<Network, Long> {
    Optional<Network> findByUuid(String uuid);
}

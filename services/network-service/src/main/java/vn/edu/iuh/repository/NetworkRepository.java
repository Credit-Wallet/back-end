package vn.edu.iuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.model.Network;

public interface NetworkRepository extends JpaRepository<Network, Long> {
}

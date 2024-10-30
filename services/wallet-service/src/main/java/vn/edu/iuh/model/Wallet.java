package vn.edu.iuh.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private double balance;
    private Long networkId;
    private String walletPath;
    private String walletAddress;
    private String privateKey;
    private String password;
    @CreationTimestamp
    @Column
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}

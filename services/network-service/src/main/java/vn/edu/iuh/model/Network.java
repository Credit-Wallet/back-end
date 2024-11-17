package vn.edu.iuh.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity(name = "networks")
public class Network {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String name;
    private String walletPath;
    private String walletAddress;
    private String privateKey;
    private String password;
    private double balance;
    private double minBalance;
    private double maxBalance;
    private Long maxMember;
    private String description;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @CreationTimestamp
    @Column
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}

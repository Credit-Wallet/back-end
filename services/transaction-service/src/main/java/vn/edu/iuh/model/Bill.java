package vn.edu.iuh.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long accountId;
    private double amount;
    private double actualAmount;
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private Set<BillRequest> billRequests = new HashSet<>();
    private Long networkId;
    @Enumerated(EnumType.STRING)
    private Status status;
    @CreationTimestamp
    @Column
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}

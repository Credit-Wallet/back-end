package vn.edu.iuh.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private Long networkId;
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
    @ManyToOne
    @JsonIgnore
    private Bill bill;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    private boolean type;
}

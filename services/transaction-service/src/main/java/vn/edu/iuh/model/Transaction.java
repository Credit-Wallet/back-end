package vn.edu.iuh.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private String name;
    private Long accountId;
    private double amount;
    private boolean allMember;
    private boolean divideEqually;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Set<TransactionDetail> transactionDetails = new HashSet<>();
    private Long networkId;
    private Status status;
    @CreatedDate
    private LocalDateTime createdAt;
}

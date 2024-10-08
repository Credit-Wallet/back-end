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
@Entity(name = "bill_requests")
public class BillRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private double amount;
    @ManyToOne
    @JsonIgnore
    private Bill bill;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String description;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}

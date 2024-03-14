package dev.roy.coinkeeper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id")
    private Integer id;
    private Enum<TransactionType> type;
    private Float amount;
    private LocalDateTime date;
    private String category;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
}

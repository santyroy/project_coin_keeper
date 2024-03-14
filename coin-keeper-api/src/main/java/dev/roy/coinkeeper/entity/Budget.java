package dev.roy.coinkeeper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "budgets")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "budget_id")
    private Integer id;
    private String name;
    private String type;
    private LocalDateTime openDate;
    private Float goal;

    @OneToMany(mappedBy = "budget")
    private Set<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

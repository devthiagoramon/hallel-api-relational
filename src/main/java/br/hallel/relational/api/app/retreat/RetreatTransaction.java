package br.hallel.relational.api.app.retreat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "retreat_transaction")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class RetreatTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "description", nullable = false)
    private String desciption;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Double value;

    @ManyToOne
    @JoinColumn(name = "retreat_id")
    private Retreat retreat;
}

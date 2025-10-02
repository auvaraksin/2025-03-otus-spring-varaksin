package ru.otus.projectwork.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.projectwork.domain.model.enums.Period;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Условия счёта.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "condition_id")
    private UUID conditionId;

    @Column(length = 50, nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Period period;

    @Column(precision = 3, scale = 2, nullable = false)
    private BigDecimal percent;

    @Column(name = "is_payoff", nullable = false)
    private Boolean isPayoff;

}

package ru.otus.projectwork.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.otus.projectwork.domain.model.enums.AccountStatus;
import ru.otus.projectwork.domain.model.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Счёт пользователя.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
    private UUID accountId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_details_id", unique = true, nullable = false)
    private AccountDetails accountDetails;

    @Column(nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(precision = 16, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime openDate;

    @Column
    private LocalDateTime closeDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isMaster;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conditions_id", referencedColumnName = "condition_id", unique = true, nullable = false)
    private Condition condition;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "currency_id", nullable = false)
    private Currency currency;

}

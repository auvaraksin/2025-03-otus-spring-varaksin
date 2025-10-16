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
import ru.otus.projectwork.domain.model.enums.CurrencyCode;
import ru.otus.projectwork.domain.model.enums.CurrencyType;

import java.util.UUID;

/**
 * Валюта счёта.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "currency_id")
    private UUID currencyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType type;

}

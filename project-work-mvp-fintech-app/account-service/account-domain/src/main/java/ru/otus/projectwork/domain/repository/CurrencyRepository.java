package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.projectwork.domain.model.Currency;
import ru.otus.projectwork.domain.model.enums.CurrencyType;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Optional<Currency> findByType(CurrencyType currency);

    Optional<Currency> findByCurrencyId(UUID currencyId);
}

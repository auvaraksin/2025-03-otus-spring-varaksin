package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.projectwork.domain.model.Transaction;

import java.util.List;
import java.util.UUID;

import static ru.otus.projectwork.domain.repository.queries.TransactionRepositoryQueries.FIND_ALL_BY_ACCOUNT_ID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(FIND_ALL_BY_ACCOUNT_ID)
    List<Transaction> findAllByAccountId(UUID accountId);
}

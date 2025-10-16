package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.projectwork.domain.model.Account;
import ru.otus.projectwork.domain.model.projection.AccountInformationProjection;
import ru.otus.projectwork.domain.model.projection.ClientAccountsProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.otus.projectwork.domain.repository.queries.AccountRepositoryQueries.FIND_ACCOUNTS_BY_CLIENT_ID;
import static ru.otus.projectwork.domain.repository.queries.AccountRepositoryQueries.VIEW_ACCOUNT_INFORMATION;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query(FIND_ACCOUNTS_BY_CLIENT_ID)
    List<ClientAccountsProjection> findAccountsByClientId(UUID clientId);

    @Query(VIEW_ACCOUNT_INFORMATION)
    Optional<AccountInformationProjection> viewAccountInformation(UUID accountId);
}

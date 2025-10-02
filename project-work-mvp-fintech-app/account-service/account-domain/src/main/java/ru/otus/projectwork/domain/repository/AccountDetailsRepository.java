package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.projectwork.domain.model.AccountDetails;

import java.util.UUID;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails, UUID> {
}

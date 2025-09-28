package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.projectwork.domain.model.Condition;

import java.util.UUID;

public interface ConditionRepository extends JpaRepository<Condition, UUID> {
}

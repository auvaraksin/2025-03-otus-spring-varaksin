package ru.otus.projectwork.domain.repository.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionRepositoryQueries {

    public static final String FIND_ALL_BY_ACCOUNT_ID =
            """
                    SELECT t FROM Transaction t
                    WHERE t.accountId = :accountId
                    """;
}

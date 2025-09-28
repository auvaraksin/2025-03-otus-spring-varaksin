package ru.otus.projectwork.domain.repository.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountRepositoryQueries {

    public static final String FIND_ACCOUNTS_BY_CLIENT_ID =
            """
                    SELECT new ru.otus.projectwork.domain.model.projection.ClientAccountsProjection(
                        a.accountId, ad.accountNumber, a.currentBalance, c.type, a.isMaster, a.accountType)
                    FROM Account a
                    JOIN a.accountDetails AS ad
                    JOIN a.currency AS c
                    WHERE a.clientId=:clientId
                    """;

    public static final String VIEW_ACCOUNT_INFORMATION =
            """
                    SELECT new ru.otus.projectwork.domain.model.projection.AccountInformationProjection(
                        a.accountId, co.accountName, a.accountType, ad.accountNumber, c.type,
                        a.currentBalance, co.period, a.accountStatus, co.percent, co.isPayoff)
                    FROM Account a
                        JOIN  a.accountDetails ad
                        JOIN  a.currency c
                        JOIN  a.condition co
                    WHERE a.accountId=:accountId
                    """;
}

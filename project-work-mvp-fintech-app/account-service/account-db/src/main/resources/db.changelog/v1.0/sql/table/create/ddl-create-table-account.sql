CREATE TABLE account
(
    -- Идентификатор счёта
    account_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- id деталей счёта
    account_details_id UUID                  NOT NULL,
    -- id связанного клиента
    client_id          UUID                  NOT NULL,
    -- Тип счёта
    account_type       ACCOUNT_TYPE          NOT NULL,
    -- Текущий баланс счета
    current_balance    DECIMAL(16, 2)        NOT NULL,
    -- Дата открытия счета
    open_date          TIMESTAMP             NOT NULL,
    -- Дата закрытия счета (может быть NULL)
    close_date         TIMESTAMP,
    -- Статус счета
    account_status     ACCOUNT_STATUS        NOT NULL,
    -- Является ли счет основным (false по умолчанию)
    is_matter          BOOLEAN DEFAULT FALSE NOT NULL,
    -- Дата обновления счета
    update_date        TIMESTAMP             NOT NULL,
    -- id условий счета
    condition      UUID                  NOT NULL,
    -- id валюты счета
    currency   UUID                  NOT NULL,
    -- Внешний ключ на таблицу account_details
    CONSTRAINT account_account_details_fkey FOREIGN KEY (account_details_id)
        REFERENCES account_details (account_details_id),
    -- Внешний ключ на таблицу condition
    CONSTRAINT account_condition_fkey FOREIGN KEY (condition)
        REFERENCES condition (condition_id),
    -- Внешний ключ на таблицу currency
    CONSTRAINT account_currency_fkey FOREIGN KEY (currency)
        REFERENCES currency (currency_id)
);

-- Комментарии к таблице
COMMENT ON TABLE account IS 'Таблица счёта пользователя';

-- Комментарии к столбцам
COMMENT ON COLUMN account.account_id IS 'Уникальный идентификатор счёта';
COMMENT ON COLUMN account.account_details_id IS 'ID деталей счёта';
COMMENT ON COLUMN account.client_id IS 'ID связанного клиента';
COMMENT ON COLUMN account.account_type IS 'Тип счёта';
COMMENT ON COLUMN account.current_balance IS 'Текущий баланс счета';
COMMENT ON COLUMN account.open_date IS 'Дата открытия счета';
COMMENT ON COLUMN account.close_date IS 'Дата закрытия счета (может быть NULL)';
COMMENT ON COLUMN account.account_status IS 'Статус счета (например, активный или закрытый)';
COMMENT ON COLUMN account.is_matter IS 'Признак, является ли счет основным (false по умолчанию)';
COMMENT ON COLUMN account.update_date IS 'Дата последнего обновления счета';
COMMENT ON COLUMN account.condition IS 'ID условий счета';
COMMENT ON COLUMN account.currency IS 'ID валюты счета';
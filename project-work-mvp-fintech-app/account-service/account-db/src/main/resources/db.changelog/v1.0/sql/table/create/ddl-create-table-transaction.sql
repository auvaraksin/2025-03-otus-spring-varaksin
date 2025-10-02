CREATE TABLE transaction
(
    -- Идентификатор транзакции
    transaction_id  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- Идентификатор счета, внешний ключ
    account_id      UUID            NOT NULL,
    -- Тип транзакции
    type            TYPE            NOT NULL,
    -- Сумма транзакции
    sum             DECIMAL(16, 2)  NOT NULL,
    -- Детали транзакции
    details         TEXT,
    -- Дата создания транзакции
    create_date     TIMESTAMP       NOT NULL,
    -- Дата обновления транзакции
    update_date     TIMESTAMP       NOT NULL,
    -- Местоположение совершения операции
    location        CHAR(50)        NOT NULL,
    -- Счет источника перевода
    from_account    CHAR(16)        NOT NULL,
    -- Счет получателя перевода
    to_account      CHAR(16)        NOT NULL,
    -- Статус перевода
    transfer_status TRANSFER_STATUS NOT NULL,
    -- Внешний ключ на таблицу account
    CONSTRAINT transaction_account_fkey FOREIGN KEY (account_id) REFERENCES account (account_id)
);

-- Комментарии к таблице
COMMENT ON TABLE transaction IS 'Таблица с информацией о банковских транзакциях';

-- Комментарии к столбцам
COMMENT ON COLUMN transaction.transaction_id IS 'Уникальный идентификатор транзакции';
COMMENT ON COLUMN transaction.account_id IS 'Идентификатор счета, на который ссылается транзакция';
COMMENT ON COLUMN transaction.type IS 'Тип транзакции';
COMMENT ON COLUMN transaction.sum IS 'Сумма, участвующая в транзакции';
COMMENT ON COLUMN transaction.details IS 'Дополнительная информация о транзакции';
COMMENT ON COLUMN transaction.create_date IS 'Дата создания транзакции';
COMMENT ON COLUMN transaction.update_date IS 'Дата последнего обновления транзакции';
COMMENT ON COLUMN transaction.location IS 'Местоположение, где была совершена транзакция';
COMMENT ON COLUMN transaction.from_account IS 'Счет источника перевода (номер счета отправителя)';
COMMENT ON COLUMN transaction.to_account IS 'Счет получателя перевода (номер счета получателя)';
COMMENT ON COLUMN transaction.transfer_status IS 'Статус перевода';
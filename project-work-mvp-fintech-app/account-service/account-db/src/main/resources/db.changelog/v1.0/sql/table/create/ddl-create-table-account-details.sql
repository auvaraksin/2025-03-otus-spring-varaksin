CREATE TABLE account_details
(
    -- Идентификатор деталей счета
    account_details_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- Номер счета (уникальный)
    account_number     VARCHAR(50) NOT NULL UNIQUE,
    -- Банковский идентификационный код (БИК)
    bik_bank           CHAR(9)     NOT NULL DEFAULT '044525970',
    -- Название банка
    bank_name          VARCHAR(50) NOT NULL DEFAULT 'Otus-Fintech',
    -- ИНН банка
    inn_bank           CHAR(10)    NOT NULL DEFAULT '7702070117',
    -- Корреспондентский счет банка
    cor_account_bank   CHAR(20)    NOT NULL DEFAULT '30101810400000000970',
    -- Код причины постановки на учет (КПП)
    kpp_bank           CHAR(9)     NOT NULL DEFAULT '784253001',
    -- Общероссийский классификатор предприятий и организаций (ОКПО)
    okpo_bank          CHAR(8)     NOT NULL DEFAULT '00032538',
    -- Основной государственный регистрационный номер (ОГРН)
    ogrn_bank          CHAR(13)    NOT NULL DEFAULT '1025600132195',
    -- SWIFT-код банка
    swift_code_bank    CHAR(14)    NOT NULL DEFAULT 'OTUS RU MM ХХХ',
    -- Дата обновления деталей счета
    update_date        TIMESTAMP   NOT NULL,
    -- Дата создания деталей счета
    created_date       TIMESTAMP   NOT NULL,
    -- Причина блокировки счета (может быть NULL)
    reason_blocking    VARCHAR(100)
);

-- Комментарии к таблице
COMMENT ON TABLE account_details IS 'Таблица с деталями счета пользователя';

-- Комментарии к столбцам
COMMENT ON COLUMN account_details.account_details_id IS 'Уникальный идентификатор деталей счета';
COMMENT ON COLUMN account_details.account_number IS 'Номер счета (уникальный)';
COMMENT ON COLUMN account_details.bik_bank IS 'Банковский идентификационный код (БИК)';
COMMENT ON COLUMN account_details.bank_name IS 'Название банка';
COMMENT ON COLUMN account_details.inn_bank IS 'ИНН банка';
COMMENT ON COLUMN account_details.cor_account_bank IS 'Корреспондентский счет банка';
COMMENT ON COLUMN account_details.kpp_bank IS 'Код причины постановки на учет (КПП)';
COMMENT ON COLUMN account_details.okpo_bank IS 'Общероссийский классификатор предприятий и организаций (ОКПО)';
COMMENT ON COLUMN account_details.ogrn_bank IS 'Основной государственный регистрационный номер (ОГРН)';
COMMENT ON COLUMN account_details.swift_code_bank IS 'SWIFT-код банка';
COMMENT ON COLUMN account_details.update_date IS 'Дата последнего обновления деталей счета';
COMMENT ON COLUMN account_details.created_date IS 'Дата создания деталей счета';
COMMENT ON COLUMN account_details.reason_blocking IS 'Причина блокировки счета (может быть NULL)';
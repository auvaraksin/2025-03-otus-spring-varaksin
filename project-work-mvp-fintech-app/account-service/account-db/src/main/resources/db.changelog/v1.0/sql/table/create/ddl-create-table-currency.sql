CREATE TABLE currency
(
    -- Идентификатор валюты
    currency_id   UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- Код валюты
    code          CODE          NOT NULL,
    -- Тип валюты
    currency_type CURRENCY_TYPE NOT NULL,
    -- Дата создания записи
    created_date  TIMESTAMP     NOT NULL
);

-- Комментарии к таблице
COMMENT ON TABLE currency IS 'Таблица с информацией о валютах';

-- Комментарии к столбцам
COMMENT ON COLUMN currency.currency_id IS 'Уникальный идентификатор валюты';
COMMENT ON COLUMN currency.code IS 'Код валюты (например, USD, EUR, RUB)';
COMMENT ON COLUMN currency.currency_type IS 'Тип валюты (например, национальная или иностранная)';
COMMENT ON COLUMN currency.created_date IS 'Дата создания записи о валюте';
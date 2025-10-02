CREATE TABLE condition
(
    -- Идентификатор условий счёта
    condition_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- Название счёта
    account_name VARCHAR(50)   NOT NULL,
    -- Период действия счёта
    period PERIOD NOT NULL,
    -- Процентная ставка (например, 5.25)
    percent      DECIMAL(3, 2) NOT NULL,
    -- Выплата процентов (true/false)
    payoff       BOOLEAN       NOT NULL
);

-- Комментарии к таблице
COMMENT ON TABLE condition IS 'Таблица условий для банковского счёта';

-- Комментарии к столбцам
COMMENT ON COLUMN condition.condition_id IS 'Уникальный идентификатор условий счёта';
COMMENT ON COLUMN condition.account_name IS 'Название счёта';
COMMENT ON COLUMN condition.period IS 'Период действия счёта';
COMMENT ON COLUMN condition.percent IS 'Процентная ставка, начисляемая на счёт';
COMMENT ON COLUMN condition.payoff IS 'Признак, указывающий, производится ли выплата процентов (true/false)';
package ru.otus.projectwork.user.service.chain.authorization;

import ru.otus.projectwork.user.dto.ChainAuthorizationDto;

/**
 * Интерфейс для определения части цепочки обработки авторизации.
 *
 * @see ru.otus.projectwork.user.service.chain.authorization.impl.CheckAuthorizationParamsChainPart 1 шаг
 * @see ru.otus.projectwork.user.service.chain.authorization.impl.GettingClientInfoChainPart 2 шаг
 * @see ru.otus.projectwork.user.service.chain.authorization.impl.CheckClientPasswordChainPart 3 шаг
 * @see ru.otus.projectwork.user.service.chain.authorization.impl.GettingTokensChainPart 4 шаг
 */
public interface ProcessAuthorizationChainPart {

    /**
     * Выполняет обработку части цепочки.
     *
     * @param chain объект {@link ChainAuthorizationDto}, содержащий данные для обработки
     * @return обновленный объект {@link ChainAuthorizationDto} после выполнения текущей части обработки
     */
    ChainAuthorizationDto process(ChainAuthorizationDto chain);
}

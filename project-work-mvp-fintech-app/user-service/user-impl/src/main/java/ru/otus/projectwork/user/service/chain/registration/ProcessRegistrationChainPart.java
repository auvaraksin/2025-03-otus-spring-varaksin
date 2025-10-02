package ru.otus.projectwork.user.service.chain.registration;

import ru.otus.projectwork.user.dto.ChainRegistrationDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;

/**
 * Интерфейс для определения части цепочки обработки регистрации.
 *
 * @see ru.otus.projectwork.user.service.chain.registration.impl.CreatePassportDataChainPart 1 шаг
 * @see ru.otus.projectwork.user.service.chain.registration.impl.CreateAddressRegistrationChainPart 2 шаг
 * @see ru.otus.projectwork.user.service.chain.registration.impl.CreateAddressActualChainPart 3 шаг
 * @see ru.otus.projectwork.user.service.chain.registration.impl.CreateClientChainPart 4 шаг
 * @see ru.otus.projectwork.user.service.chain.registration.impl.CreateUserProfileChainPart 5 шаг
 */
public interface ProcessRegistrationChainPart {

    /**
     * Выполняет обработку части цепочки.
     *
     * @param request объект {@link RegistrationRequestDto}, содержащий данные для обработки
     * @param previous объект {@link ChainRegistrationDto}, содержащий данные для обработки
     * @return обновленный объект {@link ChainRegistrationDto} после выполнения текущей части обработки
     */
    ChainRegistrationDto process(RegistrationRequestDto request, ChainRegistrationDto previous);
}

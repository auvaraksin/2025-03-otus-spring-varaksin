package ru.otus.projectwork.user.service.chain.registration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.domain.model.Address;
import ru.otus.projectwork.domain.repository.AddressRepository;
import ru.otus.projectwork.user.dto.ChainRegistrationDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.service.chain.registration.ProcessRegistrationChainPart;

import static ru.otus.projectwork.user.service.chain.registration.impl.CreateAddressActualChainPart.ORDER;

/**
 * Часть цепочки для создания актуального адреса клиента.
 * <p>
 * Эта часть цепочки отвечает за создание новой записи актуального адреса клиента на основе данных запроса регистрации.
 * Адрес сохраняется в репозитории адресов.
 * <p>
 * Порядок выполнения: 3
 * <p>
 * Процесс работы: Этот компонент цепочки регистрации принимает данные запроса регистрации и создает новую запись
 * актуального адреса клиента. Созданный адрес сохраняется в указанном репозитории адресов.
 * <p>
 * Пример использования:
 * <pre>{@code
 * RegistrationRequestDto request = RegistrationRequestDtoUtil.getValidRegistrationRequestDto();
 * ChainRegistrationDto previous = ChainRegistrationDto.builder().build();
 *
 * ChainRegistrationDto result = createAddressActualChainPart.process(request, previous);
 * }</pre>
 * <p>
 * Примечания: Для успешного создания актуального адреса необходимо, чтобы в запросе регистрации были заполнены
 * обязательные поля, такие как страна, город и улица. Устанавливаются также значения по умолчанию для других параметров
 * адреса, таких как дом, корпус и квартира.
 * <p>
 * Предусловия: Входные данные должны соответствовать структуре и типам, определенным в классе RegistrationRequestDto.
 * <p>
 * Постусловия: После выполнения метода process() в объекте previous будет обновлена информация об актуальном адресе
 * клиента.
 * <p>
 * Исключения: Может выбрасывать исключения в случае ошибок сохранения данных в репозитории или если входные данные не
 * удовлетворяют требованиям.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class CreateAddressActualChainPart implements ProcessRegistrationChainPart {

    protected static final int ORDER = 3;

    private final AddressRepository addressRepository;

    /**
     * Создает новую запись адреса клиента на основе данных запроса регистрации. Сохраняет созданный адрес в репозитории
     * адресов.
     *
     * @param request  данные запроса регистрации
     * @param previous объект, содержащий результаты предыдущих этапов цепочки регистрации
     * @return объект ChainRegistrationDto с обновленными данными об адресе клиента
     */
    @Override
    public ChainRegistrationDto process(RegistrationRequestDto request, ChainRegistrationDto previous) {

        Address addressActual = getAddressActual(request);

        addressActual = addressRepository.save(addressActual);

        previous.setAddressActual(addressActual);
        return previous;
    }

    /**
     * Формирует объект Address на основе данных запроса регистрации.
     *
     * @param request данные запроса регистрации
     * @return объект Address с данными об актуальном адресе клиента
     */
    private static Address getAddressActual(RegistrationRequestDto request) {
        return Address.builder()
                .country(request.country())
                .city(request.city())
                .street(request.street())
                .house(request.house())
                .hull(request.hull())
                .flat(request.flat())
                .postCode(request.postCode())
                .build();
    }

}

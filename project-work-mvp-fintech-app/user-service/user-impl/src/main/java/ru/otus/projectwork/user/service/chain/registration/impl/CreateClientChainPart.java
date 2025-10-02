package ru.otus.projectwork.user.service.chain.registration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.domain.model.Client;
import ru.otus.projectwork.domain.repository.ClientRepository;
import ru.otus.projectwork.user.dto.ChainRegistrationDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.service.chain.registration.ProcessRegistrationChainPart;

import static ru.otus.projectwork.user.service.chain.registration.impl.CreateClientChainPart.ORDER;

/**
 * Часть цепочки для создания клиента.
 * <p>
 * Эта часть цепочки отвечает за создание новой записи клиента на основе данных запроса регистрации. Клиент сохраняется
 * в репозитории клиентов.
 * <p>
 * Порядок выполнения: 4
 * <p>
 * Процесс работы: Этот компонент цепочки регистрации принимает данные запроса регистрации и создает нового клиента.
 * Созданный клиент сохраняется в указанном репозитории клиентов.
 * <p>
 * Пример использования:
 * <pre>{@code
 * RegistrationRequestDto request = RegistrationRequestDtoUtil.getValidRegistrationRequestDto();
 * ChainRegistrationDto previous = ChainRegistrationDto.builder().build();
 *
 * ChainRegistrationDto result = createClientChainPart.process(request, previous);
 * }</pre>
 * <p>
 * Примечания: Для успешного создания клиента необходимо, чтобы в запросе регистрации были заполнены необходимые поля,
 * такие как имя, фамилия и номер телефона. Устанавливаются также значения по умолчанию для других параметров клиента,
 * таких как верификация данных и данные паспорта.
 * <p>
 * Предусловия: Входные данные должны соответствовать структуре и типам, определенным в классе RegistrationRequestDto.
 * <p>
 * Постусловия: После выполнения метода process() в объекте previous будет обновлена информация о клиенте.
 * <p>
 * Исключения: Может выбрасывать исключения в случае ошибок сохранения данных в репозитории или если входные данные не
 * удовлетворяют требованиям.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class CreateClientChainPart implements ProcessRegistrationChainPart {

    protected static final int ORDER = 4;

    private final ClientRepository clientRepository;

    /**
     * Создает новую запись клиента на основе данных запроса регистрации. Сохраняет созданного клиента в репозитории
     * клиентов.
     *
     * @param request  данные запроса регистрации
     * @param previous объект, содержащий результаты предыдущих этапов цепочки регистрации
     * @return объект ChainRegistrationDto с обновленными данными о клиенте
     */
    @Override
    public ChainRegistrationDto process(RegistrationRequestDto request, ChainRegistrationDto previous) {

        Client client = getClient(request, previous);

        client = clientRepository.save(client);

        previous.setClient(client);
        return previous;
    }

    /**
     * Формирует объект Client на основе данных запроса регистрации и предыдущих результатов цепочки.
     *
     * @param request  данные запроса регистрации
     * @param previous объект, содержащий результаты предыдущих этапов цепочки регистрации
     * @return объект Client с данными о клиенте
     */
    private static Client getClient(RegistrationRequestDto request, ChainRegistrationDto previous) {
        return Client.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .mobilePhone(request.mobilePhone())
                .isVerificated(false)
                .addressRegistration(previous.getAddressRegistration())
                .addressActual(previous.getAddressActual())
                .passportData(previous.getPassportData())
                .build();
    }

}

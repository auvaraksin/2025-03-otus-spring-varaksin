package ru.otus.projectwork.user.service.chain.registration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.domain.model.PassportData;
import ru.otus.projectwork.domain.repository.PassportDataRepository;
import ru.otus.projectwork.user.dto.ChainRegistrationDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.service.chain.registration.ProcessRegistrationChainPart;

import static ru.otus.projectwork.user.service.chain.registration.impl.CreatePassportDataChainPart.ORDER;

/**
 * Часть цепочки для создания данных паспортных данных клиента.
 * <p>
 * Эта часть цепочки отвечает за создание новой записи паспортных данных клиента на основе данных запроса регистрации.
 * Паспортные данные сохраняются в репозитории паспортных данных.
 * <p>
 * Порядок выполнения: 1
 * <p>
 * Процесс работы: Этот компонент цепочки регистрации принимает данные запроса регистрации и создает новую запись
 * паспортных данных клиента. Созданные паспортные данные сохраняются в указанном репозитории паспортных данных.
 * <p>
 * Пример использования:
 * <pre>{@code
 * RegistrationRequestDto request = RegistrationRequestDtoUtil.getValidRegistrationRequestDto();
 * ChainRegistrationDto previous = ChainRegistrationDto.builder().build();
 *
 * ChainRegistrationDto result = createPassportDataChainPart.process(request, previous);
 * }</pre>
 * <p>
 * Примечания: Для корректной работы необходимо, чтобы в запросе регистрации были заполнены все необходимые поля, такие
 * как номер паспорта, данные органа, выдавшего паспорт, дата выдачи, код подразделения и дата рождения клиента.
 * <p>
 * Предусловия: Входные данные должны соответствовать структуре и типам, определенным в классе RegistrationRequestDto.
 * <p>
 * Постусловия: После выполнения метода process() в объекте previous будет обновлена информация о паспортных данных
 * клиента.
 * <p>
 * Исключения: Может выбрасывать исключения в случае ошибок сохранения данных в репозитории или если входные данные не
 * удовлетворяют требованиям.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class CreatePassportDataChainPart implements ProcessRegistrationChainPart {

    protected static final int ORDER = 1;

    private final PassportDataRepository passportDataRepository;

    /**
     * Создает новую запись паспортных данных клиента на основе данных запроса регистрации. Сохраняет созданные
     * паспортные данные в репозитории паспортных данных.
     *
     * @param request  данные запроса регистрации
     * @param previous объект, содержащий результаты предыдущих этапов цепочки регистрации
     * @return объект ChainRegistrationDto с обновленными данными о паспортных данных клиента
     */
    @Override
    public ChainRegistrationDto process(RegistrationRequestDto request, ChainRegistrationDto previous) {

        PassportData passportData = getPassportData(request);

        passportData = passportDataRepository.save(passportData);

        previous.setPassportData(passportData);
        return previous;
    }

    /**
     * Формирует объект PassportData на основе данных запроса регистрации.
     *
     * @param request данные запроса регистрации
     * @return объект PassportData с данными о паспортных данных клиента
     */
    private static PassportData getPassportData(RegistrationRequestDto request) {
        return PassportData.builder()
                .passportNumber(request.passportNumber())
                .issuedBy(request.issuedBy())
                .issuedDate(request.issueDate())
                .departmentCode(request.departmentCode())
                .birthDate(request.birthDate())
                .build();
    }

}

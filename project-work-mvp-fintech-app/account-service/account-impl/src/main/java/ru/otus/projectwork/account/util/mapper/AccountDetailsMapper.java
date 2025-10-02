package ru.otus.projectwork.account.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.otus.projectwork.account.util.DefaultAccountDetailsProperties;
import ru.otus.projectwork.domain.model.AccountDetails;

@Mapper(componentModel = "spring")
public interface AccountDetailsMapper {

    /**
     * Преобразует объект {@link DefaultAccountDetailsProperties} в {@link AccountDetails},
     * заполняя только те поля, которые присутствуют в конфигурации.
     *
     * @param accountDetails конфигурационные данные из application.yml
     * @return объект {@link AccountDetails} с предзаполненными банковскими реквизитами
     */
    @Mappings({
            @Mapping(target = "accountNumber", ignore = true),
            @Mapping(target = "accountDetailsId", ignore = true),
            @Mapping(target = "updateDate", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "reasonBlocking", ignore = true),
            @Mapping(source = "swiftCode", target = "swiftCodeBank")
    })
    AccountDetails toAccountDetails(DefaultAccountDetailsProperties accountDetails);
}

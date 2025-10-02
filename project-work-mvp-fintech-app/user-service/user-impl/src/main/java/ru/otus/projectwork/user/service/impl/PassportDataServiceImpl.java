package ru.otus.projectwork.user.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.otus.projectwork.domain.repository.PassportDataRepository;
import ru.otus.projectwork.user.service.PassportDataService;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@RequiredArgsConstructor
public class PassportDataServiceImpl implements PassportDataService {

    PassportDataRepository passportDataRepository;

    /**
     * Проверяет существование паспортных данных по номеру паспорта.
     *
     * @param passportNumber номер паспорта для проверки
     * @return true, если паспортные данные с указанным номером существуют в базе данных, иначе false
     */
    @Override
    public boolean existsByPassportNumber(String passportNumber) {
        return passportDataRepository.existsByPassportNumber(passportNumber);
    }
}

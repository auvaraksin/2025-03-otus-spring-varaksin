package ru.otus.projectwork.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.otus.projectwork.user.util.validator.RegExp;
import ru.otus.projectwork.user.util.validator.ValidationGroups;
import ru.otus.projectwork.user.util.validator.annotation.ValidAge;
import ru.otus.projectwork.user.util.validator.annotation.ValidIssueDate;

import java.time.LocalDate;

/**
 * DTO для запроса регистрации пользователя.
 *
 * @param firstName      - имя пользователя
 * @param lastName       - фамилия пользователя
 * @param middleName     - отчество пользователя
 * @param mobilePhone    - номер мобильного телефона пользователя
 * @param email          - адрес электронной почты пользователя
 * @param password       - захэшированный пароль пользователя
 * @param passportNumber - номер паспорта пользователя
 * @param issuedBy       - кем выдан паспорт
 * @param issueDate      - дата выдачи паспорта
 * @param departmentCode - код подразделения
 * @param birthDate      - дата рождения пользователя
 * @param country        - страна регистрации пользователя
 * @param city           - город регистрации пользователя
 * @param street         - улица регистрации пользователя
 * @param house          - номер дома регистрации пользователя
 * @param hull           - корпус регистрации пользователя
 * @param flat           - квартира регистрации пользователя
 * @param postCode       - почтовый индекс регистрации пользователя
 */
@Builder
@Schema(description = "Данные для регистрации пользователя")
@GroupSequence({
        ValidationGroups.NotNullIssueDate.class,
        ValidationGroups.NotNullBirthDate.class,
        ValidationGroups.NotBlankFirstName.class,
        ValidationGroups.NotBlankLastName.class,
        ValidationGroups.NotBlankMobilePhone.class,
        ValidationGroups.NotBlankEmail.class,
        ValidationGroups.NotBlankPassword.class,
        ValidationGroups.NotBlankPassportNumber.class,
        ValidationGroups.NotBlankIssuedBy.class,
        ValidationGroups.NotBlankDepartmentCode.class,
        ValidationGroups.NotBlankCountry.class,
        ValidationGroups.NotBlankCity.class,
        ValidationGroups.NotBlankStreet.class,
        ValidationGroups.NotBlankHouse.class,
        ValidationGroups.NotBlankPostCode.class,
        ValidationGroups.SizeFirstName.class,
        ValidationGroups.SizeLastName.class,
        ValidationGroups.SizeMiddleName.class,
        ValidationGroups.SizeEmail.class,
        ValidationGroups.SizePassword.class,
        ValidationGroups.SizeIssuedBy.class,
        ValidationGroups.SizeCountry.class,
        ValidationGroups.SizeCity.class,
        ValidationGroups.SizeStreet.class,
        ValidationGroups.SizeHull.class,
        ValidationGroups.Email.class,
        ValidationGroups.PatternMobilePhone.class,
        ValidationGroups.PatternEmail.class,
        ValidationGroups.PatternPassword.class,
        ValidationGroups.PatternPassportNumber.class,
        ValidationGroups.PatternDepartmentCode.class,
        ValidationGroups.PatternPostCode.class,
        ValidationGroups.ValidAgeBirthDate.class,
        RegistrationRequestDto.class

})
@ValidIssueDate
public record RegistrationRequestDto(

        @Schema(description = "Имя", example = "Иван")
        @NotBlank(message = "Имя не может быть пустым", groups = ValidationGroups.NotBlankFirstName.class)
        @Size(max = 30, message = "Имя не может превышать 30 символов", groups = ValidationGroups.SizeFirstName.class)
        String firstName,

        @Schema(description = "Фамилия", example = "Иванов")
        @NotBlank(message = "Фамилия не может быть пустой", groups = ValidationGroups.NotBlankLastName.class)
        @Size(max = 30, message = "Фамилия не может превышать 30 символов", groups = ValidationGroups.SizeLastName.class)
        String lastName,

        @Schema(description = "Отчество", example = "Иванович")
        @Size(max = 30, message = "Отчество не может превышать 30 символов", groups = ValidationGroups.SizeMiddleName.class)
        String middleName,

        @Schema(description = "Номер мобильного телефона", example = "73456789010")
        @NotBlank(message = "Номер телефона не может быть пустым", groups = ValidationGroups.NotBlankMobilePhone.class)
        @Pattern(message = "Некорректный номер телефона", regexp = RegExp.MOBILE_PHONE_NUMBER, groups = ValidationGroups.PatternMobilePhone.class)
        String mobilePhone,

        @Schema(description = "Адрес электронной почты", example = "user@example.com")
        @Email(message = "Некорректный адрес электронной почты", groups = ValidationGroups.Email.class)
        @Pattern(message = "Email должен заканчиваться на .com или .ru", regexp = RegExp.EMAIL, groups = ValidationGroups.PatternEmail.class)
        @NotBlank(message = "Адрес электронной почты не может быть пустым", groups = ValidationGroups.NotBlankEmail.class)
        @Size(min = 5, max = 31, message = "Email должен содержать от 5 до 31 символов", groups = ValidationGroups.SizeEmail.class)
        String email,

        @Schema(description = "Захэшированный пароль", example = "securePassword123!")
        @NotBlank(message = "Пароль не может быть пустым", groups = ValidationGroups.NotBlankPassword.class)
        @Pattern(message = "Пароль должен содержать минимум 1 букву верхнего и нижнего регистра, цифру и символ",
                regexp = RegExp.PASSWORD, groups = ValidationGroups.PatternPassword.class)
        @Size(min = 8, message = "Пароль должен содержать минимум 8 символов", groups = ValidationGroups.SizePassword.class)
        String password,

        @Schema(description = "Номер паспорта", example = "1234567890")
        @NotBlank(message = "Номер паспорта не может быть пустым", groups = ValidationGroups.NotBlankPassportNumber.class)
        @Pattern(message = "Номер паспорта не может превышать 10 символов, допускаются только числовые значения",
                regexp = RegExp.PASSPORT_NUMBER, groups = ValidationGroups.PatternPassportNumber.class)
        String passportNumber,

        @Schema(description = "Кем выдан паспорт", example = "Отделение полиции")
        @NotBlank(message = "Поле 'кем выдан' не может быть пустым", groups = ValidationGroups.NotBlankIssuedBy.class)
        @Size(max = 100, message = "Поле 'кем выдан' не может превышать 100 символов", groups = ValidationGroups.SizeIssuedBy.class)
        String issuedBy,

        @Schema(description = "Дата выдачи паспорта", example = "2010-01-01")
        @NotNull(message = "Дата выдачи не может быть пустой", groups = ValidationGroups.NotNullIssueDate.class)
        LocalDate issueDate,

        @Schema(description = "Код подразделения", example = "770001")
        @NotBlank(message = "Код подразделения не может быть пустым", groups = ValidationGroups.NotBlankDepartmentCode.class)
        @Pattern(message = "Код подразделения не может превышать 6 символов, допускаются только числовые значения",
                regexp = RegExp.PASSPORT_DEPARTMENT_CODE, groups = ValidationGroups.PatternDepartmentCode.class)
        String departmentCode,

        @Schema(description = "Дата рождения", example = "1990-05-20")
        @NotNull(message = "Дата рождения не может быть пустой", groups = ValidationGroups.NotNullBirthDate.class)
        @ValidAge(message = "Валидация возраста", groups = ValidationGroups.ValidAgeBirthDate.class)
        LocalDate birthDate,

        @Schema(description = "Страна регистрации", example = "Россия")
        @NotBlank(message = "Страна регистрации не может быть пустой", groups = ValidationGroups.NotBlankCountry.class)
        @Size(max = 100, message = "Страна регистрации не может превышать 100 символов", groups = ValidationGroups.SizeCountry.class)
        String country,

        @Schema(description = "Город регистрации", example = "Москва")
        @NotBlank(message = "Город регистрации не может быть пустым", groups = ValidationGroups.NotBlankCity.class)
        @Size(max = 100, message = "Город регистрации не может превышать 100 символов", groups = ValidationGroups.SizeCity.class)
        String city,

        @Schema(description = "Улица", example = "Арбатская")
        @NotBlank(message = "Улица не может быть пустой", groups = ValidationGroups.NotBlankStreet.class)
        @Size(max = 100, message = "Улица не может превышать 100 символов", groups = ValidationGroups.SizeStreet.class)
        String street,

        @Schema(description = "Номер дома", example = "10")
        @NotBlank(message = "Номер дома не может быть пустым", groups = ValidationGroups.NotBlankHouse.class)
        String house,

        @Schema(description = "Корпус", example = "1")
        @Size(max = 7, message = "Корпус не может превышать 7 символов", groups = ValidationGroups.SizeHull.class)
        String hull,

        @Schema(description = "Квартира", example = "5")
        String flat,

        @Schema(description = "Почтовый индекс", example = "123456")
        @Pattern(message = "Почтовый индекс должен состоять из 6 цифр", regexp = RegExp.POST_CODE, groups = ValidationGroups.PatternPostCode.class)
        @NotBlank(message = "Почтовый индекс не может быть пустым", groups = ValidationGroups.NotBlankPostCode.class)
        String postCode
) {
}

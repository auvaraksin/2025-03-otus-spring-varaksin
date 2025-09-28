package ru.otus.projectwork.user.util.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

/**
 * Класс содержащий регулярные выражения.
 * Определяет регулярные выражения для валидации данных.
 */
@UtilityClass
public class RegExp {

    public final String MOBILE_PHONE_NUMBER = "^7\\d{10}$";
    public final String MOBILE_PHONE_NUMBER_FOR_AUTHORISATION = "(^7\\d{10}$)||(^$)";
    public final String EMAIL = "^[\\w.%+-]+@[\\w.-]+\\.(com|ru)$";
    public final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\/\\.]).{8,}$";
    public final String POST_CODE = "^\\d{6}$";
    public final String FILE_DATA_BASE64 = "^[A-Za-z0-9+/=]*$";
    public final String PIN_CODE = "\\d{4}";
    public final String CARD_NUMBER = "\\d{16}";
    public final String PASSPORT_NUMBER = "\\d{10}";
    public final String PASSPORT_DEPARTMENT_CODE = "\\d{6}";
}

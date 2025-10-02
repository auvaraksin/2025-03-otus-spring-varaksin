package ru.otus.projectwork.user.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.otus.projectwork.user.util.validator.annotation.ValidIssueDate;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class IssueDateValidator implements ConstraintValidator<ValidIssueDate, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {

            Field birthDateField = obj.getClass().getDeclaredField("birthDate");
            Field issueDateField = obj.getClass().getDeclaredField("issueDate");

            birthDateField.setAccessible(true);
            issueDateField.setAccessible(true);

            LocalDate birthDate = (LocalDate) birthDateField.get(obj);
            LocalDate issueDate = (LocalDate) issueDateField.get(obj);

            if (birthDate == null || issueDate == null) {
                return false;
            }

            LocalDate minIssueDate = birthDate.plusYears(14);
            LocalDate today = LocalDate.now();

            return !issueDate.isBefore(minIssueDate) && !issueDate.isAfter(today);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}

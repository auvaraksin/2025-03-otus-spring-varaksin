package ru.otus.projectwork.user.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.otus.projectwork.user.util.validator.annotation.ValidAge;

import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false;
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}

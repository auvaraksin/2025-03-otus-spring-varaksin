package ru.otus.projectwork.user.util.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.otus.projectwork.user.util.validator.IssueDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IssueDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIssueDate {
    String message() default "Дата выдачи паспорта должна быть не раньше, чем дата рождения + 14 лет и не позже текущей даты";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

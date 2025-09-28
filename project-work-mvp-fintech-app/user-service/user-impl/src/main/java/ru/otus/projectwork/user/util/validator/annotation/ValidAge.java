package ru.otus.projectwork.user.util.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.otus.projectwork.user.util.validator.AgeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Возраст должен быть от 18 до 120 лет";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

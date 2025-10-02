package ru.otus.projectwork.account.util;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "default-account-data")
public record DefaultAccountDetailsProperties(

        @NotBlank
        String bikBank,

        @NotBlank
        String bankName,

        @NotBlank
        String innBank,

        @NotBlank
        String corAccountBank,

        @NotBlank
        String kppBank,

        @NotBlank
        String okpoBank,

        @NotBlank
        String ogrnBank,

        @NotBlank
        String swiftCode
) {
}
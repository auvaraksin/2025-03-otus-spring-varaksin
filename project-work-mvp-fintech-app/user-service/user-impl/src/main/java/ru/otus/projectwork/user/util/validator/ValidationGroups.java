package ru.otus.projectwork.user.util.validator;

public interface ValidationGroups {
    interface NotBlankFirstName {}
    interface NotBlankLastName {}
    interface NotBlankMobilePhone {}
    interface NotBlankEmail {}
    interface NotBlankPassword {}
    interface NotBlankPassportNumber {}
    interface NotBlankIssuedBy {}
    interface NotBlankDepartmentCode {}
    interface NotBlankCountry {}
    interface NotBlankCity {}
    interface NotBlankStreet {}
    interface NotBlankHouse {}
    interface NotBlankPostCode {}

    interface NotNullIssueDate {}
    interface NotNullBirthDate {}

    interface SizeFirstName {}
    interface SizeLastName {}
    interface SizeMiddleName {}
    interface SizeEmail {}
    interface SizePassword {}
    interface SizeIssuedBy {}
    interface SizeCountry {}
    interface SizeCity {}
    interface SizeStreet {}
    interface SizeHull {}

    interface Email {}

    interface PatternMobilePhone {}
    interface PatternEmail {}
    interface PatternPassword {}
    interface PatternPassportNumber {}
    interface PatternDepartmentCode {}
    interface PatternPostCode {}
    interface ValidAgeBirthDate {}
}

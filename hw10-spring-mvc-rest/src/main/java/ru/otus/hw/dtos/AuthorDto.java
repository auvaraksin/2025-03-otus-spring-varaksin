package ru.otus.hw.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Author;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private long id;

    private String fullName;

    public AuthorDto(Author author) {
        this.id = author.getId();
        this.fullName = author.getFullName();
    }
}

package com.reactivespring.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;
    @NotBlank(message = "movieInfo must have a name")
    private String name;
    @NotNull(message = "movieInfo must have a year")
    @Positive(message = "value must be positive")
    private Integer year;
    private List<@NotBlank(message = "Genre must be present") String> genre;
    private LocalDate releaseDate;
}

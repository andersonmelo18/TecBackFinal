package br.uniesp.si.techback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MovieResultDTO {
    private String title;
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;
}
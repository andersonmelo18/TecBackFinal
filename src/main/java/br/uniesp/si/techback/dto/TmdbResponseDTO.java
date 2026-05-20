package br.uniesp.si.techback.dto;

import lombok.Data;
import java.util.List;

@Data
public class TmdbResponseDTO {
    private List<MovieResultDTO> results;
}
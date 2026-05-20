package br.uniesp.si.techback.dto;

import lombok.Data;

@Data
public class FeriadoDTO {
    private String date;
    private String name;
    private String type;
}
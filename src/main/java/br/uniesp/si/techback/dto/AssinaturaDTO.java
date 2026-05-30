package br.uniesp.si.techback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaDTO {

    private Long id;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;
    private String usuarioNome;

    @NotNull(message = "O ID do plano é obrigatório")
    private Long planoId;
    private String planoNome;
    private String planoDescricao;
    private BigDecimal precoContratado;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
}
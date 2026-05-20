package br.uniesp.si.techback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private LocalDateTime dataPedido;

    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal;

    private String status;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;
    private String usuarioNome;
}
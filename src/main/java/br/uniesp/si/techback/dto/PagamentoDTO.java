package br.uniesp.si.techback.dto;

import jakarta.validation.constraints.NotBlank;
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
public class PagamentoDTO {

    private Long id;

    @NotNull(message = "O valor do pagamento é obrigatório")
    private BigDecimal valor;

    private LocalDateTime dataPagamento;

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    private String status;

    @NotNull(message = "O ID do pedido é obrigatório")
    private Long pedidoId;
}
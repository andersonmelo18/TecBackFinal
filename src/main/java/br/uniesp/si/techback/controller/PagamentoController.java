package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.PagamentoDTO;
import br.uniesp.si.techback.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Slf4j
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<PagamentoDTO>> listarTodos() {
        log.info("Recebida requisição GET para listar todos os pagamentos");
        List<PagamentoDTO> pagamentos = pagamentoService.listarTodos();
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagamentoDTO> buscarPorPedido(@PathVariable Long pedidoId) {
        log.info("Recebida requisição GET para buscar pagamento vinculados ao pedido ID: {}", pedidoId);
        PagamentoDTO pagamento = pagamentoService.buscarPorPedido(pedidoId);
        return ResponseEntity.ok(pagamento);
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> processar(@Valid @RequestBody PagamentoDTO pagamentoDTO) {
        log.info("Recebida requisição POST para efetuar pagamento do pedido ID: {}", pagamentoDTO.getPedidoId());
        try {
            PagamentoDTO novoPagamento = pagamentoService.processarPagamento(pagamentoDTO);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(novoPagamento.getId())
                    .toUri();

            log.debug("Fluxo concluído. Recurso criado em: {}", location);
            return ResponseEntity.created(location).body(novoPagamento);
        } catch (Exception e) {
            log.error("Erro fatal ao processar o endpoint de pagamento: {}", e.getMessage());
            throw e;
        }
    }
}
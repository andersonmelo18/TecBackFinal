package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.PedidoDTO;
import br.uniesp.si.techback.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        log.info("Recebida requisição GET para listar todos os pedidos do sistema");
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("Recebida requisição GET para listar pedidos do usuário ID: {}", usuarioId);
        List<PedidoDTO> pedidos = pedidoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestBody PedidoDTO pedidoDTO) {
        log.info("Recebida requisição POST para abrir novo pedido");
        try {
            PedidoDTO novoPedido = pedidoService.criarPedido(pedidoDTO);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(novoPedido.getId())
                    .toUri();

            log.debug("Pedido registrado com sucesso. URI de consulta: {}", location);
            return ResponseEntity.created(location).body(novoPedido);
        } catch (Exception e) {
            log.error("Falha no controlador ao registrar pedido: {}", e.getMessage());
            throw e;
        }
    }
}
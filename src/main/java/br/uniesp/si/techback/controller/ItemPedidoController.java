package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.ItemPedidoDTO;
import br.uniesp.si.techback.service.ItemPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
@RequiredArgsConstructor
@Slf4j
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<ItemPedidoDTO>> listarPorPedido(@PathVariable Long pedidoId) {
        log.info("Recebida requisição GET para listar itens do pedido ID: {}", pedidoId);
        List<ItemPedidoDTO> itens = itemPedidoService.listarPorPedido(pedidoId);
        return ResponseEntity.ok(itens);
    }

    @PostMapping
    public ResponseEntity<ItemPedidoDTO> adicionar(@Valid @RequestBody ItemPedidoDTO itemPedidoDTO) {
        log.info("Recebida requisição POST para incluir item no pedido");
        try {
            ItemPedidoDTO novoItem = itemPedidoService.adicionarItem(itemPedidoDTO);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(novoItem.getId())
                    .toUri();

            log.debug("Item de pedido registrado. Endpoint de acesso: {}", location);
            return ResponseEntity.created(location).body(novoItem);
        } catch (Exception e) {
            log.error("Falha ao incluir item de pedido: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        log.info("Recebida requisição DELETE para o item de pedido ID: {}", id);
        try {
            itemPedidoService.removerItem(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar item de pedido: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
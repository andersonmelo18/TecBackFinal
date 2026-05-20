package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.ProdutoDTO;
import br.uniesp.si.techback.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Slf4j
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar(@RequestParam(value = "ativos", required = false, defaultValue = "false") boolean apenasAtivos) {
        log.info("Recebida requisição GET para listar produtos. Apenas ativos? {}", apenasAtivos);
        List<ProdutoDTO> produtos = apenasAtivos ? produtoService.listarAtivos() : produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        try {
            ProdutoDTO produto = produtoService.buscarPorId(id);
            return ResponseEntity.ok(produto);
        } catch (Exception e) {
            log.error("Erro ao encontrar produto ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoDTO produtoDTO) {
        log.info("Recebida requisição POST para cadastrar produto: {}", produtoDTO.getNome());
        try {
            ProdutoDTO produtoSalvo = produtoService.salvar(produtoDTO);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(produtoSalvo.getId())
                    .toUri();

            log.debug("Produto inserido com sucesso. Endpoint: {}", location);
            return ResponseEntity.created(location).body(produtoSalvo);
        } catch (Exception e) {
            log.error("Erro no fluxo do controlador ao criar produto: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
        log.info("Recebida requisição PUT para modificar produto ID: {}", id);
        try {
            ProdutoDTO produtoAtualizado = produtoService.atualizar(id, produtoDTO);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (Exception e) {
            log.error("Erro ao modificar produto ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Recebida requisição DELETE para remover produto ID: {}", id);
        try {
            produtoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar produto ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
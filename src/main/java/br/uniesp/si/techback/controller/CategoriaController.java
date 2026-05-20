package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.CategoriaDTO;
import br.uniesp.si.techback.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriaController {

    private final CategoriaService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        log.info("Recebida requisição GET para listar todas as categorias");
        List<CategoriaDTO> categorias = categoryService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        try {
            CategoriaDTO categoria = categoryService.buscarPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            log.error("Erro ao obter categoria ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> criar(@Valid @RequestBody CategoriaDTO categoryDTO) {
        log.info("Recebida requisição POST para registrar categoria: {}", categoryDTO.getNome());
        try {
            CategoriaDTO categoriaSalva = categoryService.salvar(categoryDTO);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(categoriaSalva.getId())
                    .toUri();

            log.debug("Categoria inserida com sucesso. Endpoint: {}", location);
            return ResponseEntity.created(location).body(categoriaSalva);
        } catch (Exception e) {
            log.error("Erro no fluxo do controlador ao criar categoria: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoryDTO) {
        log.info("Recebida requisição PUT para modificar categoria ID: {}", id);
        try {
            CategoriaDTO categoriaAtualizada = categoryService.atualizar(id, categoryDTO);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (Exception e) {
            log.error("Erro ao modificar categoria ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Recebida requisição DELETE para remover categoria ID: {}", id);
        try {
            categoryService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar categoria ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
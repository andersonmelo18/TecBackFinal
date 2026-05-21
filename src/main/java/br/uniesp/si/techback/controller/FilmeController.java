package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.FilmeDTO;
import br.uniesp.si.techback.model.Filme;
import br.uniesp.si.techback.service.FilmeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/filmes")
@RequiredArgsConstructor
@Slf4j
public class FilmeController {

    private final FilmeService filmeService;

    @GetMapping("/ordenado")
    public List<Filme> listarOrdenado() {
        log.info("Listando todos os filmes");
        return filmeService.listarOrdenado();
    }

    @GetMapping
    public List<FilmeDTO> listar() {
        log.info("Listando todos os filmes");
        List<FilmeDTO> filmes = filmeService.listar();
        log.debug("Total de filmes encontrados: {}", filmes.size());
        return filmes;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmeDTO> buscarPorId(@PathVariable Long id) {
        try {
            FilmeDTO filme = filmeService.buscarPorId(id);
            log.debug("Filme encontrado: {}", filme);
            return ResponseEntity.ok(filme);
        } catch (Exception e) {
            log.error("Erro ao buscar filme com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<FilmeDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        log.info("Recebida requisição para listar filmes da categoria ID: {}", categoriaId);
        try {
            List<FilmeDTO> filmes = filmeService.listarPorCategoria(categoriaId);
            log.debug("Total de filmes encontrados para a categoria {}: {}", categoriaId, filmes.size());
            return ResponseEntity.ok(filmes);
        } catch (Exception e) {
            log.error("Erro ao listar filmes da categoria {}: {}", categoriaId, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FilmeDTO> criar(@Valid @RequestBody FilmeDTO filmeDTO) {
        log.info("Recebida requisição para criar novo filme: {}", filmeDTO.getTitulo());
        try {
            FilmeDTO filmeSalvo = filmeService.salvar(filmeDTO);
            log.info("Filme criado com sucesso. ID: {}, Título: {}", filmeSalvo.getId(), filmeSalvo.getTitulo());
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(filmeSalvo.getId())
                    .toUri();
            return ResponseEntity.created(location).body(filmeSalvo);
        } catch (Exception e) {
            log.error("Erro ao criar filme: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmeDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FilmeDTO filmeDTO) {
        log.info("Atualizando filme com ID {}: {}", id, filmeDTO);
        try {
            FilmeDTO filmeAtualizado = filmeService.atualizar(id, filmeDTO);
            return ResponseEntity.ok(filmeAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar filme ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Excluindo filme com ID: {}", id);
        try {
            filmeService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir filme com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/genero")
    public ResponseEntity<List<FilmeDTO>> buscarPorGenero(@RequestParam String valor) {
        log.info("Buscando filmes pelo gênero: '{}'", valor);
        try {
            List<FilmeDTO> filmes = filmeService.buscarPorGenero(valor);
            return ResponseEntity.ok(filmes);
        } catch (Exception e) {
            log.error("Erro ao buscar filmes pelo gênero '{}': {}", valor, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<FilmeDTO>> buscarTopPorRelevancia(@RequestParam int n) {
        log.info("Buscando top {} filmes por relevância", n);
        try {
            List<FilmeDTO> filmes = filmeService.buscarTopNPorRelevancia(n);
            return ResponseEntity.ok(filmes);
        } catch (IllegalArgumentException e) {
            log.warn("Parâmetro inválido para top N: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/lancados-apos")
    public ResponseEntity<List<FilmeDTO>> buscarLancadosAposAno(@RequestParam int ano) {
        log.info("Buscando filmes lançados após o ano {}", ano);
        try {
            List<FilmeDTO> filmes = filmeService.buscarLancadosAposAno(ano);
            return ResponseEntity.ok(filmes);
        } catch (IllegalArgumentException e) {
            log.warn("Ano inválido informado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/favoritos-recentes")
    public ResponseEntity<List<FilmeDTO>> buscarFavoritosRecentes(
            @RequestParam Long usuarioId,
            @RequestParam(defaultValue = "10") int limite) {
        log.info("Buscando {} favoritos recentes do usuário ID: {}", limite, usuarioId);
        try {
            List<FilmeDTO> filmes = filmeService.buscarFavoritosRecentes(usuarioId, limite);
            return ResponseEntity.ok(filmes);
        } catch (Exception e) {
            log.error("Erro ao buscar favoritos do usuário {}: {}", usuarioId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<FilmeDTO>> buscarPorPalavraChave(@RequestParam String palavraChave) {
        log.info("Buscando filmes com a palavra-chave: '{}'", palavraChave);
        try {
            List<FilmeDTO> filmes = filmeService.buscarPorPalavraChave(palavraChave);
            return ResponseEntity.ok(filmes);
        } catch (IllegalArgumentException e) {
            log.warn("Palavra-chave inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
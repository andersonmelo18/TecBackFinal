package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
@Slf4j
public class FavoritoController {

    private final FavoritoService favoritoService;

    @PostMapping
    public ResponseEntity<String> adicionar(
            @RequestParam Long usuarioId,
            @RequestParam Long filmeId) {
        log.info("Requisição para favoritar filme ID {} pelo usuário ID {}", filmeId, usuarioId);
        try {
            favoritoService.adicionarFavorito(usuarioId, filmeId);
            return ResponseEntity.ok("Filme adicionado aos favoritos com sucesso!");
        } catch (RuntimeException e) {
            log.warn("Erro ao adicionar favorito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> remover(
            @RequestParam Long usuarioId,
            @RequestParam Long filmeId) {
        log.info("Requisição para remover filme ID {} dos favoritos do usuário ID {}", filmeId, usuarioId);
        try {
            favoritoService.removerFavorito(usuarioId, filmeId);
            return ResponseEntity.ok("Filme removido dos favoritos com sucesso!");
        } catch (RuntimeException e) {
            log.warn("Erro ao remover favorito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        log.info("Requisição para listar todos os filmes favoritados");
        try {
            // ATENÇÃO: Verifique no seu FavoritoService qual é o nome do método que lista
            // todos os favoritos (pode ser listar(), listarTodos(), buscarTodos(), etc.)
            return ResponseEntity.ok(favoritoService.listar());
        } catch (Exception e) {
            log.error("Erro ao listar favoritos: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Erro ao carregar listagem");
        }
    }
}
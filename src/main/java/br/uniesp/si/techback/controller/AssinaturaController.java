package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.AssinaturaDTO;
import br.uniesp.si.techback.service.AssinaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/assinaturas")
@RequiredArgsConstructor
@Slf4j
public class AssinaturaController {

    private final AssinaturaService assinaturaService;

    @GetMapping
    public List<AssinaturaDTO> listar() {
        log.info("Listando todas as assinaturas");
        return assinaturaService.listar();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AssinaturaDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("Listando assinaturas do usuário ID: {}", usuarioId);
        return ResponseEntity.ok(assinaturaService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<AssinaturaDTO> contratar(@Valid @RequestBody AssinaturaDTO assinaturaDTO) {
        log.info("Contratando plano ID {} para o usuário ID {}", assinaturaDTO.getPlanoId(), assinaturaDTO.getUsuarioId());
        try {
            AssinaturaDTO salva = assinaturaService.contratar(assinaturaDTO);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(salva.getId()).toUri();
            return ResponseEntity.created(location).body(salva);
        } catch (Exception e) {
            log.error("Erro ao contratar plano: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AssinaturaDTO> cancelar(@PathVariable Long id) {
        log.info("Cancelando assinatura ID: {}", id);
        try {
            return ResponseEntity.ok(assinaturaService.cancelar(id));
        } catch (Exception e) {
            log.error("Erro ao cancelar assinatura ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Excluindo assinatura ID: {}", id);
        try {
            assinaturaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir assinatura ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.PlanoDTO;
import br.uniesp.si.techback.service.PlanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/planos")
@RequiredArgsConstructor
@Slf4j
public class PlanoController {

    private final PlanoService planoService;


    @GetMapping
    public List<PlanoDTO> listar() {
        return planoService.listarTodos();
    }

    @GetMapping("/ativos")
    public List<PlanoDTO> listarAtivos() {
        return planoService.listarAtivos();
    }

    @PostMapping
    public ResponseEntity<PlanoDTO> criar(@Valid @RequestBody PlanoDTO planoDTO) {
        log.info("Criando plano: {}", planoDTO.getNome());
        try {
            PlanoDTO salvo = planoService.salvar(planoDTO);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(salvo.getId()).toUri();
            return ResponseEntity.created(location).body(salvo);
        } catch (RuntimeException e) {
            log.warn("Erro ao criar plano: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        try {
            planoService.desativar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            planoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
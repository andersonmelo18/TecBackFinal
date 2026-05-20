package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
@Slf4j
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> listar() {
        log.info("Recebida requisição GET para listar funcionários");
        List<FuncionarioDTO> funcionarios = funcionarioService.listar();
        return ResponseEntity.ok(funcionarios);
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> incluir(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        log.info("Recebida requisição POST para cadastrar funcionário: {}", funcionarioDTO.getNome());
        try {
            FuncionarioDTO funcionarioSalvo = funcionarioService.incluir(funcionarioDTO);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(funcionarioSalvo.getId())
                    .toUri();

            log.debug("Criado com sucesso. URI de acesso: {}", location);
            return ResponseEntity.created(location).body(funcionarioSalvo);
        } catch (Exception e) {
            log.error("Erro no fluxo do controlador ao salvar funcionário: {}", e.getMessage());
            throw e;
        }
    }
}
package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.FeriadoDTO;
import br.uniesp.si.techback.service.FeriadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feriados") // Base URL para este endpoint
public class FeriadoController {

    @Autowired
    private FeriadoService feriadoService;

    // Rota: http://localhost:8080/api/feriados/{ano}
    @GetMapping("/{ano}")
    public ResponseEntity<List<FeriadoDTO>> listarFeriados(@PathVariable("ano") int ano) {
        List<FeriadoDTO> feriados = feriadoService.obterFeriadosDoAno(ano);
        return ResponseEntity.ok(feriados);
    }
}
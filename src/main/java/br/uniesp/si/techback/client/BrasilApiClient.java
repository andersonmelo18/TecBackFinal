package br.uniesp.si.techback.client;

import br.uniesp.si.techback.dto.FeriadoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "brasilApi", url = "https://brasilapi.com.br/api")
public interface BrasilApiClient {

    @GetMapping("/feriados/v1/{ano}")
    List<FeriadoDTO> listarFeriados(@PathVariable("ano") int ano);
}
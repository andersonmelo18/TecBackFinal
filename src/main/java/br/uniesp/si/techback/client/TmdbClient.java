package br.uniesp.si.techback.client;

import br.uniesp.si.techback.config.FeignConfig;
import br.uniesp.si.techback.dto.TmdbResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// configuration = FeignConfig.class vincula a chave da API automaticamente
@FeignClient(name = "tmdbClient", url = "https://api.themoviedb.org/3", configuration = FeignConfig.class)
public interface TmdbClient {

    @GetMapping("/search/movie?language=pt-BR")
    TmdbResponseDTO buscarFilmePeloNome(@RequestParam("query") String nomeFilme);
}
package br.uniesp.si.techback.service;

import br.uniesp.si.techback.client.ViaCepClient;
import br.uniesp.si.techback.client.BrasilApiClient;
import br.uniesp.si.techback.client.TmdbClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntegracaoService {

    // Injetamos todos os seus clientes aqui
    private final ViaCepClient viaCepClient;
    private final BrasilApiClient brasilApiClient;
    private final TmdbClient tmdbClient;

    // Métodos para orquestrar as chamadas...
}
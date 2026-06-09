package br.uniesp.si.techback.service;

import br.uniesp.si.techback.client.BrasilApiClient;
import br.uniesp.si.techback.dto.FeriadoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeriadoService {

    @Autowired
    private BrasilApiClient brasilApiClient;

    public List<FeriadoDTO> obterFeriadosDoAno(int ano) {

        return brasilApiClient.listarFeriados(ano);
    }
}
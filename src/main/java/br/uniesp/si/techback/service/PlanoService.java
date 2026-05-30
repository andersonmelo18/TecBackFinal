package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.PlanoDTO;
import br.uniesp.si.techback.mapper.PlanoMapper;
import br.uniesp.si.techback.model.Plano;
import br.uniesp.si.techback.repository.PlanoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanoService {

    private final PlanoRepository planoRepository;
    private final PlanoMapper planoMapper;

    public List<PlanoDTO> listarTodos() {
        log.info("Listando todos os planos cadastrados");
        return planoRepository.findAll().stream()
                .map(planoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PlanoDTO> listarAtivos() {
        log.info("Listando planos ativos para contratação");
        return planoRepository.findByAtivoTrue().stream()
                .map(planoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanoDTO salvar(PlanoDTO dto) {
        log.info("Cadastrando novo plano: {}", dto.getNome());

        if (planoRepository.findByNomeIgnoreCase(dto.getNome()).isPresent()) {
            throw new RuntimeException("Já existe um plano cadastrado com o nome: " + dto.getNome());
        }

        Plano plano = planoMapper.toEntity(dto);
        plano.setAtivo(true);
        Plano salvo = planoRepository.save(plano);
        log.info("Plano '{}' salvo com sucesso. ID: {}", salvo.getNome(), salvo.getId());
        return planoMapper.toDTO(salvo);
    }

    @Transactional
    public void desativar(Long id) {
        log.info("Desativando plano ID: {}", id);
        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado com o ID: " + id));
        plano.setAtivo(false);
        planoRepository.save(plano);
        log.info("Plano ID: {} desativado", id);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo plano ID: {}", id);
        if (!planoRepository.existsById(id)) {
            throw new RuntimeException("Plano não encontrado com o ID: " + id);
        }
        planoRepository.deleteById(id);
    }
}
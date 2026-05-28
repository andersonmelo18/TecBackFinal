package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.AssinaturaDTO;
import br.uniesp.si.techback.mapper.AssinaturaMapper;
import br.uniesp.si.techback.model.Assinatura;
import br.uniesp.si.techback.repository.AssinaturaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssinaturaService {

    private final AssinaturaRepository assinaturaRepository;
    private final AssinaturaMapper assinaturaMapper;

    public List<AssinaturaDTO> listar() {
        return assinaturaRepository.findAll().stream()
                .map(assinaturaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AssinaturaDTO salvar(AssinaturaDTO dto) {
        Assinatura assinatura = assinaturaMapper.toEntity(dto);
        Assinatura salva = assinaturaRepository.save(assinatura);
        return assinaturaMapper.toDTO(salva);
    }

    public void excluir(Long id) {
        if (!assinaturaRepository.existsById(id)) {
            throw new EntityNotFoundException("Assinatura não encontrada com ID: " + id);
        }
        assinaturaRepository.deleteById(id);
    }
}
package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.CategoriaDTO;
import br.uniesp.si.techback.mapper.CategoriaMapper;
import br.uniesp.si.techback.model.Categoria;
import br.uniesp.si.techback.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoryRepository;
    private final CategoriaMapper categoryMapper;

    public List<CategoriaDTO> listarTodas() {
        log.info("Buscando todas as categorias do catálogo");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO buscarPorId(Long id) {
        log.info("Buscando categoria pelo ID: {}", id);
        Categoria categoria = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoria com ID {} não foi encontrada", id);
                    return new RuntimeException("Categoria não encontrada.");
                });
        return categoryMapper.toDTO(categoria);
    }

    @Transactional
    public CategoriaDTO salvar(CategoriaDTO categoryDTO) {
        log.info("Tentando salvar nova categoria: {}", categoryDTO.getNome());

        if (categoryRepository.findByNomeIgnoreCase(categoryDTO.getNome()).isPresent()) {
            log.warn("Nome de categoria já existente no sistema: {}", categoryDTO.getNome());
            throw new RuntimeException("Já existe uma categoria cadastrada com este nome.");
        }

        try {
            Categoria categoria = categoryMapper.toEntity(categoryDTO);
            Categoria categoriaSalva = categoryRepository.save(categoria);
            log.info("Categoria salva com sucesso! ID: {}", categoriaSalva.getId());
            return categoryMapper.toDTO(categoriaSalva);
        } catch (Exception e) {
            log.error("Erro ao salvar categoria: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public CategoriaDTO atualizar(Long id, CategoriaDTO categoryDTO) {
        log.info("Atualizando dados da categoria ID: {}", id);
        Categoria categoriaAtualizada = categoryRepository.findById(id)
                .map(categoriaExistente -> {
                    categoryDTO.setId(id);
                    Categoria categoriaParaAtualizar = categoryMapper.toEntity(categoryDTO);
                    Categoria categoriaSalva = categoryRepository.save(categoriaParaAtualizar);
                    log.info("Categoria ID: {} modificada com sucesso.", id);
                    return categoriaSalva;
                })
                .orElseThrow(() -> new RuntimeException("Falha ao atualizar: Categoria não encontrada."));
        return categoryMapper.toDTO(categoriaAtualizada);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo categoria ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Falha ao excluir: Categoria não encontrada.");
        }
        try {
            categoryRepository.deleteById(id);
            log.info("Categoria ID: {} removida do sistema", id);
        } catch (Exception e) {
            log.error("Erro ao remover categoria ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.ProdutoDTO;
import br.uniesp.si.techback.mapper.ProdutoMapper;
import br.uniesp.si.techback.model.Produto;
import br.uniesp.si.techback.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    public List<ProdutoDTO> listarTodos() {
        log.info("Buscando todos os produtos/planos cadastrados");
        return produtoRepository.findAll().stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoDTO> listarAtivos() {
        log.info("Buscando todos os produtos ativos do catálogo");
        return produtoRepository.findByAtivoTrue().stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProdutoDTO buscarPorId(Long id) {
        log.info("Buscando produto pelo ID: {}", id);
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    String mensagem = String.format("Produto não encontrado com o ID: %d", id);
                    log.warn(mensagem);
                    return new RuntimeException(mensagem);
                });
        return produtoMapper.toDTO(produto);
    }

    @Transactional
    public ProdutoDTO salvar(ProdutoDTO produtoDTO) {
        log.info("Inserindo novo produto no catálogo: {}", produtoDTO.getNome());
        try {
            Produto produto = produtoMapper.toEntity(produtoDTO);
            produto.setAtivo(true);
            Produto produtoSalvo = produtoRepository.save(produto);
            log.info("Produto salvo com sucesso! ID: {}", produtoSalvo.getId());
            return produtoMapper.toDTO(produtoSalvo);
        } catch (Exception e) {
            log.error("Falha ao cadastrar produto: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO produtoDTO) {
        log.info("Atualizando produto ID: {}", id);
        Produto produtoAtualizado = produtoRepository.findById(id)
                .map(produtoExistente -> {
                    produtoDTO.setId(id);
                    Produto produtoParaAtualizar = produtoMapper.toEntity(produtoDTO);
                    Produto produtoSalvo = produtoRepository.save(produtoParaAtualizar);
                    log.info("Produto ID: {} modificado com sucesso.", id);
                    return produtoSalvo;
                })
                .orElseThrow(() -> {
                    String mensagem = String.format("Falha ao atualizar: produto não encontrado com o ID: %d", id);
                    log.warn(mensagem);
                    return new RuntimeException(mensagem);
                });
        return produtoMapper.toDTO(produtoAtualizado);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo produto ID: {}", id);
        if (!produtoRepository.existsById(id)) {
            String mensagem = String.format("Falha ao excluir: produto não encontrado com o ID: %d", id);
            log.warn(mensagem);
            throw new RuntimeException(mensagem);
        }
        try {
            produtoRepository.deleteById(id);
            log.info("Produto ID: {} removido com sucesso", id);
        } catch (Exception e) {
            log.error("Erro ao remover produto ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
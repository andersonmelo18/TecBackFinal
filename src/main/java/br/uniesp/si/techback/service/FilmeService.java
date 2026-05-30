package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.FilmeDTO;
import br.uniesp.si.techback.mapper.FilmeMapper;
import br.uniesp.si.techback.model.Categoria;
import br.uniesp.si.techback.model.Filme;
import br.uniesp.si.techback.repository.CategoriaRepository;
import br.uniesp.si.techback.repository.FilmeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final FilmeMapper filmeMapper;
    private final CategoriaRepository categoriaRepository;

    public List<Filme> listarOrdenado() {
        return filmeRepository.listarFilmesOrdenados();
    }

    public List<FilmeDTO> listar() {
        log.info("Buscando todos os filmes cadastrados");
        try {
            List<Filme> filmes = filmeRepository.findAll();
            List<FilmeDTO> filmesDTO = filmes.stream()
                    .map(filmeMapper::toDTO)
                    .collect(Collectors.toList());
            log.debug("Total de filmes encontrados: {}", filmesDTO.size());
            return filmesDTO;
        } catch (Exception e) {
            log.error("Falha ao buscar filmes: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FilmeDTO> listarPorCategoria(Long categoriaId) {
        log.info("Buscando filmes cadastrados para a categoria ID: {}", categoriaId);
        try {
            List<Filme> filmes = filmeRepository.findByCategoriaId(categoriaId);
            List<FilmeDTO> filmesDTO = filmes.stream()
                    .map(filmeMapper::toDTO)
                    .collect(Collectors.toList());
            log.debug("Total de filmes encontrados para a categoria {}: {}", categoriaId, filmesDTO.size());
            return filmesDTO;
        } catch (Exception e) {
            log.error("Falha ao buscar filmes para a categoria ID {}: {}", categoriaId, e.getMessage(), e);
            throw e;
        }
    }

    public FilmeDTO buscarPorId(Long id) {
        log.info("Buscando filme pelo ID: {}", id);
        Filme filme = filmeRepository.findById(id)
                .map(filmeEncontrado -> {
                    log.debug("Filme encontrado: ID={}, Título={}", filmeEncontrado.getId(), filmeEncontrado.getTitulo());
                    return filmeEncontrado;
                })
                .orElseThrow(() -> {
                    String mensagem = String.format("Filme não encontrado com o ID: %d", id);
                    log.warn(mensagem);
                    return new RuntimeException(mensagem);
                });
        return filmeMapper.toDTO(filme);
    }

    @Transactional
    public FilmeDTO atualizar(Long id, FilmeDTO filmeDTO) {
        log.info("Atualizando filme ID: {}", id);
        Filme filmeAtualizado = filmeRepository.findById(id)
                .map(filmeExistente -> {
                    filmeDTO.setId(id);
                    Filme filmeParaAtualizar = filmeMapper.toEntity(filmeDTO);

                    if (filmeDTO.getCategoriaId() != null) {
                        Categoria categoria = categoriaRepository.findById(filmeDTO.getCategoriaId())
                                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + filmeDTO.getCategoriaId()));
                        filmeParaAtualizar.setCategoria(categoria);
                    }

                    Filme filmeSalvo = filmeRepository.save(filmeParaAtualizar);
                    log.info("Filme ID: {} atualizado com sucesso. Novo título: {}", id, filmeSalvo.getTitulo());
                    return filmeSalvo;
                })
                .orElseThrow(() -> {
                    String mensagem = String.format("Falha ao atualizar: filme não encontrado com o ID: %d", id);
                    log.warn(mensagem);
                    return new RuntimeException(mensagem);
                });
        return filmeMapper.toDTO(filmeAtualizado);
    }

    @Transactional
    public FilmeDTO salvar(FilmeDTO filmeDTO) {
        log.info("Salvando novo filme: {}", filmeDTO.getTitulo());
        try {
            Filme filme = filmeMapper.toEntity(filmeDTO);

            if (filmeDTO.getCategoriaId() != null) {
                Categoria categoria = categoriaRepository.findById(filmeDTO.getCategoriaId())
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + filmeDTO.getCategoriaId()));
                filme.setCategoria(categoria);
            }

            Filme filmeSalvo = filmeRepository.save(filme);
            log.info("Filme salvo com sucesso. ID: {}, Título: {}", filmeSalvo.getId(), filmeSalvo.getTitulo());
            return filmeMapper.toDTO(filmeSalvo);
        } catch (Exception e) {
            log.error("Falha ao salvar filme '{}': {}", filmeDTO.getTitulo(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo filme ID: {}", id);
        if (!filmeRepository.existsById(id)) {
            String mensagem = String.format("Falha ao excluir: filme não encontrado com o ID: %d", id);
            log.warn(mensagem);
            throw new RuntimeException(mensagem);
        }
        try {
            filmeRepository.deleteById(id);
            log.info("Filme ID: {} excluído com sucesso", id);
        } catch (Exception e) {
            log.error("Erro ao excluir filme ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public List<FilmeDTO> buscarPorGenero(String genero) {
        log.info("Buscando filmes pelo gênero: '{}'", genero);
        List<Filme> filmes = filmeRepository.buscarPorGeneroCaseInsensitive(genero.trim());
        log.debug("Filmes encontrados para o gênero '{}': {}", genero, filmes.size());
        return filmes.stream().map(filmeMapper::toDTO).collect(Collectors.toList());
    }

    public List<FilmeDTO> buscarTopNPorRelevancia(int n) {
        log.info("Buscando top {} filmes por relevância", n);
        if (n <= 0) throw new IllegalArgumentException("O valor de N deve ser maior que zero.");
        List<Filme> filmes = filmeRepository.buscarTopNPorRelevancia(n);
        log.debug("Top {} filmes retornados: {}", n, filmes.size());
        return filmes.stream().map(filmeMapper::toDTO).collect(Collectors.toList());
    }

    public List<FilmeDTO> buscarLancadosAposAno(int ano) {
        log.info("Buscando filmes lançados após o ano {}", ano);
        if (ano < 1888) throw new IllegalArgumentException("O ano informado é inválido. O cinema surgiu em 1888.");
        List<Filme> filmes = filmeRepository.buscarLancadosAposAno(ano);
        log.debug("Filmes encontrados após {}: {}", ano, filmes.size());
        return filmes.stream().map(filmeMapper::toDTO).collect(Collectors.toList());
    }

    public List<FilmeDTO> buscarFavoritosRecentes(Long usuarioId, int limite) {
        log.info("Buscando os {} favoritos mais recentes do usuário ID: {}", limite, usuarioId);
        if (limite <= 0) limite = 10;
        List<Filme> filmes = filmeRepository.buscarFavoritosRecentesDoUsuario(usuarioId, limite);
        log.debug("Favoritos recentes encontrados para usuário {}: {}", usuarioId, filmes.size());
        return filmes.stream().map(filmeMapper::toDTO).collect(Collectors.toList());
    }

    public List<FilmeDTO> buscarPorPalavraChave(String palavraChave) {
        log.info("Buscando filmes com a palavra-chave: '{}'", palavraChave);
        if (palavraChave == null || palavraChave.isBlank())
            throw new IllegalArgumentException("A palavra-chave não pode ser vazia.");
        List<Filme> filmes = filmeRepository.buscarPorPalavraChave(palavraChave.trim());
        log.debug("Filmes encontrados para '{}': {}", palavraChave, filmes.size());
        return filmes.stream().map(filmeMapper::toDTO).collect(Collectors.toList());
    }
}
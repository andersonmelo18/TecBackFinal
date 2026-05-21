package br.uniesp.si.techback.service;

import br.uniesp.si.techback.model.Favorito;
import br.uniesp.si.techback.model.Filme;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.repository.FavoritoRepository;
import br.uniesp.si.techback.repository.FilmeRepository;
import br.uniesp.si.techback.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FilmeRepository filmeRepository;

    @Transactional
    public void adicionarFavorito(Long usuarioId, Long filmeId) {
        log.info("Adicionando filme ID {} aos favoritos do usuário ID {}", filmeId, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.warn("Usuário ID {} não encontrado", usuarioId);
                    return new RuntimeException("Usuário não encontrado com o ID: " + usuarioId);
                });

        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> {
                    log.warn("Filme ID {} não encontrado", filmeId);
                    return new RuntimeException("Filme não encontrado com o ID: " + filmeId);
                });

        if (favoritoRepository.existsByUsuarioIdAndFilmeId(usuarioId, filmeId)) {
            log.warn("Usuário ID {} já favoritou o filme ID {}", usuarioId, filmeId);
            throw new RuntimeException("Este filme já está nos favoritos do usuário.");
        }

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .filme(filme)
                .build();

        favoritoRepository.save(favorito);
        log.info("Filme ID {} adicionado com sucesso aos favoritos do usuário ID {}", filmeId, usuarioId);
    }

    @Transactional
    public void removerFavorito(Long usuarioId, Long filmeId) {
        log.info("Removendo filme ID {} dos favoritos do usuário ID {}", filmeId, usuarioId);

        Favorito favorito = favoritoRepository.findByUsuarioIdAndFilmeId(usuarioId, filmeId)
                .orElseThrow(() -> {
                    log.warn("Favorito não encontrado para usuário ID {} e filme ID {}", usuarioId, filmeId);
                    return new RuntimeException("Este filme não está nos favoritos do usuário.");
                });

        favoritoRepository.delete(favorito);
        log.info("Filme ID {} removido dos favoritos do usuário ID {}", filmeId, usuarioId);
    }
}
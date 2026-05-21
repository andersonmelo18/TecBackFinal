package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    boolean existsByUsuarioIdAndFilmeId(Long usuarioId, Long filmeId);

    List<Favorito> findByUsuarioIdOrderByAdicionadoEmDesc(Long usuarioId);

    Optional<Favorito> findByUsuarioIdAndFilmeId(Long usuarioId, Long filmeId);
}
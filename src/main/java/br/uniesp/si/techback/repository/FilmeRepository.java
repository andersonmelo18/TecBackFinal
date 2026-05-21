package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Favorito;
import br.uniesp.si.techback.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {

    @Query("select f from Filme f order by f.titulo asc")
    List<Filme> listarFilmesOrdenados();

    List<Filme> findAllByOrderByTituloAsc();

    Optional<Filme> findByGeneroAndTitulo(String genero, String titulo);

    @Query("select f from Filme f where f.genero = :genero and f.titulo = :titulo")
    Filme buscarPorGenero(@Param("genero") String gen, @Param("titulo") String tit);

    List<Filme> findByCategoriaId(Long categoriaId);

    @Query("SELECT f FROM Filme f WHERE LOWER(f.genero) = LOWER(:genero)")
    List<Filme> buscarPorGeneroCaseInsensitive(@Param("genero") String genero);

    @Query("SELECT f FROM Filme f ORDER BY f.relevancia DESC LIMIT :n")
    List<Filme> buscarTopNPorRelevancia(@Param("n") int n);

    @Query("SELECT f FROM Filme f WHERE YEAR(f.dataLancamento) > :ano ORDER BY f.dataLancamento DESC")
    List<Filme> buscarLancadosAposAno(@Param("ano") int ano);

    @Query("""
            SELECT f FROM Filme f
            JOIN Favorito fav ON fav.filme = f
            WHERE fav.usuario.id = :usuarioId
            ORDER BY fav.adicionadoEm DESC
            LIMIT :limite
            """)
    List<Filme> buscarFavoritosRecentesDoUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("limite") int limite);

    @Query("""
            SELECT f FROM Filme f
            WHERE LOWER(f.titulo) LIKE LOWER(CONCAT('%', :palavraChave, '%'))
               OR LOWER(f.sinopse) LIKE LOWER(CONCAT('%', :palavraChave, '%'))
            ORDER BY f.titulo ASC
            """)
    List<Filme> buscarPorPalavraChave(@Param("palavraChave") String palavraChave);
}
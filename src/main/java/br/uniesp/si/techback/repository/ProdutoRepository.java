package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca apenas os produtos/planos que estão ativos no catálogo
    List<Produto> findByAtivoTrue();
}
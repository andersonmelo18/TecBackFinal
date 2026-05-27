package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    Page<Produto> findByAtivoTrue(Pageable pageable);
}
package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {

    Optional<Plano> findByNomeIgnoreCase(String nome);

    List<Plano> findByAtivoTrue();
}
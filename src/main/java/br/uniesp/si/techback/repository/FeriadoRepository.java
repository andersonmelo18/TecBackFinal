package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    // Aqui você já ganha métodos como save(), findAll(), deleteId(), etc.
}
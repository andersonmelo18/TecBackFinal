package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    // Busca o pagamento realizado para um determinado pedido
    Optional<Pagamento> findByPedidoId(Long pedidoId);
}
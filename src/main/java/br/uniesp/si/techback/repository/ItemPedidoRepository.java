package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // Busca todos os itens que pertencem a um pedido específico
    List<ItemPedido> findByPedidoId(Long pedidoId);
}
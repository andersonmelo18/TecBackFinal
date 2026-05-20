package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Busca todos os pedidos/assinaturas de um usuário específico
    List<Pedido> findByUsuarioId(Long usuarioId);
}
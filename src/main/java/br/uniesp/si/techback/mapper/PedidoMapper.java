package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.PedidoDTO;
import br.uniesp.si.techback.model.Pedido;
import br.uniesp.si.techback.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public Pedido toEntity(PedidoDTO dto) {
        if (dto == null) return null;

        Pedido pedido = new Pedido();
        pedido.setId(dto.getId());
        pedido.setDataPedido(dto.getDataPedido());
        pedido.setValorTotal(dto.getValorTotal());
        pedido.setStatus(dto.getStatus());

        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            pedido.setUsuario(usuario);
        }

        return pedido;
    }

    public PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) return null;

        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setDataPedido(pedido.getDataPedido());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setStatus(pedido.getStatus());

        if (pedido.getUsuario() != null) {
            dto.setUsuarioId(pedido.getUsuario().getId());
            dto.setUsuarioNome(pedido.getUsuario().getNome());
        }

        return dto;
    }
}
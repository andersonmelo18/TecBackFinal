package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.ItemPedidoDTO;
import br.uniesp.si.techback.model.ItemPedido;
import br.uniesp.si.techback.model.Pedido;
import br.uniesp.si.techback.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoMapper {

    public ItemPedido toEntity(ItemPedidoDTO dto) {
        if (dto == null) return null;

        ItemPedido item = new ItemPedido();
        item.setId(dto.getId());
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(dto.getPrecoUnitario());

        if (dto.getPedidoId() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(dto.getPedidoId());
            item.setPedido(pedido);
        }

        if (dto.getProdutoId() != null) {
            Produto produto = new Produto();
            produto.setId(dto.getProdutoId());
            item.setProduto(produto);
        }

        return item;
    }

    public ItemPedidoDTO toDTO(ItemPedido item) {
        if (item == null) return null;

        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setId(item.getId());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());

        if (item.getPedido() != null) {
            dto.setPedidoId(item.getPedido().getId());
        }

        if (item.getProduto() != null) {
            dto.setProdutoId(item.getProduto().getId());
            dto.setProdutoNome(item.getProduto().getNome());
        }

        return dto;
    }
}
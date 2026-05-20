package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.ItemPedidoDTO;
import br.uniesp.si.techback.model.ItemPedido;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoMapper {

    private final ModelMapper modelMapper;

    public ItemPedidoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ItemPedido toEntity(ItemPedidoDTO dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, ItemPedido.class);
    }

    public ItemPedidoDTO toDTO(ItemPedido entity) {
        if (entity == null) {
            return null;
        }
        ItemPedidoDTO dto = modelMapper.map(entity, ItemPedidoDTO.class);
        if (entity.getProduto() != null) {
            dto.setProdutoNome(entity.getProduto().getNome());
        }
        return dto;
    }
}
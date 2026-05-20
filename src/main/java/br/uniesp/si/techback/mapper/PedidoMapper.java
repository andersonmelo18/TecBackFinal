package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.PedidoDTO;
import br.uniesp.si.techback.model.Pedido;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    private final ModelMapper modelMapper;

    public PedidoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Pedido toEntity(PedidoDTO dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Pedido.class);
    }

    public PedidoDTO toDTO(Pedido entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, PedidoDTO.class);
    }
}
package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.PagamentoDTO;
import br.uniesp.si.techback.model.Pagamento;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    private final ModelMapper modelMapper;

    public PagamentoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Pagamento toEntity(PagamentoDTO dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Pagamento.class);
    }

    public PagamentoDTO toDTO(Pagamento entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, PagamentoDTO.class);
    }
}
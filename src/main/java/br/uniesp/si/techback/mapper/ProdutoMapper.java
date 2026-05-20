package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.ProdutoDTO;
import br.uniesp.si.techback.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    private final ModelMapper modelMapper;

    public ProdutoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Produto toEntity(ProdutoDTO dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Produto.class);
    }

    public ProdutoDTO toDTO(Produto entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, ProdutoDTO.class);
    }
}
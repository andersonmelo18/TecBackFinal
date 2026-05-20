package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.CategoriaDTO;
import br.uniesp.si.techback.model.Categoria;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    private final ModelMapper modelMapper;

    public CategoriaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Categoria.class);
    }

    public CategoriaDTO toDTO(Categoria entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, CategoriaDTO.class);
    }
}
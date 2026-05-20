package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.model.Funcionario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioMapper {

    private final ModelMapper modelMapper;

    public FuncionarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Funcionario toEntity(FuncionarioDTO dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Funcionario.class);
    }

    public FuncionarioDTO toDTO(Funcionario entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, FuncionarioDTO.class);
    }
}
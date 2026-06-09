package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.FeriadoDTO;
import br.uniesp.si.techback.model.Feriado;
import org.springframework.stereotype.Component;

@Component
public class FeriadoMapper {

    public Feriado toEntity(FeriadoDTO dto) {
        if (dto == null) {
            return null;
        }

        Feriado feriado = new Feriado();
        // Converte os campos da BrasilAPI (inglês) para os seus campos locais (português)
        feriado.setDataFeriado(dto.getDate());
        feriado.setNome(dto.getName());
        feriado.setTipo(dto.getType());

        return feriado;
    }

    public FeriadoDTO toDto(Feriado entity) {
        if (entity == null) {
            return null;
        }

        FeriadoDTO dto = new FeriadoDTO();
        dto.setDate(entity.getDataFeriado());
        dto.setName(entity.getNome());
        dto.setType(entity.getTipo());

        return dto;
    }
}
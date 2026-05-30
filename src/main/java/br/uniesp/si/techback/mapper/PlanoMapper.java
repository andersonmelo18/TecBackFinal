package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.PlanoDTO;
import br.uniesp.si.techback.model.Plano;
import org.springframework.stereotype.Component;

@Component
public class PlanoMapper {

    public Plano toEntity(PlanoDTO dto) {
        if (dto == null) return null;

        Plano plano = new Plano();
        plano.setId(dto.getId());
        plano.setNome(dto.getNome());
        plano.setDescricao(dto.getDescricao());
        plano.setPreco(dto.getPreco());
        plano.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        return plano;
    }

    public PlanoDTO toDTO(Plano plano) {
        if (plano == null) return null;

        PlanoDTO dto = new PlanoDTO();
        dto.setId(plano.getId());
        dto.setNome(plano.getNome());
        dto.setDescricao(plano.getDescricao());
        dto.setPreco(plano.getPreco());
        dto.setAtivo(plano.getAtivo());
        return dto;
    }
}
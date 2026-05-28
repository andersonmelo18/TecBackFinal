package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.AssinaturaDTO;
import br.uniesp.si.techback.model.Assinatura;
import org.springframework.stereotype.Component;

@Component
public class AssinaturaMapper {

    public AssinaturaDTO toDTO(Assinatura assinatura) {
        if (assinatura == null) return null;
        return new AssinaturaDTO(
                assinatura.getId(),
                assinatura.getNome(),
                assinatura.getDescricao(),
                assinatura.getPreco()
        );
    }

    public Assinatura toEntity(AssinaturaDTO dto) {
        if (dto == null) return null;
        return new Assinatura(
                dto.getId(),
                dto.getNome(),
                dto.getDescricao(),
                dto.getPreco()
        );
    }
}
package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.AssinaturaDTO;
import br.uniesp.si.techback.model.Assinatura;
import br.uniesp.si.techback.model.Plano;
import br.uniesp.si.techback.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AssinaturaMapper {

    public Assinatura toEntity(AssinaturaDTO dto) {
        if (dto == null) return null;

        Assinatura assinatura = new Assinatura();
        assinatura.setId(dto.getId());
        assinatura.setStatus(dto.getStatus());
        assinatura.setDataInicio(dto.getDataInicio());
        assinatura.setDataFim(dto.getDataFim());
        assinatura.setPrecoContratado(dto.getPrecoContratado());

        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            assinatura.setUsuario(usuario);
        }

        if (dto.getPlanoId() != null) {
            Plano plano = new Plano();
            plano.setId(dto.getPlanoId());
            assinatura.setPlano(plano);
        }

        return assinatura;
    }

    public AssinaturaDTO toDTO(Assinatura assinatura) {
        if (assinatura == null) return null;

        AssinaturaDTO dto = new AssinaturaDTO();
        dto.setId(assinatura.getId());
        dto.setStatus(assinatura.getStatus());
        dto.setDataInicio(assinatura.getDataInicio());
        dto.setDataFim(assinatura.getDataFim());
        dto.setPrecoContratado(assinatura.getPrecoContratado());

        if (assinatura.getUsuario() != null) {
            dto.setUsuarioId(assinatura.getUsuario().getId());
            dto.setUsuarioNome(assinatura.getUsuario().getNome());
        }

        if (assinatura.getPlano() != null) {
            dto.setPlanoId(assinatura.getPlano().getId());
            dto.setPlanoNome(assinatura.getPlano().getNome());
            dto.setPlanoDescricao(assinatura.getPlano().getDescricao());
        }

        return dto;
    }
}
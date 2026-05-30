package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.AssinaturaDTO;
import br.uniesp.si.techback.mapper.AssinaturaMapper;
import br.uniesp.si.techback.model.Assinatura;
import br.uniesp.si.techback.model.Plano;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.repository.AssinaturaRepository;
import br.uniesp.si.techback.repository.PlanoRepository;
import br.uniesp.si.techback.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssinaturaService {

    private final AssinaturaRepository assinaturaRepository;
    private final AssinaturaMapper assinaturaMapper;
    private final UsuarioRepository usuarioRepository;
    private final PlanoRepository planoRepository;

    public List<AssinaturaDTO> listar() {
        log.info("Listando todas as assinaturas");
        return assinaturaRepository.findAll().stream()
                .map(assinaturaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando assinaturas do usuário ID: {}", usuarioId);
        return assinaturaRepository.findByUsuarioId(usuarioId).stream()
                .map(assinaturaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssinaturaDTO contratar(AssinaturaDTO dto) {
        log.info("Contratando plano ID {} para o usuário ID {}", dto.getPlanoId(), dto.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + dto.getUsuarioId()));

        Plano plano = planoRepository.findById(dto.getPlanoId())
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado: " + dto.getPlanoId()));

        if (!plano.getAtivo()) {
            throw new RuntimeException("O plano '" + plano.getNome() + "' não está disponível para contratação.");
        }

        Assinatura assinatura = new Assinatura();
        assinatura.setUsuario(usuario);
        assinatura.setPlano(plano);
        assinatura.setPrecoContratado(plano.getPreco());

        Assinatura salva = assinaturaRepository.save(assinatura);
        log.info("Assinatura ID {} criada. Usuário: {}, Plano: {}, Preço: {}",
                salva.getId(), usuario.getNome(), plano.getNome(), plano.getPreco());
        return assinaturaMapper.toDTO(salva);
    }

    @Transactional
    public AssinaturaDTO cancelar(Long id) {
        log.info("Cancelando assinatura ID: {}", id);
        Assinatura assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura não encontrada: " + id));
        assinatura.setStatus("CANCELADA");
        return assinaturaMapper.toDTO(assinaturaRepository.save(assinatura));
    }

    public void excluir(Long id) {
        if (!assinaturaRepository.existsById(id))
            throw new EntityNotFoundException("Assinatura não encontrada: " + id);
        assinaturaRepository.deleteById(id);
    }
}
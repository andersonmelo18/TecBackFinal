package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.PedidoDTO;
import br.uniesp.si.techback.mapper.PedidoMapper;
import br.uniesp.si.techback.model.Pedido;
import br.uniesp.si.techback.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    public List<PedidoDTO> listarTodos() {
        log.info("Buscando todos os pedidos de assinatura do sistema");
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoDTO> listarPorUsuario(Long usuarioId) {
        log.info("Buscando histórico de pedidos do usuário ID: {}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(pedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoDTO criarPedido(PedidoDTO pedidoDTO) {
        log.info("Iniciando a criação de um novo pedido de assinatura para o usuário ID: {}", pedidoDTO.getUsuarioId());
        try {
            Pedido pedido = pedidoMapper.toEntity(pedidoDTO);

            // Configurações padrão de um novo pedido
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus("AGUARDANDO_PAGAMENTO");

            Pedido pedidoSalvo = pedidoRepository.save(pedido);
            log.info("Pedido ID {} criado com sucesso com status: {}", pedidoSalvo.getId(), pedidoSalvo.getStatus());

            return pedidoMapper.toDTO(pedidoSalvo);
        } catch (Exception e) {
            log.error("Erro ao processar criação do pedido: {}", e.getMessage(), e);
            throw e;
        }
    }
}
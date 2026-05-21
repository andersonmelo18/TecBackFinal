package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.PagamentoDTO;
import br.uniesp.si.techback.mapper.PagamentoMapper;
import br.uniesp.si.techback.model.Pagamento;
import br.uniesp.si.techback.model.Pedido;
import br.uniesp.si.techback.repository.PagamentoRepository;
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
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoMapper pagamentoMapper;

    public List<PagamentoDTO> listarTodos() {
        log.info("Buscando histórico de todas as transações financeiras");
        return pagamentoRepository.findAll().stream()
                .map(pagamentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PagamentoDTO buscarPorPedido(Long pedidoId) {
        log.info("Consultando pagamento do pedido ID: {}", pedidoId);
        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    log.warn("Nenhum pagamento registrado para o pedido ID: {}", pedidoId);
                    return new RuntimeException("Pagamento não encontrado para o pedido especificado.");
                });
        return pagamentoMapper.toDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO processarPagamento(PagamentoDTO pagamentoDTO) {
        log.info("Iniciando processamento de pagamento para o pedido ID: {}", pagamentoDTO.getPedidoId());

        Pedido pedido = pedidoRepository.findById(pagamentoDTO.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido de assinatura não encontrado. ID: " + pagamentoDTO.getPedidoId()));

        Pagamento pagamento = pagamentoMapper.toEntity(pagamentoDTO);
        pagamento.setPedido(pedido);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setStatus("CONCLUIDO"); // Simulação de aprovação imediata do pagamento (PIX/Cartão)

        pedido.setStatus("ATIVO");
        pedidoRepository.save(pedido);
        log.info("Pedido ID {} atualizado para o status ATIVO. Assinatura liberada!", pedido.getId());

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        log.info("Pagamento ID {} registrado com sucesso no banco de dados", pagamentoSalvo.getId());

        return pagamentoMapper.toDTO(pagamentoSalvo);
    }
}
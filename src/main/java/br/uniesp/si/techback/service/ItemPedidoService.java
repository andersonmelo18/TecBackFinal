package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.ItemPedidoDTO;
import br.uniesp.si.techback.mapper.ItemPedidoMapper;
import br.uniesp.si.techback.model.ItemPedido;
import br.uniesp.si.techback.model.Pedido;
import br.uniesp.si.techback.model.Produto;
import br.uniesp.si.techback.repository.ItemPedidoRepository;
import br.uniesp.si.techback.repository.PedidoRepository;
import br.uniesp.si.techback.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoMapper itemPedidoMapper;

    public List<ItemPedidoDTO> listarPorPedido(Long pedidoId) {
        log.info("Buscando todos os itens associados ao pedido ID: {}", pedidoId);
        return itemPedidoRepository.findByPedidoId(pedidoId).stream()
                .map(itemPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemPedidoDTO adicionarItem(ItemPedidoDTO itemPedidoDTO) {
        log.info("Adicionando produto ID {} ao pedido ID {}", itemPedidoDTO.getProdutoId(), itemPedidoDTO.getPedidoId());

        // 1. Validar se o Pedido existe
        Pedido pedido = pedidoRepository.findById(itemPedidoDTO.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado. ID: " + itemPedidoDTO.getPedidoId()));

        // 2. Validar se o Produto existe
        Produto produto = produtoRepository.findById(itemPedidoDTO.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado. ID: " + itemPedidoDTO.getProdutoId()));

        try {
            ItemPedido itemPedido = itemPedidoMapper.toEntity(itemPedidoDTO);
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);

            // Regra de Negócio: Defina automaticamente o preço atual do produto para congelar o valor na compra
            itemPedido.setPrecoUnitario(produto.getPreco());

            ItemPedido itemSalvo = itemPedidoRepository.save(itemPedido);
            log.info("Item de pedido cadastrado com sucesso. ID gerado: {}", itemSalvo.getId());

            return itemPedidoMapper.toDTO(itemSalvo);
        } catch (Exception e) {
            log.error("Erro ao vincular item ao pedido: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void removerItem(Long id) {
        log.info("Removendo item de pedido ID: {}", id);
        if (!itemPedidoRepository.existsById(id)) {
            throw new RuntimeException("Item de pedido não encontrado para remoção. ID: " + id);
        }
        itemPedidoRepository.deleteById(id);
        log.info("Item removido com sucesso.");
    }
}
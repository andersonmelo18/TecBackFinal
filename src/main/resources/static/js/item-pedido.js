// Rota ajustada para bater exatamente com a anotação @RequestMapping("/itens-pedido") do seu Java
const URL_API = '/itens-pedido';
const URL_PEDIDOS = '/pedidos';
const URL_PRODUTOS = '/produtos';

// Caches locais para as tabelas e modais
let cacheItens = [];
let cachePedidos = [];
let cacheProdutos = [];

// ==========================================
// 1. CARREGAR E RENDERIZAR TABELA POR PEDIDO
// ==========================================

// Modificado para carregar os itens filtrados pelo ID do pedido selecionado
async function carregarItensDoPedido(pedidoId) {
    if (!pedidoId) return;

    try {
        // Consome exatamente o endpoint @GetMapping("/pedido/{pedidoId}") do seu Java
        const resposta = await fetch(`${URL_API}/pedido/${pedidoId}`);

        if (resposta.ok) {
            const dados = await resposta.json();
            // Tratamento caso o backend use paginação (Spring Pageable)
            cacheItens = dados.content ? dados.content : dados;
            renderizarTabela(cacheItens);
        } else {
            console.error("Erro ao buscar registros de itens de pedido.");
            renderizarTabela([]); // Limpa a tabela se der erro na requisição
        }
    } catch (erro) {
        console.error("Erro ao buscar registros de itens de pedido:", erro);
    }
}

function renderizarTabela(lista) {
    const corpoTabela = document.getElementById('tabela-itens-pedido');
    corpoTabela.innerHTML = '';

    if (!lista || lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="7" style="text-align:center; color:#888; padding: 20px;">Nenhum produto vinculado a este pedido ainda.</td></tr>`;
        return;
    }

    lista.forEach(item => {
        const precoFormatado = (item.precoUnitario || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        const subtotal = ((item.precoUnitario || 0) * (item.quantidade || 0)).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>#${item.id || '-'}</strong></td>
            <td>Pedido #${item.pedidoId}</td>
            <td>${item.produtoNome || 'Produto'}</td>
            <td>${precoFormatado}</td>
            <td>${item.quantidade}</td>
            <td><strong>${subtotal}</strong></td>
            <td>
                <button type="button" class="btn-excluir" onclick="deletarItem(${item.id}, ${item.pedidoId})">Remover</button>
            </td>
        `;
        corpoTabela.appendChild(tr);
    });
}

// Filtro em tempo real (Lupa principal) focado nos produtos do pedido aberto
document.getElementById('pesquisa-item').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();
    const filtrados = cacheItens.filter(item =>
        (item.id && item.id.toString().includes(termo)) ||
        (item.produtoNome && item.produtoNome.toLowerCase().includes(termo))
    );
    renderizarTabela(filtrados);
});

// ==========================================
// 2. SALVAR NOVO ITEM (POST)
// ==========================================
document.getElementById('form-item-pedido').addEventListener('submit', async function(event) {
    event.preventDefault();

    // Validação básica para garantir que o usuário usou as lupas
    const produtoIdValue = document.getElementById('produtoId').value;
    const pedidoIdValue = document.getElementById('pedidoId').value;

    if (!pedidoIdValue) {
        alert("Por favor, utilize a lupa para selecionar um Pedido válido.");
        return;
    }
    if (!produtoIdValue) {
        alert("Por favor, utilize a lupa para selecionar um Produto válido.");
        return;
    }

    // Monta o objeto JSON estritamente conforme o seu ItemPedidoDTO
    const novoItem = {
        quantidade: parseInt(document.getElementById('quantidade').value, 10),
        precoUnitario: parseFloat(document.getElementById('precoUnitario').value),
        pedidoId: parseInt(pedidoIdValue, 10),
        produtoId: parseInt(produtoIdValue, 10),
        produtoNome: document.getElementById('produtoNome').value
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoItem)
        });

        if (resposta.ok) {
            alert("Item adicionado ao pedido com sucesso!");

            // Limpa apenas os campos do produto inserido, mantendo o ID do pedido ativo
            document.getElementById('produtoId').value = '';
            document.getElementById('produtoNome').value = '';
            document.getElementById('precoUnitario').value = '';
            document.getElementById('quantidade').value = '';

            // Recarrega o histórico atualizado deste pedido específico
            carregarItensDoPedido(novoItem.pedidoId);
        } else {
            const erroApi = await resposta.text();
            alert(`Erro ao adicionar item (Status ${resposta.status}):\n${erroApi}`);
        }
    } catch (erro) {
        console.error("Erro no POST do item:", erro);
        alert("Erro de conexão com o servidor.");
    }
});

// Função de Exclusão atualizada para lembrar de qual pedido recarregar a lista
async function deletarItem(id, pedidoId) {
    if (confirm(`Remover este item do pedido?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });
            if (resposta.ok) {
                carregarItensDoPedido(pedidoId);
            } else {
                const erroApi = await resposta.text();
                alert(`Erro ao remover (Status ${resposta.status}):\n${erroApi}`);
            }
        } catch (erro) {
            console.error("Erro na exclusão:", erro);
        }
    }
}

// ==========================================
// 3. LÓGICA DO MODAL: BUSCAR PEDIDO
// ==========================================
const modalPedido = document.getElementById('modal-buscar-pedido');

document.getElementById('btn-buscar-pedido').addEventListener('click', async () => {
    modalPedido.style.display = 'flex';
    if (cachePedidos.length === 0) {
        try {
            const res = await fetch(URL_PEDIDOS);
            const dados = await res.json();
            cachePedidos = dados.content ? dados.content : dados;
            renderizarModalPedidos(cachePedidos);
        } catch (erro) {
            console.error("Erro ao buscar pedidos:", erro);
        }
    } else {
        renderizarModalPedidos(cachePedidos);
    }
});

function renderizarModalPedidos(lista) {
    const tbody = document.getElementById('tabela-pedidos-modal');
    tbody.innerHTML = '';
    lista.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${p.id}</td>
            <td>${p.usuarioNome || 'Desconhecido'}</td>
            <td><span style="font-size:11px; padding:3px 6px; background:#eee; border-radius:4px; font-weight:bold;">${p.status}</span></td>
            <td style="text-align: right;">
                <button type="button" class="btn-selecionar" onclick="selecionarPedido(${p.id})">✔ Escolher</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

document.getElementById('pesquisa-pedido-modal').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();
    const filtrados = cachePedidos.filter(p =>
        p.id.toString().includes(termo) ||
        (p.status && p.status.toLowerCase().includes(termo))
    );
    renderizarModalPedidos(filtrados);
});

function selecionarPedido(id) {
    document.getElementById('pedidoId').value = id;
    modalPedido.style.display = 'none';

    // Assim que escolhe o pedido, puxa os produtos vinculados a ele
    carregarItensDoPedido(id);
}

// ==========================================
// 4. LÓGICA DO MODAL: BUSCAR PRODUTO
// ==========================================
const modalProduto = document.getElementById('modal-buscar-produto');

document.getElementById('btn-buscar-produto').addEventListener('click', async () => {
    modalProduto.style.display = 'flex';
    if (cacheProdutos.length === 0) {
        try {
            const res = await fetch(URL_PRODUTOS);
            const dados = await res.json();
            cacheProdutos = dados.content ? dados.content : dados;
            renderizarModalProdutos(cacheProdutos);
        } catch (erro) {
            console.error("Erro ao buscar produtos:", erro);
        }
    } else {
        renderizarModalProdutos(cacheProdutos);
    }
});

function renderizarModalProdutos(lista) {
    const tbody = document.getElementById('tabela-produtos-modal');
    tbody.innerHTML = '';
    lista.forEach(p => {
        // Ignora produtos inativos para não serem vendidos
        const isAtivo = String(p.ativo).toLowerCase() === 'true';
        if (!isAtivo) return;

        const preco = p.preco ? p.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }) : 'R$ 0,00';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${p.id}</td>
            <td>${p.nome}</td>
            <td>${preco}</td>
            <td style="text-align: right;">
                <button type="button" class="btn-selecionar" onclick="selecionarProduto(${p.id}, '${p.nome.replace(/'/g, "\\'")}', ${p.preco || 0})">✔ Escolher</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

document.getElementById('pesquisa-produto-modal').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();
    const filtrados = cacheProdutos.filter(p => p.nome.toLowerCase().includes(termo));
    renderizarModalProdutos(filtrados);
});

function selecionarProduto(id, nome, precoSugestao) {
    document.getElementById('produtoId').value = id;
    document.getElementById('produtoNome').value = nome;

    // Auto-preenche o preço unitário com o valor do catálogo
    if(precoSugestao > 0) {
        document.getElementById('precoUnitario').value = precoSugestao;
    }
    // Coloca a quantidade inicial como 1 por conveniência
    document.getElementById('quantidade').value = 1;

    modalProduto.style.display = 'none';
}

// ==========================================
// 5. CONTROLE DE FECHAMENTO DOS MODAIS
// ==========================================
document.getElementById('fechar-modal-pedido').addEventListener('click', () => modalPedido.style.display = 'none');
document.getElementById('fechar-modal-produto').addEventListener('click', () => modalProduto.style.display = 'none');

window.addEventListener('click', (event) => {
    if (event.target === modalPedido) modalPedido.style.display = 'none';
    if (event.target === modalProduto) modalProduto.style.display = 'none';
});
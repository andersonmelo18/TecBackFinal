// Ajuste para a rota correspondente ao seu PagamentoController no backend
const URL_API = '/pagamentos';
const URL_PEDIDOS = '/pedidos'; // Rota adicionada para buscar os pedidos no modal

// Caches locais que abastecem a lupa e os modais instantaneamente
let cachePagamentos = [];
let cachePedidos = [];

// ==========================================
// 1. LÓGICA PRINCIPAL DOS PAGAMENTOS
// ==========================================

// 1. Função para carregar (GET) todos os pagamentos salvos no banco
async function carregarPagamentos() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        // Extrai os dados caso o backend retorne paginação (Spring Pageable) ou uma lista limpa
        cachePagamentos = dados.content ? dados.content : dados;

        renderizarTabela(cachePagamentos);
    } catch (erro) {
        console.error("Erro ao buscar registros de pagamentos:", erro);
    }
}

// Função auxiliar para estruturar as linhas dentro da tabela HTML
function renderizarTabela(lista) {
    const corpoTabela = document.getElementById('tabela-pagamentos');
    corpoTabela.innerHTML = ''; // Limpa a tabela antes de remontar

    if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="7" style="text-align:center; color:#888; padding: 20px;">Nenhum pagamento registrado encontrado.</td></tr>`;
        return;
    }

    lista.forEach(pag => {
        const linha = document.createElement('tr');

        // Formatações visuais
        const valorFormatado = pag.valor
            ? pag.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
            : 'R$ 0,00';

        const dataFormatada = pag.dataPagamento
            ? new Date(pag.dataPagamento).toLocaleString('pt-BR')
            : 'Não registrada';

        // Escolha dinâmica de classe CSS baseado no status retornado
        let classeBadgeStatus = 'status-pendente'; // Usando as classes do novo HTML
        if (pag.status === 'PAGO' || pag.status === 'APROVADO') classeBadgeStatus = 'status-aprovado';
        if (pag.status === 'CANCELADO' || pag.status === 'RECUSADO' || pag.status === 'REEMBOLSADO') classeBadgeStatus = 'status-recusado';

        linha.innerHTML = `
            <td>#${pag.id}</td>
            <td><strong>Pedido #${pag.pedidoId}</strong></td>
            <td>${dataFormatada}</td>
            <td><span class="status" style="background:#555;">${pag.formaPagamento}</span></td>
            <td><span class="status ${classeBadgeStatus}">${pag.status}</span></td>
            <td>${valorFormatado}</td>
            <td>
                <button type="button" class="btn-detalhes" onclick="abrirModalDetalhes(${pag.id})">🔍 Ver Mais</button>
                <button type="button" class="btn-excluir" onclick="deletarPagamento(${pag.id})">Excluir</button>
            </td>
        `;
        corpoTabela.appendChild(linha);
    });
}

// 2. Ouvinte da Lupa: Filtra dados em tempo real sem fazer novas requisições HTTP
document.getElementById('pesquisa-pagamento').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();

    const filtrados = cachePagamentos.filter(pag =>
        pag.id.toString().includes(termo) ||
        pag.pedidoId.toString().includes(termo) ||
        pag.formaPagamento.toLowerCase().includes(termo) ||
        pag.status.toLowerCase().includes(termo)
    );

    renderizarTabela(filtrados);
});

// 3. Captura e envio do formulário (POST) igual ao mapeamento do Swagger
document.getElementById('form-pagamento').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede a página de recarregar no envio

    const inputData = document.getElementById('dataPagamento').value;

    // Converte a data do HTML (local) para o formato ISO estrito esperado pelo Swagger
    const dataISO = inputData ? new Date(inputData).toISOString() : new Date().toISOString();

    // Montagem exata do JSON esperado pelas chaves do seu Swagger
    const novoPagamento = {
        valor: parseFloat(document.getElementById('valor').value),
        dataPagamento: dataISO,
        formaPagamento: document.getElementById('formaPagamento').value,
        status: document.getElementById('status').value,
        pedidoId: parseInt(document.getElementById('pedidoId').value, 10)
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoPagamento)
        });

        if (resposta.ok) {
            alert("Pagamento registrado com sucesso!");
            document.getElementById('form-pagamento').reset(); // Limpa campos
            carregarPagamentos(); // Atualiza a listagem automaticamente
        } else {
            const erroApi = await resposta.text();
            alert(`Erro ao salvar pagamento (Status ${resposta.status}):\n${erroApi}`);
        }
    } catch (erro) {
        console.error("Erro na comunicação via POST:", erro);
        alert("Erro crítico de conexão com o servidor.");
    }
});

// 4. Modal de Exibição Avançada (Ver Mais)
const modal = document.getElementById('modal-pagamento');

function abrirModalDetalhes(id) {
    const pag = cachePagamentos.find(p => p.id === id);
    if (!pag) return;

    const valorFormatado = pag.valor ? pag.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }) : 'R$ 0,00';
    const dataFormatada = pag.dataPagamento ? new Date(pag.dataPagamento).toLocaleString('pt-BR') : 'Não cadastrada';

    const container = document.getElementById('conteudo-modal-detalhes');
    container.innerHTML = `
        <div class="item-detalhe"><strong>Código da Transação (ID)</strong>#${pag.id}</div>
        <div class="item-detalhe"><strong>Código do Pedido Vinculado</strong>#${pag.pedidoId}</div>
        <div class="item-detalhe"><strong>Valor Capturado</strong>${valorFormatado}</div>
        <div class="item-detalhe"><strong>Data de Processamento</strong>${dataFormatada}</div>
        <div class="item-detalhe"><strong>Gateway / Meio de Pagamento</strong>${pag.formaPagamento}</div>
        <div class="item-detalhe"><strong>Status da Operação</strong>${pag.status}</div>
    `;

    modal.style.display = 'flex'; // Abre a overlay flutuante
}

// 5. Função de Exclusão Física (DELETE) integrada com o tratamento de erro do seu Spring Boot
async function deletarPagamento(id) {
    if (confirm(`Atenção: Tem certeza que deseja remover permanentemente o registro de pagamento #${id}?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                alert("Registro de pagamento removido com sucesso!");
                carregarPagamentos(); // Atualiza tabela
            } else {
                const erroApi = await resposta.text();
                alert(`Falha ao remover registro (Erro HTTP ${resposta.status}):\n\n${erroApi}`);
            }
        } catch (erro) {
            console.error("Erro na comunicação via DELETE:", erro);
            alert("Erro de conexão ao tentar remover.");
        }
    }
}

// ==========================================
// 2. NOVA LÓGICA: BUSCA DE PEDIDOS (MODAL)
// ==========================================
const modalBuscaPedido = document.getElementById('modal-buscar-pedido');

// Abrir o modal de pedidos e carregar os dados
document.getElementById('btn-abrir-busca-pedido').addEventListener('click', async () => {
    modalBuscaPedido.style.display = 'flex';

    // Se o cache estiver vazio, busca na API. Senão, reaproveita os dados.
    if (cachePedidos.length === 0) {
        try {
            const resposta = await fetch(URL_PEDIDOS);
            const dados = await resposta.json();

            cachePedidos = dados.content ? dados.content : dados;
            renderizarTabelaPedidos(cachePedidos);
        } catch (erro) {
            console.error("Erro ao buscar pedidos:", erro);
            document.getElementById('tabela-pedidos-modal').innerHTML = `<tr><td colspan="5" style="text-align:center;">Erro ao carregar pedidos. Verifique o servidor.</td></tr>`;
        }
    } else {
        renderizarTabelaPedidos(cachePedidos);
    }
});

// Renderizar a tabela de pedidos dentro do Modal
function renderizarTabelaPedidos(lista) {
    const tbody = document.getElementById('tabela-pedidos-modal');
    tbody.innerHTML = '';

    if (lista.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;">Nenhum pedido encontrado.</td></tr>`;
        return;
    }

    lista.forEach(p => {
        const valor = p.valorTotal ? p.valorTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }) : 'R$ 0,00';

        // Trata o status para evitar erros de digitação do banco
        const statusReal = p.status ? p.status.toUpperCase() : '';

        // Verifica se o pedido já foi pago/ativado
        const jaEstaPago = statusReal === 'ATIVO' || statusReal === 'PAGO' || statusReal === 'CONCLUIDO' || statusReal === 'APROVADO';

        // Lógica: Se já estiver pago, mostra um texto. Se não, mostra o botão verde.
        let acaoHTML = '';
        if (jaEstaPago) {
            acaoHTML = `<span style="font-size: 11px; color: #155724; background-color: #d4edda; padding: 4px 8px; border-radius: 4px; font-weight: bold;">✅ Já Pago</span>`;
        } else {
            acaoHTML = `<button type="button" class="btn-selecionar" onclick="selecionarPedido(${p.id}, ${p.valorTotal || 0})">✔ Escolher</button>`;
        }

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${p.id}</td>
            <td>${p.usuarioNome || 'Desconhecido'}</td>
            <td>${valor}</td>
            <td><span style="font-size:11px; padding:3px 6px; background:#eee; border-radius:4px; font-weight:bold;">${p.status}</span></td>
            <td style="text-align: right;">
                ${acaoHTML}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Filtro (Lupa) da tabela de pedidos no Modal
document.getElementById('pesquisa-pedido-modal').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();
    const filtrados = cachePedidos.filter(p =>
        p.id.toString().includes(termo) ||
        (p.usuarioNome && p.usuarioNome.toLowerCase().includes(termo))
    );
    renderizarTabelaPedidos(filtrados);
});

// Ação de clique: Seleciona o pedido e preenche o formulário principal
function selecionarPedido(id, valorSugerido) {
    document.getElementById('pedidoId').value = id;

    // Auto-preenche o valor do pagamento para facilitar
    if(valorSugerido > 0) {
        document.getElementById('valor').value = valorSugerido;
    }

    modalBuscaPedido.style.display = 'none'; // Fecha o modal
}

// ==========================================
// 3. CONTROLE DE FECHAMENTO DOS MODAIS
// ==========================================

// Botões "X"
document.getElementById('fechar-modal-x').addEventListener('click', () => modal.style.display = 'none');
document.getElementById('fechar-modal-pedido-x').addEventListener('click', () => modalBuscaPedido.style.display = 'none');

// Clicando no fundo escuro
window.addEventListener('click', (event) => {
    if (event.target === modal) modal.style.display = 'none';
    if (event.target === modalBuscaPedido) modalBuscaPedido.style.display = 'none';
});

// Execução inicial ao carregar a página
carregarPagamentos();
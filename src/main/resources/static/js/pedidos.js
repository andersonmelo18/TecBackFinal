// Ajuste para as rotas da sua API
const URL_API = '/pedidos';
const URL_USUARIOS = '/usuarios'; // Rota para buscar os clientes

// Variáveis globais para armazenar os dados e permitir buscas rápidas no navegador
let cachePedidos = [];
let cacheUsuarios = [];

// ==========================================
// 1. LÓGICA PRINCIPAL DOS PEDIDOS
// ==========================================

// Função para buscar (GET) e listar na tabela
async function carregarPedidos() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        // Salva os dados no cache (trata caso o Spring devolva paginação via 'content')
        cachePedidos = dados.content ? dados.content : dados;

        renderizarTabela(cachePedidos);
    } catch (erro) {
        console.error("Erro ao carregar os pedidos:", erro);
    }
}

// Função auxiliar para montar as linhas da tabela no HTML
function renderizarTabela(lista) {
    const corpoTabela = document.getElementById('tabela-pedidos');
    corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

    if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 20px; color:#666;">Nenhum pedido encontrado.</td></tr>`;
        return;
    }

    lista.forEach(pedido => {
            const linha = document.createElement('tr');

            const valorFormatado = pedido.valorTotal
                ? pedido.valorTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
                : 'R$ 0,00';

            const dataFormatada = pedido.dataPedido
                ? new Date(pedido.dataPedido).toLocaleString('pt-BR')
                : 'Data não informada';

            // ==========================================
            // NOVA LÓGICA DE CORES DOS STATUS
            // ==========================================
            let classeStatus = 'status-pendente'; // Padrão (Laranja)

            // Pega o status do banco e converte para maiúsculo para evitar erros
            const statusReal = pedido.status ? pedido.status.toUpperCase() : '';

            // Se for Pago, Ativo, Concluído ou Aprovado -> VERDE
            if (statusReal === 'PAGO' || statusReal === 'ATIVO' || statusReal === 'CONCLUIDO' || statusReal === 'APROVADO') {
                classeStatus = 'status-concluido';
            }
            // Se for Cancelado, Recusado ou Estornado -> VERMELHO
            else if (statusReal === 'CANCELADO' || statusReal === 'RECUSADO') {
                classeStatus = 'status-cancelado';
            }
            // ==========================================

            // Para forçar a exibição da palavra "PAGO" na tela quando vier "ATIVO" do banco
            // (Se quiser que continue aparecendo "ATIVO" verde, pode apagar essa linha abaixo)
            const textoStatus = (statusReal === 'ATIVO') ? 'PAGO' : statusReal;

            linha.innerHTML = `
                <td><strong>#${pedido.id}</strong></td>
                <td>#${pedido.usuarioId || 'N/A'}</td>
                <td>${pedido.usuarioNome || 'Não informado'}</td>
                <td>${dataFormatada}</td>
                <td><span class="status ${classeStatus}">${textoStatus}</span></td>
                <td>${valorFormatado}</td>
                <td>
                    <button type="button" class="btn-detalhes" onclick="abrirModalDetalhes(${pedido.id})">🔍 Ver Mais</button>
                    <button type="button" class="btn-excluir" onclick="deletarPedido(${pedido.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
}

// Ouvinte da Lupa de Pedidos (Filtro em tempo real)
document.getElementById('pesquisa-pedido').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();

    const filtrados = cachePedidos.filter(p =>
        p.id.toString().includes(termo) ||
        (p.usuarioId && p.usuarioId.toString().includes(termo)) ||
        (p.usuarioNome && p.usuarioNome.toLowerCase().includes(termo)) ||
        (p.status && p.status.toLowerCase().includes(termo))
    );

    renderizarTabela(filtrados);
});

// Função para salvar (POST) um novo pedido
document.getElementById('form-pedido').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede o recarregamento da página

    const inputData = document.getElementById('dataPedido').value;
    const dataISO = inputData ? new Date(inputData).toISOString() : new Date().toISOString();

    const novoPedido = {
        dataPedido: dataISO,
        valorTotal: parseFloat(document.getElementById('valorTotal').value),
        status: document.getElementById('status').value,
        usuarioId: parseInt(document.getElementById('usuarioId').value, 10),
        usuarioNome: document.getElementById('usuarioNome').value
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoPedido)
        });

        if (resposta.ok) {
            alert("Pedido registrado com sucesso!");
            document.getElementById('form-pedido').reset(); // Limpa os campos
            carregarPedidos(); // Recarrega a tabela e o cache
        } else {
            const erroApi = await resposta.text();
            alert(`Erro ao registrar pedido (Status ${resposta.status}).\nDetalhe: ${erroApi}`);
        }
    } catch (erro) {
        console.error("Erro ao fazer o POST:", erro);
        alert("Erro de conexão com o backend.");
    }
});

// Lógica do Modal de Detalhes do Pedido
const modalPedido = document.getElementById('modal-pedido');

function abrirModalDetalhes(id) {
    const p = cachePedidos.find(item => item.id === id);
    if (!p) return;

    const valorFormatado = p.valorTotal ? p.valorTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }) : 'R$ 0,00';
    const dataFormatada = p.dataPedido ? new Date(p.dataPedido).toLocaleString('pt-BR') : 'Data não informada';

    const container = document.getElementById('conteudo-modal-detalhes');
    container.innerHTML = `
        <div class="item-detalhe"><strong>ID do Pedido</strong>#${p.id}</div>
        <div class="item-detalhe"><strong>ID do Cliente</strong>#${p.usuarioId || 'Não registrado'}</div>
        <div class="item-detalhe"><strong>Nome do Cliente</strong>${p.usuarioNome || 'Não registrado'}</div>
        <div class="item-detalhe"><strong>Data de Registro</strong>${dataFormatada}</div>
        <div class="item-detalhe"><strong>Valor da Compra</strong>${valorFormatado}</div>
        <div class="item-detalhe"><strong>Status Atual</strong>${p.status}</div>
    `;

    modalPedido.style.display = 'flex';
}

// Função para deletar (DELETE) um pedido
async function deletarPedido(id) {
    if (confirm(`Atenção: Tem certeza que deseja cancelar/remover o pedido #${id}?\nEsta ação pode afetar itens e pagamentos vinculados.`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                alert("Pedido excluído com sucesso!");
                carregarPedidos();
            } else {
                const erroApi = await resposta.text();
                alert(`Falha ao remover o pedido (Erro ${resposta.status}).\nEle pode estar vinculado a itens ou pagamentos.\nDetalhe: ${erroApi}`);
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
            alert("Erro fatal ao tentar comunicar com o servidor para exclusão.");
        }
    }
}


// ==========================================
// 2. NOVA LÓGICA: BUSCA DE USUÁRIOS/CLIENTES
// ==========================================
const modalBuscaUsuario = document.getElementById('modal-buscar-usuario');

// Abrir o modal de clientes e carregar os dados
document.getElementById('btn-abrir-busca-usuario').addEventListener('click', async () => {
    modalBuscaUsuario.style.display = 'flex';

    // Se o cache estiver vazio, busca na API. Senão, reaproveita os dados.
    if (cacheUsuarios.length === 0) {
        try {
            const resposta = await fetch(URL_USUARIOS);
            const dados = await resposta.json();

            // Suporta retorno envelopado em paginação (content) ou array direto
            cacheUsuarios = dados.content ? dados.content : dados;
            renderizarTabelaUsuarios(cacheUsuarios);
        } catch (erro) {
            console.error("Erro ao buscar clientes:", erro);
            document.getElementById('tabela-usuarios-modal').innerHTML = `<tr><td colspan="3" style="text-align:center;">Erro ao carregar clientes. Verifique o servidor.</td></tr>`;
        }
    } else {
        renderizarTabelaUsuarios(cacheUsuarios);
    }
});

// Renderizar a tabela de clientes dentro do Modal
function renderizarTabelaUsuarios(lista) {
    const tbody = document.getElementById('tabela-usuarios-modal');
    tbody.innerHTML = '';

    if (lista.length === 0) {
        tbody.innerHTML = `<tr><td colspan="3" style="text-align:center;">Nenhum cliente encontrado.</td></tr>`;
        return;
    }

    lista.forEach(u => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${u.id}</td>
            <td><strong>${u.nome}</strong></td>
            <td style="text-align: right;">
                <button type="button" class="btn-selecionar" onclick="selecionarUsuario(${u.id}, '${u.nome}')">✔ Escolher</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Filtro (Lupa) da tabela de clientes no Modal
document.getElementById('pesquisa-usuario-modal').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();
    const filtrados = cacheUsuarios.filter(u =>
        (u.nome && u.nome.toLowerCase().includes(termo)) ||
        u.id.toString().includes(termo)
    );
    renderizarTabelaUsuarios(filtrados);
});

// Ação de clique: Seleciona o cliente e preenche o formulário principal
function selecionarUsuario(id, nome) {
    document.getElementById('usuarioId').value = id;
    document.getElementById('usuarioNome').value = nome;
    modalBuscaUsuario.style.display = 'none'; // Fecha o modal
}


// ==========================================
// 3. CONTROLE DE FECHAMENTO DOS MODAIS
// ==========================================

document.getElementById('fechar-modal-x').addEventListener('click', () => modalPedido.style.display = 'none');
document.getElementById('fechar-modal-usuario-x').addEventListener('click', () => modalBuscaUsuario.style.display = 'none');

// Fechar modais ao clicar na área escura (fundo)
window.addEventListener('click', (event) => {
    if (event.target === modalPedido) modalPedido.style.display = 'none';
    if (event.target === modalBuscaUsuario) modalBuscaUsuario.style.display = 'none';
});

// Inicializa a tabela de pedidos ao carregar a página
carregarPedidos();
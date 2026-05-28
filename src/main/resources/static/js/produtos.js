// Rota do seu backend para a entidade Produto
const URL_API = '/produtos';

// Cache local para permitir buscas rápidas na lupa sem sobrecarregar o servidor
let cacheProdutos = [];

// ==========================================
// 1. CARREGAR E RENDERIZAR A TABELA
// ==========================================

// Função para buscar (GET) os produtos cadastrados
async function carregarProdutos() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        // Extrai os dados caso o backend retorne paginação (Spring Pageable) ou a lista direta
        cacheProdutos = dados.content ? dados.content : dados;

        renderizarTabela(cacheProdutos);
    } catch (erro) {
        console.error("Erro ao buscar registros de produtos:", erro);
    }
}

// Função para desenhar as linhas da tabela no HTML
function renderizarTabela(lista) {
    const corpoTabela = document.getElementById('tabela-produtos');
    corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

    if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="5" style="text-align:center; color:#888; padding: 20px;">Nenhum produto encontrado.</td></tr>`;
        return;
    }

    lista.forEach(p => {
        const linha = document.createElement('tr');

        // Formatação de moeda
        const precoFormatado = (p.preco || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

        // Tratamento do status booleano (Ativo/Inativo)
        // Como o JSON retorna um booleano (true/false), avaliamos isso para criar a tag visual
        const isAtivo = String(p.ativo).toLowerCase() === 'true';
        const classeStatus = isAtivo ? 'status-ativo' : 'status-inativo';
        const textoStatus = isAtivo ? 'ATIVO' : 'INATIVO';

        linha.innerHTML = `
            <td><strong>#${p.id}</strong></td>
            <td>${p.nome}</td>
            <td>${precoFormatado}</td>
            <td><span class="status ${classeStatus}">${textoStatus}</span></td>
            <td>
                <button type="button" class="btn-detalhes" onclick="abrirModalDetalhes(${p.id})">🔍 Detalhes</button>
                <button type="button" class="btn-excluir" onclick="deletarProduto(${p.id})">Excluir</button>
            </td>
        `;
        corpoTabela.appendChild(linha);
    });
}

// ==========================================
// 2. BUSCA EM TEMPO REAL (LUPA)
// ==========================================
document.getElementById('pesquisa-produto').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();

    const filtrados = cacheProdutos.filter(p =>
        p.id.toString().includes(termo) ||
        (p.nome && p.nome.toLowerCase().includes(termo))
    );

    renderizarTabela(filtrados);
});

// ==========================================
// 3. SALVAR NOVO PRODUTO (POST)
// ==========================================
document.getElementById('form-produto').addEventListener('submit', async function(event) {
    event.preventDefault();

    // Captura o valor do select e converte para booleano real (true/false)
    const selectAtivo = document.getElementById('ativo').value;
    const isAtivoBooleano = (selectAtivo === 'true');

    // Monta o objeto JSON estritamente como o seu Swagger exige
    const novoProduto = {
        nome: document.getElementById('nome').value,
        descricao: document.getElementById('descricao').value,
        preco: parseFloat(document.getElementById('preco').value),
        ativo: isAtivoBooleano
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoProduto)
        });

        if (resposta.ok) {
            alert("Produto cadastrado com sucesso!");
            document.getElementById('form-produto').reset(); // Limpa o formulário
            carregarProdutos(); // Recarrega a tabela e o cache
        } else {
            const erroApi = await resposta.text();
            alert(`Erro ao cadastrar produto (Status ${resposta.status}):\n${erroApi}`);
        }
    } catch (erro) {
        console.error("Erro na requisição POST:", erro);
        alert("Erro crítico de conexão com o backend.");
    }
});

// ==========================================
// 4. MODAL DE DETALHES E EXCLUSÃO
// ==========================================
const modal = document.getElementById('modal-produto');

function abrirModalDetalhes(id) {
    const p = cacheProdutos.find(item => item.id === id);
    if (!p) return;

    const precoFormatado = (p.preco || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    const isAtivo = String(p.ativo).toLowerCase() === 'true';
    const textoStatus = isAtivo ? '🟢 Ativo para Venda' : '⚪ Inativo (Fora de catálogo)';

    const container = document.getElementById('conteudo-modal-detalhes');
    container.innerHTML = `
        <div class="item-detalhe"><strong>Código do Produto</strong>#${p.id}</div>
        <div class="item-detalhe"><strong>Nome</strong>${p.nome}</div>
        <div class="item-detalhe"><strong>Preço de Venda</strong>${precoFormatado}</div>
        <div class="item-detalhe"><strong>Status Atual</strong>${textoStatus}</div>
        <div class="item-detalhe" style="margin-top: 15px;">
            <strong>Descrição do Produto</strong>
            <p style="margin: 5px 0 0 0; font-size: 13px; color: #444; line-height: 1.5;">
                ${p.descricao || 'Nenhuma descrição fornecida.'}
            </p>
        </div>
    `;

    modal.style.display = 'flex';
}

async function deletarProduto(id) {
    if (confirm(`Atenção: Tem certeza que deseja excluir permanentemente o produto #${id} do catálogo?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                alert("Produto excluído com sucesso!");
                carregarProdutos();
            } else {
                const erroApi = await resposta.text();
                alert(`Erro ao excluir (Status ${resposta.status}):\nPode haver pedidos vinculados a este produto.\n${erroApi}`);
            }
        } catch (erro) {
            console.error("Erro na exclusão:", erro);
            alert("Erro de comunicação com o servidor.");
        }
    }
}

// ==========================================
// 5. CONTROLES DO MODAL
// ==========================================
document.getElementById('fechar-modal-x').addEventListener('click', () => {
    modal.style.display = 'none';
});

// Fechar modal clicando fora da caixa branca
window.addEventListener('click', (event) => {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
});

// Executa a busca inicial ao abrir a página
carregarProdutos();
// URL apontando para o seu CategoriaController no Spring Boot
const URL_API = '/categorias';

// Cache local para guardar os dados das categorias (importante para os modais)
let cacheCategorias = [];

// ==========================================
// 1. CARREGAR E RENDERIZAR TABELA (GET)
// ==========================================
async function carregarCategorias() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        // Armazena no cache tratando paginação do Spring (Pageable) se houver
        cacheCategorias = dados.content ? dados.content : dados;

        const corpoTabela = document.getElementById('tabela-categorias');
        corpoTabela.innerHTML = '';

        if (cacheCategorias.length === 0) {
            corpoTabela.innerHTML = `<tr><td colspan="3" style="text-align:center; color:#888; padding: 20px;">Nenhuma categoria cadastrada.</td></tr>`;
            return;
        }

        cacheCategorias.forEach(categoria => {
            const linha = document.createElement('tr');

            // Tratamento seguro de strings para evitar que aspas simples/duplas quebrem o HTML do onclick
            const descricaoTratada = (categoria.descricao || "Sem descrição informada.")
                .replace(/\\/g, '\\\\')
                .replace(/'/g, "\\'")
                .replace(/"/g, '&quot;');

            linha.innerHTML = `
                <td><strong>#${categoria.id}</strong></td>
                <td><strong>${categoria.nome}</strong></td>
                <td>
                    <button type="button" class="btn-lupa" onclick="abrirModalDescricao('${categoria.nome.replace(/'/g, "\\'")}', '${descricaoTratada}')">🔍 Detalhes</button>
                    <button type="button" class="btn-excluir" onclick="deletarCategoria(${categoria.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar categorias:", erro);
    }
}

// ==========================================
// 2. CONTROLAR O MODAL DE DETALHES
// ==========================================
const modal = document.getElementById('modal-descricao');
const btnFecharModal = document.getElementById('btn-fechar-modal');

function abrirModalDescricao(nome, descricao) {
    document.getElementById('modal-titulo-categoria').innerText = `Gênero: ${nome}`;
    document.getElementById('modal-texto-descricao').innerText = descricao;
    modal.style.display = 'flex';
}

// Eventos para fechar o modal clicando no 'X' ou fora da caixa branca
btnFecharModal.addEventListener('click', () => modal.style.display = 'none');
window.addEventListener('click', (event) => {
    if (event.target === modal) modal.style.display = 'none';
});

// ==========================================
// 3. SALVAR NOVA CATEGORIA (POST)
// ==========================================
document.getElementById('form-categoria').addEventListener('submit', async function(event) {
    event.preventDefault();

    const nomeCategoria = document.getElementById('nome').value.trim();
    const descricaoCategoria = document.getElementById('descricao').value.trim();

    const novaCategoria = {
        nome: nomeCategoria,
        descricao: descricaoCategoria
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novaCategoria)
        });

        if (resposta.ok) {
            alert("Categoria salva com sucesso!");
            document.getElementById('form-categoria').reset();
            carregarCategorias(); // Recarrega a tabela e atualiza o cache
        } else {
            const erroApi = await resposta.text();
            alert("Erro ao salvar a categoria: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao realizar o POST:", erro);
        alert("Erro de conexão com o servidor.");
    }
});

// ==========================================
// 4. DELETAR CATEGORIA (DELETE)
// ==========================================
async function deletarCategoria(id) {
    if (confirm(`Tem certeza que deseja excluir a categoria ID #${id}?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarCategorias();
            } else {
                alert("Não foi possível excluir a categoria. Verifique se existem filmes vinculados a ela no seu banco de dados.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Inicializa a listagem ao carregar o script
carregarCategorias();
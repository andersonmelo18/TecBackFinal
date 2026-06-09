// Substitua pela URL exata do seu FilmeController
const URL_API = '/filmes';
const URL_CATEGORIAS = '/categorias';

// Cache local de filmes
let cacheFilmes = [];

// ==========================================
// 1. POPULAR O SELECT DE CATEGORIAS (GET)
// ==========================================
async function popularSelectCategorias() {
    const select = document.getElementById('categoriaId');
    if (!select) return;

    try {
        const resposta = await fetch(URL_CATEGORIAS);
        const dados = await resposta.json();

        // Limpa opções antigas e deixa a instrução padrão
        select.innerHTML = '<option value="">Selecione uma Categoria...</option>';

        // Trata paginação do Spring (Pageable) caso exista no backend
        const listaCategorias = dados.content ? dados.content : dados;

        if (listaCategorias.length === 0) {
            select.innerHTML = '<option value="">Nenhuma categoria cadastrada no sistema</option>';
            return;
        }

        listaCategorias.forEach(cat => {
            const opcao = document.createElement('option');
            opcao.value = cat.id; // ID numérico enviado ao banco
            opcao.textContent = cat.nome; // Nome visível ao usuário
            select.appendChild(opcao);
        });
    } catch (erro) {
        console.error("Erro ao carregar categorias no select:", erro);
        select.innerHTML = '<option value="">Erro ao carregar categorias</option>';
    }
}

// ==========================================
// 2. BUSCAR (GET) E LISTAR NA TABELA
// ==========================================
async function carregarFilmes() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-filmes');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

        // Trata paginação do Spring (Pageable)
        cacheFilmes = dados.content ? dados.content : dados;

        if (cacheFilmes.length === 0) {
            corpoTabela.innerHTML = `<tr><td colspan="5" style="text-align:center; color:#888; padding:20px;">Nenhum filme cadastrado.</td></tr>`;
            return;
        }

        cacheFilmes.forEach(filme => {
            const linha = document.createElement('tr');

            // Pega os 4 primeiros dígitos da data (ex: "2026-05-28" vira "2026")
            const anoExibicao = filme.dataLancamento ? filme.dataLancamento.substring(0, 4) : 'Não informado';

            // Proteção contra undefined na Categoria
            let nomeCategoria = 'Sem categoria';
            if (filme.categoriaNome) {
                nomeCategoria = filme.categoriaNome;
            } else if (filme.categoria && typeof filme.categoria === 'object') {
                nomeCategoria = filme.categoria.nome;
            } else if (typeof filme.categoria === 'string') {
                nomeCategoria = filme.categoria;
            }

            linha.innerHTML = `
                <td><strong>#${filme.id}</strong></td>
                <td><strong>${filme.titulo}</strong></td>
                <td>${anoExibicao}</td>
                <td><span style="background:#eee; padding:3px 8px; border-radius:4px; font-size:12px; font-weight:bold;">${nomeCategoria}</span></td>
                <td>
                    <button class="btn-excluir" onclick="deletarFilme(${filme.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar os filmes:", erro);
    }
}

// ==========================================
// 3. SALVAR (POST) UM NOVO FILME
// ==========================================
document.getElementById('form-filme').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede a página de recarregar

    const idCategoriaSelecionada = document.getElementById('categoriaId').value;
    if (!idCategoriaSelecionada) {
        alert("Por favor, selecione uma categoria válida.");
        return;
    }

    // Coleta os valores do formulário
    const tituloFilme = document.getElementById('titulo').value.trim();
    const anoFilme = parseInt(document.getElementById('anoLancamento').value, 10);
    const sinopseFilme = document.getElementById('sinopse').value.trim();

    // ---> NOVA LINHA: Captura o valor do campo de gênero do HTML
    const generoFilme = document.getElementById('genero').value;

    // Monta o payload JSON compatível com o DTO/Entity do seu Spring Boot
    const novoFilme = {
        titulo: tituloFilme,
        sinopse: sinopseFilme,
        // Converte o ano digitado (ex: 2026) na string padrão do banco (ex: "2026-01-01")
        dataLancamento: `${document.getElementById('anoLancamento').value}-01-01`,
        categoriaId: parseInt(idCategoriaSelecionada, 10),

        // ---> NOVA LINHA: Adiciona o gênero no JSON que vai para o backend
        genero: generoFilme
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoFilme)
        });

        if (resposta.ok) {
            alert("Filme salvo no catálogo com sucesso!");
            document.getElementById('form-filme').reset(); // Limpa o formulário
            carregarFilmes(); // Recarrega a tabela imediatamente
        } else {
            const erroApi = await resposta.text();
            alert("Erro ao cadastrar o filme: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao fazer o POST:", erro);
        alert("Erro de conexão com o backend.");
    }
});

// ==========================================
// 4. DELETAR (DELETE) UM FILME
// ==========================================
async function deletarFilme(id) {
    if (confirm(`Tem certeza que deseja remover o filme ID #${id} do catálogo?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarFilmes(); // Atualiza a tabela
            } else {
                alert("Falha ao remover o filme. Verifique se ele não faz parte dos favoritos de algum usuário no banco de dados.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// ==========================================
// 5. INICIALIZAÇÃO CONTROLADA DA TELA
// ==========================================
async function inicializarTela() {
    await popularSelectCategorias(); // Primeiro carrega o select de categorias
    await carregarFilmes();            // Depois carrega a lista de filmes
}

// Dispara a inicialização ao carregar a página
inicializarTela();
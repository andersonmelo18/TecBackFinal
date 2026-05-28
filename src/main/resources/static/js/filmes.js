// Substitua pela URL exata do seu FilmeController
const URL_API = '/filmes';

// 1. Função para buscar (GET) e listar na tabela
async function carregarFilmes() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-filmes');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

        dados.forEach(filme => {
            const linha = document.createElement('tr');

            linha.innerHTML = `
                <td>${filme.id}</td>
                <td><strong>${filme.titulo}</strong></td>
                <td>${filme.anoLancamento}</td>
                <td><span class="badge">${filme.categoria}</span></td>
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

// 2. Função para salvar (POST) um novo filme
document.getElementById('form-filme').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede a página de recarregar

    // Coleta os valores do formulário
    const tituloFilme = document.getElementById('titulo').value;
    const anoFilme = parseInt(document.getElementById('anoLancamento').value);
    const categoriaFilme = document.getElementById('categoria').value;
    const sinopseFilme = document.getElementById('sinopse').value;

    // Monta o JSON (Atenção: as chaves aqui devem ter o mesmo nome dos atributos da sua classe Filme.java)
    const novoFilme = {
        titulo: tituloFilme,
        anoLancamento: anoFilme,
        categoria: categoriaFilme,
        sinopse: sinopseFilme
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
            document.getElementById('form-filme').reset(); // Limpa o form
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

// 3. Função para deletar (DELETE) um filme
async function deletarFilme(id) {
    if (confirm(`Tem certeza que deseja remover o filme ID ${id} do catálogo?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarFilmes(); // Atualiza a tabela
            } else {
                alert("Falha ao remover o filme. Verifique se ele não faz parte dos favoritos de algum usuário.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Carrega a tabela assim que o arquivo JS é lido pelo navegador
carregarFilmes();
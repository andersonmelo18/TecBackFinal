// Apontando para o controller que já existe no seu Java
const URL_API = '/favoritos';

// 1. Função para buscar (GET) e listar na tabela
async function carregarFavoritos() {
    try {
        const resposta = await fetch(URL_API);

        if (!resposta.ok) {
            throw new Error(`Erro HTTP: ${resposta.status}`);
        }

        const dados = await resposta.json();
        const corpoTabela = document.getElementById('tabela-favoritos');
        corpoTabela.innerHTML = ''; // Limpa a tabela

        dados.forEach(fav => {
            const linha = document.createElement('tr');

            // Adaptação para ler os dados independente do formato do seu DTO
            const idUsuario = fav.usuarioId || (fav.usuario ? fav.usuario.id : 'N/A');
            const idFilme = fav.filmeId || (fav.filme ? fav.filme.id : 'N/A');
            const vinculoId = fav.id || ' - ';

            linha.innerHTML = `
                <td>#${vinculoId}</td>
                <td>Usuário: <strong>${idUsuario}</strong></td>
                <td><span class="badge-filme">Filme: ${idFilme}</span></td>
                <td>
                    <button class="btn-excluir" onclick="deletarFavorito(${idUsuario}, ${idFilme})">Remover</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar os favoritos:", erro);
    }
}

// 2. Função para salvar (POST) um novo favorito
document.getElementById('form-favorito').addEventListener('submit', async function(event) {
    event.preventDefault();

    const inputUsuario = parseInt(document.getElementById('usuarioId').value);
    const inputFilme = parseInt(document.getElementById('filmeId').value);

    try {
        // CORREÇÃO: Enviando os dados na URL (?usuarioId=X&filmeId=Y) sem BODY, combinando com o @RequestParam do seu Java
        const resposta = await fetch(`${URL_API}?usuarioId=${inputUsuario}&filmeId=${inputFilme}`, {
            method: 'POST'
        });

        if (resposta.ok) {
            alert("Filme adicionado aos favoritos com sucesso!");
            document.getElementById('form-favorito').reset();
            carregarFavoritos(); // Atualiza a tabela
        } else {
            const erroApi = await resposta.text();
            alert("Erro ao adicionar favorito: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao fazer o POST:", erro);
        alert("Erro de conexão com o backend.");
    }
});

// 3. Função para deletar (DELETE) um favorito
async function deletarFavorito(usuarioId, filmeId) {
    if (confirm(`Tem certeza que deseja remover este filme dos favoritos?`)) {
        try {
            // CORREÇÃO: Enviando via URL conforme o @RequestParam do seu método remover no Java
            const resposta = await fetch(`${URL_API}?usuarioId=${usuarioId}&filmeId=${filmeId}`, {
                method: 'DELETE'
            });

            if (resposta.ok) {
                alert("Favorito removido com sucesso!");
                carregarFavoritos(); // Atualiza a tabela
            } else {
                const erroApi = await resposta.text();
                alert("Falha ao remover o favorito: " + erroApi);
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Inicia o carregamento da tela
carregarFavoritos();
// Ajuste para a rota exata do seu UsuarioController
const URL_API = '/usuarios';

// 1. Função para buscar (GET) e listar na tabela
async function carregarUsuarios() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-usuarios');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

        dados.forEach(usuario => {
            const linha = document.createElement('tr');

            linha.innerHTML = `
                <td>${usuario.id}</td>
                <td><strong>${usuario.nome}</strong> <span class="badge-cliente">Cliente</span></td>
                <td>${usuario.email}</td>
                <td>${usuario.telefone || 'Não informado'}</td>
                <td>
                    <button class="btn-excluir" onclick="deletarUsuario(${usuario.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar os usuários:", erro);
    }
}

// 2. Função para salvar (POST) um novo usuário
document.getElementById('form-usuario').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede o recarregamento da página

    // Coleta os valores do formulário
    const nomeUsuario = document.getElementById('nome').value;
    const emailUsuario = document.getElementById('email').value;
    const telefoneUsuario = document.getElementById('telefone').value;
    const senhaUsuario = document.getElementById('senha').value;

    // Monta o JSON (Atenção: as chaves devem ter os mesmos nomes da sua classe Usuario.java)
    const novoUsuario = {
        nome: nomeUsuario,
        email: emailUsuario,
        telefone: telefoneUsuario,
        senha: senhaUsuario
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoUsuario)
        });

        if (resposta.ok) {
            alert("Usuário cadastrado com sucesso!");
            document.getElementById('form-usuario').reset(); // Limpa o formulário
            carregarUsuarios(); // Recarrega a tabela imediatamente
        } else {
            const erroApi = await resposta.text();
            alert("Erro ao cadastrar o usuário: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao fazer o POST:", erro);
        alert("Erro de conexão com o backend.");
    }
});

// 3. Função para deletar (DELETE) um usuário
async function deletarUsuario(id) {
    if (confirm(`Tem certeza que deseja excluir o usuário ID ${id}? Isso pode apagar os pedidos e favoritos dele.`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarUsuarios(); // Atualiza a tabela
            } else {
                alert("Falha ao remover o usuário. Verifique se ele possui pedidos atrelados no banco de dados.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Carrega a tabela assim que o arquivo JS é lido pelo navegador
carregarUsuarios();
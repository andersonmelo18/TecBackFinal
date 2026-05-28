// URL apontando para o seu CategoriaController no Spring Boot
const URL_API = '/categorias';

// 1. Função para buscar (GET) e listar na tabela
async function carregarCategorias() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-categorias');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de renderizar os novos dados

        dados.forEach(categoria => {
            const linha = document.createElement('tr');

            linha.innerHTML = `
                <td>${categoria.id}</td>
                <td><strong>${categoria.nome}</strong></td>
                <td>${categoria.descricao}</td>
                <td>
                    <button class="btn-excluir" onclick="deletarCategoria(${categoria.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar categorias:", erro);
    }
}

// 2. Função para salvar (POST) uma nova categoria
document.getElementById('form-categoria').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede o recarregamento padrão do formulário

    // Coleta os valores digitados no HTML
    const nomeCategoria = document.getElementById('nome').value;
    const descricaoCategoria = document.getElementById('descricao').value;

    // Cria o objeto JSON de acordo com sua entidade Categoria no Java
    const novaCategoria = {
        nome: nomeCategoria,
        descricao: descricaoCategoria
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novaCategoria)
        });

        if (resposta.ok) {
            alert("Categoria salva com sucesso!");
            document.getElementById('form-categoria').reset(); // Limpa os campos do formulário
            carregarCategorias(); // Recarrega a tabela imediatamente
        } else {
            // Caso falhe alguma validação do backend (@NotBlank, etc)
            const erroApi = await resposta.text();
            alert("Erro ao salvar a categoria: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao realizar o POST:", erro);
        alert("Erro de conexão com o servidor.");
    }
});

// 3. Função para deletar (DELETE) uma categoria
async function deletarCategoria(id) {
    if (confirm(`Tem certeza que deseja excluir a categoria ID ${id}?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarCategorias(); // Atualiza a tabela se a exclusão for bem sucedida
            } else {
                alert("Não foi possível excluir a categoria. Verifique se existem filmes vinculados a ela.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Inicializa a tabela assim que a tela abre
carregarCategorias();
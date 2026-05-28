// A URL deve apontar para o Controller correspondente no seu Spring Boot
const URL_API = '/assinaturas';

// 1. Função para buscar (GET) e listar na tabela
async function carregarAssinaturas() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-assinaturas');
        corpoTabela.innerHTML = ''; // Limpa antes de renderizar

        dados.forEach(plano => {
            const linha = document.createElement('tr');

            // Formata o preço para o padrão brasileiro (R$)
            const precoFormatado = plano.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

            linha.innerHTML = `
                <td>${plano.id}</td>
                <td><strong>${plano.nome}</strong></td>
                <td>${plano.descricao}</td>
                <td>${precoFormatado}</td>
                <td>
                    <button class="btn-excluir" onclick="deletarAssinatura(${plano.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar assinaturas:", erro);
        // Não exibe alert aqui para não travar a tela caso a API esteja vazia
    }
}

// 2. Função para salvar (POST) um novo plano
document.getElementById('form-assinatura').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede o recarregamento da página

    // Coleta os valores digitados
    const nomePlano = document.getElementById('nome').value;
    const precoPlano = parseFloat(document.getElementById('preco').value);
    const descricaoPlano = document.getElementById('descricao').value;

    // Cria o objeto JSON que o Spring Boot espera receber
    const novoPlano = {
        nome: nomePlano,
        preco: precoPlano,
        descricao: descricaoPlano
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoPlano)
        });

        if (resposta.ok) {
            alert("Plano salvo com sucesso!");
            document.getElementById('form-assinatura').reset(); // Limpa o formulário
            carregarAssinaturas(); // Recarrega a tabela com o novo dado
        } else {
            // Se o Spring Boot barrar (ex: validação do @NotBlank), pegamos o erro
            const erroApi = await resposta.text();
            alert("Erro ao salvar o plano: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao realizar o POST:", erro);
        alert("Erro de conexão com o servidor.");
    }
});

// 3. Função para deletar (DELETE) um plano
async function deletarAssinatura(id) {
    if (confirm(`Tem certeza que deseja excluir o plano ID ${id}?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarAssinaturas(); // Atualiza a tabela se deu certo
            } else {
                alert("Não foi possível excluir o plano. Ele pode estar em uso por algum cliente.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Carrega a tabela automaticamente quando a página é aberta
carregarAssinaturas();
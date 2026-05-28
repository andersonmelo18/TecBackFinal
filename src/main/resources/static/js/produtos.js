const URL_API = '/produtos';

// 1. Buscar do banco (GET) - Corrigido para ler Paginação do Spring Boot
async function carregarProdutos() {
    try {
        const resposta = await fetch(URL_API);
        if (!resposta.ok) throw new Error("Erro ao buscar produtos");

        const resultadoAgrupado = await resposta.json();

        // CORREÇÃO: Verifica se a API mandou os dados dentro de "content" (paginado) ou como lista pura
        const produtos = resultadoAgrupado.content ? resultadoAgrupado.content : resultadoAgrupado;

        const corpoTabela = document.getElementById('tabela-produtos');
        corpoTabela.innerHTML = ''; // Limpa a tabela

        if (produtos.length === 0) {
            corpoTabela.innerHTML = `<tr><td colspan="5" style="text-align:center; color:#888;">Nenhum produto cadastrado.</td></tr>`;
            return;
        }

        produtos.forEach(prod => {
            const linha = document.createElement('tr');
            linha.innerHTML = `
                <td>#${prod.id}</td>
                <td><strong>${prod.nome}</strong></td>
                <td>${prod.descricao || 'Sem descrição'}</td>
                <td><span class="preco-tag">R$ ${parseFloat(prod.preco).toFixed(2)}</span></td>
                <td>
                    <button class="btn-excluir" onclick="deletarProduto(${prod.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro na listagem de produtos:", erro);
    }
}

// 2. Cadastrar no banco (POST)
document.getElementById('form-produto').addEventListener('submit', async function(event) {
    event.preventDefault();

    const novoProduto = {
        nome: document.getElementById('nome').value,
        preco: parseFloat(document.getElementById('preco').value),
        descricao: document.getElementById('descricao').value
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoProduto)
        });

        if (resposta.ok) {
            alert("Produto cadastrado com sucesso!");
            document.getElementById('form-produto').reset();
            carregarProdutos();
        } else {
            alert("Erro ao cadastrar produto.");
        }
    } catch (erro) {
        console.error("Erro no cadastro:", erro);
    }
});

// 3. Deletar do banco (DELETE)
async function deletarProduto(id) {
    if (confirm(`Deseja realmente excluir o produto #${id}?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });
            if (resposta.ok) {
                carregarProdutos();
            } else {
                alert("Erro ao deletar produto.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Inicializa a tela carregando os dados
carregarProdutos();
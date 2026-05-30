const URL_API = '/planos';

async function carregarPlanos() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();
        const corpoTabela = document.getElementById('tabela-planos');
        corpoTabela.innerHTML = '';

        if (dados.length === 0) {
            corpoTabela.innerHTML = `<tr><td colspan="6" style="text-align:center;color:#888;padding:20px;">Nenhum plano cadastrado.</td></tr>`;
            return;
        }

        dados.forEach(plano => {
            const linha = document.createElement('tr');
            const preco = Number(plano.preco).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
            const statusCor = plano.ativo ? '#28a745' : '#6c757d';
            const statusLabel = plano.ativo ? 'Ativo' : 'Inativo';

            linha.innerHTML = `
                <td><strong>#${plano.id}</strong></td>
                <td><strong>${plano.nome}</strong></td>
                <td>${plano.descricao || '—'}</td>
                <td>${preco}</td>
                <td><span style="background:${statusCor};color:#fff;padding:3px 8px;border-radius:4px;font-size:12px;">${statusLabel}</span></td>
                <td>
                    ${plano.ativo ? `<button onclick="desativarPlano(${plano.id})"
                        style="background:#ffc107;color:#333;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;font-size:12px;margin-right:4px;">
                        Desativar
                    </button>` : ''}
                    <button class="btn-excluir" onclick="excluirPlano(${plano.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar planos:", erro);
    }
}

document.getElementById('form-plano').addEventListener('submit', async function(event) {
    event.preventDefault();

    const novoPlano = {
        nome: document.getElementById('nome').value.trim(),
        preco: parseFloat(document.getElementById('preco').value),
        descricao: document.getElementById('descricao').value.trim()
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoPlano)
        });

        if (resposta.ok) {
            alert("Plano cadastrado com sucesso!");
            document.getElementById('form-plano').reset();
            carregarPlanos();
        } else {
            const erro = await resposta.text();
            alert("Erro ao salvar plano: " + erro);
        }
    } catch (erro) {
        console.error("Erro ao salvar:", erro);
    }
});

async function desativarPlano(id) {
    if (confirm(`Desativar o plano ID ${id}? Ele não poderá mais ser contratado por novos usuários.`)) {
        const resposta = await fetch(`${URL_API}/${id}/desativar`, { method: 'PATCH' });
        if (resposta.ok) carregarPlanos();
        else alert("Erro ao desativar o plano.");
    }
}

async function excluirPlano(id) {
    if (confirm(`Excluir definitivamente o plano ID ${id}?`)) {
        const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });
        if (resposta.ok) carregarPlanos();
        else alert("Não foi possível excluir. O plano pode estar vinculado a assinaturas ativas.");
    }
}

carregarPlanos();
const URL_API = '/assinaturas';
const URL_USUARIOS = '/usuarios';
const URL_PLANOS = '/planos/ativos';

async function popularSelects() {
    // Carrega usuários
    try {
        const resUsuarios = await fetch(URL_USUARIOS);
        const dados = await resUsuarios.json();
        const usuarios = dados.content ? dados.content : dados;
        const selectUsuario = document.getElementById('usuarioId');
        selectUsuario.innerHTML = '<option value="">Selecione um Cliente...</option>';
        usuarios.forEach(u => {
            const op = document.createElement('option');
            op.value = u.id;
            op.textContent = `#${u.id} — ${u.nome}`;
            selectUsuario.appendChild(op);
        });
    } catch (e) {
        console.error("Erro ao carregar usuários:", e);
    }

    // Carrega planos ativos
    try {
        const resPlanos = await fetch(URL_PLANOS);
        const planos = await resPlanos.json();
        const selectPlano = document.getElementById('planoId');
        selectPlano.innerHTML = '<option value="">Selecione um Plano...</option>';
        planos.forEach(p => {
            const preco = Number(p.preco).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
            const op = document.createElement('option');
            op.value = p.id;
            op.textContent = `${p.nome} — ${preco}`;
            // Guarda os dados do plano no option para preencher o resumo
            op.dataset.preco = p.preco;
            op.dataset.descricao = p.descricao || '';
            selectPlano.appendChild(op);
        });
    } catch (e) {
        console.error("Erro ao carregar planos:", e);
    }
}

// Ao escolher um plano, mostra o resumo do que será contratado
document.getElementById('planoId').addEventListener('change', function() {
    const opcao = this.options[this.selectedIndex];
    const resumo = document.getElementById('resumo-plano');
    if (this.value && opcao.dataset.preco) {
        const preco = Number(opcao.dataset.preco).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        resumo.innerHTML = `
            <div style="background:#f0fff4;border:1px solid #28a745;padding:10px;border-radius:6px;margin-top:8px;font-size:13px;">
                ✅ <strong>${opcao.textContent}</strong><br>
                ${opcao.dataset.descricao ? `📋 ${opcao.dataset.descricao}<br>` : ''}
                💰 Valor mensal: <strong>${preco}</strong>
            </div>`;
    } else {
        resumo.innerHTML = '';
    }
});

async function carregarAssinaturas() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();
        const lista = dados.content ? dados.content : dados;
        const corpoTabela = document.getElementById('tabela-assinaturas');
        corpoTabela.innerHTML = '';

        if (lista.length === 0) {
            corpoTabela.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#888;padding:20px;">Nenhuma assinatura encontrada.</td></tr>`;
            return;
        }

        lista.forEach(a => {
            const linha = document.createElement('tr');
            const preco = Number(a.precoContratado).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
            const corStatus = a.status === 'ATIVA' ? '#28a745' : a.status === 'CANCELADA' ? '#dc3545' : '#ffc107';

            linha.innerHTML = `
                <td>${a.id}</td>
                <td>${a.usuarioNome || '—'}</td>
                <td><strong>${a.planoNome || '—'}</strong></td>
                <td>${a.planoDescricao || '—'}</td>
                <td>${preco}</td>
                <td><span style="background:${corStatus};color:#fff;padding:3px 8px;border-radius:4px;font-size:12px;">${a.status}</span></td>
                <td>
                    ${a.status === 'ATIVA' ? `<button onclick="cancelarAssinatura(${a.id})"
                        style="background:#ffc107;color:#333;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;font-size:12px;margin-right:4px;">
                        Cancelar
                    </button>` : ''}
                    <button class="btn-excluir" onclick="deletarAssinatura(${a.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar assinaturas:", erro);
    }
}

document.getElementById('form-assinatura').addEventListener('submit', async function(event) {
    event.preventDefault();

    const usuarioId = document.getElementById('usuarioId').value;
    const planoId = document.getElementById('planoId').value;

    if (!usuarioId) { alert("Selecione um cliente."); return; }
    if (!planoId) { alert("Selecione um plano."); return; }

    const payload = {
        usuarioId: parseInt(usuarioId, 10),
        planoId: parseInt(planoId, 10)
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (resposta.ok) {
            alert("Plano assinado com sucesso!");
            document.getElementById('form-assinatura').reset();
            document.getElementById('resumo-plano').innerHTML = '';
            carregarAssinaturas();
        } else {
            const erro = await resposta.text();
            alert("Erro ao contratar plano: " + erro);
        }
    } catch (erro) {
        console.error("Erro ao salvar:", erro);
    }
});

async function cancelarAssinatura(id) {
    if (confirm(`Cancelar a assinatura ID ${id}?`)) {
        const resposta = await fetch(`${URL_API}/${id}/cancelar`, { method: 'PATCH' });
        if (resposta.ok) carregarAssinaturas();
        else alert("Erro ao cancelar.");
    }
}

async function deletarAssinatura(id) {
    if (confirm(`Excluir definitivamente a assinatura ID ${id}?`)) {
        const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });
        if (resposta.ok) carregarAssinaturas();
        else alert("Não foi possível excluir.");
    }
}

async function inicializar() {
    await popularSelects();
    await carregarAssinaturas();
}

inicializar();
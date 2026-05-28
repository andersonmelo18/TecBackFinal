// Ajuste para a rota exata do seu PedidoController
const URL_API = '/pedidos';

// 1. Função para buscar (GET) e listar na tabela
async function carregarPedidos() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-pedidos');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

        dados.forEach(pedido => {
            const linha = document.createElement('tr');

            // Formata o valor total para Real Brasileiro (R$)
            const valorFormatado = pedido.valorTotal
                ? pedido.valorTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
                : 'R$ 0,00';

            // Define a cor da badge do status
            let classeStatus = 'status-pendente';
            if (pedido.status === 'CONCLUIDO') classeStatus = 'status-concluido';
            if (pedido.status === 'CANCELADO') classeStatus = 'status-cancelado';

            // Extrai o ID do usuário (trata se vier como um objeto aninhado ou número direto)
            const idDoCliente = pedido.usuario ? pedido.usuario.id : pedido.usuarioId;

            linha.innerHTML = `
                <td><strong>#${pedido.id}</strong></td>
                <td>Cliente ${idDoCliente || 'N/A'}</td>
                <td><span class="status ${classeStatus}">${pedido.status}</span></td>
                <td>${valorFormatado}</td>
                <td>
                    <button class="btn-excluir" onclick="deletarPedido(${pedido.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar os pedidos:", erro);
    }
}

// 2. Função para salvar (POST) um novo pedido
document.getElementById('form-pedido').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede o recarregamento

    const inputUsuarioId = parseInt(document.getElementById('usuarioId').value);
    const inputStatus = document.getElementById('status').value;
    const inputValorTotal = parseFloat(document.getElementById('valorTotal').value);

    /* * MONTAGEM DO JSON:
     * Se o seu Spring Boot espera receber apenas o ID direto, use: { usuarioId: inputUsuarioId, ... }
     * Se ele espera um objeto de Usuário para fazer o vínculo no banco, use a estrutura abaixo.
     * Como o uso de objetos é o padrão do Spring Boot/JPA, deixei assim:
     */
    const novoPedido = {
        usuario: { id: inputUsuarioId }, // Vínculo com a tabela de Usuários
        status: inputStatus,
        valorTotal: inputValorTotal
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoPedido)
        });

        if (resposta.ok) {
            alert("Pedido registrado com sucesso!");
            document.getElementById('form-pedido').reset();
            carregarPedidos(); // Recarrega a tabela
        } else {
            const erroApi = await resposta.text();
            alert("Erro ao registrar pedido. Verifique se o ID do Usuário existe.\nDetalhe: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao fazer o POST:", erro);
        alert("Erro de conexão com o backend.");
    }
});

// 3. Função para deletar (DELETE) um pedido
async function deletarPedido(id) {
    if (confirm(`Atenção: Tem certeza que deseja cancelar/remover o pedido #${id}?`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarPedidos(); // Atualiza a tabela
            } else {
                alert("Falha ao remover o pedido. Ele pode estar vinculado a itens ou pagamentos.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Inicializa a tabela
carregarPedidos();
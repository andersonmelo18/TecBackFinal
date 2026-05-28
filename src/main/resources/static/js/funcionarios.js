// Ajuste para a rota exata do seu FuncionarioController
const URL_API = '/funcionarios';

// 1. Função para buscar (GET) e listar na tabela
async function carregarFuncionarios() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        const corpoTabela = document.getElementById('tabela-funcionarios');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

        dados.forEach(func => {
            const linha = document.createElement('tr');

            // Formata o salário para Real Brasileiro (R$)
            const salarioFormatado = func.salario.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

            linha.innerHTML = `
                <td>${func.id}</td>
                <td><strong>${func.nome}</strong></td>
                <td>${func.email}</td>
                <td><span class="badge-cargo">${func.cargo}</span></td>
                <td>${salarioFormatado}</td>
                <td>
                    <button class="btn-excluir" onclick="deletarFuncionario(${func.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar os funcionários:", erro);
    }
}

// 2. Função para salvar (POST) um novo funcionário
document.getElementById('form-funcionario').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede a página de recarregar

    // Monta o JSON capturando os valores EXATOS da tela neste momento, INCLUINDO A SENHA
    const novoFuncionario = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        cargo: document.getElementById('cargo').value,
        salario: parseFloat(document.getElementById('salario').value),
        senha: document.getElementById('senha').value // <--- CAMPO DE SENHA ADICIONADO AQUI
    };

    try {
        const resposta = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoFuncionario)
        });

        if (resposta.ok) {
            alert("Funcionário cadastrado com sucesso!");
            document.getElementById('form-funcionario').reset(); // Limpa o formulário
            carregarFuncionarios(); // Recarrega a tabela imediatamente
        } else {
            const erroApi = await resposta.text();
            alert("Erro ao cadastrar funcionário: " + erroApi);
        }
    } catch (erro) {
        console.error("Erro ao fazer o POST:", erro);
        alert("Erro de conexão com o backend.");
    }
});

// 3. Função para deletar (DELETE) um funcionário
async function deletarFuncionario(id) {
    if (confirm(`Tem certeza que deseja remover o funcionário ID ${id}? O acesso dele será revogado.`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                carregarFuncionarios(); // Atualiza a tabela
            } else {
                alert("Falha ao remover o funcionário. Verifique se ele possui dependências no sistema.");
            }
        } catch (erro) {
            console.error("Erro ao deletar:", erro);
        }
    }
}

// Carrega a tabela assim que o arquivo JS é lido
carregarFuncionarios();
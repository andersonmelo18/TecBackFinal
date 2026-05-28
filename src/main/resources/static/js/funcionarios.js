// Ajuste para a rota exata do seu FuncionarioController
const URL_API = '/funcionarios';
let cacheFuncionarios = []; // Guarda a lista completa para alimentar a janelinha (modal) e o filtro da lupa

// 1. Função para buscar (GET) e listar na tabela
async function carregarFuncionarios() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        // Tratamento caso o backend use paginação ou envie uma lista pura
        const funcionarios = dados.content ? dados.content : dados;
        cacheFuncionarios = funcionarios; // Atualiza o cache local com os dados novos do backend

        renderizarTabela(cacheFuncionarios);
    } catch (erro) {
        console.error("Erro ao carregar os funcionários:", erro);
    }
}

// Função auxiliar para renderizar as linhas da tabela (usada também pela lupa)
function renderizarTabela(lista) {
    const corpoTabela = document.getElementById('tabela-funcionarios');
    corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

    if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#777;">Nenhum funcionário encontrado.</td></tr>`;
        return;
    }

    lista.forEach(func => {
        const linha = document.createElement('tr');

        // Mantida a sua formatação original de salário para Real Brasileiro (R$)
        // Adicionada uma proteção caso o salário venha nulo/vazio do banco
        const salarioFormatado = func.salario
            ? func.salario.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
            : 'Não informado';

        linha.innerHTML = `
            <td>#${func.id}</td>
            <td><strong>${func.nome}</strong></td>
            <td>${func.email}</td>
            <td><span class="badge-cargo">${func.cargo}</span></td>
            <td>${salarioFormatado}</td>
            <td>
                <button class="btn-detalhes" onclick="abrirModalDetalhes(${func.id})">🔍 Ver Mais</button>
                <button class="btn-excluir" onclick="deletarFuncionario(${func.id})">Excluir</button>
            </td>
        `;
        corpoTabela.appendChild(linha);
    });
}

// LUPA DE PESQUISA: Ouvinte do campo de pesquisa para filtrar os funcionários em tempo real
document.getElementById('pesquisa-funcionario').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();

    // Filtra no cache local por nome, e-mail ou cargo
    const filtrados = cacheFuncionarios.filter(func =>
        func.nome.toLowerCase().includes(termo) ||
        func.email.toLowerCase().includes(termo) ||
        func.cargo.toLowerCase().includes(termo)
    );

    renderizarTabela(filtrados); // Remonta a tabela apenas com os correspondentes
});

// VIACEP: Integração automática ao tirar o foco (blur) do campo CEP
document.getElementById('cep').addEventListener('blur', async function() {
    const cep = this.value.replace(/\D/g, ''); // Remove traços ou espaços, deixando apenas números

    if (cep.length === 8) {
        try {
            const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);

            // CORREÇÃO: A linha abaixo estava com um erro de digitação
            const dadosCep = await resposta.json();

            if (!dadosCep.erro) {
                // Preenche automaticamente os novos inputs de endereço do seu HTML
                document.getElementById('logradouro').value = dadosCep.logradouro;
                document.getElementById('bairro').value = dadosCep.bairro;
                document.getElementById('localidade').value = dadosCep.localidade;
                document.getElementById('uf').value = dadosCep.uf;
            } else {
                alert("CEP não localizado na base do ViaCEP.");
            }
        } catch (erro) {
            console.error("Erro de conexão com o ViaCEP:", erro);
        }
    }
});

// 2. Função para salvar (POST) um novo funcionário (Adaptado com os campos do Swagger)
document.getElementById('form-funcionario').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede a página de recarregar

    const campoSalario = document.getElementById('salario').value;

    // Monta o JSON capturando as chaves EXATAS que o seu Swagger espera
    const novoFuncionario = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        senha: document.getElementById('senha').value,
        cargo: document.getElementById('cargo').value,
        salario: campoSalario ? parseFloat(campoSalario) : null,
        cep: document.getElementById('cep').value,
        logradouro: document.getElementById('logradouro').value,
        bairro: document.getElementById('bairro').value,
        localidade: document.getElementById('localidade').value,
        uf: document.getElementById('uf').value
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

// JANELA MODAL FLUTUANTE: Controle de abertura, montagem dos dados e fechamento dinâmico
const modal = document.getElementById('modal-funcionario');

function abrirModalDetalhes(id) {
    // Busca os dados do funcionário alvo de forma instantânea dentro do nosso cache
    const func = cacheFuncionarios.find(f => f.id === id);
    if (!func) return;

    const salarioFormatado = func.salario
        ? func.salario.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
        : 'Não informado';

    const container = document.getElementById('conteudo-modal-detalhes');
    container.innerHTML = `
        <div class="item-detalhe"><strong>ID do Registro</strong>#${func.id}</div>
        <div class="item-detalhe"><strong>Nome Completo</strong>${func.nome}</div>
        <div class="item-detalhe"><strong>Cargo / Função</strong>${func.cargo}</div>
        <div class="item-detalhe"><strong>E-mail Corporativo</strong>${func.email}</div>
        <div class="item-detalhe"><strong>Salário Base</strong>${salarioFormatado}</div>
        <div class="item-detalhe"><strong>Código Postal (CEP)</strong>${func.cep || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Logradouro / Rua</strong>${func.logradouro || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Bairro</strong>${func.bairro || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Cidade</strong>${func.localidade || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Estado (UF)</strong>${func.uf || '--'}</div>
    `;

    modal.style.display = 'flex'; // Torna a overlay visível em modo flex
}

// Fecha o modal ao clicar no botão "X" da janela
document.getElementById('fechar-modal-x').addEventListener('click', () => {
    modal.style.display = 'none';
});

// FECHAR AO CLICAR FORA: Fecha o modal se o clique do usuário for no fundo escuro (fora da caixa branca)
window.addEventListener('click', function(event) {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
});

// 3. Função para deletar (DELETE) um funcionário
async function deletarFuncionario(id) {
    if (confirm(`Tem certeza que deseja remover o funcionário ID ${id}? O acesso dele será revogado.`)) {
        try {
            const resposta = await fetch(`${URL_API}/${id}`, { method: 'DELETE' });

            if (resposta.ok) {
                alert("Funcionário excluído com sucesso!");
                carregarFuncionarios(); // Atualiza a tabela
            } else {
                // Aqui está o truque: vamos ler o erro exato que o seu backend (Java) está mandando!
                const erroApi = await resposta.text();
                alert(`Falha ao remover o funcionário (Erro HTTP ${resposta.status}):\n\n${erroApi}`);
                console.error("Erro do backend no DELETE:", erroApi);
            }
        } catch (erro) {
            console.error("Erro fatal ao deletar:", erro);
            alert("Erro de conexão com o backend. O servidor está rodando?");
        }
    }
}

// Carrega a tabela assim que o arquivo JS é lido pelo navegador
carregarFuncionarios();
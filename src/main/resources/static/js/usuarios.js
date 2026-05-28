// Ajuste para a rota exata do seu UsuarioController
const URL_API = '/usuarios';
let cacheUsuarios = []; // Guarda a lista completa para alimentar a janelinha (modal)

// 1. Função para buscar (GET) e listar na tabela
async function carregarUsuarios() {
    try {
        const resposta = await fetch(URL_API);
        const dados = await resposta.json();

        // Tratamento caso o backend use paginação (evita tabela vazia)
        const usuarios = dados.content ? dados.content : dados;
        cacheUsuarios = usuarios; // Atualiza o cache local com os dados novos

        const corpoTabela = document.getElementById('tabela-usuarios');
        corpoTabela.innerHTML = ''; // Limpa a tabela antes de preencher

        usuarios.forEach(usuario => {
            const linha = document.createElement('tr');

            linha.innerHTML = `
                <td>#${usuario.id}</td>
                <td><strong>${usuario.nome}</strong> <span class="badge-cliente">Cliente</span></td>
                <td>${usuario.email}</td>
                <td>${usuario.cpf}</td>
                <td>
                    <span class="badge-status ${usuario.ativo ? 'status-ativo' : 'status-inativo'}">
                        ${usuario.ativo ? 'Ativo' : 'Inativo'}
                    </span>
                </td>
                <td>
                    <button class="btn-detalhes" onclick="abrirModalDetalhes(${usuario.id})">🔍 Ver Mais</button>
                    <button class="btn-excluir" onclick="deletarUsuario(${usuario.id})">Excluir</button>
                </td>
            `;
            corpoTabela.appendChild(linha);
        });
    } catch (erro) {
        console.error("Erro ao carregar os usuários:", erro);
    }
}

// GATILHO: Integração automática com o ViaCEP ao tirar o foco (blur) do campo CEP
document.getElementById('cep').addEventListener('blur', async function() {
    const cep = this.value.replace(/\D/g, ''); // Remove qualquer caractere que não seja número

    if (cep.length === 8) {
        try {
            const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const dadosCep = await resposta.json();

            if (!dadosCep.erro) {
                // Preenche os campos do formulário automaticamente com o retorno do ViaCEP
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

// 2. Função para salvar (POST) um novo usuário (Mapeado com o Swagger)
document.getElementById('form-usuario').addEventListener('submit', async function(event) {
    event.preventDefault(); // Impede o recarregamento da página

    // Coleta todos os valores do formulário exatamente como o Swagger espera
    const novoUsuario = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        senha: document.getElementById('senha').value,
        cpf: document.getElementById('cpf').value,
        dataNascimento: document.getElementById('dataNascimento').value,
        ativo: true, // Entra como ativo por padrão no sistema
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

// LÓGICA DO MODAL: Controle de abertura, renderização dinâmica e fechamento
const modal = document.getElementById('modal-usuario');

function abrirModalDetalhes(id) {
    // Busca os dados do usuário direto da nossa lista em cache
    const usuario = cacheUsuarios.find(u => u.id === id);
    if (!usuario) return;

    const container = document.getElementById('conteudo-modal-detalhes');
    container.innerHTML = `
        <div class="item-detalhe"><strong>ID do Registro</strong>#${usuario.id}</div>
        <div class="item-detalhe"><strong>Nome Completo</strong>${usuario.nome}</div>
        <div class="item-detalhe"><strong>Documento CPF</strong>${usuario.cpf}</div>
        <div class="item-detalhe"><strong>Data de Nascimento</strong>${usuario.dataNascimento}</div>
        <div class="item-detalhe"><strong>E-mail de Acesso</strong>${usuario.email}</div>
        <div class="item-detalhe"><strong>Código Postal (CEP)</strong>${usuario.cep || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Logradouro / Rua</strong>${usuario.logradouro || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Bairro</strong>${usuario.bairro || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Cidade</strong>${usuario.localidade || 'Não cadastrado'}</div>
        <div class="item-detalhe"><strong>Estado (UF)</strong>${usuario.uf || '--'}</div>
    `;

    modal.style.display = 'flex'; // Torna o modal visível na tela
}

// Fecha o modal ao clicar no botão "X"
document.getElementById('fechar-modal-x').addEventListener('click', () => {
    modal.style.display = 'none';
});

// FECHAR AO CLICAR FORA: Fecha a janelinha ao clicar na parte escura (fundo do modal)
window.addEventListener('click', function(event) {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
});

// 3. Função para escolher e deletar (DELETE) um usuário
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

// --- COLE ESTE BLOCO NO FINAL DO ARQUIVO (Antes de carregarUsuarios();) ---

// Ouvinte do campo de pesquisa para filtrar em tempo real
document.getElementById('pesquisa-usuario').addEventListener('input', function() {
    const termo = this.value.toLowerCase().trim();
    const corpoTabela = document.getElementById('tabela-usuarios');
    corpoTabela.innerHTML = ''; // Limpa os resultados atuais

    // Filtra a lista que já está salva na memória (cacheUsuarios)
    const filtrados = cacheUsuarios.filter(usuario =>
        usuario.nome.toLowerCase().includes(termo) ||
        usuario.email.toLowerCase().includes(termo) ||
        usuario.cpf.includes(termo)
    );

    // Se não encontrar ninguém no filtro
    if (filtrados.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#777;">Nenhum usuário encontrado para a busca.</td></tr>`;
        return;
    }

    // Remonta as linhas da tabela apenas com os usuários filtrados
    filtrados.forEach(usuario => {
        const linha = document.createElement('tr');
        linha.innerHTML = `
            <td>#${usuario.id}</td>
            <td><strong>${usuario.nome}</strong> <span class="badge-cliente">Cliente</span></td>
            <td>${usuario.email}</td>
            <td>${usuario.cpf}</td>
            <td>
                <span class="badge-status ${usuario.ativo ? 'status-ativo' : 'status-inativo'}">
                    ${usuario.ativo ? 'Ativo' : 'Inativo'}
                </span>
            </td>
            <td>
                <button class="btn-detalhes" onclick="abrirModalDetalhes(${usuario.id})">🔍 Ver Mais</button>
                <button class="btn-excluir" onclick="deletarUsuario(${usuario.id})">Excluir</button>
            </td>
        `;
        corpoTabela.appendChild(linha);
    });
});

// ----------------=======================================----------------

// Carrega a tabela assim que o arquivo JS é lido pelo navegador
carregarUsuarios();
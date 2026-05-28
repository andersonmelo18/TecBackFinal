package br.uniesp.si.techback.service;

import br.uniesp.si.techback.client.ViaCepClient;
import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.dto.ViaCepResponseDTO;
import br.uniesp.si.techback.exception.CustomBeanException;
import br.uniesp.si.techback.mapper.FuncionarioMapper;
import br.uniesp.si.techback.model.Funcionario;
import br.uniesp.si.techback.repository.FuncionarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ViaCepClient viaCepClient;
    private final FuncionarioMapper funcionarioMapper;

    public List<FuncionarioDTO> listar() {
        log.info("Buscando todos os funcionários administrativos");
        return funcionarioRepository.findAll().stream()
                .map(funcionarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FuncionarioDTO incluir(FuncionarioDTO funcionarioDTO) {
        log.info("Iniciando a inclusão de um novo funcionário: {}", funcionarioDTO.getNome());

        Funcionario funcionario = funcionarioMapper.toEntity(funcionarioDTO);

        if (funcionario.getCep() != null && !funcionario.getCep().isBlank()) {
            log.debug("Buscando dados de endereço no ViaCEP para o CEP: {}", funcionario.getCep());
            String cepLimpo = funcionario.getCep().replaceAll("\\D", "");
            ViaCepResponseDTO endereco = viaCepClient.buscarPorCep(cepLimpo);

            if (Boolean.TRUE.equals(endereco.getErro())) {
                log.error("CEP inválido retornado pela API externa: {}", cepLimpo);
                throw new CustomBeanException("CEP invalido para consulta no ViaCEP");
            }

            funcionario.setCep(endereco.getCep());
            funcionario.setLogradouro(endereco.getLogradouro());
            funcionario.setBairro(endereco.getBairro());
            funcionario.setLocalidade(endereco.getLocalidade());
            funcionario.setUf(endereco.getUf());
            log.debug("Endereço preenchido com sucesso para a localidade: {}", endereco.getLocalidade());
        }

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        log.info("Funcionário salvo com sucesso! ID gerado: {}", funcionarioSalvo.getId());

        return funcionarioMapper.toDTO(funcionarioSalvo);
    }

    // =========================================================================
    // NOVO MÉTODO: Adicionado para executar a exclusão física no Banco de Dados
    // =========================================================================
    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando a exclusão do funcionário com ID: {}", id);

        // Verifica antes se o ID realmente existe para evitar erros genéricos
        if (!funcionarioRepository.existsById(id)) {
            log.error("Tentativa de exclusão falhou. Funcionário ID {} não localizado.", id);
            throw new CustomBeanException("Funcionário não encontrado no sistema.");
        }

        funcionarioRepository.deleteById(id);
        log.info("Funcionário com ID {} removido com sucesso do banco de dados.", id);
    }
}
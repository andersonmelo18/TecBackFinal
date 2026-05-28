package br.uniesp.si.techback.service;

import br.uniesp.si.techback.client.ViaCepClient;
import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.exception.CustomBeanException;
import br.uniesp.si.techback.mapper.UsuarioMapper;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final ViaCepClient viaCepClient;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listar() {
        log.info("Buscando todos os usuários cadastrados no IespFlix");
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            List<UsuarioDTO> usuariosDTO = usuarios.stream()
                    .map(usuarioMapper::toDTO)
                    .collect(Collectors.toList());
            log.debug("Total de usuários encontrados: {}", usuariosDTO.size());
            return usuariosDTO;
        } catch (Exception e) {
            log.error("Falha ao buscar usuários: {}", e.getMessage(), e);
            throw e;
        }
    }

    public UsuarioDTO buscarPorId(Long id) {
        log.info("Buscando usuário pelo ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    String mensagem = String.format("Usuário não encontrado com o ID: %d", id);
                    log.warn(mensagem);
                    return new CustomBeanException(mensagem);
                });
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO salvar(UsuarioDTO usuarioDTO) {
        log.info("Cadastrando novo usuário: {}", usuarioDTO.getEmail());

        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            log.warn("Tentativa de cadastro com e-mail já existente: {}", usuarioDTO.getEmail());
            throw new CustomBeanException("Este e-mail já está sendo utilizado por outro usuário.");
        }

        if (usuarioDTO.getCep() != null && !usuarioDTO.getCep().isBlank()) {
            log.info("Buscando endereço via CEP: {}", usuarioDTO.getCep());
            try {
                var cepResponse = viaCepClient.buscarPorCep(usuarioDTO.getCep());

                if (cepResponse != null && Boolean.TRUE.equals(cepResponse.getErro())) {
                    log.warn("O CEP {} não existe na base de dados do ViaCEP.", usuarioDTO.getCep());
                    throw new CustomBeanException("O CEP informado não existe.");
                }

                if (cepResponse != null) {
                    usuarioDTO.setLogradouro(cepResponse.getLogradouro());
                    usuarioDTO.setBairro(cepResponse.getBairro());
                    usuarioDTO.setLocalidade(cepResponse.getLocalidade());
                    usuarioDTO.setUf(cepResponse.getUf());
                }
            } catch (CustomBeanException e) {
                throw e;
            } catch (Exception e) {
                log.error("Falha de conexão com o ViaCEP. O cadastro continuará apenas com o CEP digitado. Erro: {}", e.getMessage());
            }
        }

        try {
            Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
            usuario.setAtivo(true);

            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            log.info("Usuário cadastrado com sucesso. ID: {}", usuarioSalvo.getId());
            UsuarioDTO retorno = usuarioMapper.toDTO(usuarioSalvo);
            retorno.setSenha(null);
            return retorno;
        } catch (Exception e) {
            log.error("Falha ao salvar usuário: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO usuarioDTO) {
        log.info("Atualizando dados do usuário ID: {}", id);

        if (usuarioDTO.getCep() != null && !usuarioDTO.getCep().isBlank()) {
            log.info("Verificando/Atualizando endereço via CEP: {}", usuarioDTO.getCep());
            try {
                var cepResponse = viaCepClient.buscarPorCep(usuarioDTO.getCep());

                if (cepResponse != null && Boolean.TRUE.equals(cepResponse.getErro())) {
                    log.warn("Tentativa de atualização com CEP inexistente: {}", usuarioDTO.getCep());
                    throw new CustomBeanException("O CEP informado não existe.");
                }

                if (cepResponse != null) {
                    usuarioDTO.setLogradouro(cepResponse.getLogradouro());
                    usuarioDTO.setBairro(cepResponse.getBairro());
                    usuarioDTO.setLocalidade(cepResponse.getLocalidade());
                    usuarioDTO.setUf(cepResponse.getUf());
                }
            } catch (CustomBeanException e) {
                throw e;
            } catch (Exception e) {
                log.error("Falha de conexão com o ViaCEP na atualização. Erro: {}", e.getMessage());
            }
        }

        Usuario usuarioAtualizado = usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioDTO.setId(id);
                    Usuario usuarioParaAtualizar = usuarioMapper.toEntity(usuarioDTO);
                    usuarioParaAtualizar.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
                    Usuario usuarioSalvo = usuarioRepository.save(usuarioParaAtualizar);
                    log.info("Usuário ID: {} updated com sucesso.", id);
                    return usuarioSalvo;
                })
                .orElseThrow(() -> {
                    String mensagem = String.format("Falha ao atualizar: usuário não encontrado com o ID: %d", id);
                    log.warn(mensagem);
                    return new CustomBeanException(mensagem);
                });
        return usuarioMapper.toDTO(usuarioAtualizado);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo usuário ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            String mensagem = String.format("Falha ao excluir: usuário não encontrado com o ID: %d", id);
            log.warn(mensagem);
            throw new CustomBeanException(mensagem);
        }
        try {
            usuarioRepository.deleteById(id);
            log.info("Usuário ID: {} excluído com sucesso do sistema", id);
        } catch (Exception e) {
            log.error("Erro ao excluir usuário ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
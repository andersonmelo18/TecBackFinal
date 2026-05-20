package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // --- BUSCA POR DADOS ÚNICOS ---

    // Utilizado para login e para garantir que o e-mail seja único
    Optional<Usuario> findByEmail(String email);

    // Adicionado para suportar sua validação de CPF (essencial se o login puder ser por CPF)
    Optional<Usuario> findByCpf(String cpf);


    // --- VERIFICAÇÃO DE EXISTÊNCIA (MAIS RÁPIDO) ---

    // Performance: Retorna apenas true/false.
    // Melhor que buscar o objeto inteiro quando você só quer saber se o email já existe.
    boolean existsByEmail(String email);

    // Performance: Verifica se o CPF já está cadastrado
    boolean existsByCpf(String cpf);
}
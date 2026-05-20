package br.uniesp.si.techback.dto;

import br.uniesp.si.techback.validation.SenhaForte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @SenhaForte // <--- NOSSO CUSTOM VALIDATOR CRIADO DO ZERO!
    private String senha;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido. Certifique-se de digitar um CPF real com dígitos válidos") // <--- REQUISITO DO PROFESSOR!
    private String cpf;

    private LocalDate dataNascimento;
    private Boolean ativo;

    // --- NOVOS CAMPOS PARA INTEGRAÇÃO COM VIACEP ---
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
}
package br.uniesp.si.techback.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SenhaForteValidator.class) // Aponta para a classe que vai validar de verdade
@Target({ ElementType.FIELD }) // Diz que essa anotação vai em cima de campos/atributos
@Retention(RetentionPolicy.RUNTIME) // Fica ativa enquanto o sistema roda
public @interface SenhaForte {

    // Mensagem de erro que vai aparecer se a senha falhar na regra
    String message() default "A senha deve conter pelo menos 8 caracteres, uma letra maiúscula, uma letra minúscula e um número.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
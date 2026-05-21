package br.uniesp.si.techback.model;

import br.uniesp.si.techback.validation.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max; // Importante
import jakarta.validation.constraints.Min; // Importante
import jakarta.validation.constraints.PastOrPresent; // Importante
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "filmes")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @PastOrPresent(message = "A data de lançamento não pode ser no futuro")
    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    @Column(length = 50)
    @Genero
    private String genero;

    @Min(value = 1, message = "A duração deve ser de pelo menos 1 minuto")
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Min(value = 0, message = "A nota deve ser no mínimo 0")
    @Max(value = 5, message = "A nota deve ser no máximo 5")
    @Column(name = "relevancia")
    private Integer relevancia;

    @Column(name = "classificacao_indicativa", length = 10)
    private String classificacaoIndicativa;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
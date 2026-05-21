package br.uniesp.si.techback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @Column(name = "adicionado_em", nullable = false)
    private LocalDateTime adicionadoEm;

    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = {"usuario_id", "filme_id"})
    })

    @PrePersist
    public void prePersist() {
        this.adicionadoEm = LocalDateTime.now();
    }
}
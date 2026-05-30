package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.FilmeDTO;
import br.uniesp.si.techback.model.Categoria;
import br.uniesp.si.techback.model.Filme;
import org.springframework.stereotype.Component;

@Component
public class FilmeMapper {

    public Filme toEntity(FilmeDTO dto) {
        if (dto == null) return null;

        Filme filme = new Filme();
        filme.setId(dto.getId());
        filme.setTitulo(dto.getTitulo());
        filme.setSinopse(dto.getSinopse());
        filme.setDataLancamento(dto.getDataLancamento());
        filme.setGenero(dto.getGenero());
        filme.setDuracaoMinutos(dto.getDuracaoMinutos());
        filme.setClassificacaoIndicativa(dto.getClassificacaoIndicativa());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoriaId());
            filme.setCategoria(categoria);
        }

        return filme;
    }

    public FilmeDTO toDTO(Filme filme) {
        if (filme == null) return null;

        FilmeDTO dto = new FilmeDTO();
        dto.setId(filme.getId());
        dto.setTitulo(filme.getTitulo());
        dto.setSinopse(filme.getSinopse());
        dto.setDataLancamento(filme.getDataLancamento());
        dto.setGenero(filme.getGenero());
        dto.setDuracaoMinutos(filme.getDuracaoMinutos());
        dto.setClassificacaoIndicativa(filme.getClassificacaoIndicativa());

        if (filme.getCategoria() != null) {
            dto.setCategoriaId(filme.getCategoria().getId());
            dto.setCategoriaNome(filme.getCategoria().getNome());
        }

        return dto;
    }
}
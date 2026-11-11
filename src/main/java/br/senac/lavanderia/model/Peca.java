package br.senac.lavanderia.model;

import java.math.BigDecimal;

/**
 * Tipo de peca lavada, com preco unitario e categoria.
 */
public class Peca {

    private Integer id;
    private Integer categoriaId;
    private String categoriaNome;
    private String nome;
    private BigDecimal preco = BigDecimal.ZERO;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return nome == null ? "" : nome;
    }
}

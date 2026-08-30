package br.senac.lavanderia.model;

import java.math.BigDecimal;

public class Peca {

    private Integer id;
    private String nome;
    private BigDecimal preco = BigDecimal.ZERO;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

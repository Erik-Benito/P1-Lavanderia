package br.senac.lavanderia.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Ticket {

    private Integer id;
    private Integer lojaId;
    private String lojaNome;
    private Integer pecaId;
    private String pecaNome;
    private String numero;
    private LocalDate dataTicket = LocalDate.now();
    private int quantidade = 1;
    private String descricao;
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLojaId() {
        return lojaId;
    }

    public void setLojaId(Integer lojaId) {
        this.lojaId = lojaId;
    }

    public String getLojaNome() {
        return lojaNome;
    }

    public void setLojaNome(String lojaNome) {
        this.lojaNome = lojaNome;
    }

    public Integer getPecaId() {
        return pecaId;
    }

    public void setPecaId(Integer pecaId) {
        this.pecaId = pecaId;
    }

    public String getPecaNome() {
        return pecaNome;
    }

    public void setPecaNome(String pecaNome) {
        this.pecaNome = pecaNome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDataTicket() {
        return dataTicket;
    }

    public void setDataTicket(LocalDate dataTicket) {
        this.dataTicket = dataTicket;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        String codigo = id == null ? "Novo" : "#" + id;
        String loja = lojaNome == null ? "" : lojaNome;
        return codigo + " - " + loja;
    }
}

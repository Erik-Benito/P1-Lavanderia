package br.senac.lavanderia.dao;

import br.senac.lavanderia.model.Ticket;
import br.senac.lavanderia.util.Conexao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a tabela ticket, ja trazendo os nomes da loja, peca e motorista.
 */
public class TicketDAO {

    private static final String COLUNAS = "SELECT t.id, t.loja_id, l.nome AS loja_nome, "
            + "t.peca_id, p.nome AS peca_nome, t.motorista_id, m.nome AS motorista_nome, "
            + "t.numero, t.data_ticket, t.quantidade, t.descricao, t.valor_total "
            + "FROM ticket t "
            + "INNER JOIN loja l ON l.id = t.loja_id "
            + "INNER JOIN peca p ON p.id = t.peca_id "
            + "LEFT JOIN motorista m ON m.id = t.motorista_id ";

    public List<Ticket> listar() throws SQLException {
        String sql = COLUNAS + "ORDER BY t.data_ticket DESC, t.id DESC";
        List<Ticket> lista = new ArrayList<>();
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public List<Ticket> listarPorData(LocalDate data) throws SQLException {
        String sql = COLUNAS + "WHERE t.data_ticket = ? ORDER BY t.id DESC";
        List<Ticket> lista = new ArrayList<>();
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(data));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public LocalDate ultimaDataComTicket() throws SQLException {
        String sql = "SELECT MAX(data_ticket) FROM ticket";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            Date data = rs.getDate(1);
            return data == null ? null : data.toLocalDate();
        }
    }

    public Ticket buscarPorId(int id) throws SQLException {
        String sql = COLUNAS + "WHERE t.id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return montar(rs);
        }
    }

    public void salvar(Ticket ticket) throws SQLException {
        if (ticket.getId() == null) {
            inserir(ticket);
        } else {
            atualizar(ticket);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM ticket WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void inserir(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO ticket (loja_id, peca_id, motorista_id, numero, data_ticket, quantidade, descricao, valor_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, ticket);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ticket.setId(rs.getInt(1));
            }
        }
    }

    private void atualizar(Ticket ticket) throws SQLException {
        String sql = "UPDATE ticket SET loja_id = ?, peca_id = ?, motorista_id = ?, numero = ?, data_ticket = ?, quantidade = ?, descricao = ?, valor_total = ? "
                + "WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            preencher(ps, ticket);
            ps.setInt(9, ticket.getId());
            ps.executeUpdate();
        }
    }

    private void preencher(PreparedStatement ps, Ticket ticket) throws SQLException {
        ps.setInt(1, ticket.getLojaId());
        ps.setInt(2, ticket.getPecaId());
        ps.setObject(3, ticket.getMotoristaId());
        ps.setString(4, ticket.getNumero());
        ps.setDate(5, Date.valueOf(ticket.getDataTicket()));
        ps.setInt(6, ticket.getQuantidade());
        ps.setString(7, ticket.getDescricao());
        ps.setBigDecimal(8, ticket.getValorTotal());
    }

    private Ticket montar(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getInt("id"));
        t.setLojaId(rs.getInt("loja_id"));
        t.setLojaNome(rs.getString("loja_nome"));
        t.setPecaId(rs.getInt("peca_id"));
        t.setPecaNome(rs.getString("peca_nome"));
        int motoristaId = rs.getInt("motorista_id");
        t.setMotoristaId(rs.wasNull() ? null : motoristaId);
        t.setMotoristaNome(rs.getString("motorista_nome"));
        t.setNumero(rs.getString("numero"));
        t.setDataTicket(rs.getDate("data_ticket").toLocalDate());
        t.setQuantidade(rs.getInt("quantidade"));
        t.setDescricao(rs.getString("descricao"));
        java.math.BigDecimal valor = rs.getBigDecimal("valor_total");
        t.setValorTotal(valor == null ? java.math.BigDecimal.ZERO : valor);
        return t;
    }
}

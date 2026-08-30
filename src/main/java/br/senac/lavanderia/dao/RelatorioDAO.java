package br.senac.lavanderia.dao;

import br.senac.lavanderia.model.Peca;
import br.senac.lavanderia.model.Ticket;
import br.senac.lavanderia.util.Conexao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatorioDAO {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final PecaDAO pecaDAO = new PecaDAO();

    public List<Ticket> ticketsDoDia(LocalDate data) throws SQLException {
        return ticketDAO.listarPorData(data);
    }

    public BigDecimal totalDoDia(LocalDate data) throws SQLException {
        String sql = "SELECT IFNULL(SUM(valor_total), 0) FROM ticket WHERE data_ticket = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(data));
            ResultSet rs = ps.executeQuery();
            rs.next();
            BigDecimal total = rs.getBigDecimal(1);
            return total == null ? BigDecimal.ZERO : total;
        }
    }

    public int quantidadeDoDia(LocalDate data) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ticket WHERE data_ticket = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(data));
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public Map<String, Integer> pecasDoDia(LocalDate data) throws SQLException {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        List<Peca> pecas = pecaDAO.listar();
        for (int i = 0; i < pecas.size(); i++) {
            mapa.put(pecas.get(i).getNome(), 0);
        }
        String sql = "SELECT p.nome, IFNULL(SUM(t.quantidade), 0) AS qtd "
                + "FROM ticket t "
                + "INNER JOIN peca p ON p.id = t.peca_id "
                + "WHERE t.data_ticket = ? "
                + "GROUP BY p.nome";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(data));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mapa.put(rs.getString("nome"), rs.getInt("qtd"));
            }
        }
        return mapa;
    }
}

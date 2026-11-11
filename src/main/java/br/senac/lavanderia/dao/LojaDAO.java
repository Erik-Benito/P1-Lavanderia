package br.senac.lavanderia.dao;

import br.senac.lavanderia.model.Loja;
import br.senac.lavanderia.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a tabela loja: listar, buscar, salvar e excluir.
 */
public class LojaDAO {

    public List<Loja> listar() throws SQLException {
        String sql = "SELECT id, nome, telefone FROM loja ORDER BY nome";
        List<Loja> lista = new ArrayList<>();
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Loja buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, telefone FROM loja WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return montar(rs);
            }
            return null;
        }
    }

    public void salvar(Loja loja) throws SQLException {
        if (loja.getId() == null) {
            inserir(loja);
        } else {
            atualizar(loja);
        }
    }

    private void inserir(Loja loja) throws SQLException {
        String sql = "INSERT INTO loja (nome, telefone) VALUES (?, ?)";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, loja.getNome());
            ps.setString(2, loja.getTelefone());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                loja.setId(rs.getInt(1));
            }
        }
    }

    private void atualizar(Loja loja) throws SQLException {
        String sql = "UPDATE loja SET nome = ?, telefone = ? WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, loja.getNome());
            ps.setString(2, loja.getTelefone());
            ps.setInt(3, loja.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection cn = Conexao.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM ticket WHERE loja_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM loja WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    private Loja montar(ResultSet rs) throws SQLException {
        Loja loja = new Loja();
        loja.setId(rs.getInt("id"));
        loja.setNome(rs.getString("nome"));
        loja.setTelefone(rs.getString("telefone"));
        return loja;
    }
}

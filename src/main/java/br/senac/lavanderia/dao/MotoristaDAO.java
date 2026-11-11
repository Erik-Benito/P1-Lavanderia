package br.senac.lavanderia.dao;

import br.senac.lavanderia.model.Motorista;
import br.senac.lavanderia.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a tabela motorista: listar, buscar, salvar e excluir.
 */
public class MotoristaDAO {

    public List<Motorista> listar() throws SQLException {
        String sql = "SELECT id, nome, telefone FROM motorista ORDER BY nome";
        List<Motorista> lista = new ArrayList<>();
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Motorista buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, telefone FROM motorista WHERE id = ?";
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

    public void salvar(Motorista motorista) throws SQLException {
        if (motorista.getId() == null) {
            inserir(motorista);
        } else {
            atualizar(motorista);
        }
    }

    private void inserir(Motorista motorista) throws SQLException {
        String sql = "INSERT INTO motorista (nome, telefone) VALUES (?, ?)";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, motorista.getNome());
            ps.setString(2, motorista.getTelefone());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                motorista.setId(rs.getInt(1));
            }
        }
    }

    private void atualizar(Motorista motorista) throws SQLException {
        String sql = "UPDATE motorista SET nome = ?, telefone = ? WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, motorista.getNome());
            ps.setString(2, motorista.getTelefone());
            ps.setInt(3, motorista.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection cn = Conexao.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement("UPDATE ticket SET motorista_id = NULL WHERE motorista_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM motorista WHERE id = ?")) {
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

    private Motorista montar(ResultSet rs) throws SQLException {
        Motorista motorista = new Motorista();
        motorista.setId(rs.getInt("id"));
        motorista.setNome(rs.getString("nome"));
        motorista.setTelefone(rs.getString("telefone"));
        return motorista;
    }
}

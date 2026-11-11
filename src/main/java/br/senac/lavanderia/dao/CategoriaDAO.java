package br.senac.lavanderia.dao;

import br.senac.lavanderia.model.Categoria;
import br.senac.lavanderia.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() throws SQLException {
        String sql = "SELECT id, nome FROM categoria ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome FROM categoria WHERE id = ?";
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

    public void salvar(Categoria categoria) throws SQLException {
        if (categoria.getId() == null) {
            inserir(categoria);
        } else {
            atualizar(categoria);
        }
    }

    private void inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nome) VALUES (?)";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNome());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                categoria.setId(rs.getInt(1));
            }
        }
    }

    private void atualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nome = ? WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setInt(2, categoria.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection cn = Conexao.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement("UPDATE peca SET categoria_id = NULL WHERE categoria_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM categoria WHERE id = ?")) {
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

    private Categoria montar(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNome(rs.getString("nome"));
        return categoria;
    }
}

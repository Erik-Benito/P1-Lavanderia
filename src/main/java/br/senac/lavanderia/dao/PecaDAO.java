package br.senac.lavanderia.dao;

import br.senac.lavanderia.model.Peca;
import br.senac.lavanderia.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PecaDAO {

    public List<Peca> listar() throws SQLException {
        String sql = "SELECT p.id, p.nome, p.preco, p.categoria_id, c.nome AS categoria_nome "
                + "FROM peca p LEFT JOIN categoria c ON c.id = p.categoria_id ORDER BY p.nome";
        List<Peca> lista = new ArrayList<>();
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Peca buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.nome, p.preco, p.categoria_id, c.nome AS categoria_nome "
                + "FROM peca p LEFT JOIN categoria c ON c.id = p.categoria_id WHERE p.id = ?";
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

    public void salvar(Peca peca) throws SQLException {
        if (peca.getId() == null) {
            inserir(peca);
        } else {
            atualizar(peca);
        }
    }

    private void inserir(Peca peca) throws SQLException {
        String sql = "INSERT INTO peca (nome, preco, categoria_id) VALUES (?, ?, ?)";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, peca.getNome());
            ps.setBigDecimal(2, peca.getPreco());
            ps.setObject(3, peca.getCategoriaId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                peca.setId(rs.getInt(1));
            }
        }
    }

    private void atualizar(Peca peca) throws SQLException {
        String sql = "UPDATE peca SET nome = ?, preco = ?, categoria_id = ? WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, peca.getNome());
            ps.setBigDecimal(2, peca.getPreco());
            ps.setObject(3, peca.getCategoriaId());
            ps.setInt(4, peca.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM peca WHERE id = ?";
        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Peca montar(ResultSet rs) throws SQLException {
        Peca peca = new Peca();
        peca.setId(rs.getInt("id"));
        peca.setNome(rs.getString("nome"));
        int categoriaId = rs.getInt("categoria_id");
        peca.setCategoriaId(rs.wasNull() ? null : categoriaId);
        peca.setCategoriaNome(rs.getString("categoria_nome"));
        java.math.BigDecimal preco = rs.getBigDecimal("preco");
        peca.setPreco(preco == null ? java.math.BigDecimal.ZERO : preco);
        return peca;
    }
}

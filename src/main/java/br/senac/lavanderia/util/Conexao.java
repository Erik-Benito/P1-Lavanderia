package br.senac.lavanderia.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    private static final String URL = "jdbc:mysql://localhost:3306/lavanderia_roger"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testar() throws SQLException {
        try (Connection cn = getConnection(); Statement st = cn.createStatement()) {
            st.execute("SELECT 1");
        }
    }
}

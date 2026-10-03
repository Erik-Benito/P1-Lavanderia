package br.senac.lavanderia.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class BancoInicial {

    private BancoInicial() {
    }

    public static void preparar(Connection cn) throws SQLException {
        criarTabelas(cn);
        if (vazio(cn, "loja")) {
            popular(cn);
        }
    }

    private static void criarTabelas(Connection cn) throws SQLException {
        try (Statement st = cn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS categoria ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nome VARCHAR(80) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS motorista ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nome VARCHAR(120) NOT NULL,"
                    + "telefone VARCHAR(20))");
            st.execute("CREATE TABLE IF NOT EXISTS loja ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nome VARCHAR(120) NOT NULL,"
                    + "telefone VARCHAR(20))");
            st.execute("CREATE TABLE IF NOT EXISTS peca ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nome VARCHAR(80) NOT NULL,"
                    + "preco DECIMAL(10,2) NOT NULL DEFAULT 0.00,"
                    + "categoria_id INT NULL,"
                    + "CONSTRAINT fk_peca_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id))");
            st.execute("CREATE TABLE IF NOT EXISTS ticket ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "loja_id INT NOT NULL,"
                    + "peca_id INT NOT NULL,"
                    + "motorista_id INT NULL,"
                    + "numero VARCHAR(40),"
                    + "data_ticket DATE NOT NULL,"
                    + "quantidade INT NOT NULL DEFAULT 1,"
                    + "descricao VARCHAR(255),"
                    + "valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,"
                    + "CONSTRAINT fk_ticket_loja FOREIGN KEY (loja_id) REFERENCES loja(id),"
                    + "CONSTRAINT fk_ticket_peca FOREIGN KEY (peca_id) REFERENCES peca(id),"
                    + "CONSTRAINT fk_ticket_motorista FOREIGN KEY (motorista_id) REFERENCES motorista(id))");
            garantirColuna(cn, st, "peca", "categoria_id",
                    "ALTER TABLE peca ADD COLUMN categoria_id INT NULL");
            garantirColuna(cn, st, "ticket", "motorista_id",
                    "ALTER TABLE ticket ADD COLUMN motorista_id INT NULL");
        }
    }

    private static void garantirColuna(Connection cn, Statement st, String tabela, String coluna, String alter)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tabela);
            ps.setString(2, coluna);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                st.execute(alter);
            }
        }
    }

    private static boolean vazio(Connection cn, String tabela) throws SQLException {
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static void popular(Connection cn) throws SQLException {
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("INSERT INTO categoria (id, nome) VALUES "
                    + "(1,'Cama'),(2,'Banho'),(3,'Decoracao')");
            st.executeUpdate("INSERT INTO motorista (id, nome, telefone) VALUES "
                    + "(1,'Carlos Mendes','(11) 98811-2200'),"
                    + "(2,'Paulo Henrique','(11) 97722-3344'),"
                    + "(3,'Jose da Silva','(11) 96633-4455'),"
                    + "(4,'Rafael Souza','(11) 95544-5566'),"
                    + "(5,'Marcos Oliveira','(11) 94455-6677')");
            st.executeUpdate("INSERT INTO loja (id, nome, telefone) VALUES "
                    + "(1,'Hotel Plaza Center','(11) 3344-2100'),"
                    + "(2,'Pousada Beira Rio','(11) 98765-4321'),"
                    + "(3,'Motel Estrela','(11) 3222-8899'),"
                    + "(4,'Hospital Santa Clara','(11) 3456-1000'),"
                    + "(5,'Clinica Ortho Vida','(11) 3999-2211'),"
                    + "(6,'Hotel Executivo Paulista','(11) 3100-5500'),"
                    + "(7,'Spa Harmonia','(11) 98888-1010'),"
                    + "(8,'Restaurante Sabor Mineiro','(11) 3555-7070')");
            st.executeUpdate("INSERT INTO peca (id, nome, preco, categoria_id) VALUES "
                    + "(1,'Edredom',45.00,1),(2,'Cortina',30.00,3),(3,'Lencol',18.00,1),"
                    + "(4,'Toalha de banho',8.00,2),(5,'Toalha de rosto',5.00,2),(6,'Fronha',6.00,1),"
                    + "(7,'Colcha',35.00,1),(8,'Roupao',22.00,2),(9,'Manta',28.00,1),(10,'Toalha de mesa',15.00,3)");
        }

        String insert = "INSERT INTO ticket (loja_id, peca_id, motorista_id, numero, data_ticket, quantidade, descricao, valor_total) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        LocalDate hoje = LocalDate.now();
        Object[][] tickets = {
                {1, 3, 1, "T-1840", hoje.minusDays(13), 40, "Troca semanal", bd(720)},
                {1, 4, 1, "T-1841", hoje.minusDays(13), 60, "Toalhas do 2 e 3 andar", bd(480)},
                {4, 3, 3, "T-1844", hoje.minusDays(10), 80, "Enxoval da internacao", bd(1440)},
                {6, 1, 2, "T-1905", hoje.minusDays(7), 6, "Suites presidenciais", bd(270)},
                {8, 10, 4, "T-1923", hoje.minusDays(5), 16, "Jantar a la carte", bd(240)},
                {1, 3, 1, "T-D01", hoje.minusDays(2), 38, "Troca semanal", bd(684)},
                {6, 1, 2, "T-D02", hoje.minusDays(2), 8, "Suites executivas", bd(360)},
                {4, 3, 3, "T-D03", hoje.minusDays(2), 70, "Enxoval da internacao", bd(1260)},
                {1, 3, 1, "T-D11", hoje.minusDays(1), 40, "Troca do dia anterior", bd(720)},
                {1, 4, 1, "T-D12", hoje.minusDays(1), 52, null, bd(416)},
                {7, 8, 5, "T-D13", hoje.minusDays(1), 12, "Roupoes do spa", bd(264)},
                {1, 3, 1, "T-HOJE1", hoje, 40, "Troca de hoje", bd(720)},
                {1, 4, 1, "T-HOJE2", hoje, 52, "Toalhas dos andares 2 e 3", bd(416)},
                {1, 1, 1, "T-HOJE3", hoje, 8, "Edredons das suites master", bd(360)},
                {2, 3, 2, "T-HOJE4", hoje, 14, "Check-out da manha", bd(252)},
                {3, 7, 3, "T-HOJE5", hoje, 7, "Urgente para hoje a noite", bd(245)},
                {4, 3, 3, "T-HOJE6", hoje, 78, "Enxoval da internacao", bd(1404)},
                {4, 4, 3, "T-HOJE7", hoje, 88, null, bd(704)},
                {5, 3, 4, "T-HOJE8", hoje, 9, "Lencois das enfermarias", bd(162)},
                {6, 3, 2, "T-HOJE9", hoje, 26, "Andar corporativo", bd(468)},
                {6, 8, 2, "T-HOJE10", hoje, 10, "Roupoes do fitness", bd(220)},
                {7, 4, 5, "T-HOJE11", hoje, 28, "Toalhas da hidro e piscina", bd(224)},
                {7, 8, 5, "T-HOJE12", hoje, 12, null, bd(264)},
                {8, 10, 4, "T-HOJE13", hoje, 18, "Almoco", bd(270)},
                {2, 9, 2, "T-HOJE14", hoje, 5, "Mantas dos chales", bd(140)},
                {1, 5, 1, "T-HOJE15", hoje, 40, null, bd(200)},
        };
        try (PreparedStatement ps = cn.prepareStatement(insert)) {
            for (int i = 0; i < tickets.length; i++) {
                Object[] t = tickets[i];
                ps.setInt(1, (Integer) t[0]);
                ps.setInt(2, (Integer) t[1]);
                ps.setInt(3, (Integer) t[2]);
                ps.setString(4, (String) t[3]);
                ps.setDate(5, java.sql.Date.valueOf((LocalDate) t[4]));
                ps.setInt(6, (Integer) t[5]);
                ps.setString(7, (String) t[6]);
                ps.setBigDecimal(8, (BigDecimal) t[7]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static BigDecimal bd(int valor) {
        return BigDecimal.valueOf(valor).setScale(2);
    }
}

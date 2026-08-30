package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.RelatorioDAO;
import br.senac.lavanderia.model.Ticket;
import br.senac.lavanderia.util.Formatos;
import br.senac.lavanderia.util.Mensagens;
import br.senac.lavanderia.util.Validacoes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PainelRelatorios extends JPanel {

    private final RelatorioDAO dao = new RelatorioDAO();
    private final JTextField txtData = new JTextField(10);
    private final JLabel lblQtd = novoIndicador("Tickets: 0");
    private final JLabel lblTotal = novoIndicador("Total do dia: R$ 0,00");

    private final DefaultTableModel modeloTickets = new DefaultTableModel(
            new Object[]{"ID", "Loja", "Numero", "Peca", "Qtd", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloPecas = new DefaultTableModel(
            new Object[]{"Peca", "Quantidade"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PainelRelatorios() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtData.setText(Formatos.data(LocalDate.now()));

        JPanel topo = new JPanel(new BorderLayout(8, 8));
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton atualizar = new JButton("Atualizar");
        atualizar.addActionListener(e -> recarregar());
        filtro.add(new JLabel("Data"));
        filtro.add(txtData);
        filtro.add(atualizar);
        topo.add(filtro, BorderLayout.NORTH);

        JPanel indicadores = new JPanel(new GridLayout(1, 2, 8, 8));
        indicadores.add(caixa(lblQtd));
        indicadores.add(caixa(lblTotal));
        topo.add(indicadores, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 8, 8));
        centro.add(painelTabela("Tickets do dia", modeloTickets));
        centro.add(painelTabela("Pecas do dia", modeloPecas));
        add(centro, BorderLayout.CENTER);
    }

    private JLabel novoIndicador(String texto) {
        JLabel l = new JLabel(texto, JLabel.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
        return l;
    }

    private JPanel caixa(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEtchedBorder());
        p.add(label, BorderLayout.CENTER);
        return p;
    }

    private JPanel painelTabela(String titulo, DefaultTableModel modelo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
        return p;
    }

    public void recarregar() {
        modeloTickets.setRowCount(0);
        modeloPecas.setRowCount(0);
        try {
            LocalDate data;
            try {
                data = Formatos.parseData(txtData.getText());
            } catch (IllegalArgumentException ex) {
                Mensagens.aviso(this, "Data no formato dd/MM/aaaa.");
                return;
            }
            String erro = Validacoes.data(data);
            if (erro != null) {
                Mensagens.aviso(this, erro);
                return;
            }
            lblQtd.setText("Tickets: " + dao.quantidadeDoDia(data));
            lblTotal.setText("Total do dia: " + Formatos.moeda(dao.totalDoDia(data)));

            List<Ticket> tickets = dao.ticketsDoDia(data);
            for (int i = 0; i < tickets.size(); i++) {
                Ticket t = tickets.get(i);
                modeloTickets.addRow(new Object[]{
                        t.getId(),
                        t.getLojaNome(),
                        t.getNumero(),
                        t.getPecaNome(),
                        t.getQuantidade(),
                        Formatos.moeda(t.getValorTotal())
                });
            }

            Map<String, Integer> pecas = dao.pecasDoDia(data);
            for (String nome : pecas.keySet()) {
                modeloPecas.addRow(new Object[]{nome, pecas.get(nome)});
            }
        } catch (Exception e) {
        }
    }
}

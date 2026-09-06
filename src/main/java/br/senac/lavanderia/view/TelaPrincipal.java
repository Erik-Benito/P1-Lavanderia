package br.senac.lavanderia.view;

import br.senac.lavanderia.util.Conexao;
import br.senac.lavanderia.util.Mensagens;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TelaPrincipal extends JFrame {

    private final PainelLojas painelLojas = new PainelLojas();
    private final PainelMotoristas painelMotoristas = new PainelMotoristas();
    private final PainelCategorias painelCategorias = new PainelCategorias();
    private final PainelPecas painelPecas = new PainelPecas();
    private final PainelTickets painelTickets = new PainelTickets();
    private final PainelRelatorios painelRelatorios = new PainelRelatorios();

    public TelaPrincipal() {
        setTitle("Roger Lavanderia Industrial");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 740);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarTopo(), BorderLayout.NORTH);
        add(criarAbas(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                conectar();
            }
        });
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(18, 78, 108));
        topo.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel titulo = new JLabel("Roger Lavanderia Industrial");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        JLabel sub = new JLabel("Lojas  |  Motoristas  |  Categorias  |  Pecas  |  Tickets");
        sub.setForeground(new Color(210, 230, 238));
        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(sub, BorderLayout.SOUTH);
        topo.add(textos, BorderLayout.CENTER);
        return topo;
    }

    private JTabbedPane criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Lojas", painelLojas);
        abas.addTab("Motoristas", painelMotoristas);
        abas.addTab("Categorias", painelCategorias);
        abas.addTab("Pecas", painelPecas);
        abas.addTab("Tickets", painelTickets);
        abas.addTab("Relatorios", painelRelatorios);
        abas.addChangeListener(e -> recarregarTudo());
        return abas;
    }

    private void conectar() {
        try {
            Conexao.testar();
            recarregarTudo();
        } catch (Exception ex) {
            Mensagens.erro(this, "Sem conexao com o MySQL. Suba o banco com: docker compose up -d");
        }
    }

    private void recarregarTudo() {
        painelLojas.recarregar();
        painelMotoristas.recarregar();
        painelCategorias.recarregar();
        painelPecas.recarregar();
        painelTickets.recarregarListas();
        painelRelatorios.recarregar();
    }
}

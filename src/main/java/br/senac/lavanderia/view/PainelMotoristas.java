package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.MotoristaDAO;
import br.senac.lavanderia.model.Motorista;
import br.senac.lavanderia.util.Mensagens;
import br.senac.lavanderia.util.Validacoes;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class PainelMotoristas extends JPanel {

    private final MotoristaDAO dao = new MotoristaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Telefone"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelMotoristas() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(tabela);
        MenuContextoTabela.aplicar(tabela, scroll, this::excluir, this::incluir, this::editar);

        add(scroll, BorderLayout.CENTER);
        add(new JLabel("Botao direito na tabela: Filtrar, Excluir, Cadastrar, Editar"), BorderLayout.SOUTH);
    }

    public void recarregar() {
        modelo.setRowCount(0);
        try {
            List<Motorista> lista = dao.listar();
            for (int i = 0; i < lista.size(); i++) {
                Motorista motorista = lista.get(i);
                modelo.addRow(new Object[]{motorista.getId(), motorista.getNome(), motorista.getTelefone()});
            }
        } catch (Exception e) {
        }
    }

    private void incluir() {
        salvar(null);
    }

    private void editar() {
        Motorista motorista = motoristaSelecionado();
        if (motorista == null) {
            Mensagens.aviso(this, "Selecione um motorista para editar.");
            return;
        }
        salvar(motorista);
    }

    private Motorista motoristaSelecionado() {
        int row = MenuContextoTabela.linhaModelo(tabela);
        if (row < 0) {
            return null;
        }
        try {
            return dao.buscarPorId((Integer) modelo.getValueAt(row, 0));
        } catch (Exception ex) {
            Mensagens.erro(this, ex.getMessage());
            return null;
        }
    }

    private void salvar(Motorista existente) {
        JTextField txtId = new JTextField(6);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(28);
        JTextField txtTelefone = new JTextField(16);
        if (existente != null) {
            txtId.setText(String.valueOf(existente.getId()));
            txtNome.setText(existente.getNome());
            txtTelefone.setText(existente.getTelefone());
        }

        JPanel form = Formulario.campos("Cadastro do motorista");
        Formulario.linha(form, 0, "ID", txtId);
        Formulario.linha(form, 1, "Nome *", txtNome);
        Formulario.linha(form, 2, "Telefone", txtTelefone);

        while (true) {
            if (!Formulario.confirmar(this, existente == null ? "Cadastrar motorista" : "Editar motorista", form)) {
                return;
            }
            String erro = Validacoes.nome(txtNome.getText(), "nome do motorista", 3, 120);
            if (erro == null) {
                erro = Validacoes.telefone(txtTelefone.getText());
            }
            if (erro != null) {
                Mensagens.aviso(this, erro);
                continue;
            }

            Motorista motorista = existente == null ? new Motorista() : existente;
            motorista.setNome(txtNome.getText().trim());
            motorista.setTelefone(txtTelefone.getText().trim());
            try {
                dao.salvar(motorista);
                recarregar();
                return;
            } catch (Exception ex) {
                Mensagens.erro(this, "Falha ao salvar motorista:\n" + ex.getMessage());
                return;
            }
        }
    }

    private void excluir() {
        Motorista motorista = motoristaSelecionado();
        if (motorista == null) {
            Mensagens.aviso(this, "Selecione um motorista.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir o motorista selecionado?")) {
            return;
        }
        try {
            dao.excluir(motorista.getId());
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Nao foi possivel excluir. O motorista pode ter tickets.\n" + ex.getMessage());
        }
    }
}

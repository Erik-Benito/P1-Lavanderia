package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.LojaDAO;
import br.senac.lavanderia.model.Loja;
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

public class PainelLojas extends JPanel {

    private final LojaDAO dao = new LojaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Telefone"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelLojas() {
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
            List<Loja> lista = dao.listar();
            for (int i = 0; i < lista.size(); i++) {
                Loja loja = lista.get(i);
                modelo.addRow(new Object[]{loja.getId(), loja.getNome(), loja.getTelefone()});
            }
        } catch (Exception e) {
        }
    }

    private void incluir() {
        salvar(null);
    }

    private void editar() {
        Loja loja = lojaSelecionada();
        if (loja == null) {
            Mensagens.aviso(this, "Selecione uma loja para editar.");
            return;
        }
        salvar(loja);
    }

    private Loja lojaSelecionada() {
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

    private void salvar(Loja existente) {
        JTextField txtId = new JTextField(6);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(28);
        JTextField txtTelefone = new JTextField(16);
        if (existente != null) {
            txtId.setText(String.valueOf(existente.getId()));
            txtNome.setText(existente.getNome());
            txtTelefone.setText(existente.getTelefone());
        }

        JPanel form = Formulario.campos("Cadastro da loja");
        Formulario.linha(form, 0, "ID", txtId);
        Formulario.linha(form, 1, "Nome *", txtNome);
        Formulario.linha(form, 2, "Telefone", txtTelefone);

        while (true) {
            if (!Formulario.confirmar(this, existente == null ? "Cadastrar loja" : "Editar loja", form)) {
                return;
            }
            String erro = Validacoes.nome(txtNome.getText(), "nome da loja", 3, 120);
            if (erro == null) {
                erro = Validacoes.telefone(txtTelefone.getText());
            }
            if (erro != null) {
                Mensagens.aviso(this, erro);
                continue;
            }

            Loja loja = existente == null ? new Loja() : existente;
            loja.setNome(txtNome.getText().trim());
            loja.setTelefone(txtTelefone.getText().trim());
            try {
                dao.salvar(loja);
                recarregar();
                return;
            } catch (Exception ex) {
                Mensagens.erro(this, "Falha ao salvar loja:\n" + ex.getMessage());
                return;
            }
        }
    }

    private void excluir() {
        Loja loja = lojaSelecionada();
        if (loja == null) {
            Mensagens.aviso(this, "Selecione uma loja.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir a loja selecionada?")) {
            return;
        }
        try {
            dao.excluir(loja.getId());
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Nao foi possivel excluir. A loja pode ter tickets.\n" + ex.getMessage());
        }
    }
}

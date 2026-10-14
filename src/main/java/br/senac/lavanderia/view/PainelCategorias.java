package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.CategoriaDAO;
import br.senac.lavanderia.model.Categoria;
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

public class PainelCategorias extends JPanel {

    private final CategoriaDAO dao = new CategoriaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelCategorias() {
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
            List<Categoria> lista = dao.listar();
            for (int i = 0; i < lista.size(); i++) {
                Categoria categoria = lista.get(i);
                modelo.addRow(new Object[]{categoria.getId(), categoria.getNome()});
            }
        } catch (Exception e) {
            Mensagens.erro(this, "Falha ao carregar categorias:\n" + e.getMessage());
        }
    }

    private void incluir() {
        salvar(null);
    }

    private void editar() {
        Categoria categoria = categoriaSelecionada();
        if (categoria == null) {
            Mensagens.aviso(this, "Selecione uma categoria para editar.");
            return;
        }
        salvar(categoria);
    }

    private Categoria categoriaSelecionada() {
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

    private void salvar(Categoria existente) {
        JTextField txtId = new JTextField(6);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(28);
        if (existente != null) {
            txtId.setText(String.valueOf(existente.getId()));
            txtNome.setText(existente.getNome());
        }

        JPanel form = Formulario.campos("Cadastro da categoria");
        Formulario.linha(form, 0, "ID", txtId);
        Formulario.linha(form, 1, "Nome *", txtNome);

        while (true) {
            if (!Formulario.confirmar(this, existente == null ? "Cadastrar categoria" : "Editar categoria", form)) {
                return;
            }
            String erro = Validacoes.nome(txtNome.getText(), "nome da categoria", 3, 80);
            if (erro != null) {
                Mensagens.aviso(this, erro);
                continue;
            }

            Categoria categoria = existente == null ? new Categoria() : existente;
            categoria.setNome(txtNome.getText().trim());
            try {
                dao.salvar(categoria);
                recarregar();
                return;
            } catch (Exception ex) {
                Mensagens.erro(this, "Falha ao salvar categoria:\n" + ex.getMessage());
                return;
            }
        }
    }

    private void excluir() {
        Categoria categoria = categoriaSelecionada();
        if (categoria == null) {
            Mensagens.aviso(this, "Selecione uma categoria.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir a categoria? As pecas ficam sem categoria.")) {
            return;
        }
        try {
            dao.excluir(categoria.getId());
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Nao foi possivel excluir a categoria.");
        }
    }
}

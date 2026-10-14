package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.CategoriaDAO;
import br.senac.lavanderia.dao.PecaDAO;
import br.senac.lavanderia.model.Categoria;
import br.senac.lavanderia.model.Peca;
import br.senac.lavanderia.util.Formatos;
import br.senac.lavanderia.util.Mensagens;
import br.senac.lavanderia.util.Validacoes;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class PainelPecas extends JPanel {

    private final PecaDAO dao = new PecaDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Categoria", "Preco"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelPecas() {
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
            List<Peca> lista = dao.listar();
            for (int i = 0; i < lista.size(); i++) {
                Peca peca = lista.get(i);
                modelo.addRow(new Object[]{
                        peca.getId(),
                        peca.getNome(),
                        peca.getCategoriaNome() == null ? "-" : peca.getCategoriaNome(),
                        Formatos.moeda(peca.getPreco())
                });
            }
        } catch (Exception e) {
            Mensagens.erro(this, "Falha ao carregar pecas:\n" + e.getMessage());
        }
    }

    private void incluir() {
        salvar(null);
    }

    private void editar() {
        Peca peca = pecaSelecionada();
        if (peca == null) {
            Mensagens.aviso(this, "Selecione uma peca para editar.");
            return;
        }
        salvar(peca);
    }

    private Peca pecaSelecionada() {
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

    private void salvar(Peca existente) {
        List<Categoria> categorias;
        try {
            categorias = categoriaDAO.listar();
        } catch (Exception ex) {
            Mensagens.erro(this, ex.getMessage());
            return;
        }
        if (categorias.isEmpty()) {
            Mensagens.aviso(this, "Cadastre uma categoria na aba Categorias.");
            return;
        }

        JTextField txtId = new JTextField(6);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(22);
        JComboBox<Categoria> cmbCategoria = new JComboBox<>();
        JTextField txtPreco = new JTextField(10);
        for (int i = 0; i < categorias.size(); i++) {
            cmbCategoria.addItem(categorias.get(i));
        }
        if (existente != null) {
            txtId.setText(String.valueOf(existente.getId()));
            txtNome.setText(existente.getNome());
            selecionarCategoria(cmbCategoria, existente.getCategoriaId());
            txtPreco.setText(existente.getPreco() == null ? "0" : existente.getPreco().toPlainString());
        }

        JPanel form = Formulario.campos("Cadastro de peca");
        Formulario.linha(form, 0, "ID", txtId);
        Formulario.linha(form, 1, "Nome *", txtNome);
        Formulario.linha(form, 2, "Categoria *", cmbCategoria);
        Formulario.linha(form, 3, "Preco *", txtPreco);

        while (true) {
            if (!Formulario.confirmar(this, existente == null ? "Cadastrar peca" : "Editar peca", form)) {
                return;
            }
            String erro = Validacoes.nome(txtNome.getText(), "nome da peca", 3, 80);
            if (erro == null) {
                erro = Validacoes.preco(txtPreco.getText());
            }
            if (erro != null) {
                Mensagens.aviso(this, erro);
                continue;
            }

            Peca peca = existente == null ? new Peca() : existente;
            Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
            if (categoria == null) {
                Mensagens.aviso(this, "Selecione uma categoria.");
                continue;
            }
            peca.setNome(txtNome.getText().trim());
            peca.setCategoriaId(categoria.getId());
            peca.setPreco(Formatos.parseDecimal(txtPreco.getText()));
            try {
                dao.salvar(peca);
                recarregar();
                return;
            } catch (Exception ex) {
                Mensagens.erro(this, "Falha ao salvar peca:\n" + ex.getMessage());
                return;
            }
        }
    }

    private void selecionarCategoria(JComboBox<Categoria> combo, Integer id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            Categoria categoria = combo.getItemAt(i);
            if (categoria.getId() != null && categoria.getId().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void excluir() {
        Peca peca = pecaSelecionada();
        if (peca == null) {
            Mensagens.aviso(this, "Selecione uma peca.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir a peca e os tickets ligados a ela?")) {
            return;
        }
        try {
            dao.excluir(peca.getId());
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Nao foi possivel excluir a peca.");
        }
    }
}

package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.CategoriaDAO;
import br.senac.lavanderia.model.Categoria;
import br.senac.lavanderia.util.Mensagens;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PainelCategorias extends javax.swing.JPanel {

    private final CategoriaDAO dao = new CategoriaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PainelCategorias() {
        initComponents();
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MenuContextoTabela.aplicar(tabela, scrTabela, this::excluir, this::incluir, this::editar);
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
        salvar(new Categoria());
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

    private void salvar(Categoria categoria) {
        DialogCategoria dialog = new DialogCategoria(Mensagens.quadro(this), true);
        if (!dialog.editar(categoria)) {
            return;
        }
        try {
            dao.salvar(categoria);
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Falha ao salvar categoria:\n" + ex.getMessage());
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrTabela = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        lblDica = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.BorderLayout(8, 8));

        tabela.setModel(modelo);
        tabela.setRowHeight(24);
        tabela.setToolTipText("Clique com o botao direito para cadastrar, editar, excluir ou filtrar");
        scrTabela.setViewportView(tabela);

        add(scrTabela, java.awt.BorderLayout.CENTER);

        lblDica.setText("Botao direito na tabela: Filtrar, Excluir, Cadastrar, Editar");
        add(lblDica, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDica;
    private javax.swing.JScrollPane scrTabela;
    private javax.swing.JTable tabela;
    // End of variables declaration//GEN-END:variables
}

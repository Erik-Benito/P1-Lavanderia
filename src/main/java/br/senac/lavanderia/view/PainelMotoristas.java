package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.MotoristaDAO;
import br.senac.lavanderia.model.Motorista;
import br.senac.lavanderia.util.Mensagens;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PainelMotoristas extends javax.swing.JPanel {

    private final MotoristaDAO dao = new MotoristaDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Telefone"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PainelMotoristas() {
        initComponents();
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MenuContextoTabela.aplicar(tabela, scrTabela, this::excluir, this::incluir, this::editar);
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
            Mensagens.erro(this, "Falha ao carregar motoristas:\n" + e.getMessage());
        }
    }

    private void incluir() {
        salvar(new Motorista());
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

    private void salvar(Motorista motorista) {
        DialogMotorista dialog = new DialogMotorista(Mensagens.quadro(this), true);
        if (!dialog.editar(motorista)) {
            return;
        }
        try {
            dao.salvar(motorista);
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Falha ao salvar motorista:\n" + ex.getMessage());
        }
    }

    private void excluir() {
        Motorista motorista = motoristaSelecionado();
        if (motorista == null) {
            Mensagens.aviso(this, "Selecione um motorista.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir o motorista? Os tickets ficam sem motorista.")) {
            return;
        }
        try {
            dao.excluir(motorista.getId());
            recarregar();
        } catch (Exception ex) {
            Mensagens.erro(this, "Nao foi possivel excluir o motorista.");
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

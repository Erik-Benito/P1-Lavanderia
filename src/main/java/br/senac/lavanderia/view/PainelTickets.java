package br.senac.lavanderia.view;

import br.senac.lavanderia.dao.LojaDAO;
import br.senac.lavanderia.dao.MotoristaDAO;
import br.senac.lavanderia.dao.PecaDAO;
import br.senac.lavanderia.dao.TicketDAO;
import br.senac.lavanderia.model.Loja;
import br.senac.lavanderia.model.Motorista;
import br.senac.lavanderia.model.Peca;
import br.senac.lavanderia.model.Ticket;
import br.senac.lavanderia.util.Formatos;
import br.senac.lavanderia.util.Mensagens;
import br.senac.lavanderia.util.Validacoes;

import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class PainelTickets extends javax.swing.JPanel {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final LojaDAO lojaDAO = new LojaDAO();
    private final PecaDAO pecaDAO = new PecaDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private LocalDate dataSelecionada = LocalDate.now();
    private boolean primeiraCarga = true;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Cod.", "Loja", "Motorista", "Nº papel", "Peca", "Qtd", "Descricao", "Valor"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PainelTickets() {
        initComponents();
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        txtData.setText(Formatos.data(dataSelecionada));
        txtData.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarDataDigitada(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarDataDigitada(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarDataDigitada(false);
            }
        });
        MenuContextoTabela.aplicar(tabela, scrTabela, this::excluirTicket, this::incluirTicket, this::editarTicket);
    }

    public void recarregarListas() {
        if (primeiraCarga) {
            ajustarDataInicial();
            primeiraCarga = false;
        }
        recarregarTabela();
    }

    private void ajustarDataInicial() {
        try {
            if (!ticketDAO.listarPorData(dataSelecionada).isEmpty()) {
                return;
            }
            LocalDate ultima = ticketDAO.ultimaDataComTicket();
            if (ultima != null) {
                dataSelecionada = ultima;
                txtData.setText(Formatos.data(ultima));
            }
        } catch (Exception e) {
            Mensagens.erro(this, "Falha ao carregar tickets:\n" + e.getMessage());
        }
    }

    private void aplicarDataDigitada(boolean corrigir) {
        try {
            LocalDate data = Formatos.parseData(txtData.getText());
            if (data == null) {
                if (corrigir) {
                    txtData.setText(Formatos.data(dataSelecionada));
                }
                return;
            }
            String erroData = Validacoes.data(data);
            if (erroData != null) {
                if (corrigir) {
                    Mensagens.aviso(this, erroData);
                    txtData.setText(Formatos.data(dataSelecionada));
                }
                return;
            }
            if (data.equals(dataSelecionada)) {
                return;
            }
            dataSelecionada = data;
            recarregarTabela();
        } catch (IllegalArgumentException ex) {
            if (corrigir) {
                Mensagens.aviso(this, "Data no formato dd/MM/aaaa.");
                txtData.setText(Formatos.data(dataSelecionada));
            }
        }
    }

    private void recarregarTabela() {
        modelo.setRowCount(0);
        try {
            List<Ticket> lista = ticketDAO.listarPorData(dataSelecionada);
            for (int i = 0; i < lista.size(); i++) {
                Ticket t = lista.get(i);
                String numero = t.getNumero() == null || t.getNumero().isBlank() ? "-" : t.getNumero();
                String descricao = t.getDescricao() == null || t.getDescricao().isBlank() ? "-" : t.getDescricao();
                String motorista = t.getMotoristaNome() == null || t.getMotoristaNome().isBlank() ? "-" : t.getMotoristaNome();
                modelo.addRow(new Object[]{
                        t.getId(),
                        t.getLojaNome(),
                        motorista,
                        numero,
                        t.getPecaNome(),
                        t.getQuantidade(),
                        descricao,
                        Formatos.moeda(t.getValorTotal())
                });
            }
        } catch (Exception e) {
            Mensagens.erro(this, "Falha ao carregar tickets:\n" + e.getMessage());
        }
    }

    private Ticket ticketSelecionado() {
        int row = MenuContextoTabela.linhaModelo(tabela);
        if (row < 0) {
            return null;
        }
        try {
            return ticketDAO.buscarPorId((Integer) modelo.getValueAt(row, 0));
        } catch (Exception ex) {
            Mensagens.erro(this, ex.getMessage());
            return null;
        }
    }

    private void incluirTicket() {
        salvarTicket(new Ticket());
    }

    private void editarTicket() {
        Ticket ticket = ticketSelecionado();
        if (ticket == null) {
            Mensagens.aviso(this, "Selecione um ticket para editar.");
            return;
        }
        salvarTicket(ticket);
    }

    private void salvarTicket(Ticket ticket) {
        List<Loja> lojas;
        List<Peca> pecas;
        List<Motorista> motoristas;
        try {
            lojas = lojaDAO.listar();
            pecas = pecaDAO.listar();
            motoristas = motoristaDAO.listar();
        } catch (Exception ex) {
            Mensagens.erro(this, ex.getMessage());
            return;
        }
        if (lojas.isEmpty()) {
            Mensagens.aviso(this, "Cadastre uma loja na aba Lojas.");
            return;
        }
        if (pecas.isEmpty()) {
            Mensagens.aviso(this, "Cadastre uma peca na aba Pecas.");
            return;
        }
        if (motoristas.isEmpty()) {
            Mensagens.aviso(this, "Cadastre um motorista na aba Motoristas.");
            return;
        }
        String erroData = Validacoes.data(dataSelecionada);
        if (erroData != null) {
            Mensagens.aviso(this, erroData);
            return;
        }

        DialogTicket dialog = new DialogTicket(Mensagens.quadro(this), true);
        if (!dialog.editar(ticket, lojas, motoristas, pecas)) {
            return;
        }
        ticket.setDataTicket(dataSelecionada);
        try {
            ticketDAO.salvar(ticket);
            recarregarTabela();
        } catch (Exception ex) {
            Mensagens.erro(this, "Nao foi possivel salvar:\n" + ex.getMessage());
        }
    }

    private void excluirTicket() {
        Ticket ticket = ticketSelecionado();
        if (ticket == null) {
            Mensagens.aviso(this, "Selecione um ticket.");
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir este ticket?")) {
            return;
        }
        try {
            ticketDAO.excluir(ticket.getId());
            recarregarTabela();
        } catch (Exception ex) {
            Mensagens.erro(this, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTopo = new javax.swing.JPanel();
        lblData = new javax.swing.JLabel();
        txtData = new javax.swing.JTextField();
        scrTabela = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        lblDica = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new java.awt.BorderLayout(10, 10));

        pnlTopo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        lblData.setText("Data");
        pnlTopo.add(lblData);

        txtData.setColumns(10);
        txtData.setToolTipText("Formato dd/MM/aaaa. A lista atualiza ao digitar a data");
        txtData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataActionPerformed(evt);
            }
        });
        txtData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDataFocusLost(evt);
            }
        });
        pnlTopo.add(txtData);

        add(pnlTopo, java.awt.BorderLayout.NORTH);

        tabela.setModel(modelo);
        tabela.setRowHeight(24);
        tabela.setToolTipText("Clique com o botao direito para cadastrar, editar, excluir ou filtrar");
        scrTabela.setViewportView(tabela);

        add(scrTabela, java.awt.BorderLayout.CENTER);

        lblDica.setText("Botao direito na tabela: Filtrar, Excluir, Cadastrar, Editar");
        add(lblDica, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataActionPerformed
        aplicarDataDigitada(true);
    }//GEN-LAST:event_txtDataActionPerformed

    private void txtDataFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDataFocusLost
        aplicarDataDigitada(true);
    }//GEN-LAST:event_txtDataFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblDica;
    private javax.swing.JPanel pnlTopo;
    private javax.swing.JScrollPane scrTabela;
    private javax.swing.JTable tabela;
    private javax.swing.JTextField txtData;
    // End of variables declaration//GEN-END:variables
}

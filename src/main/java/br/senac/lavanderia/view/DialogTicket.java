package br.senac.lavanderia.view;

import br.senac.lavanderia.model.Loja;
import br.senac.lavanderia.model.Motorista;
import br.senac.lavanderia.model.Peca;
import br.senac.lavanderia.model.Ticket;
import br.senac.lavanderia.util.Formatos;
import br.senac.lavanderia.util.Mensagens;
import br.senac.lavanderia.util.Validacoes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

public class DialogTicket extends javax.swing.JDialog {

    private boolean confirmado;

    public DialogTicket(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(btnSalvar);
    }

    public boolean editar(Ticket ticket, List<Loja> lojas, List<Motorista> motoristas, List<Peca> pecas) {
        setTitle(ticket.getId() == null ? "Cadastrar ticket" : "Editar ticket");
        cmbLoja.removeAllItems();
        for (Loja loja : lojas) {
            cmbLoja.addItem(loja);
            if (loja.getId() != null && loja.getId().equals(ticket.getLojaId())) {
                cmbLoja.setSelectedItem(loja);
            }
        }
        cmbMotorista.removeAllItems();
        for (Motorista motorista : motoristas) {
            cmbMotorista.addItem(motorista);
            if (motorista.getId() != null && motorista.getId().equals(ticket.getMotoristaId())) {
                cmbMotorista.setSelectedItem(motorista);
            }
        }
        cmbPeca.removeAllItems();
        for (Peca peca : pecas) {
            cmbPeca.addItem(peca);
            if (peca.getId() != null && peca.getId().equals(ticket.getPecaId())) {
                cmbPeca.setSelectedItem(peca);
            }
        }
        txtNumero.setText(ticket.getNumero() == null ? "" : ticket.getNumero());
        spnQtd.setValue(Math.max(1, ticket.getQuantidade()));
        txtDescricao.setText(ticket.getDescricao() == null ? "" : ticket.getDescricao());
        atualizarValor();
        setLocationRelativeTo(getParent());
        setVisible(true);
        if (confirmado) {
            Loja loja = (Loja) cmbLoja.getSelectedItem();
            Motorista motorista = (Motorista) cmbMotorista.getSelectedItem();
            Peca peca = (Peca) cmbPeca.getSelectedItem();
            int quantidade = (Integer) spnQtd.getValue();
            ticket.setLojaId(loja.getId());
            ticket.setLojaNome(loja.getNome());
            ticket.setMotoristaId(motorista.getId());
            ticket.setMotoristaNome(motorista.getNome());
            ticket.setPecaId(peca.getId());
            ticket.setPecaNome(peca.getNome());
            ticket.setQuantidade(quantidade);
            ticket.setDescricao(txtDescricao.getText().trim());
            ticket.setValorTotal(total(peca, quantidade));
            ticket.setNumero(txtNumero.getText().trim());
        }
        return confirmado;
    }

    private BigDecimal total(Peca peca, int quantidade) {
        return peca.getPreco().multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }

    private void atualizarValor() {
        Peca peca = (Peca) cmbPeca.getSelectedItem();
        if (peca == null) {
            lblValor.setText("R$ 0,00");
            return;
        }
        lblValor.setText(Formatos.moeda(total(peca, (Integer) spnQtd.getValue())));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblLoja = new javax.swing.JLabel();
        cmbLoja = new javax.swing.JComboBox<>();
        lblMotorista = new javax.swing.JLabel();
        cmbMotorista = new javax.swing.JComboBox<>();
        lblNumero = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        lblPeca = new javax.swing.JLabel();
        cmbPeca = new javax.swing.JComboBox<>();
        lblQtd = new javax.swing.JLabel();
        spnQtd = new javax.swing.JSpinner();
        lblDescricao = new javax.swing.JLabel();
        scrDescricao = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();
        lblValorTitulo = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lancamento do ticket");

        lblLoja.setText("Loja");

        lblMotorista.setText("Motorista");

        lblNumero.setText("Nº do ticket");

        txtNumero.setToolTipText("Ex.: T-1840 (letras, numeros e hifen)");

        lblPeca.setText("Peca");

        cmbPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPecaActionPerformed(evt);
            }
        });

        lblQtd.setText("Quantidade");

        spnQtd.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        spnQtd.setToolTipText("Quantidade de pecas, de 1 a 999");
        spnQtd.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnQtdStateChanged(evt);
            }
        });

        lblDescricao.setText("Descricao");

        txtDescricao.setColumns(20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setRows(3);
        txtDescricao.setWrapStyleWord(true);

        scrDescricao.setViewportView(txtDescricao);

        lblValorTitulo.setText("Valor total");

        lblValor.setFont(lblValor.getFont().deriveFont(lblValor.getFont().getStyle() | java.awt.Font.BOLD));
        lblValor.setText("R$ 0,00");

        btnSalvar.setText("Salvar");
        btnSalvar.setToolTipText("Grava o ticket (atalho: Enter)");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.setToolTipText("Fecha sem salvar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLoja)
                            .addComponent(lblMotorista)
                            .addComponent(lblNumero)
                            .addComponent(lblPeca)
                            .addComponent(lblQtd)
                            .addComponent(lblDescricao)
                            .addComponent(lblValorTitulo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbLoja, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(cmbMotorista, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPeca, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(spnQtd, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(lblValor)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSalvar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLoja)
                    .addComponent(cmbLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMotorista)
                    .addComponent(cmbMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumero)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPeca)
                    .addComponent(cmbPeca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQtd)
                    .addComponent(spnQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescricao)
                    .addComponent(scrDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValorTitulo)
                    .addComponent(lblValor))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
            spnQtd.commitEdit();
        } catch (ParseException e) {
            Mensagens.aviso(this, "Quantidade invalida.");
            return;
        }
        String erro = null;
        if (cmbLoja.getSelectedItem() == null) {
            erro = "Selecione uma loja.";
        } else if (cmbMotorista.getSelectedItem() == null) {
            erro = "Selecione um motorista.";
        } else if (cmbPeca.getSelectedItem() == null) {
            erro = "Selecione uma peca.";
        }
        if (erro == null) {
            erro = Validacoes.numeroTicket(txtNumero.getText());
        }
        if (erro == null) {
            erro = Validacoes.quantidade((Integer) spnQtd.getValue());
        }
        if (erro == null) {
            erro = Validacoes.descricao(txtDescricao.getText());
        }
        if (erro != null) {
            Mensagens.aviso(this, erro);
            return;
        }
        confirmado = true;
        dispose();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void cmbPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPecaActionPerformed
        atualizarValor();
    }//GEN-LAST:event_cmbPecaActionPerformed

    private void spnQtdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnQtdStateChanged
        atualizarValor();
    }//GEN-LAST:event_spnQtdStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<br.senac.lavanderia.model.Loja> cmbLoja;
    private javax.swing.JComboBox<br.senac.lavanderia.model.Motorista> cmbMotorista;
    private javax.swing.JComboBox<br.senac.lavanderia.model.Peca> cmbPeca;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblLoja;
    private javax.swing.JLabel lblMotorista;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPeca;
    private javax.swing.JLabel lblQtd;
    private javax.swing.JLabel lblValor;
    private javax.swing.JLabel lblValorTitulo;
    private javax.swing.JScrollPane scrDescricao;
    private javax.swing.JSpinner spnQtd;
    private javax.swing.JTextArea txtDescricao;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables
}

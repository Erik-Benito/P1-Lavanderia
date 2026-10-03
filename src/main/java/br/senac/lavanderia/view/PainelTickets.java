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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

public class PainelTickets extends JPanel {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final LojaDAO lojaDAO = new LojaDAO();
    private final PecaDAO pecaDAO = new PecaDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final JTextField txtData = new JTextField(10);
    private LocalDate dataSelecionada = LocalDate.now();
    private boolean primeiraCarga = true;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Cod.", "Loja", "Motorista", "Nº papel", "Peca", "Qtd", "Descricao", "Valor"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelTickets() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        txtData.setText(Formatos.data(dataSelecionada));
        txtData.addActionListener(e -> aplicarDataDigitada(true));
        txtData.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                aplicarDataDigitada(true);
            }
        });
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

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.add(new JLabel("Data"));
        topo.add(txtData);

        JScrollPane scroll = new JScrollPane(tabela);
        MenuContextoTabela.aplicar(tabela, scroll, this::excluirTicket, this::incluirTicket, this::editarTicket);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(new JLabel("Botao direito na tabela: Filtrar, Excluir, Cadastrar, Editar"), BorderLayout.SOUTH);
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
        salvarTicket(null);
    }

    private void editarTicket() {
        Ticket ticket = ticketSelecionado();
        if (ticket == null) {
            Mensagens.aviso(this, "Selecione um ticket para editar.");
            return;
        }
        salvarTicket(ticket);
    }

    private void salvarTicket(Ticket existente) {
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

        JComboBox<Loja> cmbLoja = new JComboBox<>();
        JComboBox<Motorista> cmbMotorista = new JComboBox<>();
        JComboBox<Peca> cmbPeca = new JComboBox<>();
        JTextField txtNumero = new JTextField(10);
        JSpinner spnQtd = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JTextArea txtDescricao = new JTextArea(3, 22);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JLabel lblValor = new JLabel("R$ 0,00");
        cmbLoja.setPreferredSize(new Dimension(280, 28));
        cmbMotorista.setPreferredSize(new Dimension(280, 28));
        cmbPeca.setPreferredSize(new Dimension(220, 28));
        for (int i = 0; i < lojas.size(); i++) {
            cmbLoja.addItem(lojas.get(i));
        }
        for (int i = 0; i < motoristas.size(); i++) {
            cmbMotorista.addItem(motoristas.get(i));
        }
        for (int i = 0; i < pecas.size(); i++) {
            cmbPeca.addItem(pecas.get(i));
        }
        cmbPeca.addActionListener(e -> atualizarValor(cmbPeca, spnQtd, lblValor));
        spnQtd.addChangeListener(e -> atualizarValor(cmbPeca, spnQtd, lblValor));

        if (existente != null) {
            selecionarLoja(cmbLoja, existente.getLojaId());
            selecionarMotorista(cmbMotorista, existente.getMotoristaId());
            selecionarPeca(cmbPeca, existente.getPecaId());
            txtNumero.setText(existente.getNumero());
            spnQtd.setValue(Math.max(1, existente.getQuantidade()));
            txtDescricao.setText(existente.getDescricao() == null ? "" : existente.getDescricao());
        }
        atualizarValor(cmbPeca, spnQtd, lblValor);

        JPanel form = Formulario.campos("Lancamento do ticket");
        Formulario.linha(form, 0, "Loja", cmbLoja);
        Formulario.linha(form, 1, "Motorista", cmbMotorista);
        Formulario.linha(form, 2, "Nº do ticket", txtNumero);
        Formulario.linha(form, 3, "Peca", cmbPeca);
        Formulario.linha(form, 4, "Quantidade", spnQtd);
        Formulario.linha(form, 5, "Descricao", new JScrollPane(txtDescricao));
        Formulario.linha(form, 6, "Valor", lblValor);

        while (true) {
            if (!Formulario.confirmar(this, existente == null ? "Cadastrar ticket" : "Editar ticket", form)) {
                return;
            }
            try {
                spnQtd.commitEdit();
            } catch (ParseException e) {
                Mensagens.aviso(this, "Quantidade invalida.");
                continue;
            }
            Loja loja = (Loja) cmbLoja.getSelectedItem();
            Motorista motorista = (Motorista) cmbMotorista.getSelectedItem();
            Peca peca = (Peca) cmbPeca.getSelectedItem();
            if (loja == null) {
                Mensagens.aviso(this, "Selecione uma loja.");
                continue;
            }
            if (motorista == null) {
                Mensagens.aviso(this, "Selecione um motorista.");
                continue;
            }
            if (peca == null) {
                Mensagens.aviso(this, "Selecione uma peca.");
                continue;
            }
            int quantidade = (Integer) spnQtd.getValue();
            String erro = Validacoes.numeroTicket(txtNumero.getText());
            if (erro == null) {
                erro = Validacoes.quantidade(quantidade);
            }
            if (erro == null) {
                erro = Validacoes.descricao(txtDescricao.getText());
            }
            if (erro == null) {
                erro = Validacoes.data(dataSelecionada);
            }
            if (erro != null) {
                Mensagens.aviso(this, erro);
                continue;
            }

            Ticket ticket = existente == null ? new Ticket() : existente;
            ticket.setLojaId(loja.getId());
            ticket.setLojaNome(loja.getNome());
            ticket.setMotoristaId(motorista.getId());
            ticket.setMotoristaNome(motorista.getNome());
            ticket.setPecaId(peca.getId());
            ticket.setPecaNome(peca.getNome());
            ticket.setQuantidade(quantidade);
            ticket.setDescricao(txtDescricao.getText().trim());
            ticket.setValorTotal(peca.getPreco().multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP));
            ticket.setNumero(txtNumero.getText().trim());
            ticket.setDataTicket(dataSelecionada);
            try {
                ticketDAO.salvar(ticket);
                recarregarTabela();
                return;
            } catch (Exception ex) {
                Mensagens.erro(this, "Nao foi possivel salvar:\n" + ex.getMessage());
                return;
            }
        }
    }

    private void atualizarValor(JComboBox<Peca> cmbPeca, JSpinner spnQtd, JLabel lblValor) {
        Peca peca = (Peca) cmbPeca.getSelectedItem();
        if (peca == null) {
            lblValor.setText("R$ 0,00");
            return;
        }
        int quantidade = (Integer) spnQtd.getValue();
        BigDecimal total = peca.getPreco().multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
        lblValor.setText(Formatos.moeda(total));
    }

    private void selecionarLoja(JComboBox<Loja> combo, Integer id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            Loja loja = combo.getItemAt(i);
            if (loja.getId() != null && loja.getId().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarMotorista(JComboBox<Motorista> combo, Integer id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            Motorista motorista = combo.getItemAt(i);
            if (motorista.getId() != null && motorista.getId().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarPeca(JComboBox<Peca> combo, Integer id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            Peca peca = combo.getItemAt(i);
            if (peca.getId() != null && peca.getId().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
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
}

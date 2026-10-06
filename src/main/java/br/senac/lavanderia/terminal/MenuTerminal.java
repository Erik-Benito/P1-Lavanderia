package br.senac.lavanderia.terminal;

import br.senac.lavanderia.dao.CategoriaDAO;
import br.senac.lavanderia.dao.LojaDAO;
import br.senac.lavanderia.dao.MotoristaDAO;
import br.senac.lavanderia.dao.PecaDAO;
import br.senac.lavanderia.dao.TicketDAO;
import br.senac.lavanderia.model.Categoria;
import br.senac.lavanderia.model.Loja;
import br.senac.lavanderia.model.Motorista;
import br.senac.lavanderia.model.Peca;
import br.senac.lavanderia.model.Ticket;
import br.senac.lavanderia.util.BancoInicial;
import br.senac.lavanderia.util.Conexao;
import br.senac.lavanderia.util.Formatos;
import br.senac.lavanderia.util.Validacoes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Menu de terminal: testa a conexao com o MySQL e faz o CRUD de todas as tabelas.
 *
 * Para abrir: rode esta classe (no NetBeans: botao direito > Run File)
 * ou rode o App passando o argumento "terminal".
 */
public class MenuTerminal {

    private static final String[] TABELAS = {"loja", "motorista", "categoria", "peca", "ticket"};

    private final Entrada entrada;
    private final LojaDAO lojaDAO = new LojaDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final PecaDAO pecaDAO = new PecaDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    public MenuTerminal(Scanner scanner) {
        this.entrada = new Entrada(scanner);
    }

    public static void main(String[] args) {
        new MenuTerminal(new Scanner(System.in)).iniciar();
    }

    public void iniciar() {
        titulo("ROGER LAVANDERIA INDUSTRIAL - MENU DO TERMINAL");
        try {
            while (true) {
                System.out.println();
                System.out.println("1 - Testar conexao com o banco de dados");
                System.out.println("2 - Lojas");
                System.out.println("3 - Motoristas");
                System.out.println("4 - Categorias");
                System.out.println("5 - Pecas");
                System.out.println("6 - Tickets");
                System.out.println("0 - Sair");
                int opcao = entrada.opcao("Escolha uma opcao: ");
                switch (opcao) {
                    case 1:
                        testarConexao();
                        break;
                    case 2:
                        menuLojas();
                        break;
                    case 3:
                        menuMotoristas();
                        break;
                    case 4:
                        menuCategorias();
                        break;
                    case 5:
                        menuPecas();
                        break;
                    case 6:
                        menuTickets();
                        break;
                    case 0:
                        System.out.println("Ate logo!");
                        return;
                    default:
                        System.out.println("  Opcao invalida.");
                }
            }
        } catch (Entrada.FimDaEntrada e) {
            System.out.println();
            System.out.println("Entrada encerrada. Saindo do menu.");
        }
    }

    // ===================================================================
    // Teste de conexao
    // ===================================================================

    private void testarConexao() {
        titulo("TESTE DE CONEXAO");
        long inicio = System.currentTimeMillis();
        try (Connection cn = Conexao.getConnection()) {
            long tempo = System.currentTimeMillis() - inicio;
            DatabaseMetaData meta = cn.getMetaData();
            System.out.println("Status ......: CONECTADO");
            System.out.println("Tempo .......: " + tempo + " ms");
            System.out.println("Servidor ....: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("URL .........: " + meta.getURL());
            System.out.println("Usuario .....: " + meta.getUserName());
            System.out.println("Banco .......: " + cn.getCatalog());

            BancoInicial.preparar(cn);
            System.out.println();
            TabelaTexto tabela = new TabelaTexto("Tabela", "Registros");
            try (Statement st = cn.createStatement()) {
                for (String nome : TABELAS) {
                    try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + nome)) {
                        rs.next();
                        tabela.linha(nome, rs.getInt(1));
                    }
                }
            }
            tabela.imprimir();
        } catch (SQLException e) {
            System.out.println("Status ......: FALHOU");
            System.out.println("Erro ........: " + e.getMessage());
            System.out.println();
            System.out.println("Verifique se o MySQL esta rodando. Para subir o banco: docker compose up -d");
        }
        entrada.pausar();
    }

    // ===================================================================
    // Submenu generico
    // ===================================================================

    /** Mostra o submenu de CRUD e devolve a opcao escolhida (0 = voltar). */
    private int submenu(String nome) {
        System.out.println();
        titulo(nome.toUpperCase());
        System.out.println("1 - Listar");
        System.out.println("2 - Buscar por ID");
        System.out.println("3 - Cadastrar");
        System.out.println("4 - Editar");
        System.out.println("5 - Excluir");
        System.out.println("0 - Voltar");
        return entrada.opcao("Escolha uma opcao: ");
    }

    private void erro(String acao, SQLException e) {
        System.out.println("  Erro ao " + acao + ": " + e.getMessage());
    }

    // ===================================================================
    // Lojas
    // ===================================================================

    private void menuLojas() {
        while (true) {
            int opcao = submenu("Lojas");
            try {
                switch (opcao) {
                    case 1:
                        listarLojas();
                        break;
                    case 2:
                        Loja encontrada = buscarLoja();
                        if (encontrada != null) {
                            mostrarLoja(encontrada);
                        }
                        break;
                    case 3:
                        salvarLoja(new Loja());
                        break;
                    case 4:
                        Loja editar = buscarLoja();
                        if (editar != null) {
                            salvarLoja(editar);
                        }
                        break;
                    case 5:
                        Loja excluir = buscarLoja();
                        if (excluir != null && entrada.confirmar("Excluir a loja \"" + excluir.getNome()
                                + "\" e os tickets ligados a ela?")) {
                            lojaDAO.excluir(excluir.getId());
                            System.out.println("  Loja excluida.");
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("  Opcao invalida.");
                }
            } catch (SQLException e) {
                erro("acessar lojas", e);
            }
        }
    }

    private void listarLojas() throws SQLException {
        TabelaTexto tabela = new TabelaTexto("ID", "Nome", "Telefone");
        for (Loja l : lojaDAO.listar()) {
            tabela.linha(l.getId(), l.getNome(), l.getTelefone());
        }
        tabela.imprimir();
    }

    private Loja buscarLoja() throws SQLException {
        Integer id = entrada.id("ID da loja (Enter cancela): ");
        if (id == null) {
            return null;
        }
        Loja loja = lojaDAO.buscarPorId(id);
        if (loja == null) {
            System.out.println("  Loja " + id + " nao encontrada.");
        }
        return loja;
    }

    private void mostrarLoja(Loja l) {
        System.out.println("  ID .......: " + l.getId());
        System.out.println("  Nome .....: " + l.getNome());
        System.out.println("  Telefone .: " + vazio(l.getTelefone()));
    }

    private void salvarLoja(Loja loja) throws SQLException {
        boolean nova = loja.getId() == null;
        System.out.println(nova ? "\nNova loja:" : "\nEditando a loja " + loja.getId() + " (Enter mantem o valor atual):");
        loja.setNome(entrada.texto("Nome", loja.getNome(), t -> Validacoes.nome(t, "nome da loja", 3, 120)));
        loja.setTelefone(entrada.texto("Telefone (opcional)", loja.getTelefone(), Validacoes::telefone));
        lojaDAO.salvar(loja);
        System.out.println(nova ? "  Loja cadastrada com ID " + loja.getId() + "." : "  Loja atualizada.");
    }

    // ===================================================================
    // Motoristas
    // ===================================================================

    private void menuMotoristas() {
        while (true) {
            int opcao = submenu("Motoristas");
            try {
                switch (opcao) {
                    case 1:
                        listarMotoristas();
                        break;
                    case 2:
                        Motorista encontrado = buscarMotorista();
                        if (encontrado != null) {
                            mostrarMotorista(encontrado);
                        }
                        break;
                    case 3:
                        salvarMotorista(new Motorista());
                        break;
                    case 4:
                        Motorista editar = buscarMotorista();
                        if (editar != null) {
                            salvarMotorista(editar);
                        }
                        break;
                    case 5:
                        Motorista excluir = buscarMotorista();
                        if (excluir != null && entrada.confirmar("Excluir o motorista \"" + excluir.getNome()
                                + "\"? Os tickets ficam sem motorista.")) {
                            motoristaDAO.excluir(excluir.getId());
                            System.out.println("  Motorista excluido.");
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("  Opcao invalida.");
                }
            } catch (SQLException e) {
                erro("acessar motoristas", e);
            }
        }
    }

    private void listarMotoristas() throws SQLException {
        TabelaTexto tabela = new TabelaTexto("ID", "Nome", "Telefone");
        for (Motorista m : motoristaDAO.listar()) {
            tabela.linha(m.getId(), m.getNome(), m.getTelefone());
        }
        tabela.imprimir();
    }

    private Motorista buscarMotorista() throws SQLException {
        Integer id = entrada.id("ID do motorista (Enter cancela): ");
        if (id == null) {
            return null;
        }
        Motorista motorista = motoristaDAO.buscarPorId(id);
        if (motorista == null) {
            System.out.println("  Motorista " + id + " nao encontrado.");
        }
        return motorista;
    }

    private void mostrarMotorista(Motorista m) {
        System.out.println("  ID .......: " + m.getId());
        System.out.println("  Nome .....: " + m.getNome());
        System.out.println("  Telefone .: " + vazio(m.getTelefone()));
    }

    private void salvarMotorista(Motorista motorista) throws SQLException {
        boolean novo = motorista.getId() == null;
        System.out.println(novo ? "\nNovo motorista:"
                : "\nEditando o motorista " + motorista.getId() + " (Enter mantem o valor atual):");
        motorista.setNome(entrada.texto("Nome", motorista.getNome(),
                t -> Validacoes.nome(t, "nome do motorista", 3, 120)));
        motorista.setTelefone(entrada.texto("Telefone (opcional)", motorista.getTelefone(), Validacoes::telefone));
        motoristaDAO.salvar(motorista);
        System.out.println(novo ? "  Motorista cadastrado com ID " + motorista.getId() + "." : "  Motorista atualizado.");
    }

    // ===================================================================
    // Categorias
    // ===================================================================

    private void menuCategorias() {
        while (true) {
            int opcao = submenu("Categorias");
            try {
                switch (opcao) {
                    case 1:
                        listarCategorias();
                        break;
                    case 2:
                        Categoria encontrada = buscarCategoria();
                        if (encontrada != null) {
                            System.out.println("  ID ...: " + encontrada.getId());
                            System.out.println("  Nome .: " + encontrada.getNome());
                        }
                        break;
                    case 3:
                        salvarCategoria(new Categoria());
                        break;
                    case 4:
                        Categoria editar = buscarCategoria();
                        if (editar != null) {
                            salvarCategoria(editar);
                        }
                        break;
                    case 5:
                        Categoria excluir = buscarCategoria();
                        if (excluir != null && entrada.confirmar("Excluir a categoria \"" + excluir.getNome()
                                + "\"? As pecas ficam sem categoria.")) {
                            categoriaDAO.excluir(excluir.getId());
                            System.out.println("  Categoria excluida.");
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("  Opcao invalida.");
                }
            } catch (SQLException e) {
                erro("acessar categorias", e);
            }
        }
    }

    private void listarCategorias() throws SQLException {
        TabelaTexto tabela = new TabelaTexto("ID", "Nome");
        for (Categoria c : categoriaDAO.listar()) {
            tabela.linha(c.getId(), c.getNome());
        }
        tabela.imprimir();
    }

    private Categoria buscarCategoria() throws SQLException {
        Integer id = entrada.id("ID da categoria (Enter cancela): ");
        if (id == null) {
            return null;
        }
        Categoria categoria = categoriaDAO.buscarPorId(id);
        if (categoria == null) {
            System.out.println("  Categoria " + id + " nao encontrada.");
        }
        return categoria;
    }

    private void salvarCategoria(Categoria categoria) throws SQLException {
        boolean nova = categoria.getId() == null;
        System.out.println(nova ? "\nNova categoria:"
                : "\nEditando a categoria " + categoria.getId() + " (Enter mantem o valor atual):");
        categoria.setNome(entrada.texto("Nome", categoria.getNome(),
                t -> Validacoes.nome(t, "nome da categoria", 3, 80)));
        categoriaDAO.salvar(categoria);
        System.out.println(nova ? "  Categoria cadastrada com ID " + categoria.getId() + "." : "  Categoria atualizada.");
    }

    // ===================================================================
    // Pecas
    // ===================================================================

    private void menuPecas() {
        while (true) {
            int opcao = submenu("Pecas");
            try {
                switch (opcao) {
                    case 1:
                        listarPecas();
                        break;
                    case 2:
                        Peca encontrada = buscarPeca();
                        if (encontrada != null) {
                            System.out.println("  ID ........: " + encontrada.getId());
                            System.out.println("  Nome ......: " + encontrada.getNome());
                            System.out.println("  Categoria .: " + vazio(encontrada.getCategoriaNome()));
                            System.out.println("  Preco .....: " + moeda(encontrada.getPreco()));
                        }
                        break;
                    case 3:
                        salvarPeca(new Peca());
                        break;
                    case 4:
                        Peca editar = buscarPeca();
                        if (editar != null) {
                            salvarPeca(editar);
                        }
                        break;
                    case 5:
                        Peca excluir = buscarPeca();
                        if (excluir != null && entrada.confirmar("Excluir a peca \"" + excluir.getNome()
                                + "\" e os tickets ligados a ela?")) {
                            pecaDAO.excluir(excluir.getId());
                            System.out.println("  Peca excluida.");
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("  Opcao invalida.");
                }
            } catch (SQLException e) {
                erro("acessar pecas", e);
            }
        }
    }

    private void listarPecas() throws SQLException {
        TabelaTexto tabela = new TabelaTexto("ID", "Nome", "Categoria", "Preco");
        for (Peca p : pecaDAO.listar()) {
            tabela.linha(p.getId(), p.getNome(), p.getCategoriaNome(), moeda(p.getPreco()));
        }
        tabela.imprimir();
    }

    private Peca buscarPeca() throws SQLException {
        Integer id = entrada.id("ID da peca (Enter cancela): ");
        if (id == null) {
            return null;
        }
        Peca peca = pecaDAO.buscarPorId(id);
        if (peca == null) {
            System.out.println("  Peca " + id + " nao encontrada.");
        }
        return peca;
    }

    private void salvarPeca(Peca peca) throws SQLException {
        List<Categoria> categorias = categoriaDAO.listar();
        if (categorias.isEmpty()) {
            System.out.println("  Cadastre uma categoria antes (menu Categorias).");
            return;
        }
        boolean nova = peca.getId() == null;
        System.out.println(nova ? "\nNova peca:" : "\nEditando a peca " + peca.getId() + " (Enter mantem o valor atual):");
        peca.setNome(entrada.texto("Nome", peca.getNome(), t -> Validacoes.nome(t, "nome da peca", 3, 80)));

        System.out.println("Categorias disponiveis:");
        listarCategorias();
        Categoria categoria = escolher("ID da categoria", peca.getCategoriaId(), categorias);
        peca.setCategoriaId(categoria.getId());
        peca.setCategoriaNome(categoria.getNome());

        peca.setPreco(entrada.preco("Preco (ex.: 45,00)", nova ? null : peca.getPreco(), Validacoes::preco));
        pecaDAO.salvar(peca);
        System.out.println(nova ? "  Peca cadastrada com ID " + peca.getId() + "." : "  Peca atualizada.");
    }

    // ===================================================================
    // Tickets
    // ===================================================================

    private void menuTickets() {
        while (true) {
            int opcao = submenu("Tickets");
            try {
                switch (opcao) {
                    case 1:
                        listarTickets();
                        break;
                    case 2:
                        Ticket encontrado = buscarTicket();
                        if (encontrado != null) {
                            mostrarTicket(encontrado);
                        }
                        break;
                    case 3:
                        salvarTicket(new Ticket());
                        break;
                    case 4:
                        Ticket editar = buscarTicket();
                        if (editar != null) {
                            salvarTicket(editar);
                        }
                        break;
                    case 5:
                        Ticket excluir = buscarTicket();
                        if (excluir != null && entrada.confirmar("Excluir o ticket " + excluir.getNumero() + "?")) {
                            ticketDAO.excluir(excluir.getId());
                            System.out.println("  Ticket excluido.");
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("  Opcao invalida.");
                }
            } catch (SQLException e) {
                erro("acessar tickets", e);
            }
        }
    }

    private void listarTickets() throws SQLException {
        System.out.println("Filtrar por data? Digite dd/MM/aaaa ou deixe vazio para listar todos.");
        String texto = entrada.linha("Data: ");
        List<Ticket> lista;
        if (texto.isEmpty()) {
            lista = ticketDAO.listar();
        } else {
            LocalDate data;
            try {
                data = Formatos.parseData(texto);
            } catch (IllegalArgumentException e) {
                System.out.println("  Data no formato dd/MM/aaaa.");
                return;
            }
            lista = ticketDAO.listarPorData(data);
        }
        TabelaTexto tabela = new TabelaTexto("ID", "Data", "Numero", "Loja", "Motorista", "Peca", "Qtd", "Valor");
        BigDecimal total = BigDecimal.ZERO;
        for (Ticket t : lista) {
            tabela.linha(t.getId(), Formatos.data(t.getDataTicket()), t.getNumero(), t.getLojaNome(),
                    t.getMotoristaNome(), t.getPecaNome(), t.getQuantidade(), moeda(t.getValorTotal()));
            total = total.add(t.getValorTotal());
        }
        tabela.imprimir();
        System.out.println("Valor total: " + moeda(total));
    }

    private Ticket buscarTicket() throws SQLException {
        Integer id = entrada.id("ID do ticket (Enter cancela): ");
        if (id == null) {
            return null;
        }
        Ticket ticket = ticketDAO.buscarPorId(id);
        if (ticket == null) {
            System.out.println("  Ticket " + id + " nao encontrado.");
        }
        return ticket;
    }

    private void mostrarTicket(Ticket t) {
        System.out.println("  ID ..........: " + t.getId());
        System.out.println("  Data ........: " + Formatos.data(t.getDataTicket()));
        System.out.println("  Numero ......: " + vazio(t.getNumero()));
        System.out.println("  Loja ........: " + t.getLojaNome());
        System.out.println("  Motorista ...: " + vazio(t.getMotoristaNome()));
        System.out.println("  Peca ........: " + t.getPecaNome());
        System.out.println("  Quantidade ..: " + t.getQuantidade());
        System.out.println("  Descricao ...: " + vazio(t.getDescricao()));
        System.out.println("  Valor total .: " + moeda(t.getValorTotal()));
    }

    private void salvarTicket(Ticket ticket) throws SQLException {
        List<Loja> lojas = lojaDAO.listar();
        List<Motorista> motoristas = motoristaDAO.listar();
        List<Peca> pecas = pecaDAO.listar();
        if (lojas.isEmpty() || motoristas.isEmpty() || pecas.isEmpty()) {
            System.out.println("  Para lancar um ticket e preciso ter ao menos uma loja, um motorista e uma peca.");
            return;
        }
        boolean novo = ticket.getId() == null;
        System.out.println(novo ? "\nNovo ticket:" : "\nEditando o ticket " + ticket.getId() + " (Enter mantem o valor atual):");

        ticket.setDataTicket(entrada.data("Data (dd/MM/aaaa)",
                ticket.getDataTicket() == null ? LocalDate.now() : ticket.getDataTicket(), Validacoes::data));
        ticket.setNumero(entrada.texto("Numero do ticket", ticket.getNumero(), Validacoes::numeroTicket));

        System.out.println("Lojas disponiveis:");
        listarLojas();
        Loja loja = escolher("ID da loja", ticket.getLojaId(), lojas);
        ticket.setLojaId(loja.getId());
        ticket.setLojaNome(loja.getNome());

        System.out.println("Motoristas disponiveis:");
        listarMotoristas();
        Motorista motorista = escolher("ID do motorista", ticket.getMotoristaId(), motoristas);
        ticket.setMotoristaId(motorista.getId());
        ticket.setMotoristaNome(motorista.getNome());

        System.out.println("Pecas disponiveis:");
        listarPecas();
        Peca peca = escolher("ID da peca", ticket.getPecaId(), pecas);
        ticket.setPecaId(peca.getId());
        ticket.setPecaNome(peca.getNome());

        int quantidade = entrada.inteiro("Quantidade", novo ? null : ticket.getQuantidade(), 1, 999);
        ticket.setQuantidade(quantidade);
        ticket.setDescricao(entrada.texto("Descricao (opcional)", ticket.getDescricao(), Validacoes::descricao));
        ticket.setValorTotal(peca.getPreco().multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP));

        System.out.println("  Valor total: " + moeda(ticket.getValorTotal()));
        ticketDAO.salvar(ticket);
        System.out.println(novo ? "  Ticket cadastrado com ID " + ticket.getId() + "." : "  Ticket atualizado.");
    }

    // ===================================================================
    // Auxiliares
    // ===================================================================

    /** Pede um ID ate o usuario escolher um que esteja na lista. Enter mantem o atual. */
    private <T> T escolher(String rotulo, Integer atual, List<T> opcoes) {
        while (true) {
            String texto = entrada.linha(rotulo + (atual == null ? ": " : " [" + atual + "]: "));
            Integer id = null;
            if (texto.isEmpty()) {
                id = atual;
            } else {
                try {
                    id = Integer.parseInt(texto);
                } catch (NumberFormatException e) {
                    // cai na mensagem abaixo
                }
            }
            if (id != null) {
                for (T item : opcoes) {
                    if (id.equals(idDe(item))) {
                        return item;
                    }
                }
            }
            System.out.println("  ID invalido. Escolha um dos IDs da lista.");
        }
    }

    private Integer idDe(Object item) {
        if (item instanceof Loja) {
            return ((Loja) item).getId();
        }
        if (item instanceof Motorista) {
            return ((Motorista) item).getId();
        }
        if (item instanceof Categoria) {
            return ((Categoria) item).getId();
        }
        if (item instanceof Peca) {
            return ((Peca) item).getId();
        }
        return null;
    }

    /** Formata em reais trocando o espaco especial do Java por espaco normal (alguns terminais mostram "?"). */
    private String moeda(BigDecimal valor) {
        return Formatos.moeda(valor).replace('\u00A0', ' ');
    }

    private String vazio(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    private void titulo(String texto) {
        String linha = "=".repeat(texto.length() + 4);
        System.out.println(linha);
        System.out.println("  " + texto);
        System.out.println(linha);
    }
}

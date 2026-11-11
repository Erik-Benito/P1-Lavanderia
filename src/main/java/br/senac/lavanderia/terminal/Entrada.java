package br.senac.lavanderia.terminal;

import br.senac.lavanderia.util.Formatos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Leitura de dados digitados no terminal.
 * Nos metodos com "atual", apertar Enter sem digitar nada mantem o valor atual (usado na edicao).
 */
public class Entrada {

    /** Lancada quando a entrada termina (Ctrl+D / Ctrl+Z), para encerrar o menu sem erro. */
    public static class FimDaEntrada extends RuntimeException {
    }

    private final Scanner scanner;

    public Entrada(Scanner scanner) {
        this.scanner = scanner;
    }

    public String linha(String rotulo) {
        System.out.print(rotulo);
        System.out.flush();
        if (!scanner.hasNextLine()) {
            throw new FimDaEntrada();
        }
        return scanner.nextLine().trim();
    }

    /** Le o numero de uma opcao de menu, repetindo ate ser um numero valido. */
    public int opcao(String rotulo) {
        while (true) {
            String texto = linha(rotulo);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("  Opcao invalida. Digite o numero da opcao.");
            }
        }
    }

    /** Le um inteiro positivo. Retorna null se o usuario deixar vazio. */
    public Integer id(String rotulo) {
        while (true) {
            String texto = linha(rotulo);
            if (texto.isEmpty()) {
                return null;
            }
            try {
                int valor = Integer.parseInt(texto);
                if (valor > 0) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // cai na mensagem abaixo
            }
            System.out.println("  Informe um numero inteiro maior que zero (ou Enter para cancelar).");
        }
    }

    /**
     * Le um texto e valida. A funcao de validacao devolve a mensagem de erro ou null se estiver ok.
     */
    public String texto(String rotulo, String atual, Function<String, String> validacao) {
        while (true) {
            String texto = linha(rotulo + sufixo(atual));
            if (texto.isEmpty() && atual != null) {
                texto = atual;
            }
            String erro = validacao == null ? null : validacao.apply(texto);
            if (erro == null) {
                return texto;
            }
            System.out.println("  " + erro);
        }
    }

    public BigDecimal preco(String rotulo, BigDecimal atual, Function<String, String> validacao) {
        String texto = texto(rotulo, atual == null ? null : atual.toPlainString(), validacao);
        return Formatos.parseDecimal(texto);
    }

    public int inteiro(String rotulo, Integer atual, int minimo, int maximo) {
        while (true) {
            String texto = linha(rotulo + sufixo(atual == null ? null : String.valueOf(atual)));
            if (texto.isEmpty() && atual != null) {
                return atual;
            }
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // cai na mensagem abaixo
            }
            System.out.println("  Informe um numero entre " + minimo + " e " + maximo + ".");
        }
    }

    public LocalDate data(String rotulo, LocalDate atual, Function<LocalDate, String> validacao) {
        while (true) {
            String texto = linha(rotulo + sufixo(Formatos.data(atual)));
            LocalDate data;
            if (texto.isEmpty() && atual != null) {
                data = atual;
            } else {
                try {
                    data = Formatos.parseData(texto);
                } catch (IllegalArgumentException e) {
                    System.out.println("  Data no formato dd/MM/aaaa.");
                    continue;
                }
            }
            String erro = validacao.apply(data);
            if (erro == null) {
                return data;
            }
            System.out.println("  " + erro);
        }
    }

    /** Pergunta s/n e repete ate a resposta ser valida. */
    public boolean confirmar(String pergunta) {
        while (true) {
            String texto = linha(pergunta + " (s/n): ").toLowerCase();
            if (texto.equals("s") || texto.equals("sim")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("nao") || texto.equals("não")) {
                return false;
            }
            System.out.println("  Responda s ou n.");
        }
    }

    public void pausar() {
        linha("\nPressione Enter para continuar...");
    }

    private String sufixo(String atual) {
        if (atual == null || atual.isEmpty()) {
            return ": ";
        }
        return " [" + atual + "]: ";
    }
}

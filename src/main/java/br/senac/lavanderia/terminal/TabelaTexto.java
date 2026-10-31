package br.senac.lavanderia.terminal;

import java.util.ArrayList;
import java.util.List;

/** Monta uma tabela simples em texto para mostrar as listagens no terminal. */
public class TabelaTexto {

    private static final int LARGURA_MAXIMA = 40;

    private final String[] cabecalho;
    private final List<String[]> linhas = new ArrayList<>();

    public TabelaTexto(String... cabecalho) {
        this.cabecalho = cabecalho;
    }

    public void linha(Object... valores) {
        String[] textos = new String[cabecalho.length];
        for (int i = 0; i < cabecalho.length; i++) {
            Object valor = i < valores.length ? valores[i] : null;
            String texto = valor == null ? "-" : valor.toString();
            if (texto.isBlank()) {
                texto = "-";
            }
            if (texto.length() > LARGURA_MAXIMA) {
                texto = texto.substring(0, LARGURA_MAXIMA - 3) + "...";
            }
            textos[i] = texto;
        }
        linhas.add(textos);
    }

    public void imprimir() {
        int[] larguras = new int[cabecalho.length];
        for (int i = 0; i < cabecalho.length; i++) {
            larguras[i] = cabecalho[i].length();
        }
        for (String[] linha : linhas) {
            for (int i = 0; i < linha.length; i++) {
                larguras[i] = Math.max(larguras[i], linha[i].length());
            }
        }

        String separador = separador(larguras);
        System.out.println(separador);
        System.out.println(formatar(cabecalho, larguras));
        System.out.println(separador);
        for (String[] linha : linhas) {
            System.out.println(formatar(linha, larguras));
        }
        System.out.println(separador);
        System.out.println(linhas.size() + (linhas.size() == 1 ? " registro." : " registros."));
    }

    private String separador(int[] larguras) {
        StringBuilder sb = new StringBuilder("+");
        for (int largura : larguras) {
            sb.append("-".repeat(largura + 2)).append("+");
        }
        return sb.toString();
    }

    private String formatar(String[] valores, int[] larguras) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < valores.length; i++) {
            sb.append(" ").append(String.format("%-" + larguras[i] + "s", valores[i])).append(" |");
        }
        return sb.toString();
    }
}

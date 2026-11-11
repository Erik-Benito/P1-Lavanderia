package br.senac.lavanderia.util;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Regras de validacao dos campos. Cada metodo devolve a mensagem de erro ou null.
 */
public final class Validacoes {

    private Validacoes() {
    }

    public static String nome(String texto, String rotulo, int minimo, int maximo) {
        if (texto == null || texto.isBlank()) {
            return "Informe o " + rotulo + ".";
        }
        String t = texto.trim();
        if (t.length() < minimo) {
            return rotulo + " deve ter pelo menos " + minimo + " caracteres.";
        }
        if (t.length() > maximo) {
            return rotulo + " deve ter no maximo " + maximo + " caracteres.";
        }
        if (!t.matches(".*[\\p{L}].*")) {
            return rotulo + " invalido. Informe um texto, nao apenas numeros.";
        }
        return null;
    }

    public static String telefone(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String digitos = texto.replaceAll("\\D", "");
        if (digitos.length() != 10 && digitos.length() != 11) {
            return "Telefone invalido. Use DDD + numero (10 ou 11 digitos).";
        }
        int ddd = Integer.parseInt(digitos.substring(0, 2));
        if (ddd < 11 || ddd > 99) {
            return "DDD do telefone invalido.";
        }
        return null;
    }

    public static String preco(String texto) {
        if (texto == null || texto.isBlank()) {
            return "Informe o preco.";
        }
        BigDecimal valor;
        try {
            valor = Formatos.parseDecimal(texto);
        } catch (Exception e) {
            return "Preco invalido. Use um numero, por exemplo 45,00.";
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return "Preco deve ser maior que zero.";
        }
        if (valor.compareTo(new BigDecimal("99999.99")) > 0) {
            return "Preco invalido.";
        }
        if (valor.scale() > 2) {
            return "Preco deve ter no maximo 2 casas decimais.";
        }
        return null;
    }

    public static String numeroTicket(String texto) {
        if (texto == null || texto.isBlank()) {
            return "Informe o numero do ticket.";
        }
        String t = texto.trim();
        if (t.length() > 40) {
            return "Numero do ticket deve ter no maximo 40 caracteres.";
        }
        if (!t.matches("[A-Za-z0-9\\-]+")) {
            return "Numero do ticket invalido. Use letras, numeros e hifen.";
        }
        if (!t.matches(".*\\d.*")) {
            return "Numero do ticket invalido. Informe ao menos um digito.";
        }
        return null;
    }

    public static String quantidade(int valor) {
        if (valor < 1 || valor > 999) {
            return "Quantidade deve ser entre 1 e 999.";
        }
        return null;
    }

    public static String descricao(String texto) {
        if (texto != null && texto.length() > 255) {
            return "Descricao deve ter no maximo 255 caracteres.";
        }
        return null;
    }

    public static String data(LocalDate data) {
        if (data == null) {
            return "Data no formato dd/MM/aaaa.";
        }
        if (data.getYear() < 2020 || data.getYear() > 2100) {
            return "Data invalida.";
        }
        return null;
    }
}

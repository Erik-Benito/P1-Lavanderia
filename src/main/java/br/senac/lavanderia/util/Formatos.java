package br.senac.lavanderia.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class Formatos {

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(PT_BR);

    private Formatos() {
    }

    public static String data(LocalDate valor) {
        return valor == null ? "" : DATA.format(valor);
    }

    public static LocalDate parseData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data invalida. Use o formato dd/MM/aaaa.");
        }
    }

    public static String moeda(BigDecimal valor) {
        return MOEDA.format(valor == null ? BigDecimal.ZERO : valor);
    }

    public static BigDecimal parseDecimal(String texto) {
        if (texto == null || texto.isBlank()) {
            return BigDecimal.ZERO;
        }
        String t = texto.trim().replace("R$", "").trim();
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        return new BigDecimal(t);
    }
}

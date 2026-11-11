package br.senac.lavanderia.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;

public final class Mensagens {

    private Mensagens() {
    }

    public static void info(Component pai, String texto) {
        mostrar(pai, new JOptionPane(texto, JOptionPane.INFORMATION_MESSAGE), "Informacao");
    }

    public static void erro(Component pai, String texto) {
        mostrar(pai, new JOptionPane(texto, JOptionPane.ERROR_MESSAGE), "Erro");
    }

    public static void aviso(Component pai, String texto) {
        mostrar(pai, new JOptionPane(texto, JOptionPane.WARNING_MESSAGE), "Atencao");
    }

    public static boolean confirmar(Component pai, String texto) {
        JOptionPane pane = new JOptionPane(texto, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        mostrar(pai, pane, "Confirmar");
        return Integer.valueOf(JOptionPane.YES_OPTION).equals(pane.getValue());
    }

    public static Frame quadro(Component pai) {
        Window janela = janela(pai);
        return janela instanceof Frame ? (Frame) janela : null;
    }

    public static String perguntar(Component pai, String mensagem, String titulo) {
        JOptionPane pane = new JOptionPane(mensagem, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        pane.setWantsInput(true);
        pane.setInitialSelectionValue("");
        mostrar(pai, pane, titulo);
        if (!Integer.valueOf(JOptionPane.OK_OPTION).equals(pane.getValue())) {
            return null;
        }
        Object valor = pane.getInputValue();
        if (valor == null || valor == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return valor.toString();
    }

    private static void mostrar(Component pai, JOptionPane pane, String titulo) {
        Window janela = janela(pai);
        JDialog dialog = pane.createDialog(janela, titulo);
        dialog.pack();
        if (janela != null && janela.isShowing()) {
            Point pos = janela.getLocationOnScreen();
            int x = pos.x + (janela.getWidth() - dialog.getWidth()) / 2;
            int y = pos.y + (janela.getHeight() - dialog.getHeight()) / 2;
            dialog.setLocation(Math.max(0, x), Math.max(0, y));
        } else {
            dialog.setLocationRelativeTo(null);
        }
        dialog.setVisible(true);
    }

    private static Window janela(Component pai) {
        if (pai instanceof Window) {
            return (Window) pai;
        }
        if (pai == null) {
            return null;
        }
        return SwingUtilities.getWindowAncestor(pai);
    }
}

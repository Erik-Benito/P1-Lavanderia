package br.senac.lavanderia;

import br.senac.lavanderia.terminal.MenuTerminal;
import br.senac.lavanderia.view.TelaPrincipal;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("terminal")) {
            MenuTerminal.main(args);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}

package br.senac.lavanderia;

import br.senac.lavanderia.terminal.MenuTerminal;
import br.senac.lavanderia.view.TelaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Ponto de entrada. Abre a tela grafica ou, com o argumento "terminal", o menu de terminal.
 */
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

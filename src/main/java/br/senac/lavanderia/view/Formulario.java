package br.senac.lavanderia.view;

import br.senac.lavanderia.util.Mensagens;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class Formulario {

    public static JPanel campos(String titulo) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(titulo));
        return form;
    }

    public static void linha(JPanel form, int y, String rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel(rotulo), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        form.add(campo, c);
    }

    public static boolean confirmar(Component pai, String titulo, JPanel form) {
        return Mensagens.confirmarPainel(pai, titulo, form);
    }
}

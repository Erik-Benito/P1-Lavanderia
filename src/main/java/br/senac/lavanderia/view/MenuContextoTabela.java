package br.senac.lavanderia.view;

import br.senac.lavanderia.util.Mensagens;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

/**
 * Menu do botao direito usado nas tabelas (Filtrar, Excluir, Cadastrar, Editar).
 */
public class MenuContextoTabela {

    public static void aplicar(JTable tabela, JScrollPane scroll, Runnable excluir, Runnable incluir, Runnable editar) {
        JPopupMenu menu = new JPopupMenu();
        item(menu, "Filtrar na tabela", () -> filtrar(tabela));
        menu.addSeparator();
        item(menu, "Excluir", excluir);
        item(menu, "Cadastrar", incluir);
        item(menu, "Editar", editar);

        tabela.setRowSorter(new TableRowSorter<>(tabela.getModel()));
        tabela.setComponentPopupMenu(menu);
        scroll.setComponentPopupMenu(menu);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());
                if (linha >= 0) {
                    tabela.setRowSelectionInterval(linha, linha);
                }
            }
        });
    }

    public static int linhaModelo(JTable tabela) {
        int visivel = tabela.getSelectedRow();
        return visivel < 0 ? -1 : tabela.convertRowIndexToModel(visivel);
    }

    private static void item(JPopupMenu menu, String texto, Runnable acao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> SwingUtilities.invokeLater(acao));
        menu.add(item);
    }

    private static void filtrar(JTable tabela) {
        TableRowSorter<?> sorter = (TableRowSorter<?>) tabela.getRowSorter();
        String texto = Mensagens.perguntar(
                tabela,
                "Texto para filtrar (vazio limpa o filtro):",
                "Filtrar na tabela");
        if (texto == null || texto.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }
}

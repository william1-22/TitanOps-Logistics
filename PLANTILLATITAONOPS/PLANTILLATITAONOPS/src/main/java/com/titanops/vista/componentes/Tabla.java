package com.titanops.vista.componentes;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class Tabla extends JTable {
    private Color fondoCabecera;
    private Color textoCabecera;
    private Color bordeCabecera;

    public Tabla() { instalarCabecera(); }
    public Color getFondoCabecera() { return fondoCabecera; }
    public void setFondoCabecera(Color valor) { Color anterior = fondoCabecera; fondoCabecera = valor; firePropertyChange("fondoCabecera", anterior, valor); getTableHeader().repaint(); }
    public Color getTextoCabecera() { return textoCabecera; }
    public void setTextoCabecera(Color valor) { Color anterior = textoCabecera; textoCabecera = valor; firePropertyChange("textoCabecera", anterior, valor); getTableHeader().repaint(); }
    public Color getBordeCabecera() { return bordeCabecera; }
    public void setBordeCabecera(Color valor) { Color anterior = bordeCabecera; bordeCabecera = valor; firePropertyChange("bordeCabecera", anterior, valor); getTableHeader().repaint(); }
    @Override public void updateUI() { super.updateUI(); instalarCabecera(); }
    private void instalarCabecera() {
        if (getTableHeader() == null) return;
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setBackground(fondoCabecera == null ? table.getBackground() : fondoCabecera);
                setForeground(textoCabecera == null ? table.getForeground() : textoCabecera);
                setFont(table.getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, bordeCabecera == null ? table.getGridColor() : bordeCabecera),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
                setIcon(null);
                if (table.getRowSorter() != null && !table.getRowSorter().getSortKeys().isEmpty()) {
                    RowSorter.SortKey key = table.getRowSorter().getSortKeys().get(0);
                    if (key.getColumn() == table.convertColumnIndexToModel(column))
                        setIcon(UIManager.getIcon(key.getSortOrder() == SortOrder.ASCENDING ? "Table.ascendingSortIcon" : "Table.descendingSortIcon"));
                }
                return this;
            }
        });
    }
}

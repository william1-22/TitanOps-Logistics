package com.titanops.vista.componentes;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/** Comportamiento compartido; los colores se asignan en cada .form. */
final class PinturaEstado {
    private PinturaEstado() { }

    static void observarFoco(JComponent componente) {
        componente.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { componente.repaint(); }
            @Override public void focusLost(FocusEvent e) { componente.repaint(); }
        });
    }

    static void foco(Graphics g, JComponent componente, Color color) {
        Component foco = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (color == null || foco == null || !(foco == componente
                || SwingUtilities.isDescendingFrom(foco, componente))) return;
        Graphics copia = g.create();
        copia.setColor(color);
        copia.drawRect(0, 0, componente.getWidth() - 1, componente.getHeight() - 1);
        copia.drawRect(1, 1, componente.getWidth() - 3, componente.getHeight() - 3);
        copia.dispose();
    }

    static void placeholder(Graphics g, JTextComponent campo, String texto, Color color) {
        if (texto == null || texto.isEmpty() || campo.getDocument().getLength() != 0) return;
        Graphics copia = g.create();
        Insets margen = campo.getInsets();
        copia.clipRect(margen.left, margen.top, Math.max(0, campo.getWidth() - margen.left - margen.right),
                Math.max(0, campo.getHeight() - margen.top - margen.bottom));
        copia.setColor(color == null ? campo.getForeground() : color);
        copia.setFont(campo.getFont());
        FontMetrics fm = copia.getFontMetrics();
        int y = campo instanceof JTextField ? (campo.getHeight() - fm.getHeight()) / 2 + fm.getAscent()
                : margen.top + fm.getAscent();
        copia.drawString(texto, margen.left, y);
        copia.dispose();
    }
}

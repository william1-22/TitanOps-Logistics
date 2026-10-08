package com.titanops.vista.componentes;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/** Estados configurables por instancia, sin paleta global. */
public class Boton extends JButton {
    private Color fondoActivo;
    private Color textoActivo;
    private Color colorFoco;

    public Boton() { PinturaEstado.observarFoco(this); }
    public Color getFondoActivo() { return fondoActivo; }
    public void setFondoActivo(Color valor) { Color anterior = fondoActivo; fondoActivo = valor; firePropertyChange("fondoActivo", anterior, valor); repaint(); }
    public Color getTextoActivo() { return textoActivo; }
    public void setTextoActivo(Color valor) { Color anterior = textoActivo; textoActivo = valor; firePropertyChange("textoActivo", anterior, valor); repaint(); }
    public Color getColorFoco() { return colorFoco; }
    public void setColorFoco(Color valor) { Color anterior = colorFoco; colorFoco = valor; firePropertyChange("colorFoco", anterior, valor); repaint(); }
    private boolean activo() { return isEnabled() && (getModel().isPressed() && getModel().isArmed() || getModel().isRollover() || getModel().isSelected()); }

    @Override public void updateUI() {
        setUI(new BasicButtonUI() {
            @Override public void update(Graphics g, JComponent c) { paint(g, c); }
            @Override protected void paintButtonPressed(Graphics g, AbstractButton b) { }
            @Override protected void paintFocus(Graphics g, AbstractButton b, Rectangle view, Rectangle text, Rectangle icon) { }
            @Override protected void paintText(Graphics g, AbstractButton b, Rectangle r, String text) {
                g.setColor(activo() && textoActivo != null ? textoActivo : getForeground());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, b.getDisplayedMnemonicIndex(),
                        r.x, r.y + g.getFontMetrics().getAscent());
            }
        });
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D copia = (Graphics2D) g.create();
        if (!isEnabled()) copia.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        copia.setColor(activo() && fondoActivo != null ? fondoActivo : getBackground());
        copia.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(copia);
        copia.dispose();
    }
    @Override protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        PinturaEstado.foco(g, this, colorFoco);
    }
}

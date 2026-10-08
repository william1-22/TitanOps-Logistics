package com.titanops.vista.componentes;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPasswordField;

/** Bean visual: cada instancia conserva sus propiedades en el diseñador. */
public class CampoClave extends JPasswordField {
    private Color colorFoco;
    private Color colorPlaceholder;
    private String placeholder = "";

    public CampoClave() { PinturaEstado.observarFoco(this); }
    public Color getColorFoco() { return colorFoco; }
    public void setColorFoco(Color valor) { Color anterior = colorFoco; colorFoco = valor; firePropertyChange("colorFoco", anterior, valor); repaint(); }
    public Color getColorPlaceholder() { return colorPlaceholder; }
    public void setColorPlaceholder(Color valor) { Color anterior = colorPlaceholder; colorPlaceholder = valor; firePropertyChange("colorPlaceholder", anterior, valor); repaint(); }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String valor) { String anterior = placeholder; placeholder = valor; firePropertyChange("placeholder", anterior, valor); repaint(); }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        PinturaEstado.placeholder(g, this, placeholder, colorPlaceholder);
    }
    @Override protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        PinturaEstado.foco(g, this, colorFoco);
    }
}

package com.titanops.vista.componentes;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComboBox;

public class Selector<E> extends JComboBox<E> {
    private Color colorFoco;
    public Selector() { PinturaEstado.observarFoco(this); }
    public Color getColorFoco() { return colorFoco; }
    public void setColorFoco(Color valor) { Color anterior = colorFoco; colorFoco = valor; firePropertyChange("colorFoco", anterior, valor); repaint(); }
    @Override protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        PinturaEstado.foco(g, this, colorFoco);
    }
}

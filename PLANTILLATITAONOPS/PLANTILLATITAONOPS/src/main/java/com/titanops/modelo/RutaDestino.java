package com.titanops.modelo;

import java.math.BigDecimal;

/** Mapea la tabla public.rutas_destinos de Supabase. */
public class RutaDestino {
    private int idRuta;
    private String nombreProyecto;
    private String puntoOrigen;
    private String puntoDestino;
    private BigDecimal distanciaKm;
    private String estado;

    public RutaDestino() {}

    public RutaDestino(int idRuta, String nombreProyecto, String puntoOrigen,
                       String puntoDestino, BigDecimal distanciaKm, String estado) {
        this.idRuta = idRuta;
        this.nombreProyecto = nombreProyecto;
        this.puntoOrigen = puntoOrigen;
        this.puntoDestino = puntoDestino;
        this.distanciaKm = distanciaKm;
        this.estado = estado;
    }

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getPuntoOrigen() { return puntoOrigen; }
    public void setPuntoOrigen(String puntoOrigen) { this.puntoOrigen = puntoOrigen; }

    public String getPuntoDestino() { return puntoDestino; }
    public void setPuntoDestino(String puntoDestino) { this.puntoDestino = puntoDestino; }

    public BigDecimal getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(BigDecimal distanciaKm) { this.distanciaKm = distanciaKm; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return nombreProyecto + ": " + puntoOrigen + " - " + puntoDestino;
    }
}

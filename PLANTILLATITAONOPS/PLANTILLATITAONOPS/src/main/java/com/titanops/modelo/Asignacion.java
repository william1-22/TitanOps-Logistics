package com.titanops.modelo;

import java.sql.Timestamp;

/** Mapea la tabla public.asignaciones de Supabase. */
public class Asignacion {
    private int idAsignacion;
    private int idMaquinaria;
    private int idOperador;
    private int idRuta;
    private int idUsuarioRegistro;
    private Timestamp fechaAsignacion;
    private Timestamp fechaEstimadaRetorno;
    private Timestamp fechaRetornoReal;
    private String estadoAsignacion;
    private String observaciones;

    public Asignacion() {}

    public Asignacion(int idAsignacion, int idMaquinaria, int idOperador,
                      int idRuta, int idUsuarioRegistro, Timestamp fechaAsignacion,
                      Timestamp fechaEstimadaRetorno, Timestamp fechaRetornoReal,
                      String estadoAsignacion, String observaciones) {
        this.idAsignacion = idAsignacion;
        this.idMaquinaria = idMaquinaria;
        this.idOperador = idOperador;
        this.idRuta = idRuta;
        this.idUsuarioRegistro = idUsuarioRegistro;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaEstimadaRetorno = fechaEstimadaRetorno;
        this.fechaRetornoReal = fechaRetornoReal;
        this.estadoAsignacion = estadoAsignacion;
        this.observaciones = observaciones;
    }

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public int getIdMaquinaria() { return idMaquinaria; }
    public void setIdMaquinaria(int idMaquinaria) { this.idMaquinaria = idMaquinaria; }

    public int getIdOperador() { return idOperador; }
    public void setIdOperador(int idOperador) { this.idOperador = idOperador; }

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public int getIdUsuarioRegistro() { return idUsuarioRegistro; }
    public void setIdUsuarioRegistro(int idUsuarioRegistro) { this.idUsuarioRegistro = idUsuarioRegistro; }

    public Timestamp getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Timestamp fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Timestamp getFechaEstimadaRetorno() { return fechaEstimadaRetorno; }
    public void setFechaEstimadaRetorno(Timestamp fechaEstimadaRetorno) { this.fechaEstimadaRetorno = fechaEstimadaRetorno; }

    public Timestamp getFechaRetornoReal() { return fechaRetornoReal; }
    public void setFechaRetornoReal(Timestamp fechaRetornoReal) { this.fechaRetornoReal = fechaRetornoReal; }

    public String getEstadoAsignacion() { return estadoAsignacion; }
    public void setEstadoAsignacion(String estadoAsignacion) { this.estadoAsignacion = estadoAsignacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}

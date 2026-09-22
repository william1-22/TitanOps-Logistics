package com.titanops.modelo;

import java.sql.Date;

/** Mapea la tabla public.operadores_certificaciones de Supabase. */
public class OperadorCertificacion {
    private int idCertificacion;
    private int idOperador;
    private int idCategoria;
    private String numeroAcreditacion;
    private Date fechaExpedicion;
    private Date fechaVencimiento;

    public OperadorCertificacion() {}

    public OperadorCertificacion(int idCertificacion, int idOperador,
                                 int idCategoria, String numeroAcreditacion,
                                 Date fechaExpedicion, Date fechaVencimiento) {
        this.idCertificacion = idCertificacion;
        this.idOperador = idOperador;
        this.idCategoria = idCategoria;
        this.numeroAcreditacion = numeroAcreditacion;
        this.fechaExpedicion = fechaExpedicion;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdCertificacion() { return idCertificacion; }
    public void setIdCertificacion(int idCertificacion) { this.idCertificacion = idCertificacion; }

    public int getIdOperador() { return idOperador; }
    public void setIdOperador(int idOperador) { this.idOperador = idOperador; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNumeroAcreditacion() { return numeroAcreditacion; }
    public void setNumeroAcreditacion(String numeroAcreditacion) { this.numeroAcreditacion = numeroAcreditacion; }

    public Date getFechaExpedicion() { return fechaExpedicion; }
    public void setFechaExpedicion(Date fechaExpedicion) { this.fechaExpedicion = fechaExpedicion; }

    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}

package com.titanops.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** Mapea la tabla public.mantenimientos de Supabase. */
public class Mantenimiento {
    private int idMantenimiento;
    private int idMaquinaria;
    private int idUsuarioRegistro;
    private String tipoMantenimiento;
    private Timestamp fechaIngreso;
    private Timestamp fechaSalidaEstimada;
    private Timestamp fechaSalidaReal;
    private String diagnostico;
    private BigDecimal costo;
    private String estadoMantenimiento;
    private String tallerResponsable;

    public Mantenimiento() {}

    public Mantenimiento(int idMantenimiento, int idMaquinaria,
                         int idUsuarioRegistro, String tipoMantenimiento,
                         Timestamp fechaIngreso, Timestamp fechaSalidaEstimada,
                         Timestamp fechaSalidaReal, String diagnostico,
                         BigDecimal costo, String estadoMantenimiento,
                         String tallerResponsable) {
        this.idMantenimiento = idMantenimiento;
        this.idMaquinaria = idMaquinaria;
        this.idUsuarioRegistro = idUsuarioRegistro;
        this.tipoMantenimiento = tipoMantenimiento;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalidaEstimada = fechaSalidaEstimada;
        this.fechaSalidaReal = fechaSalidaReal;
        this.diagnostico = diagnostico;
        this.costo = costo;
        this.estadoMantenimiento = estadoMantenimiento;
        this.tallerResponsable = tallerResponsable;
    }

    public int getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(int idMantenimiento) { this.idMantenimiento = idMantenimiento; }

    public int getIdMaquinaria() { return idMaquinaria; }
    public void setIdMaquinaria(int idMaquinaria) { this.idMaquinaria = idMaquinaria; }

    public int getIdUsuarioRegistro() { return idUsuarioRegistro; }
    public void setIdUsuarioRegistro(int idUsuarioRegistro) { this.idUsuarioRegistro = idUsuarioRegistro; }

    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String tipoMantenimiento) { this.tipoMantenimiento = tipoMantenimiento; }

    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Timestamp getFechaSalidaEstimada() { return fechaSalidaEstimada; }
    public void setFechaSalidaEstimada(Timestamp fechaSalidaEstimada) { this.fechaSalidaEstimada = fechaSalidaEstimada; }

    public Timestamp getFechaSalidaReal() { return fechaSalidaReal; }
    public void setFechaSalidaReal(Timestamp fechaSalidaReal) { this.fechaSalidaReal = fechaSalidaReal; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getEstadoMantenimiento() { return estadoMantenimiento; }
    public void setEstadoMantenimiento(String estadoMantenimiento) { this.estadoMantenimiento = estadoMantenimiento; }

    public String getTallerResponsable() { return tallerResponsable; }
    public void setTallerResponsable(String tallerResponsable) { this.tallerResponsable = tallerResponsable; }
}

package com.titanops.modelo;

import java.sql.Timestamp;

/** Mapea la tabla public.operadores de Supabase. */
public class Operador {
    private int idOperador;
    private String nombres;
    private String apellidos;
    private String dui;
    private String licenciaTipo;
    private String turno;
    private String telefono;
    private String estadoOperativo;
    private Boolean activo;
    private Timestamp fechaRegistro;

    public Operador() {}

    public Operador(int idOperador, String nombres, String apellidos, String dui,
                    String licenciaTipo, String turno, String telefono,
                    String estadoOperativo, Boolean activo, Timestamp fechaRegistro) {
        this.idOperador = idOperador;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dui = dui;
        this.licenciaTipo = licenciaTipo;
        this.turno = turno;
        this.telefono = telefono;
        this.estadoOperativo = estadoOperativo;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdOperador() { return idOperador; }
    public void setIdOperador(int idOperador) { this.idOperador = idOperador; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getLicenciaTipo() { return licenciaTipo; }
    public void setLicenciaTipo(String licenciaTipo) { this.licenciaTipo = licenciaTipo; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstadoOperativo() { return estadoOperativo; }
    public void setEstadoOperativo(String estadoOperativo) { this.estadoOperativo = estadoOperativo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return nombres + " " + apellidos;
    }
}

package com.titanops.modelo;

import java.sql.Timestamp;

/** Mapea la tabla public.usuarios de Supabase. */
public class Usuario {
    private int idUsuario;
    private int idRol;
    private String nombreCompleto;
    private String username;
    private String claveHash;
    private Boolean activo;
    private Timestamp fechaCreacion;

    public Usuario() {}

    public Usuario(int idUsuario, int idRol, String nombreCompleto, String username,
                   String claveHash, Boolean activo, Timestamp fechaCreacion) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.claveHash = claveHash;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClaveHash() { return claveHash; }
    public void setClaveHash(String claveHash) { this.claveHash = claveHash; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() {
        return nombreCompleto + " (" + username + ")";
    }
}

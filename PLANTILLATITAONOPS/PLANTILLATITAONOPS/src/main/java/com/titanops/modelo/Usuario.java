package com.titanops.modelo;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private String nombreCompleto;
    private String dui;
    private String correo;
    private String username;
    private String passwordHash;
    private int idRol;
    private String nombreRol;
    private String estado;
    private Timestamp fechaCreacion;

    public Usuario() {}

    public Usuario(int idUsuario, String nombreCompleto, String dui, String correo, 
                   String username, String passwordHash, int idRol, String estado) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.dui = dui;
        this.correo = correo;
        this.username = username;
        this.passwordHash = passwordHash;
        this.idRol = idRol;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
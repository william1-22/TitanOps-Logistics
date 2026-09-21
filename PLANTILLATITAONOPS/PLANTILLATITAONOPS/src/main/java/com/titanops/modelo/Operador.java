package com.titanops.modelo;

public class Operador {
    private int idOperador;
    private String nombre;
    private String apellido;
    private String dui;
    private String telefono;
    private String tipoLicencia;
    private String estado;

    public Operador() {}

    public Operador(int idOperador, String nombre, String apellido, String dui, 
                    String telefono, String tipoLicencia, String estado) {
        this.idOperador = idOperador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dui = dui;
        this.telefono = telefono;
        this.tipoLicencia = tipoLicencia;
        this.estado = estado;
    }

    public int getIdOperador() { return idOperador; }
    public void setIdOperador(int idOperador) { this.idOperador = idOperador; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoLicencia() { return tipoLicencia; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
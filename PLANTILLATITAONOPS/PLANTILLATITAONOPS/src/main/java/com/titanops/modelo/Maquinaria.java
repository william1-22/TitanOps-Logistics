package com.titanops.modelo;

import java.math.BigDecimal;

public class Maquinaria {
    private int idMaquinaria;
    private String codigo;
    private String nombre;
    private int idCategoria;
    private String nombreCategoria;
    private String marca;
    private String modelo;
    private Integer anio;
    private String numeroSerie;
    private String estadoOperativo;
    private BigDecimal costoHora;

    public Maquinaria() {}

    public Maquinaria(int idMaquinaria, String codigo, String nombre, int idCategoria, 
                      String marca, String modelo, Integer anio, String numeroSerie, 
                      String estadoOperativo, BigDecimal costoHora) {
        this.idMaquinaria = idMaquinaria;
        this.codigo = codigo;
        this.nombre = nombre;
        this.idCategoria = idCategoria;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.numeroSerie = numeroSerie;
        this.estadoOperativo = estadoOperativo;
        this.costoHora = costoHora;
    }

    public int getIdMaquinaria() { return idMaquinaria; }
    public void setIdMaquinaria(int idMaquinaria) { this.idMaquinaria = idMaquinaria; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getEstadoOperativo() { return estadoOperativo; }
    public void setEstadoOperativo(String estadoOperativo) { this.estadoOperativo = estadoOperativo; }

    public BigDecimal getCostoHora() { return costoHora; }
    public void setCostoHora(BigDecimal costoHora) { this.costoHora = costoHora; }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
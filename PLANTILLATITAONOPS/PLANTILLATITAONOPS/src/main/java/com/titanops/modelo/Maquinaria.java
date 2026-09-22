package com.titanops.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** Mapea la tabla public.maquinaria de Supabase. */
public class Maquinaria {
    private int idMaquinaria;
    private int idCategoria;
    private String codigoInventario;
    private String marca;
    private String modelo;
    private BigDecimal tonelaje;
    private BigDecimal horasUso;
    private String estadoOperativo;
    private Boolean activo;
    private Timestamp fechaRegistro;

    public Maquinaria() {}

    public Maquinaria(int idMaquinaria, int idCategoria, String codigoInventario,
                      String marca, String modelo, BigDecimal tonelaje,
                      BigDecimal horasUso, String estadoOperativo, Boolean activo,
                      Timestamp fechaRegistro) {
        this.idMaquinaria = idMaquinaria;
        this.idCategoria = idCategoria;
        this.codigoInventario = codigoInventario;
        this.marca = marca;
        this.modelo = modelo;
        this.tonelaje = tonelaje;
        this.horasUso = horasUso;
        this.estadoOperativo = estadoOperativo;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdMaquinaria() { return idMaquinaria; }
    public void setIdMaquinaria(int idMaquinaria) { this.idMaquinaria = idMaquinaria; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getCodigoInventario() { return codigoInventario; }
    public void setCodigoInventario(String codigoInventario) { this.codigoInventario = codigoInventario; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public BigDecimal getTonelaje() { return tonelaje; }
    public void setTonelaje(BigDecimal tonelaje) { this.tonelaje = tonelaje; }

    public BigDecimal getHorasUso() { return horasUso; }
    public void setHorasUso(BigDecimal horasUso) { this.horasUso = horasUso; }

    public String getEstadoOperativo() { return estadoOperativo; }
    public void setEstadoOperativo(String estadoOperativo) { this.estadoOperativo = estadoOperativo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        String descripcion = marca == null ? "" : " - " + marca;
        if (modelo != null && !modelo.isBlank()) {
            descripcion += " " + modelo;
        }
        return codigoInventario + descripcion;
    }
}

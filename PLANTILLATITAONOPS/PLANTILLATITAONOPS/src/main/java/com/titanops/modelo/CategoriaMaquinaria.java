package com.titanops.modelo;

/** Mapea la tabla public.categorias_maquinaria de Supabase. */
public class CategoriaMaquinaria {
    private int idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private Boolean activo;

    public CategoriaMaquinaria() {}

    public CategoriaMaquinaria(int idCategoria, String nombreCategoria,
                               String descripcion, Boolean activo) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreCategoria;
    }
}

package com.titanops.dao;

import java.util.List;

public interface CrudDAO<T> {
    boolean insertar(T entidad);
    boolean actualizar(T entidad);
    boolean eliminar(int id);
    T obtenerPorId(int id);
    List<T> listarTodos();
}
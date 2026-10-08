package com.titanops.reporte;

import com.titanops.controlador.GestionControlReportesController.EstadoMaquinariaFila;
import com.titanops.controlador.GestionControlReportesController.ResumenControl;
import java.time.Instant;
import java.util.List;

/** Datos congelados de un reporte de estado para exportarlo en distintos formatos. */
public record ReporteEstadoMaquinaria(
        List<EstadoMaquinariaFila> filas,
        ResumenControl resumen,
        String filtro,
        String estado,
        Instant generadoEn) {

    public ReporteEstadoMaquinaria {
        filas = List.copyOf(filas == null ? List.of() : filas);
        resumen = resumen == null ? new ResumenControl(0, 0, 0, 0) : resumen;
        filtro = filtro == null ? "" : filtro;
        estado = estado == null ? "TODOS" : estado;
        generadoEn = generadoEn == null ? Instant.now() : generadoEn;
    }
}

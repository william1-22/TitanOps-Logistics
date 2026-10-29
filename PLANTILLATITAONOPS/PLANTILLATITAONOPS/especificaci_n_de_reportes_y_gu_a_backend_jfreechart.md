# Especificación Técnica de Reportes y Arquitectura Backend con JFreeChart (Java / NetBeans)

Este documento detalla la definición funcional, matemática y de origen de datos de los **9 reportes operativos del sistema de gestión de maquinaria** (excluyendo el reporte #4 de kilometraje/tonelaje por decisión de equipo), además de la guía paso a paso para implementar la capa de datos y generación de gráficos en **Java (NetBeans)** utilizando **JFreeChart**, manteniendo **cero acoplamiento con el frontend (vistas Swing)**.

---

## Parte 1: Catálogo de Reportes, Mejoras y Origen de Datos

### 1. Ratio de Mantenimiento Preventivo vs. Correctivo
* **Enfoque mejorado:** Cruza la frecuencia de ingresos al taller contra el impacto financiero y el tiempo muerto por periodo. Permite evaluar si el plan preventivo está evitando reparaciones costosas.
* **Filtros de entrada:** `fecha_inicio`, `fecha_fin` (sobre `fecha_ingreso`), `id_maquinaria` (opcional/todas), `id_categoria` (opcional).
* **Origen de Datos (`TABLA.campo`):**
  * `MAQUINARIA`: `id_maquinaria`, `codigo_inventario`, `marca`, `modelo`
  * `CATEGORIAS_MAQUINARIA`: `nombre_categoria`
  * `MANTENIMIENTOS`: `tipo_mantenimiento` (`PREVENTIVO`, `CORRECTIVO`), `fecha_ingreso`, `fecha_salida_real`, `costo`, `taller_responsable`, `estado_mantenimiento`
* **Fórmulas / KPIs:**
  * $\text{\% Incidencias Correctivas} = \left(\frac{\text{Conteo Correctivos}}{\text{Total Mantenimientos}}\right) \times 100$
  * $\text{\% Gasto Correctivo} = \left(\frac{\sum \text{costo}_{\text{CORRECTIVO}}}{\sum \text{costo}_{\text{TOTAL}}}\right) \times 100$
* **Gráfico JFreeChart sugerido:** `StackedBarChart` (Barras apiladas por máquina o mes) o `PieChart` (Proporción Preventivo vs. Correctivo).

---

### 2. Costo de Mantenimiento por Hora de Uso (Índice TCO Operativo)
* **Enfoque mejorado:** Detector de rentabilidad de activos. Calcula cuánto cuesta en taller cada hora que la máquina ha encendido el motor, identificando "máquinas vampiro".
* **Filtros de entrada:** `id_categoria`, `activo` (`true`/`false`).
* **Origen de Datos (`TABLA.campo`):**
  * `MAQUINARIA`: `id_maquinaria`, `codigo_inventario`, `marca`, `modelo`, `horas_uso`, `activo`
  * `CATEGORIAS_MAQUINARIA`: `nombre_categoria`
  * `MANTENIMIENTOS`: `SUM(costo)` donde `estado_mantenimiento != 'CANCELADO'`
* **Fórmulas / KPIs:**
  * $$\text{Costo por Hora (CPH)} = \frac{\sum \text{MANTENIMIENTOS.costo}}{\text{NULLIF}(\text{MAQUINARIA.horas\_uso}, 0)}$$
  * $$\text{Costo Promedio por Intervención} = \frac{\sum \text{MANTENIMIENTOS.costo}}{\text{COUNT(id\_mantenimiento)}}$$
* **Gráfico JFreeChart sugerido:** `BarChart` horizontal ordenando las máquinas de mayor a menor CPH.

---

### 3. Cumplimiento de Tiempos de Rutas (SLA Logístico / Puntualidad)
* **Enfoque mejorado:** Semáforo de puntualidad (*On-Time Delivery*). Clasifica cada viaje finalizado como *A Tiempo / Adelantado* ($\Delta t \le 0$) o *Retrasado* ($\Delta t > 0$), midiendo las horas exactas de desviación.
* **Filtros de entrada:** `fecha_inicio`, `fecha_fin`, `id_ruta`, `id_operador` (solo con `estado_asignacion = 'FINALIZADA'`).
* **Origen de Datos (`TABLA.campo`):**
  * `RUTAS_DESTINOS`: `nombre_proyecto`, `punto_origen`, `punto_destino`
  * `OPERADORES`: `nombres`, `apellidos`
  * `MAQUINARIA`: `codigo_inventario`
  * `ASIGNACIONES`: `fecha_asignacion`, `fecha_estimada_retorno`, `fecha_retorno_real`, `estado_asignacion`
* **Fórmulas / KPIs:**
  * $\Delta t \text{ (Horas)} = \frac{\text{EPOCH}(\text{fecha\_retorno\_real} - \text{fecha\_estimada\_retorno})}{3600}$
  * $\text{\% Puntualidad Global} = \left(\frac{\text{Viajes con } \Delta t \le 0}{\text{Total Viajes Finalizados}}\right) \times 100$
* **Gráfico JFreeChart sugerido:** `PieChart` (A tiempo vs. Retrasados) o `BarChart` de desviación promedio en horas por proyecto.

---

### 4. *(Descartado por requerimiento del equipo: Kilometraje y Tonelaje por Proyecto)*

---

### 5. Maquinarias: Datos Generales y Disponibilidad en Tiempo Real (Snapshot Maestro)
* **Enfoque mejorado:** Tablero de control de inventario que consolida ficha técnica, estado actual (`DISPONIBLE`, `EN_RUTA`, `MANTENIMIENTO`), estado administrativo (`activo`) y conteo histórico de rutas y talleres en una sola vista.
* **Filtros de entrada:** `activo` (`true`/`false`), `estado_operativo`, `id_categoria`.
* **Origen de Datos (`TABLA.campo`):**
  * `MAQUINARIA`: `codigo_inventario`, `marca`, `modelo`, `tonelaje`, `horas_uso`, `estado_operativo`, `activo`, `fecha_registro`
  * `CATEGORIAS_MAQUINARIA`: `nombre_categoria`
  * Subconsultas agregadas: `COUNT(ASIGNACIONES.id_asignacion)`, `COUNT(MANTENIMIENTOS.id_mantenimiento)`, `MAX(ASIGNACIONES.fecha_asignacion)`.
* **Fórmulas / KPIs:**
  * $\text{Tasa de Disponibilidad} = \left(\frac{\text{Máquinas Activas en estado DISPONIBLE}}{\text{Total Máquinas Activas}}\right) \times 100$
* **Gráfico JFreeChart sugerido:** `RingChart` o `PieChart` mostrando la distribución actual (`DISPONIBLE`, `EN_RUTA`, `MANTENIMIENTO`).

---

### 6. Reporte Integral de Certificaciones y Vigencia Operativa (Compliance)
* **Enfoque mejorado:** Matriz de riesgo y bloqueo preventivo. Evalúa los días restantes de vigencia para alertar qué operadores están vencidos o próximos a vencer (30 días) y qué categorías corren riesgo de quedarse sin personal autorizado.
* **Filtros de entrada:** Estado de vigencia (`VENCIDA`, `POR_VENCER`, `VIGENTE`), `id_categoria`, `licencia_tipo`.
* **Origen de Datos (`TABLA.campo`):**
  * `OPERADORES`: `nombres`, `apellidos`, `dui`, `licencia_tipo`, `estado_operativo`, `activo`
  * `OPERADORES_CERTIFICACIONES`: `numero_acreditacion`, `fecha_expedicion`, `fecha_vencimiento`
  * `CATEGORIAS_MAQUINARIA`: `nombre_categoria`
* **Fórmulas / KPIs:**
  * $\text{Días Restantes} = \text{fecha\_vencimiento} - \text{CURRENT\_DATE}$
  * Clasificación: $\le 0 \rightarrow \text{VENCIDA}$; $1 \text{ a } 30 \rightarrow \text{POR VENCER}$; $> 30 \rightarrow \text{VIGENTE}$.
* **Gráfico JFreeChart sugerido:** `BarChart` agrupado por Categoría de Maquinaria mostrando certificaciones Vigentes, Por Vencer y Vencidas.

---

### 7. Reporte de Auditoría y Gestión de Usuarios
* **Enfoque mejorado:** Mide el volumen de trabajo administrativo (despachos y órdenes de taller registradas por usuario) y audita qué usuarios registran mayor tasa de cancelaciones o costos de taller autorizados.
* **Filtros de entrada:** `id_rol`, `id_usuario`, `activo`, rango de fechas.
* **Origen de Datos (`TABLA.campo`):**
  * `USUARIOS`: `id_usuario`, `nombre_completo`, `username`, `activo`, `fecha_creacion`
  * `ROLES`: `nombre_rol`
  * `ASIGNACIONES`: `id_usuario_registro`, `estado_asignacion`, `fecha_asignacion`
  * `MANTENIMIENTOS`: `id_usuario_registro`, `costo`, `estado_mantenimiento`, `fecha_ingreso`
* **Fórmulas / KPIs:**
  * Total despachos registrados (`COUNT(DISTINCT id_asignacion)`) y total mantenimientos (`COUNT(DISTINCT id_mantenimiento)`).
  * Monto financiero gestionado en taller (`SUM(MANTENIMIENTOS.costo)`).
* **Gráfico JFreeChart sugerido:** `BarChart` comparando asignaciones vs. mantenimientos registrados por cada usuario.

---

### 8. Reporte de Utilización y Desgaste de Flota
* **Enfoque mejorado:** Compara la intensidad de desgaste diario desde el registro de la máquina y contrasta las horas productivas en ruta contra las horas muertas en taller (*Downtime*).
* **Filtros de entrada:** `id_categoria`, rango de fechas.
* **Origen de Datos (`TABLA.campo`):**
  * `MAQUINARIA`: `codigo_inventario`, `marca`, `modelo`, `horas_uso`, `fecha_registro`
  * `CATEGORIAS_MAQUINARIA`: `nombre_categoria`
  * `ASIGNACIONES`: $\sum(\text{fecha\_retorno\_real} - \text{fecha\_asignacion})$
  * `MANTENIMIENTOS`: $\sum(\text{fecha\_salida\_real} - \text{fecha\_ingreso})$
* **Fórmulas / KPIs:**
  * $\text{Antigüedad (Días)} = \text{CURRENT\_DATE} - \text{DATE(fecha\_registro)}$
  * $\text{Promedio Uso Diario} = \frac{\text{horas\_uso}}{\text{NULLIF(Antigüedad}, 0)}$
* **Gráfico JFreeChart sugerido:** `CategoryPlot` de barras dobles (Horas en Ruta vs. Horas en Taller por unidad).

---

### 9. Historial de Cumplimiento de Rutas, Proyectos y Bitácora de Observaciones
* **Enfoque mejorado:** Evalúa la tasa de éxito de cada proyecto (viajes realizados vs. cancelados) y extrae el registro cualitativo del campo `observaciones` reportado por el área de operadores durante la entrega.
* **Filtros de entrada:** `id_ruta`, `RUTAS_DESTINOS.estado`, `ASIGNACIONES.estado_asignacion`, `id_operador`.
* **Origen de Datos (`TABLA.campo`):**
  * `RUTAS_DESTINOS`: `nombre_proyecto`, `punto_origen`, `punto_destino`, `estado`
  * `ASIGNACIONES`: `id_asignacion`, `estado_asignacion`, `fecha_asignacion`, `fecha_retorno_real`, `observaciones`
  * `OPERADORES`: `nombres`, `apellidos`, `turno`, `telefono`
  * `MAQUINARIA`: `codigo_inventario`
* **Fórmulas / KPIs:**
  * $\text{Tasa de Efectividad} = \left(\frac{\text{Viajes FINALIZADA}}{\text{Total Viajes del Proyecto}}\right) \times 100$
  * $\text{Tasa de Cancelación} = \left(\frac{\text{Viajes CANCELADA}}{\text{Total Viajes del Proyecto}}\right) \times 100$
* **Gráfico JFreeChart sugerido:** `StackedBarChart` por proyecto mostrando viajes Finalizados, En Curso y Cancelados.

---

### 10. Productividad y Carga Laboral de Operadores por Turno
* **Enfoque mejorado:** Analiza el rendimiento humano y balanceo de carga cruzando viajes completados, horas conducidas y polivalencia (cantidad de certificaciones) agrupado por turno (`DIURNO`, `NOCTURNO`, `ROTATIVO`).
* **Filtros de entrada:** `turno`, `estado_operativo`, rango de fechas.
* **Origen de Datos (`TABLA.campo`):**
  * `OPERADORES`: `id_operador`, `nombres`, `apellidos`, `dui`, `licencia_tipo`, `turno`, `estado_operativo`
  * `ASIGNACIONES`: `id_asignacion`, `estado_asignacion`, `fecha_asignacion`, `fecha_retorno_real`
  * `OPERADORES_CERTIFICACIONES`: `COUNT(DISTINCT id_categoria)`
* **Fórmulas / KPIs:**
  * $\text{Horas Conducidas} = \sum \frac{\text{EPOCH}(\text{fecha\_retorno\_real} - \text{fecha\_asignacion})}{3600}$
  * Promedio de cumplimiento y cancelaciones agrupado por `OPERADORES.turno`.
* **Gráfico JFreeChart sugerido:** `BarChart` de horas conducidas y viajes completados agrupado por operador o por turno.

---

## Parte 2: Instalación de JFreeChart en Apache NetBeans

Para trabajar en una aplicación de escritorio Java sin tocar las vistas actuales, primero debes agregar la librería al proyecto.

### Opción A: Proyecto basado en Maven (Recomendado en NetBeans moderno)
Abre el archivo `pom.xml` dentro de **Project Files** y agrega la siguiente dependencia dentro de la etiqueta `<dependencies>`:

```xml
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.4</version>
</dependency>
```
*(Haz clic derecho sobre el proyecto en NetBeans y selecciona **Build with Dependencies** para descargar el `.jar` automáticamente).*

### Opción B: Proyecto Java Tradicional (Java with Ant)
1. Descarga `jfreechart-1.5.4.jar` (o si usas la versión `1.0.19`, incluye también `jcommon-1.0.23.jar`) desde Maven Central o SourceForge.
2. En NetBeans, ve al panel **Projects**, haz clic derecho sobre la carpeta **Libraries** de tu proyecto y selecciona **Add JAR/Folder...**
3. Selecciona los archivos `.jar` descargados y presiona **Open**.

---

## Parte 3: Arquitectura Backend Desacoplada (Sin tocar Vistas)

Para cumplir con la regla de **no tocar los `JFrame` ni el código del diseñador visual de NetBeans**, implementaremos 3 paquetes/capas puras de backend:

1. **`dto` (Data Transfer Objects):** Clases simples que guardan las filas calculadas de cada reporte (sirven tanto para llenar un `DefaultTableModel` como para alimentar a JFreeChart).
2. **`dao` (Data Access Object):** Ejecuta los `PreparedStatement` con las consultas SQL y devuelve listas de DTOs (`List<...>`) o datasets.
3. **`service` (Servicio de Gráficos JFreeChart):** Recibe los datos del DAO y construye objetos `JFreeChart` o `JPanel` (`ChartPanel`) listos para ser incrustados en cualquier contenedor cuando el equipo de frontend lo decida, o exportados directamente a imagen PNG/PDF sin abrir ninguna ventana.

---

### Paso 1: Crear los DTOs (`src/main/java/com/sistema/reportes/dto/ReportesDTO.java`)

```java
package com.sistema.reportes.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Contenedor de DTOs para los reportes del sistema.
 * Puedes separarlos en archivos individuales si lo prefieres.
 */
public class ReportesDTO {

    // DTO Reporte 1: Ratio Preventivo vs Correctivo
    public static class RatioMantenimientoDTO {
        private String codigoInventario;
        private String categoria;
        private String tipoMantenimiento;
        private int totalIntervenciones;
        private BigDecimal costoTotal;
        private double horasTaller;

        public RatioMantenimientoDTO(String codigoInventario, String categoria, String tipoMantenimiento,
                                     int totalIntervenciones, BigDecimal costoTotal, double horasTaller) {
            this.codigoInventario = codigoInventario;
            this.categoria = categoria;
            this.tipoMantenimiento = tipoMantenimiento;
            this.totalIntervenciones = totalIntervenciones;
            this.costoTotal = costoTotal;
            this.horasTaller = horasTaller;
        }

        public String getCodigoInventario() { return codigoInventario; }
        public String getCategoria() { return categoria; }
        public String getTipoMantenimiento() { return tipoMantenimiento; }
        public int getTotalIntervenciones() { return totalIntervenciones; }
        public BigDecimal getCostoTotal() { return costoTotal; }
        public double getHorasTaller() { return horasTaller; }
    }

    // DTO Reporte 2: Costo por Hora de Uso
    public static class CostoHoraUsoDTO {
        private String codigoInventario;
        private String marcaModelo;
        private String categoria;
        private BigDecimal horasUso;
        private BigDecimal costoAcumulado;
        private BigDecimal costoPorHora;

        public CostoHoraUsoDTO(String codigoInventario, String marcaModelo, String categoria,
                               BigDecimal horasUso, BigDecimal costoAcumulado, BigDecimal costoPorHora) {
            this.codigoInventario = codigoInventario;
            this.marcaModelo = marcaModelo;
            this.categoria = categoria;
            this.horasUso = horasUso;
            this.costoAcumulado = costoAcumulado;
            this.costoPorHora = costoPorHora;
        }

        public String getCodigoInventario() { return codigoInventario; }
        public String getMarcaModelo() { return marcaModelo; }
        public String getCategoria() { return categoria; }
        public BigDecimal getHorasUso() { return horasUso; }
        public BigDecimal getCostoAcumulado() { return costoAcumulado; }
        public BigDecimal getCostoPorHora() { return costoPorHora; }
    }

    // DTO Reporte 6: Semáforo de Certificaciones
    public static class CertificacionVigenciaDTO {
        private String nombreOperador;
        private String dui;
        private String categoriaAutorizada;
        private String numeroAcreditacion;
        private int diasRestantes;
        private String estadoVigencia; // VENCIDA, POR_VENCER, VIGENTE

        public CertificacionVigenciaDTO(String nombreOperador, String dui, String categoriaAutorizada,
                                        String numeroAcreditacion, int diasRestantes, String estadoVigencia) {
            this.nombreOperador = nombreOperador;
            this.dui = dui;
            this.categoriaAutorizada = categoriaAutorizada;
            this.numeroAcreditacion = numeroAcreditacion;
            this.diasRestantes = diasRestantes;
            this.estadoVigencia = estadoVigencia;
        }

        public String getNombreOperador() { return nombreOperador; }
        public String getDui() { return dui; }
        public String getCategoriaAutorizada() { return categoriaAutorizada; }
        public String getNumeroAcreditacion() { return numeroAcreditacion; }
        public int getDiasRestantes() { return diasRestantes; }
        public String getEstadoVigencia() { return estadoVigencia; }
    }

    // DTO Reporte 9: Historial de Rutas y Observaciones
    public static class HistorialRutaDTO {
        private String nombreProyecto;
        private String origenDestino;
        private String codigoMaquinaria;
        private String operador;
        private String estadoAsignacion;
        private Timestamp fechaAsignacion;
        private Timestamp fechaRetornoReal;
        private String observaciones;

        public HistorialRutaDTO(String nombreProyecto, String origenDestino, String codigoMaquinaria,
                                String operador, String estadoAsignacion, Timestamp fechaAsignacion,
                                Timestamp fechaRetornoReal, String observaciones) {
            this.nombreProyecto = nombreProyecto;
            this.origenDestino = origenDestino;
            this.codigoMaquinaria = codigoMaquinaria;
            this.operador = operador;
            this.estadoAsignacion = estadoAsignacion;
            this.fechaAsignacion = fechaAsignacion;
            this.fechaRetornoReal = fechaRetornoReal;
            this.observaciones = observaciones;
        }

        public String getNombreProyecto() { return nombreProyecto; }
        public String getOrigenDestino() { return origenDestino; }
        public String getCodigoMaquinaria() { return codigoMaquinaria; }
        public String getOperador() { return operador; }
        public String getEstadoAsignacion() { return estadoAsignacion; }
        public Timestamp getFechaAsignacion() { return fechaAsignacion; }
        public Timestamp getFechaRetornoReal() { return fechaRetornoReal; }
        public String getObservaciones() { return observaciones; }
    }
}
```

---

### Paso 2: Crear el DAO de Consultas (`src/main/java/com/sistema/reportes/dao/ReportesDAO.java`)

Esta clase recibe tu `java.sql.Connection` activa y ejecuta las consultas SQL exactas según tu diagrama ER (compatible con PostgreSQL; si usas MySQL/MariaDB solo cambia `EXTRACT(EPOCH FROM ...)` por `TIMESTAMPDIFF`).

```java
package com.sistema.reportes.dao;

import com.sistema.reportes.dto.ReportesDTO.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportesDAO {

    private final Connection conexion;

    public ReportesDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * REPORTE 1: Ratio Preventivo vs Correctivo filtrado por fechas y maquinaria opcional.
     */
    public List<RatioMantenimientoDTO> obtenerRatioMantenimiento(Date fechaInicio, Date fechaFin, Integer idMaquinaria) throws SQLException {
        String sql = "SELECT m.codigo_inventario, c.nombre_categoria, mt.tipo_mantenimiento, "
                   + "COUNT(mt.id_mantenimiento) AS total_intervenciones, "
                   + "COALESCE(SUM(mt.costo), 0) AS costo_total, "
                   + "COALESCE(SUM(EXTRACT(EPOCH FROM (COALESCE(mt.fecha_salida_real, NOW()) - mt.fecha_ingreso)) / 3600), 0) AS horas_taller "
                   + "FROM MANTENIMIENTOS mt "
                   + "INNER JOIN MAQUINARIA m ON mt.id_maquinaria = m.id_maquinaria "
                   + "INNER JOIN CATEGORIAS_MAQUINARIA c ON m.id_categoria = c.id_categoria "
                   + "WHERE mt.estado_mantenimiento != 'CANCELADO' "
                   + "AND DATE(mt.fecha_ingreso) BETWEEN ? AND ? "
                   + (idMaquinaria != null ? "AND m.id_maquinaria = ? " : "")
                   + "GROUP BY m.codigo_inventario, c.nombre_categoria, mt.tipo_mantenimiento "
                   + "ORDER BY m.codigo_inventario, mt.tipo_mantenimiento";

        List<RatioMantenimientoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            if (idMaquinaria != null) {
                ps.setInt(3, idMaquinaria);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new RatioMantenimientoDTO(
                        rs.getString("codigo_inventario"),
                        rs.getString("nombre_categoria"),
                        rs.getString("tipo_mantenimiento"),
                        rs.getInt("total_intervenciones"),
                        rs.getBigDecimal("costo_total"),
                        rs.getDouble("horas_taller")
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * REPORTE 2: Costo de Mantenimiento por Hora de Uso.
     */
    public List<CostoHoraUsoDTO> obtenerCostoPorHoraUso() throws SQLException {
        String sql = "SELECT m.codigo_inventario, "
                   + "CONCAT(COALESCE(m.marca, ''), ' ', COALESCE(m.modelo, '')) AS marca_modelo, "
                   + "c.nombre_categoria, m.horas_uso, "
                   + "COALESCE(SUM(mt.costo), 0) AS costo_acumulado, "
                   + "CASE WHEN m.horas_uso > 0 THEN ROUND(COALESCE(SUM(mt.costo), 0) / m.horas_uso, 2) ELSE 0 END AS costo_por_hora "
                   + "FROM MAQUINARIA m "
                   + "INNER JOIN CATEGORIAS_MAQUINARIA c ON m.id_categoria = c.id_categoria "
                   + "LEFT JOIN MANTENIMIENTOS mt ON m.id_maquinaria = mt.id_maquinaria AND mt.estado_mantenimiento != 'CANCELADO' "
                   + "WHERE m.activo = true "
                   + "GROUP BY m.id_maquinaria, m.codigo_inventario, m.marca, m.modelo, c.nombre_categoria, m.horas_uso "
                   + "ORDER BY costo_por_hora DESC";

        List<CostoHoraUsoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CostoHoraUsoDTO(
                    rs.getString("codigo_inventario"),
                    rs.getString("marca_modelo"),
                    rs.getString("nombre_categoria"),
                    rs.getBigDecimal("horas_uso"),
                    rs.getBigDecimal("costo_acumulado"),
                    rs.getBigDecimal("costo_por_hora")
                ));
            }
        }
        return lista;
    }

    /**
     * REPORTE 3: Cumplimiento de Tiempos de Ruta (Conteo A Tiempo vs Retrasados para Gráfico).
     */
    public Map<String, Integer> obtenerResumenCumplimientoRutas(Date fechaInicio, Date fechaFin) throws SQLException {
        String sql = "SELECT "
                   + "CASE WHEN fecha_retorno_real <= fecha_estimada_retorno THEN 'A Tiempo / Adelantado' ELSE 'Con Retraso' END AS clasificacion, "
                   + "COUNT(id_asignacion) AS total "
                   + "FROM ASIGNACIONES "
                   + "WHERE estado_asignacion = 'FINALIZADA' "
                   + "AND fecha_retorno_real IS NOT NULL "
                   + "AND DATE(fecha_asignacion) BETWEEN ? AND ? "
                   + "GROUP BY clasificacion";

        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.put(rs.getString("clasificacion"), rs.getInt("total"));
                }
            }
        }
        return resultado;
    }

    /**
     * REPORTE 5: Snapshot de Estado Operativo de Maquinarias Activas.
     */
    public Map<String, Integer> obtenerDistribucionEstadoMaquinaria() throws SQLException {
        String sql = "SELECT estado_operativo, COUNT(id_maquinaria) AS total "
                   + "FROM MAQUINARIA "
                   + "WHERE activo = true "
                   + "GROUP BY estado_operativo";

        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("estado_operativo"), rs.getInt("total"));
            }
        }
        return resultado;
    }

    /**
     * REPORTE 6: Certificaciones y Semáforo de Vigencia.
     */
    public List<CertificacionVigenciaDTO> obtenerEstadoCertificaciones() throws SQLException {
        String sql = "SELECT CONCAT(o.nombres, ' ', o.apellidos) AS operador, o.dui, "
                   + "c.nombre_categoria, oc.numero_acreditacion, "
                   + "(oc.fecha_vencimiento - CURRENT_DATE) AS dias_restantes, "
                   + "CASE "
                   + "  WHEN (oc.fecha_vencimiento - CURRENT_DATE) <= 0 THEN 'VENCIDA' "
                   + "  WHEN (oc.fecha_vencimiento - CURRENT_DATE) <= 30 THEN 'POR_VENCER' "
                   + "  ELSE 'VIGENTE' "
                   + "END AS estado_vigencia "
                   + "FROM OPERADORES_CERTIFICACIONES oc "
                   + "INNER JOIN OPERADORES o ON oc.id_operador = o.id_operador "
                   + "INNER JOIN CATEGORIAS_MAQUINARIA c ON oc.id_categoria = c.id_categoria "
                   + "WHERE o.activo = true "
                   + "ORDER BY dias_restantes ASC";

        List<CertificacionVigenciaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CertificacionVigenciaDTO(
                    rs.getString("operador"),
                    rs.getString("dui"),
                    rs.getString("nombre_categoria"),
                    rs.getString("numero_acreditacion"),
                    rs.getInt("dias_restantes"),
                    rs.getString("estado_vigencia")
                ));
            }
        }
        return lista;
    }

    /**
     * REPORTE 9: Historial de Rutas, Cancelaciones y Observaciones del Operador.
     */
    public List<HistorialRutaDTO> obtenerHistorialRutasYObservaciones(Integer idRuta) throws SQLException {
        String sql = "SELECT r.nombre_proyecto, CONCAT(r.punto_origen, ' -> ', r.punto_destino) AS trayecto, "
                   + "m.codigo_inventario, CONCAT(o.nombres, ' ', o.apellidos) AS operador, "
                   + "a.estado_asignacion, a.fecha_asignacion, a.fecha_retorno_real, "
                   + "COALESCE(a.observaciones, 'Sin observaciones') AS observaciones "
                   + "FROM ASIGNACIONES a "
                   + "INNER JOIN RUTAS_DESTINOS r ON a.id_ruta = r.id_ruta "
                   + "INNER JOIN MAQUINARIA m ON a.id_maquinaria = m.id_maquinaria "
                   + "INNER JOIN OPERADORES o ON a.id_operador = o.id_operador "
                   + (idRuta != null ? "WHERE a.id_ruta = ? " : "")
                   + "ORDER BY a.fecha_asignacion DESC";

        List<HistorialRutaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (idRuta != null) {
                ps.setInt(1, idRuta);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new HistorialRutaDTO(
                        rs.getString("nombre_proyecto"),
                        rs.getString("trayecto"),
                        rs.getString("codigo_inventario"),
                        rs.getString("operador"),
                        rs.getString("estado_asignacion"),
                        rs.getTimestamp("fecha_asignacion"),
                        rs.getTimestamp("fecha_retorno_real"),
                        rs.getString("observaciones")
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * REPORTE 10: Productividad de Operadores por Turno (Viajes completados vs Cancelados).
     */
    public List<Object[]> obtenerProductividadPorTurno(Date fechaInicio, Date fechaFin) throws SQLException {
        String sql = "SELECT o.turno, a.estado_asignacion, COUNT(a.id_asignacion) AS total_viajes "
                   + "FROM OPERADORES o "
                   + "INNER JOIN ASIGNACIONES a ON o.id_operador = a.id_operador "
                   + "WHERE DATE(a.fecha_asignacion) BETWEEN ? AND ? "
                   + "GROUP BY o.turno, a.estado_asignacion "
                   + "ORDER BY o.turno";

        List<Object[]> resultados = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(new Object[]{
                        rs.getString("turno"),
                        rs.getString("estado_asignacion"),
                        rs.getInt("total_viajes")
                    });
                }
            }
        }
        return resultados;
    }
}
```

---

### Paso 3: Crear el Servicio Generador de Gráficos JFreeChart (`src/main/java/com/sistema/reportes/service/GraficosReporteService.java`)

Esta clase construye objetos `JFreeChart` estilizados y proporciona métodos utilitarios para envolverlos en un `JPanel` estándar o exportarlos a un archivo `.png` sin depender de ninguna ventana.

```java
package com.sistema.reportes.service;

import com.sistema.reportes.dto.ReportesDTO.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class GraficosReporteService {

    /**
     * Genera el gráfico del Reporte 1: Preventivo vs Correctivo por unidad.
     */
    public JFreeChart crearGraficoRatioMantenimiento(List<RatioMantenimientoDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (RatioMantenimientoDTO fila : datos) {
            dataset.addValue(fila.getTotalIntervenciones(), fila.getTipoMantenimiento(), fila.getCodigoInventario());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Ratio de Mantenimiento: Preventivo vs Correctivo",
                "Maquinaria (Código)",
                "Cantidad de Intervenciones",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    /**
     * Genera el gráfico del Reporte 2: Costo de Mantenimiento por Hora de Uso.
     */
    public JFreeChart crearGraficoCostoPorHora(List<CostoHoraUsoDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (CostoHoraUsoDTO fila : datos) {
            dataset.addValue(fila.getCostoPorHora(), "Costo / Hora ($)", fila.getCodigoInventario());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Costo de Mantenimiento por Hora de Uso",
                "Maquinaria",
                "USD ($) por Hora",
                dataset,
                PlotOrientation.HORIZONTAL,
                false, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    /**
     * Genera el gráfico del Reporte 3 o Reporte 5: Gráfico Circular (Puntualidad o Estado de Flota).
     */
    public JFreeChart crearGraficoPastelGenerico(String titulo, Map<String, Integer> datos) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                titulo,
                dataset,
                true, true, false
        );

        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        return chart;
    }

    /**
     * Genera el gráfico del Reporte 6: Semáforo de Certificaciones por Categoría.
     */
    public JFreeChart crearGraficoCertificaciones(List<CertificacionVigenciaDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (CertificacionVigenciaDTO fila : datos) {
            dataset.incrementValue(1.0, fila.getEstadoVigencia(), fila.getCategoriaAutorizada());
        }

        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Vigencia de Certificaciones por Categoría",
                "Categoría de Maquinaria",
                "Operadores Acreditados",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    /**
     * Genera el gráfico del Reporte 10: Productividad y Estados de Viaje por Turno.
     */
    public JFreeChart crearGraficoProductividadTurno(List<Object[]> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] fila : datos) {
            String turno = (String) fila[0];
            String estado = (String) fila[1];
            Integer total = (Integer) fila[2];
            dataset.addValue(total, estado, turno);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Rendimiento de Viajes por Turno Operativo",
                "Turno",
                "Cantidad de Asignaciones",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    /**
     * Método puente para el Frontend: Convierte cualquier JFreeChart en un JPanel
     * listo para insertarse en un contenedor Swing sin exponer la lógica interna.
     */
    public JPanel construirPanelGrafico(JFreeChart chart, int anchoPreferido, int altoPreferido) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(anchoPreferido, altoPreferido));
        chartPanel.setMouseWheelEnabled(true); // Permite zoom con la rueda del ratón
        return chartPanel;
    }

    /**
     * Método para pruebas o exportación sin interfaz gráfica: Guarda el gráfico como PNG.
     */
    public void exportarGraficoAPng(JFreeChart chart, String rutaArchivo, int ancho, int alto) throws IOException {
        File archivo = new File(rutaArchivo);
        ChartUtils.saveChartAsPNG(archivo, chart, ancho, alto);
    }

    private void aplicarEstiloCorporativoBarras(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 247, 250));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185)); // Azul corporativo
        renderer.setSeriesPaint(1, new Color(231, 76, 60));  // Rojo alerta (ej. Correctivo / Cancelado)
        renderer.setSeriesPaint(2, new Color(46, 204, 113)); // Verde éxito
    }
}
```

---

## Parte 4: Indicaciones para Conectar el Backend a las Vistas (Guía para el Equipo Frontend)

Cuando las pantallas (`JFrame` o `JDialog`) estén listas en NetBeans, la persona encargada de las vistas **no tendrá que escribir SQL ni configurar datasets de JFreeChart**. Solo deberá seguir estos 3 pasos:

### 1. Preparar un contenedor vacío en el Diseñador de NetBeans
1. En la vista de diseño (`Design`) del `JFrame` de reportes, arrastrar un **`JPanel`** vacío desde la paleta (por ejemplo, llamarlo `pnlContenedorGrafico`).
2. Hacer clic derecho sobre `pnlContenedorGrafico` $\rightarrow$ **Set Layout** $\rightarrow$ seleccionar **`BorderLayout`** (esto es indispensable para que el gráfico se redimensione automáticamente al tamaño del panel).

### 2. Inyectar el Gráfico en el evento de un botón (`ActionPerformed`)
Dentro del código del evento del botón "Generar Reporte" en el `JFrame`, pegar únicamente estas líneas de conexión:

```java
// 1. Instanciar el DAO (pasando tu conexión actual) y el Servicio de Gráficos
ReportesDAO reportesDAO = new ReportesDAO(ConexionBD.getConexion());
GraficosReporteService graficosService = new GraficosReporteService();

try {
    // 2. Obtener los datos desde el backend (Ejemplo: Reporte 2 - Costo por Hora)
    List<ReportesDTO.CostoHoraUsoDTO> datos = reportesDAO.obtenerCostoPorHoraUso();

    // 3. Generar el gráfico y convertirlo en un JPanel estándar
    JFreeChart grafico = graficosService.crearGraficoCostoPorHora(datos);
    JPanel panelListo = graficosService.construirPanelGrafico(grafico, pnlContenedorGrafico.getWidth(), pnlContenedorGrafico.getHeight());

    // 4. Incrustar el panel en el contenedor de la vista y refrescar
    pnlContenedorGrafico.removeAll();
    pnlContenedorGrafico.add(panelListo, java.awt.BorderLayout.CENTER);
    pnlContenedorGrafico.revalidate();
    pnlContenedorGrafico.repaint();

} catch (SQLException ex) {
    javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar el reporte: " + ex.getMessage());
}
```

### 3. Llenar un `JTable` con la misma lista de datos (Opcional)
Como el método `reportesDAO.obtenerCostoPorHoraUso()` ya devuelve la lista `datos`, el frontend puede usar esa misma variable en el mismo bloque `try` para llenar la tabla de datos debajo del gráfico:

```java
DefaultTableModel modeloTabla = (DefaultTableModel) tblReporteDatos.getModel();
modeloTabla.setRowCount(0); // Limpiar tabla

for (ReportesDTO.CostoHoraUsoDTO fila : datos) {
    modeloTabla.addRow(new Object[]{
        fila.getCodigoInventario(),
        fila.getMarcaModelo(),
        fila.getCategoria(),
        fila.getHorasUso(),
        fila.getCostoAcumulado(),
        fila.getCostoPorHora()
    });
}
```
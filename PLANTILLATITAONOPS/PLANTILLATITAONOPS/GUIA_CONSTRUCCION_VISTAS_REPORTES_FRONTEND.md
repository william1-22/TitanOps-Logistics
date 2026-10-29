# Guía para el Agente Frontend: Construcción de Vistas de Reportes Operativos (NetBeans + Swing + JFreeChart)

Este documento describe **exactamente qué vistas o pestañas debes construir**, **qué herramientas y componentes propios del repositorio debes usar**, y **qué datos, DTOs, KPIs y gráficos te entrega el backend ya implementado** sin que tengas que escribir una sola línea de SQL ni configurar datasets de JFreeChart.

---

## 1. Mapa de Clases Backend Listas para Consumir

Se adaptó la especificación original a la estructura real de paquetes y conexión (`ConexionBD.getInstancia().getConexion()`) del proyecto **TitanOps**:

| Capa | Clase / Paquete | Responsabilidad |
|---|---|---|
| **Controlador Orquestador** | `com.titanops.controlador.ReportesOperativosController` | **Punto de entrada recomendado para las vistas.** Ejecuta consultas, calcula los KPIs matemáticos de los 9 reportes, genera los objetos `JFreeChart`, construye el `DefaultTableModel` no editable e incrusta los gráficos en cualquier `JPanel`. |
| **Acceso a Datos (DAO)** | `com.titanops.dao.ReportesDAO` | Ejecuta todas las consultas parametrizadas sobre PostgreSQL (`Supabase`) para los 9 reportes y provee los catálogos de filtros (`listarOpcionesCategorias()`, `listarOpcionesMaquinaria()`, `listarOpcionesRutas()`, `listarOpcionesOperadores()`, `listarOpcionesUsuarios()`, `listarOpcionesRoles()`). Soporta tanto `new ReportesDAO()` como `new ReportesDAO(conexion)`. |
| **DTOs, KPIs y Filtros** | `com.titanops.reporte.ReportesDTO` | Contiene los DTOs de filas (`RatioMantenimientoDTO`, `CostoHoraUsoDTO`, `CumplimientoRutaDTO`, `SnapshotMaquinariaDTO`, `CertificacionVigenciaDTO`, `AuditoriaUsuarioDTO`, `UtilizacionDesgasteDTO`, `HistorialRutaDTO`, `ProductividadOperadorDTO`), los records de KPIs (`Kpi...DTO`) y `OpcionFiltro` para poblar `JComboBox`/`Selector`. |
| **Servicio de Gráficos** | `com.titanops.reporte.GraficosReporteService` | Construye los gráficos `JFreeChart` con la paleta corporativa de TitanOps, los envuelve en un `ChartPanel` (`construirPanelGrafico`) con zoom de ratón habilitado y permite exportarlos a PNG (`exportarGraficoAPng`). |

---

## 2. Herramientas y Componentes Visuales que Debes Usar

Para mantener la identidad visual del proyecto en **Apache NetBeans**, construye las vistas usando los componentes personalizados ya existentes en `com.titanops.vista.componentes` y las librerías del `pom.xml`:

### 2.1 Componentes Swing / Personalizados del Proyecto
1. **Ventana / Contenedor Principal:**
   - Puedes agregar una nueva pestaña `"REPORTES ANALÍTICOS"` dentro de `com.titanops.vista.Control_y_reportes` (`tabsControl`), o crear un `javax.swing.JInternalFrame` invocable desde `com.titanops.vista.FILES` (`jDesktopPane1.add(ventana)`).
   - Dentro del módulo de reportes, utiliza un `javax.swing.JTabbedPane` o un `Selector<String>` para alternar entre los **9 reportes operativos** (Reportes #1, #2, #3, #5, #6, #7, #8, #9 y #10; recuerda que el #4 fue descartado por el equipo).
2. **Selectores Desplegables (`com.titanops.vista.componentes.Selector<T>`):**
   - Para filtros de base de datos (Categoría, Maquinaria, Ruta, Operador, Usuario, Rol): usa `Selector<ReportesDTO.OpcionFiltro>`. El método `toString()` de `OpcionFiltro` ya muestra el texto amigable y `opcion.id()` devuelve el `Integer` (`null` cuando el usuario elige `"Todas..."`).
   - Para filtros de estado o turno: usa `Selector<String>` llenándolo con las constantes de `ReportesOperativosController`.
3. **Selectores de Rango de Fechas (`com.toedter.calendar.JDateChooser`):**
   - Ya está incluido en el `pom.xml` (`jcalendar 1.4`).
   - Configúralo con `dateChooser.setDateFormatString("dd/MM/yyyy")`.
   - Convierte su valor a `java.sql.Date` con:
     ```java
     java.sql.Date sqlDate = chooser.getDate() == null
             ? null : new java.sql.Date(chooser.getDate().getTime());
     ```
   - Si el usuario deja la fecha vacía (`null`), el backend automáticamente consulta sin límite de fecha sin lanzar error.
4. **Botones (`com.titanops.vista.componentes.Boton`):**
   - Usa `Boton` para `"GENERAR REPORTE"`, `"LIMPIAR FILTROS"` y `"EXPORTAR GRÁFICO PNG"`.
5. **Tablas de Datos (`com.titanops.vista.componentes.Tabla` dentro de `JScrollPane`):**
   - Usa `Tabla` y asígnale directamente el modelo generado por `controller.construirModeloTabla(resultado.columnasTabla(), resultado.filasTabla())`.
   - Activa el ordenamiento con `tabla.setAutoCreateRowSorter(true)`.
6. **Contenedor del Gráfico (`javax.swing.JPanel`):**
   - Crea un `JPanel` vacío (ej. `pnlContenedorGrafico`) y **obligatoriamente configúrale `new java.awt.BorderLayout()`** para que el `ChartPanel` de JFreeChart se redimensione automáticamente.
7. **Tarjetas de KPIs (`javax.swing.JPanel` con `GridLayout(1, N, 8, 4)` y `JLabel`):**
   - Igual que `panelResumen` en `Control_y_reportes.java`, coloca de 3 a 5 `JLabel` en la parte superior para mostrar los indicadores matemáticos calculados en `resultado.kpi()`.

### 2.2 Paleta Corporativa TitanOps
- **Azul Cabecera:** `new Color(30, 64, 175)` con texto blanco `new Color(255, 255, 255)` y fuente `SansSerif Bold 24-26pt`.
- **Fondo General:** `new Color(239, 246, 255)` / Paneles internos: `new Color(248, 250, 252)`.
- **Bordes de Tablas y Controles:** `new Color(125, 211, 252)`.
- **Texto Principal:** `new Color(30, 41, 59)`.
- **Cabecera de `Tabla`:** `fondoCabecera = new Color(224, 242, 254)`, `textoCabecera = new Color(30, 64, 175)`.

---

## 3. Patrón Estándar de Conexión en la Vista (Plantilla Lista para Usar)

Usa `SwingWorker` (tal como se hace en `Control_y_reportes.java`) o llámalo en el `ActionPerformed` del botón `"GENERAR"`:

```java
private final ReportesOperativosController reportesController = new ReportesOperativosController();

// Ejemplo para cargar los combos de filtros al abrir la vista:
private void cargarCombosFiltros() {
    try {
        ReportesDAO dao = reportesController.getReportesDAO();
        cmbCategoria.removeAllItems();
        for (ReportesDTO.OpcionFiltro op : dao.listarOpcionesCategorias()) {
            cmbCategoria.addItem(op);
        }
        cmbMaquinaria.removeAllItems();
        for (ReportesDTO.OpcionFiltro op : dao.listarOpcionesMaquinaria()) {
            cmbMaquinaria.addItem(op);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error cargando filtros: " + ex.getMessage());
    }
}

// Ejemplo en el botón "Generar Reporte 1":
private void generarReporte1() {
    java.sql.Date inicio = fechaInicio.getDate() == null ? null : new java.sql.Date(fechaInicio.getDate().getTime());
    java.sql.Date fin = fechaFin.getDate() == null ? null : new java.sql.Date(fechaFin.getDate().getTime());
    ReportesDTO.OpcionFiltro maq = (ReportesDTO.OpcionFiltro) cmbMaquinaria.getSelectedItem();
    ReportesDTO.OpcionFiltro cat = (ReportesDTO.OpcionFiltro) cmbCategoria.getSelectedItem();

    try {
        var resultado = reportesController.generarReporte1RatioMantenimiento(
                inicio, fin,
                maq == null ? null : maq.id(),
                cat == null ? null : cat.id()
        );

        // 1. Llenar la Tabla automáticamente
        tablaDatos.setModel(reportesController.construirModeloTabla(
                resultado.columnasTabla(), resultado.filasTabla()));

        // 2. Incrustar el Gráfico JFreeChart en el JPanel contenedor
        reportesController.incrustarGraficoEnPanel(
                pnlContenedorGrafico, resultado.graficoPrincipal());

        // 3. Mostrar los KPIs en los JLabels
        var kpi = resultado.kpi();
        lblKpi1.setText("Total Mantenimientos: " + kpi.totalMantenimientos());
        lblKpi2.setText("% Incidencias Correctivas: " + kpi.porcentajeIncidenciasCorrectivas() + "%");
        lblKpi3.setText("% Gasto Correctivo: " + kpi.porcentajeGastoCorrectivo() + "%");
        lblKpi4.setText("Costo Total: $" + kpi.costoTotal());

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage());
    }
}
```

---

## 4. Especificación Detallada de las 9 Vistas de Reportes y Datos que Reciben

### Vista / Pestaña 1: Ratio de Mantenimiento Preventivo vs. Correctivo
- **Controles de Filtro a colocar:**
  1. `JDateChooser fechaInicio` (Opcional)
  2. `JDateChooser fechaFin` (Opcional)
  3. `Selector<OpcionFiltro> cmbMaquinaria` $\rightarrow$ llenar con `dao.listarOpcionesMaquinaria()`
  4. `Selector<OpcionFiltro> cmbCategoria` $\rightarrow$ llenar con `dao.listarOpcionesCategorias()`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<RatioMantenimientoDTO, KpiRatioMantenimientoDTO> res =
      reportesController.generarReporte1RatioMantenimiento(fechaInicio, fechaFin, idMaquinaria, idCategoria);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiRatioMantenimientoDTO`):**
  - `totalMantenimientos()` (`int`)
  - `conteoPreventivos()` (`int`)
  - `conteoCorrectivos()` (`int`)
  - `porcentajeIncidenciasCorrectivas()` (`double` - %)
  - `costoPreventivo()` (`BigDecimal`)
  - `costoCorrectivo()` (`BigDecimal`)
  - `costoTotal()` (`BigDecimal`)
  - `porcentajeGastoCorrectivo()` (`double` - %)
  - `horasTallerTotales()` (`double`)
- **Gráficos entregados:**
  - `res.graficoPrincipal()`: `StackedBarChart` (Preventivo vs Correctivo por unidad).
  - `res.graficoSecundario()`: `PieChart` (Proporción global Preventivo vs Correctivo).
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_1`):**
  `["CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "TIPO", "INTERVENCIONES", "COSTO TOTAL ($)", "HORAS TALLER"]`
- **Getters de cada fila (`RatioMantenimientoDTO`):**
  `getIdMaquinaria()`, `getCodigoInventario()`, `getMarcaModelo()`, `getCategoria()`, `getTipoMantenimiento()`, `getTotalIntervenciones()`, `getCostoTotal()`, `getHorasTaller()`, `toTableRow()`.

---

### Vista / Pestaña 2: Costo de Mantenimiento por Hora de Uso (Índice TCO Operativo)
- **Controles de Filtro a colocar:**
  1. `Selector<OpcionFiltro> cmbCategoria` $\rightarrow$ llenar con `dao.listarOpcionesCategorias()`
  2. `Selector<String> cmbActivo` $\rightarrow$ llenar con `ReportesOperativosController.FILTRO_ACTIVO_OPCIONES` (`"TODOS"`, `"ACTIVOS"`, `"INACTIVOS"`) y convertir con `ReportesOperativosController.interpretarFiltroActivo(seleccion)`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<CostoHoraUsoDTO, KpiCostoHoraUsoDTO> res =
      reportesController.generarReporte2CostoHoraUso(idCategoria, activo);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiCostoHoraUsoDTO`):**
  - `totalMaquinasEvaluadas()` (`int`)
  - `horasUsoTotales()` (`BigDecimal`)
  - `costoAcumuladoTotal()` (`BigDecimal`)
  - `costoPorHoraGlobal()` (`BigDecimal` - USD/h promedio de la flota)
  - `unidadMayorCostoPorHora()` (`String` - alerta de "máquina vampiro", ej. `"EQ-01 ($12.50/h)"`)
- **Gráfico entregado:**
  - `res.graficoPrincipal()`: `BarChart` horizontal ordenando las máquinas de mayor a menor CPH.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_2`):**
  `["CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "HORAS USO", "INTERVENCIONES", "COSTO ACUMULADO ($)", "COSTO / HORA ($)", "COSTO PROM. ($)", "ESTADO"]`
- **Getters de cada fila (`CostoHoraUsoDTO`):**
  `getIdMaquinaria()`, `getCodigoInventario()`, `getMarcaModelo()`, `getCategoria()`, `getHorasUso()`, `getTotalIntervenciones()`, `getCostoAcumulado()`, `getCostoPorHora()`, `getCostoPromedioIntervencion()`, `isActivo()`, `toTableRow()`.

---

### Vista / Pestaña 3: Cumplimiento de Tiempos de Rutas (SLA Logístico / Puntualidad)
- **Controles de Filtro a colocar:**
  1. `JDateChooser fechaInicio`
  2. `JDateChooser fechaFin`
  3. `Selector<OpcionFiltro> cmbRuta` $\rightarrow$ llenar con `dao.listarOpcionesRutas()`
  4. `Selector<OpcionFiltro> cmbOperador` $\rightarrow$ llenar con `dao.listarOpcionesOperadores()`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<CumplimientoRutaDTO, KpiCumplimientoRutasDTO> res =
      reportesController.generarReporte3CumplimientoRutas(fechaInicio, fechaFin, idRuta, idOperador);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiCumplimientoRutasDTO`):**
  - `totalViajesFinalizados()` (`int`)
  - `viajesATiempo()` (`int` - $\Delta t \le 0$)
  - `viajesConRetraso()` (`int` - $\Delta t > 0$)
  - `porcentajePuntualidadGlobal()` (`double` - %)
  - `desviacionPromedioHoras()` (`double` - horas)
- **Gráficos entregados:**
  - `res.graficoPrincipal()`: `PieChart` (`A Tiempo / Adelantado` vs `Con Retraso`).
  - `res.graficoSecundario()`: `BarChart` de desviación promedio en horas ($\Delta t$) por proyecto.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_3`):**
  `["ID VIAJE", "PROYECTO", "TRAYECTO", "MAQUINARIA", "OPERADOR", "ASIGNACIÓN", "RETORNO EST.", "RETORNO REAL", "DESVIACIÓN (H)", "CLASIFICACIÓN SLA"]`
- **Getters de cada fila (`CumplimientoRutaDTO`):**
  `getIdAsignacion()`, `getNombreProyecto()`, `getOrigenDestino()`, `getCodigoMaquinaria()`, `getNombreOperador()`, `getFechaAsignacion()`, `getFechaEstimadaRetorno()`, `getFechaRetornoReal()`, `getDesviacionHoras()`, `getClasificacion()`, `toTableRow()`.

---

### Vista / Pestaña 5: Maquinarias: Datos Generales y Disponibilidad en Tiempo Real (Snapshot Maestro)
- **Controles de Filtro a colocar:**
  1. `Selector<String> cmbActivo` $\rightarrow$ `ReportesOperativosController.FILTRO_ACTIVO_OPCIONES`
  2. `Selector<String> cmbEstadoOperativo` $\rightarrow$ `ReportesOperativosController.ESTADOS_MAQUINARIA` (`"TODOS"`, `"DISPONIBLE"`, `"EN_RUTA"`, `"MANTENIMIENTO"`)
  3. `Selector<OpcionFiltro> cmbCategoria` $\rightarrow$ `dao.listarOpcionesCategorias()`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<SnapshotMaquinariaDTO, KpiDisponibilidadMaquinariaDTO> res =
      reportesController.generarReporte5SnapshotMaquinaria(activo, estadoOperativo, idCategoria);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiDisponibilidadMaquinariaDTO`):**
  - `totalMaquinasActivas()` (`int`)
  - `disponibles()` (`int`)
  - `enRuta()` (`int`)
  - `enMantenimiento()` (`int`)
  - `inactivas()` (`int`)
  - `tasaDisponibilidad()` (`double` - % de máquinas activas en estado `DISPONIBLE`)
- **Gráfico entregado:**
  - `res.graficoPrincipal()`: `RingChart` (Gráfico de anillo con la distribución en tiempo real `DISPONIBLE`, `EN_RUTA`, `MANTENIMIENTO`, `INACTIVA`).
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_5`):**
  `["CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "TONELAJE", "HORAS USO", "ESTADO OPERATIVO", "ESTATUS", "RUTAS HIST.", "TALLERES HIST.", "ÚLTIMA RUTA", "REGISTRO"]`
- **Getters de cada fila (`SnapshotMaquinariaDTO`):**
  `getIdMaquinaria()`, `getCodigoInventario()`, `getMarca()`, `getModelo()`, `getMarcaModelo()`, `getCategoria()`, `getTonelaje()`, `getHorasUso()`, `getEstadoOperativo()`, `isActivo()`, `getFechaRegistro()`, `getTotalAsignaciones()`, `getTotalMantenimientos()`, `getUltimaAsignacion()`, `toTableRow()`.

---

### Vista / Pestaña 6: Reporte Integral de Certificaciones y Vigencia Operativa (Compliance)
- **Controles de Filtro a colocar:**
  1. `Selector<String> cmbEstadoVigencia` $\rightarrow$ `ReportesOperativosController.ESTADOS_VIGENCIA_CERTIFICACION` (`"TODOS"`, `"VENCIDA"`, `"POR_VENCER"`, `"VIGENTE"`)
  2. `Selector<OpcionFiltro> cmbCategoria` $\rightarrow$ `dao.listarOpcionesCategorias()`
  3. `CampoTexto txtLicenciaTipo` (Opcional, o selector de tipo de licencia)
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<CertificacionVigenciaDTO, KpiCertificacionesDTO> res =
      reportesController.generarReporte6Certificaciones(estadoVigencia, idCategoria, licenciaTipo);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiCertificacionesDTO`):**
  - `totalCertificaciones()` (`int`)
  - `vigentes()` (`int` - $> 30$ días)
  - `porVencer()` (`int` - $1 \text{ a } 30$ días)
  - `vencidas()` (`int` - $\le 0$ días)
  - `porcentajeVigentes()` (`double` - %)
- **Gráfico entregado:**
  - `res.graficoPrincipal()`: `StackedBarChart` agrupado por Categoría de Maquinaria mostrando certificaciones `VIGENTE`, `POR_VENCER` y `VENCIDA`.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_6`):**
  `["OPERADOR", "DUI", "LICENCIA", "CATEGORÍA AUTORIZADA", "N° ACREDITACIÓN", "EXPEDICIÓN", "VENCIMIENTO", "DÍAS RESTANTES", "VIGENCIA"]`
- **Getters de cada fila (`CertificacionVigenciaDTO`):**
  `getNombreOperador()`, `getDui()`, `getLicenciaTipo()`, `getEstadoOperativo()`, `getCategoriaAutorizada()`, `getNumeroAcreditacion()`, `getFechaExpedicion()`, `getFechaVencimiento()`, `getDiasRestantes()`, `getEstadoVigencia()`, `toTableRow()`.

---

### Vista / Pestaña 7: Reporte de Auditoría y Gestión de Usuarios
- **Controles de Filtro a colocar:**
  1. `Selector<OpcionFiltro> cmbRol` $\rightarrow$ `dao.listarOpcionesRoles()`
  2. `Selector<OpcionFiltro> cmbUsuario` $\rightarrow$ `dao.listarOpcionesUsuarios()`
  3. `Selector<String> cmbActivo` $\rightarrow$ `ReportesOperativosController.FILTRO_ACTIVO_OPCIONES`
  4. `JDateChooser fechaInicio` y `JDateChooser fechaFin`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<AuditoriaUsuarioDTO, KpiAuditoriaUsuariosDTO> res =
      reportesController.generarReporte7AuditoriaUsuarios(idRol, idUsuario, activo, fechaInicio, fechaFin);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiAuditoriaUsuariosDTO`):**
  - `totalUsuariosAuditados()` (`int`)
  - `totalDespachosRegistrados()` (`int`)
  - `totalMantenimientosRegistrados()` (`int`)
  - `totalCancelaciones()` (`int`)
  - `montoTotalTallerAutorizado()` (`BigDecimal`)
- **Gráfico entregado:**
  - `res.graficoPrincipal()`: `BarChart` comparando Despachos Registrados, Órdenes de Taller y Cancelaciones por cada usuario.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_7`):**
  `["ID", "NOMBRE COMPLETO", "USERNAME", "ROL", "ESTATUS", "DESPACHOS REG.", "DESPACHOS CANC.", "TALLERES REG.", "TALLERES CANC.", "MONTO TALLER ($)", "CREACIÓN"]`
- **Getters de cada fila (`AuditoriaUsuarioDTO`):**
  `getIdUsuario()`, `getNombreCompleto()`, `getUsername()`, `getNombreRol()`, `isActivo()`, `getFechaCreacion()`, `getTotalAsignaciones()`, `getAsignacionesCanceladas()`, `getTotalMantenimientos()`, `getMantenimientosCancelados()`, `getMontoTallerAutorizado()`, `toTableRow()`.

---

### Vista / Pestaña 8: Reporte de Utilización y Desgaste de Flota
- **Controles de Filtro a colocar:**
  1. `Selector<OpcionFiltro> cmbCategoria` $\rightarrow$ `dao.listarOpcionesCategorias()`
  2. `JDateChooser fechaInicio` y `JDateChooser fechaFin`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<UtilizacionDesgasteDTO, KpiUtilizacionFlotaDTO> res =
      reportesController.generarReporte8UtilizacionFlota(idCategoria, fechaInicio, fechaFin);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiUtilizacionFlotaDTO`):**
  - `totalUnidades()` (`int`)
  - `totalHorasEnRuta()` (`double`)
  - `totalHorasEnTaller()` (`double`)
  - `promedioUsoDiarioGlobal()` (`BigDecimal` - horas/día)
- **Gráfico entregado:**
  - `res.graficoPrincipal()`: `BarChart` de barras dobles comparando **Horas Productivas en Ruta** vs **Horas Muertas en Taller (Downtime)** por unidad.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_8`):**
  `["CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "ANTIGÜEDAD (DÍAS)", "HORAS USO", "USO DIARIO PROM. (H)", "HORAS EN RUTA", "HORAS EN TALLER", "REGISTRO"]`
- **Getters de cada fila (`UtilizacionDesgasteDTO`):**
  `getIdMaquinaria()`, `getCodigoInventario()`, `getMarcaModelo()`, `getCategoria()`, `getFechaRegistro()`, `getAntiguedadDias()`, `getHorasUso()`, `getPromedioUsoDiario()`, `getHorasEnRuta()`, `getHorasEnTaller()`, `toTableRow()`.

---

### Vista / Pestaña 9: Historial de Cumplimiento de Rutas, Proyectos y Bitácora de Observaciones
- **Controles de Filtro a colocar:**
  1. `Selector<OpcionFiltro> cmbRuta` $\rightarrow$ `dao.listarOpcionesRutas()`
  2. `Selector<String> cmbEstadoRuta` $\rightarrow$ `ReportesOperativosController.ESTADOS_RUTA` (`"TODOS"`, `"PLANIFICADA"`, `"EN_CURSO"`, `"FINALIZADA"`, `"CANCELADA"`)
  3. `Selector<String> cmbEstadoAsignacion` $\rightarrow$ `ReportesOperativosController.ESTADOS_ASIGNACION` (`"TODOS"`, `"PROGRAMADA"`, `"EN_CURSO"`, `"FINALIZADA"`, `"CANCELADA"`)
  4. `Selector<OpcionFiltro> cmbOperador` $\rightarrow$ `dao.listarOpcionesOperadores()`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<HistorialRutaDTO, KpiHistorialRutasDTO> res =
      reportesController.generarReporte9HistorialRutas(idRuta, estadoRuta, estadoAsignacion, idOperador);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiHistorialRutasDTO`):**
  - `totalViajes()` (`int`)
  - `viajesFinalizados()` (`int`)
  - `viajesEnCurso()` (`int`)
  - `viajesProgramados()` (`int`)
  - `viajesCancelados()` (`int`)
  - `tasaEfectividad()` (`double` - %)
  - `tasaCancelacion()` (`double` - %)
- **Gráfico entregado:**
  - `res.graficoPrincipal()`: `StackedBarChart` por proyecto mostrando viajes `FINALIZADA`, `EN_CURSO`, `PROGRAMADA` y `CANCELADA`.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_9`):**
  `["ID VIAJE", "PROYECTO", "TRAYECTO", "ESTADO RUTA", "MAQUINARIA", "OPERADOR", "TURNO", "ESTADO VIAJE", "ASIGNACIÓN", "RETORNO REAL", "OBSERVACIONES"]`
- **Getters de cada fila (`HistorialRutaDTO`):**
  `getIdAsignacion()`, `getNombreProyecto()`, `getOrigenDestino()`, `getEstadoRuta()`, `getCodigoMaquinaria()`, `getOperador()`, `getTurnoOperador()`, `getTelefonoOperador()`, `getEstadoAsignacion()`, `getFechaAsignacion()`, `getFechaRetornoReal()`, `getObservaciones()`, `toTableRow()`.

---

### Vista / Pestaña 10: Productividad y Carga Laboral de Operadores por Turno
- **Controles de Filtro a colocar:**
  1. `Selector<String> cmbTurno` $\rightarrow$ `ReportesOperativosController.TURNOS_OPERADOR` (`"TODOS"`, `"DIURNO"`, `"NOCTURNO"`, `"ROTATIVO"`)
  2. `Selector<String> cmbEstadoOperador` $\rightarrow$ `ReportesOperativosController.ESTADOS_OPERADOR` (`"TODOS"`, `"DISPONIBLE"`, `"EN_RUTA"`, `"DESCANSO"`, `"INACTIVO"`)
  3. `JDateChooser fechaInicio` y `JDateChooser fechaFin`
- **Método del Controlador a invocar:**
  ```java
  ResultadoReporte<ProductividadOperadorDTO, KpiProductividadTurnoDTO> res =
      reportesController.generarReporte10ProductividadTurno(turno, estadoOperativo, fechaInicio, fechaFin);
  ```
- **KPIs disponibles en `res.kpi()` (`KpiProductividadTurnoDTO`):**
  - `totalOperadores()` (`int`)
  - `totalViajesFinalizados()` (`int`)
  - `totalViajesCancelados()` (`int`)
  - `totalHorasConducidas()` (`double`)
  - `promedioCumplimientoPorcentaje()` (`double` - %)
- **Gráficos entregados:**
  - `res.graficoPrincipal()`: `BarChart` comparando Viajes Finalizados, Viajes Cancelados y Horas Conducidas agrupado por Turno.
  - También puedes invocar `graficosService.crearGraficoProductividadTurno(dao.obtenerProductividadPorTurno(fechaInicio, fechaFin))` para el gráfico directo de asignaciones por turno.
- **Columnas de Tabla (`ReportesOperativosController.COLUMNAS_REPORTE_10`):**
  `["OPERADOR", "DUI", "LICENCIA", "TURNO", "ESTADO OPERATIVO", "CATEGORÍAS CERT.", "TOTAL VIAJES", "FINALIZADOS", "CANCELADOS", "HORAS CONDUCIDAS"]`
- **Getters de cada fila (`ProductividadOperadorDTO`):**
  `getIdOperador()`, `getNombreOperador()`, `getDui()`, `getLicenciaTipo()`, `getTurno()`, `getEstadoOperativo()`, `getCategoriasCertificadas()`, `getTotalViajes()`, `getViajesFinalizados()`, `getViajesCancelados()`, `getViajesEnCurso()`, `getHorasConducidas()`, `toTableRow()`.

package com.titanops.reporte;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Contenedor de DTOs, KPIs y opciones de filtrado para los 9 reportes operativos
 * del sistema TitanOps (excluyendo el reporte #4 por definición del equipo).
 */
public final class ReportesDTO {

    private ReportesDTO() {
    }

    /**
     * Elemento genérico para poblar selectores (JComboBox / Selector) de filtros
     * sin acoplar las vistas a entidades completas. Cuando {@code id} es {@code null},
     * representa la opción "TODOS / TODAS".
     */
    public record OpcionFiltro(Integer id, String etiqueta) {
        public static OpcionFiltro todos(String texto) {
            return new OpcionFiltro(null, texto);
        }

        @Override
        public String toString() {
            return etiqueta == null ? "" : etiqueta;
        }
    }

    // =========================================================================
    // REPORTE 1: Ratio de Mantenimiento Preventivo vs. Correctivo
    // =========================================================================

    public static class RatioMantenimientoDTO {
        private final int idMaquinaria;
        private final String codigoInventario;
        private final String marcaModelo;
        private final String categoria;
        private final String tipoMantenimiento;
        private final int totalIntervenciones;
        private final BigDecimal costoTotal;
        private final double horasTaller;

        public RatioMantenimientoDTO(String codigoInventario, String categoria,
                                     String tipoMantenimiento, int totalIntervenciones,
                                     BigDecimal costoTotal, double horasTaller) {
            this(0, codigoInventario, "", categoria, tipoMantenimiento,
                    totalIntervenciones, costoTotal, horasTaller);
        }

        public RatioMantenimientoDTO(int idMaquinaria, String codigoInventario,
                                     String marcaModelo, String categoria,
                                     String tipoMantenimiento, int totalIntervenciones,
                                     BigDecimal costoTotal, double horasTaller) {
            this.idMaquinaria = idMaquinaria;
            this.codigoInventario = valorSeguro(codigoInventario);
            this.marcaModelo = valorSeguro(marcaModelo);
            this.categoria = valorSeguro(categoria);
            this.tipoMantenimiento = valorSeguro(tipoMantenimiento);
            this.totalIntervenciones = totalIntervenciones;
            this.costoTotal = decimalSeguro(costoTotal);
            this.horasTaller = redondear(horasTaller);
        }

        public int getIdMaquinaria() { return idMaquinaria; }
        public String getCodigoInventario() { return codigoInventario; }
        public String getMarcaModelo() { return marcaModelo; }
        public String getCategoria() { return categoria; }
        public String getTipoMantenimiento() { return tipoMantenimiento; }
        public int getTotalIntervenciones() { return totalIntervenciones; }
        public BigDecimal getCostoTotal() { return costoTotal; }
        public double getHorasTaller() { return horasTaller; }

        public Object[] toTableRow() {
            return new Object[]{
                codigoInventario, marcaModelo, categoria,
                tipoMantenimiento, totalIntervenciones, costoTotal, horasTaller
            };
        }
    }

    public record KpiRatioMantenimientoDTO(
            int totalMantenimientos,
            int conteoPreventivos,
            int conteoCorrectivos,
            double porcentajeIncidenciasCorrectivas,
            BigDecimal costoPreventivo,
            BigDecimal costoCorrectivo,
            BigDecimal costoTotal,
            double porcentajeGastoCorrectivo,
            double horasTallerTotales) {
    }

    // =========================================================================
    // REPORTE 2: Costo de Mantenimiento por Hora de Uso (Índice TCO Operativo)
    // =========================================================================

    public static class CostoHoraUsoDTO {
        private final int idMaquinaria;
        private final String codigoInventario;
        private final String marcaModelo;
        private final String categoria;
        private final BigDecimal horasUso;
        private final int totalIntervenciones;
        private final BigDecimal costoAcumulado;
        private final BigDecimal costoPorHora;
        private final BigDecimal costoPromedioIntervencion;
        private final boolean activo;

        public CostoHoraUsoDTO(String codigoInventario, String marcaModelo,
                               String categoria, BigDecimal horasUso,
                               BigDecimal costoAcumulado, BigDecimal costoPorHora) {
            this(0, codigoInventario, marcaModelo, categoria, horasUso, 0,
                    costoAcumulado, costoPorHora, BigDecimal.ZERO, true);
        }

        public CostoHoraUsoDTO(int idMaquinaria, String codigoInventario,
                               String marcaModelo, String categoria,
                               BigDecimal horasUso, int totalIntervenciones,
                               BigDecimal costoAcumulado, BigDecimal costoPorHora,
                               BigDecimal costoPromedioIntervencion, boolean activo) {
            this.idMaquinaria = idMaquinaria;
            this.codigoInventario = valorSeguro(codigoInventario);
            this.marcaModelo = valorSeguro(marcaModelo);
            this.categoria = valorSeguro(categoria);
            this.horasUso = decimalSeguro(horasUso);
            this.totalIntervenciones = totalIntervenciones;
            this.costoAcumulado = decimalSeguro(costoAcumulado);
            this.costoPorHora = decimalSeguro(costoPorHora);
            this.costoPromedioIntervencion = decimalSeguro(costoPromedioIntervencion);
            this.activo = activo;
        }

        public int getIdMaquinaria() { return idMaquinaria; }
        public String getCodigoInventario() { return codigoInventario; }
        public String getMarcaModelo() { return marcaModelo; }
        public String getCategoria() { return categoria; }
        public BigDecimal getHorasUso() { return horasUso; }
        public int getTotalIntervenciones() { return totalIntervenciones; }
        public BigDecimal getCostoAcumulado() { return costoAcumulado; }
        public BigDecimal getCostoPorHora() { return costoPorHora; }
        public BigDecimal getCostoPromedioIntervencion() { return costoPromedioIntervencion; }
        public boolean isActivo() { return activo; }

        public Object[] toTableRow() {
            return new Object[]{
                codigoInventario, marcaModelo, categoria, horasUso,
                totalIntervenciones, costoAcumulado, costoPorHora,
                costoPromedioIntervencion, activo ? "ACTIVA" : "INACTIVA"
            };
        }
    }

    public record KpiCostoHoraUsoDTO(
            int totalMaquinasEvaluadas,
            BigDecimal horasUsoTotales,
            BigDecimal costoAcumuladoTotal,
            BigDecimal costoPorHoraGlobal,
            String unidadMayorCostoPorHora) {
    }

    // =========================================================================
    // REPORTE 3: Cumplimiento de Tiempos de Rutas (SLA Logístico / Puntualidad)
    // =========================================================================

    public static class CumplimientoRutaDTO {
        private final int idAsignacion;
        private final String nombreProyecto;
        private final String origenDestino;
        private final String codigoMaquinaria;
        private final String nombreOperador;
        private final Timestamp fechaAsignacion;
        private final Timestamp fechaEstimadaRetorno;
        private final Timestamp fechaRetornoReal;
        private final double desviacionHoras;
        private final String clasificacion;

        public CumplimientoRutaDTO(int idAsignacion, String nombreProyecto,
                                   String origenDestino, String codigoMaquinaria,
                                   String nombreOperador, Timestamp fechaAsignacion,
                                   Timestamp fechaEstimadaRetorno, Timestamp fechaRetornoReal,
                                   double desviacionHoras, String clasificacion) {
            this.idAsignacion = idAsignacion;
            this.nombreProyecto = valorSeguro(nombreProyecto);
            this.origenDestino = valorSeguro(origenDestino);
            this.codigoMaquinaria = valorSeguro(codigoMaquinaria);
            this.nombreOperador = valorSeguro(nombreOperador);
            this.fechaAsignacion = fechaAsignacion;
            this.fechaEstimadaRetorno = fechaEstimadaRetorno;
            this.fechaRetornoReal = fechaRetornoReal;
            this.desviacionHoras = redondear(desviacionHoras);
            this.clasificacion = valorSeguro(clasificacion);
        }

        public int getIdAsignacion() { return idAsignacion; }
        public String getNombreProyecto() { return nombreProyecto; }
        public String getOrigenDestino() { return origenDestino; }
        public String getCodigoMaquinaria() { return codigoMaquinaria; }
        public String getNombreOperador() { return nombreOperador; }
        public Timestamp getFechaAsignacion() { return fechaAsignacion; }
        public Timestamp getFechaEstimadaRetorno() { return fechaEstimadaRetorno; }
        public Timestamp getFechaRetornoReal() { return fechaRetornoReal; }
        public double getDesviacionHoras() { return desviacionHoras; }
        public String getClasificacion() { return clasificacion; }

        public Object[] toTableRow() {
            return new Object[]{
                idAsignacion, nombreProyecto, origenDestino, codigoMaquinaria,
                nombreOperador, fechaAsignacion, fechaEstimadaRetorno,
                fechaRetornoReal, desviacionHoras, clasificacion
            };
        }
    }

    public record KpiCumplimientoRutasDTO(
            int totalViajesFinalizados,
            int viajesATiempo,
            int viajesConRetraso,
            double porcentajePuntualidadGlobal,
            double desviacionPromedioHoras) {
    }

    // =========================================================================
    // REPORTE 5: Maquinarias: Datos Generales y Disponibilidad en Tiempo Real
    // =========================================================================

    public static class SnapshotMaquinariaDTO {
        private final int idMaquinaria;
        private final String codigoInventario;
        private final String marca;
        private final String modelo;
        private final String categoria;
        private final BigDecimal tonelaje;
        private final BigDecimal horasUso;
        private final String estadoOperativo;
        private final boolean activo;
        private final Timestamp fechaRegistro;
        private final int totalAsignaciones;
        private final int totalMantenimientos;
        private final Timestamp ultimaAsignacion;

        public SnapshotMaquinariaDTO(int idMaquinaria, String codigoInventario,
                                     String marca, String modelo, String categoria,
                                     BigDecimal tonelaje, BigDecimal horasUso,
                                     String estadoOperativo, boolean activo,
                                     Timestamp fechaRegistro, int totalAsignaciones,
                                     int totalMantenimientos, Timestamp ultimaAsignacion) {
            this.idMaquinaria = idMaquinaria;
            this.codigoInventario = valorSeguro(codigoInventario);
            this.marca = valorSeguro(marca);
            this.modelo = valorSeguro(modelo);
            this.categoria = valorSeguro(categoria);
            this.tonelaje = decimalSeguro(tonelaje);
            this.horasUso = decimalSeguro(horasUso);
            this.estadoOperativo = valorSeguro(estadoOperativo);
            this.activo = activo;
            this.fechaRegistro = fechaRegistro;
            this.totalAsignaciones = totalAsignaciones;
            this.totalMantenimientos = totalMantenimientos;
            this.ultimaAsignacion = ultimaAsignacion;
        }

        public int getIdMaquinaria() { return idMaquinaria; }
        public String getCodigoInventario() { return codigoInventario; }
        public String getMarca() { return marca; }
        public String getModelo() { return modelo; }
        public String getMarcaModelo() {
            return (marca + " " + modelo).trim();
        }
        public String getCategoria() { return categoria; }
        public BigDecimal getTonelaje() { return tonelaje; }
        public BigDecimal getHorasUso() { return horasUso; }
        public String getEstadoOperativo() { return estadoOperativo; }
        public boolean isActivo() { return activo; }
        public Timestamp getFechaRegistro() { return fechaRegistro; }
        public int getTotalAsignaciones() { return totalAsignaciones; }
        public int getTotalMantenimientos() { return totalMantenimientos; }
        public Timestamp getUltimaAsignacion() { return ultimaAsignacion; }

        public Object[] toTableRow() {
            return new Object[]{
                codigoInventario, getMarcaModelo(), categoria, tonelaje,
                horasUso, estadoOperativo, activo ? "ACTIVA" : "INACTIVA",
                totalAsignaciones, totalMantenimientos, ultimaAsignacion, fechaRegistro
            };
        }
    }

    public record KpiDisponibilidadMaquinariaDTO(
            int totalMaquinasActivas,
            int disponibles,
            int enRuta,
            int enMantenimiento,
            int inactivas,
            double tasaDisponibilidad) {
    }

    // =========================================================================
    // REPORTE 6: Certificaciones y Vigencia Operativa (Compliance)
    // =========================================================================

    public static class CertificacionVigenciaDTO {
        private final String nombreOperador;
        private final String dui;
        private final String licenciaTipo;
        private final String estadoOperativo;
        private final String categoriaAutorizada;
        private final String numeroAcreditacion;
        private final Date fechaExpedicion;
        private final Date fechaVencimiento;
        private final int diasRestantes;
        private final String estadoVigencia;

        public CertificacionVigenciaDTO(String nombreOperador, String dui,
                                        String categoriaAutorizada, String numeroAcreditacion,
                                        int diasRestantes, String estadoVigencia) {
            this(nombreOperador, dui, "", "DISPONIBLE", categoriaAutorizada,
                    numeroAcreditacion, null, null, diasRestantes, estadoVigencia);
        }

        public CertificacionVigenciaDTO(String nombreOperador, String dui,
                                        String licenciaTipo, String estadoOperativo,
                                        String categoriaAutorizada, String numeroAcreditacion,
                                        Date fechaExpedicion, Date fechaVencimiento,
                                        int diasRestantes, String estadoVigencia) {
            this.nombreOperador = valorSeguro(nombreOperador);
            this.dui = valorSeguro(dui);
            this.licenciaTipo = valorSeguro(licenciaTipo);
            this.estadoOperativo = valorSeguro(estadoOperativo);
            this.categoriaAutorizada = valorSeguro(categoriaAutorizada);
            this.numeroAcreditacion = valorSeguro(numeroAcreditacion);
            this.fechaExpedicion = fechaExpedicion;
            this.fechaVencimiento = fechaVencimiento;
            this.diasRestantes = diasRestantes;
            this.estadoVigencia = valorSeguro(estadoVigencia);
        }

        public String getNombreOperador() { return nombreOperador; }
        public String getDui() { return dui; }
        public String getLicenciaTipo() { return licenciaTipo; }
        public String getEstadoOperativo() { return estadoOperativo; }
        public String getCategoriaAutorizada() { return categoriaAutorizada; }
        public String getNumeroAcreditacion() { return numeroAcreditacion; }
        public Date getFechaExpedicion() { return fechaExpedicion; }
        public Date getFechaVencimiento() { return fechaVencimiento; }
        public int getDiasRestantes() { return diasRestantes; }
        public String getEstadoVigencia() { return estadoVigencia; }

        public Object[] toTableRow() {
            return new Object[]{
                nombreOperador, dui, licenciaTipo, categoriaAutorizada,
                numeroAcreditacion, fechaExpedicion, fechaVencimiento,
                diasRestantes, estadoVigencia
            };
        }
    }

    public record KpiCertificacionesDTO(
            int totalCertificaciones,
            int vigentes,
            int porVencer,
            int vencidas,
            double porcentajeVigentes) {
    }

    // =========================================================================
    // REPORTE 7: Auditoría y Gestión de Usuarios
    // =========================================================================

    public static class AuditoriaUsuarioDTO {
        private final int idUsuario;
        private final String nombreCompleto;
        private final String username;
        private final String nombreRol;
        private final boolean activo;
        private final Timestamp fechaCreacion;
        private final int totalAsignaciones;
        private final int asignacionesCanceladas;
        private final int totalMantenimientos;
        private final int mantenimientosCancelados;
        private final BigDecimal montoTallerAutorizado;

        public AuditoriaUsuarioDTO(int idUsuario, String nombreCompleto,
                                   String username, String nombreRol,
                                   boolean activo, Timestamp fechaCreacion,
                                   int totalAsignaciones, int asignacionesCanceladas,
                                   int totalMantenimientos, int mantenimientosCancelados,
                                   BigDecimal montoTallerAutorizado) {
            this.idUsuario = idUsuario;
            this.nombreCompleto = valorSeguro(nombreCompleto);
            this.username = valorSeguro(username);
            this.nombreRol = valorSeguro(nombreRol);
            this.activo = activo;
            this.fechaCreacion = fechaCreacion;
            this.totalAsignaciones = totalAsignaciones;
            this.asignacionesCanceladas = asignacionesCanceladas;
            this.totalMantenimientos = totalMantenimientos;
            this.mantenimientosCancelados = mantenimientosCancelados;
            this.montoTallerAutorizado = decimalSeguro(montoTallerAutorizado);
        }

        public int getIdUsuario() { return idUsuario; }
        public String getNombreCompleto() { return nombreCompleto; }
        public String getUsername() { return username; }
        public String getNombreRol() { return nombreRol; }
        public boolean isActivo() { return activo; }
        public Timestamp getFechaCreacion() { return fechaCreacion; }
        public int getTotalAsignaciones() { return totalAsignaciones; }
        public int getAsignacionesCanceladas() { return asignacionesCanceladas; }
        public int getTotalMantenimientos() { return totalMantenimientos; }
        public int getMantenimientosCancelados() { return mantenimientosCancelados; }
        public BigDecimal getMontoTallerAutorizado() { return montoTallerAutorizado; }

        public Object[] toTableRow() {
            return new Object[]{
                idUsuario, nombreCompleto, username, nombreRol,
                activo ? "ACTIVO" : "INACTIVO", totalAsignaciones,
                asignacionesCanceladas, totalMantenimientos,
                mantenimientosCancelados, montoTallerAutorizado, fechaCreacion
            };
        }
    }

    public record KpiAuditoriaUsuariosDTO(
            int totalUsuariosAuditados,
            int totalDespachosRegistrados,
            int totalMantenimientosRegistrados,
            int totalCancelaciones,
            BigDecimal montoTotalTallerAutorizado) {
    }

    // =========================================================================
    // REPORTE 8: Utilización y Desgaste de Flota
    // =========================================================================

    public static class UtilizacionDesgasteDTO {
        private final int idMaquinaria;
        private final String codigoInventario;
        private final String marcaModelo;
        private final String categoria;
        private final Timestamp fechaRegistro;
        private final int antiguedadDias;
        private final BigDecimal horasUso;
        private final BigDecimal promedioUsoDiario;
        private final double horasEnRuta;
        private final double horasEnTaller;

        public UtilizacionDesgasteDTO(int idMaquinaria, String codigoInventario,
                                      String marcaModelo, String categoria,
                                      Timestamp fechaRegistro, int antiguedadDias,
                                      BigDecimal horasUso, BigDecimal promedioUsoDiario,
                                      double horasEnRuta, double horasEnTaller) {
            this.idMaquinaria = idMaquinaria;
            this.codigoInventario = valorSeguro(codigoInventario);
            this.marcaModelo = valorSeguro(marcaModelo);
            this.categoria = valorSeguro(categoria);
            this.fechaRegistro = fechaRegistro;
            this.antiguedadDias = antiguedadDias;
            this.horasUso = decimalSeguro(horasUso);
            this.promedioUsoDiario = decimalSeguro(promedioUsoDiario);
            this.horasEnRuta = redondear(horasEnRuta);
            this.horasEnTaller = redondear(horasEnTaller);
        }

        public int getIdMaquinaria() { return idMaquinaria; }
        public String getCodigoInventario() { return codigoInventario; }
        public String getMarcaModelo() { return marcaModelo; }
        public String getCategoria() { return categoria; }
        public Timestamp getFechaRegistro() { return fechaRegistro; }
        public int getAntiguedadDias() { return antiguedadDias; }
        public BigDecimal getHorasUso() { return horasUso; }
        public BigDecimal getPromedioUsoDiario() { return promedioUsoDiario; }
        public double getHorasEnRuta() { return horasEnRuta; }
        public double getHorasEnTaller() { return horasEnTaller; }

        public Object[] toTableRow() {
            return new Object[]{
                codigoInventario, marcaModelo, categoria, antiguedadDias,
                horasUso, promedioUsoDiario, horasEnRuta, horasEnTaller, fechaRegistro
            };
        }
    }

    public record KpiUtilizacionFlotaDTO(
            int totalUnidades,
            double totalHorasEnRuta,
            double totalHorasEnTaller,
            BigDecimal promedioUsoDiarioGlobal) {
    }

    // =========================================================================
    // REPORTE 9: Historial de Rutas, Proyectos y Bitácora de Observaciones
    // =========================================================================

    public static class HistorialRutaDTO {
        private final int idAsignacion;
        private final String nombreProyecto;
        private final String origenDestino;
        private final String estadoRuta;
        private final String codigoMaquinaria;
        private final String operador;
        private final String turnoOperador;
        private final String telefonoOperador;
        private final String estadoAsignacion;
        private final Timestamp fechaAsignacion;
        private final Timestamp fechaRetornoReal;
        private final String observaciones;

        public HistorialRutaDTO(String nombreProyecto, String origenDestino,
                                String codigoMaquinaria, String operador,
                                String estadoAsignacion, Timestamp fechaAsignacion,
                                Timestamp fechaRetornoReal, String observaciones) {
            this(0, nombreProyecto, origenDestino, "", codigoMaquinaria,
                    operador, "", "", estadoAsignacion, fechaAsignacion,
                    fechaRetornoReal, observaciones);
        }

        public HistorialRutaDTO(int idAsignacion, String nombreProyecto,
                                String origenDestino, String estadoRuta,
                                String codigoMaquinaria, String operador,
                                String turnoOperador, String telefonoOperador,
                                String estadoAsignacion, Timestamp fechaAsignacion,
                                Timestamp fechaRetornoReal, String observaciones) {
            this.idAsignacion = idAsignacion;
            this.nombreProyecto = valorSeguro(nombreProyecto);
            this.origenDestino = valorSeguro(origenDestino);
            this.estadoRuta = valorSeguro(estadoRuta);
            this.codigoMaquinaria = valorSeguro(codigoMaquinaria);
            this.operador = valorSeguro(operador);
            this.turnoOperador = valorSeguro(turnoOperador);
            this.telefonoOperador = valorSeguro(telefonoOperador);
            this.estadoAsignacion = valorSeguro(estadoAsignacion);
            this.fechaAsignacion = fechaAsignacion;
            this.fechaRetornoReal = fechaRetornoReal;
            this.observaciones = valorSeguro(observaciones);
        }

        public int getIdAsignacion() { return idAsignacion; }
        public String getNombreProyecto() { return nombreProyecto; }
        public String getOrigenDestino() { return origenDestino; }
        public String getEstadoRuta() { return estadoRuta; }
        public String getCodigoMaquinaria() { return codigoMaquinaria; }
        public String getOperador() { return operador; }
        public String getTurnoOperador() { return turnoOperador; }
        public String getTelefonoOperador() { return telefonoOperador; }
        public String getEstadoAsignacion() { return estadoAsignacion; }
        public Timestamp getFechaAsignacion() { return fechaAsignacion; }
        public Timestamp getFechaRetornoReal() { return fechaRetornoReal; }
        public String getObservaciones() { return observaciones; }

        public Object[] toTableRow() {
            return new Object[]{
                idAsignacion, nombreProyecto, origenDestino, estadoRuta,
                codigoMaquinaria, operador, turnoOperador, estadoAsignacion,
                fechaAsignacion, fechaRetornoReal, observaciones
            };
        }
    }

    public record KpiHistorialRutasDTO(
            int totalViajes,
            int viajesFinalizados,
            int viajesEnCurso,
            int viajesProgramados,
            int viajesCancelados,
            double tasaEfectividad,
            double tasaCancelacion) {
    }

    // =========================================================================
    // REPORTE 10: Productividad y Carga Laboral de Operadores por Turno
    // =========================================================================

    public static class ProductividadOperadorDTO {
        private final int idOperador;
        private final String nombreOperador;
        private final String dui;
        private final String licenciaTipo;
        private final String turno;
        private final String estadoOperativo;
        private final int categoriasCertificadas;
        private final int totalViajes;
        private final int viajesFinalizados;
        private final int viajesCancelados;
        private final int viajesEnCurso;
        private final double horasConducidas;

        public ProductividadOperadorDTO(int idOperador, String nombreOperador,
                                        String dui, String licenciaTipo,
                                        String turno, String estadoOperativo,
                                        int categoriasCertificadas, int totalViajes,
                                        int viajesFinalizados, int viajesCancelados,
                                        int viajesEnCurso, double horasConducidas) {
            this.idOperador = idOperador;
            this.nombreOperador = valorSeguro(nombreOperador);
            this.dui = valorSeguro(dui);
            this.licenciaTipo = valorSeguro(licenciaTipo);
            this.turno = valorSeguro(turno);
            this.estadoOperativo = valorSeguro(estadoOperativo);
            this.categoriasCertificadas = categoriasCertificadas;
            this.totalViajes = totalViajes;
            this.viajesFinalizados = viajesFinalizados;
            this.viajesCancelados = viajesCancelados;
            this.viajesEnCurso = viajesEnCurso;
            this.horasConducidas = redondear(horasConducidas);
        }

        public int getIdOperador() { return idOperador; }
        public String getNombreOperador() { return nombreOperador; }
        public String getDui() { return dui; }
        public String getLicenciaTipo() { return licenciaTipo; }
        public String getTurno() { return turno; }
        public String getEstadoOperativo() { return estadoOperativo; }
        public int getCategoriasCertificadas() { return categoriasCertificadas; }
        public int getTotalViajes() { return totalViajes; }
        public int getViajesFinalizados() { return viajesFinalizados; }
        public int getViajesCancelados() { return viajesCancelados; }
        public int getViajesEnCurso() { return viajesEnCurso; }
        public double getHorasConducidas() { return horasConducidas; }

        public Object[] toTableRow() {
            return new Object[]{
                nombreOperador, dui, licenciaTipo, turno, estadoOperativo,
                categoriasCertificadas, totalViajes, viajesFinalizados,
                viajesCancelados, horasConducidas
            };
        }
    }

    public record KpiProductividadTurnoDTO(
            int totalOperadores,
            int totalViajesFinalizados,
            int totalViajesCancelados,
            double totalHorasConducidas,
            double promedioCumplimientoPorcentaje) {
    }

    private static String valorSeguro(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static BigDecimal decimalSeguro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}

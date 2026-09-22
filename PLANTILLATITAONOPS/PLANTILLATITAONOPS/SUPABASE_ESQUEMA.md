# Esquema verificado de Supabase

Verificado el 2026-09-21 mediante una consulta de solo lectura al metadato del
esquema `public`. No se consultaron registros de negocio y `ConexionBD.java` no
fue modificada.

## Resumen

- Motor: PostgreSQL en Supabase.
- Tablas base: 9.
- Row Level Security: desactivado en las 9 tablas.
- Todas las llaves primarias son `integer` autoincrementales mediante secuencia.
- Los `numeric(10,2)` se mapean como `BigDecimal`.
- Los `timestamp without time zone` se mapean como `Timestamp`.
- Los `date` se mapean como `Date`.
- Los booleanos anulables se mapean como `Boolean`.

## Tablas y modelos Java

### `roles` → `Rol`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_rol` | `integer` | no | `int idRol` |
| `nombre_rol` | `varchar(50)` | no | `String nombreRol` |
| `descripcion` | `text` | sí | `String descripcion` |

Restricciones: `nombre_rol` es único.

### `usuarios` → `Usuario`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_usuario` | `integer` | no | `int idUsuario` |
| `id_rol` | `integer` | no | `int idRol` |
| `nombre_completo` | `varchar(150)` | no | `String nombreCompleto` |
| `username` | `varchar(50)` | no | `String username` |
| `clave_hash` | `varchar(255)` | no | `String claveHash` |
| `activo` | `boolean` | sí | `Boolean activo` |
| `fecha_creacion` | `timestamp` | sí | `Timestamp fechaCreacion` |

Restricciones: `username` es único; `id_rol` referencia `roles.id_rol`.
La tabla no contiene columnas de DUI ni correo.

### `categorias_maquinaria` → `CategoriaMaquinaria`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_categoria` | `integer` | no | `int idCategoria` |
| `nombre_categoria` | `varchar(100)` | no | `String nombreCategoria` |
| `descripcion` | `text` | sí | `String descripcion` |
| `activo` | `boolean` | sí | `Boolean activo` |

Restricciones: `nombre_categoria` es único.

### `maquinaria` → `Maquinaria`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_maquinaria` | `integer` | no | `int idMaquinaria` |
| `id_categoria` | `integer` | no | `int idCategoria` |
| `codigo_inventario` | `varchar(50)` | no | `String codigoInventario` |
| `marca` | `varchar(50)` | sí | `String marca` |
| `modelo` | `varchar(50)` | sí | `String modelo` |
| `tonelaje` | `numeric(10,2)` | sí | `BigDecimal tonelaje` |
| `horas_uso` | `numeric(10,2)` | sí | `BigDecimal horasUso` |
| `estado_operativo` | `varchar(20)` | no | `String estadoOperativo` |
| `activo` | `boolean` | sí | `Boolean activo` |
| `fecha_registro` | `timestamp` | sí | `Timestamp fechaRegistro` |

Restricciones: `codigo_inventario` es único; `id_categoria` referencia
`categorias_maquinaria.id_categoria`.

Estados permitidos: `DISPONIBLE`, `EN_RUTA`, `MANTENIMIENTO`.

### `operadores` → `Operador`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_operador` | `integer` | no | `int idOperador` |
| `nombres` | `varchar(100)` | no | `String nombres` |
| `apellidos` | `varchar(100)` | no | `String apellidos` |
| `dui` | `varchar(10)` | no | `String dui` |
| `licencia_tipo` | `varchar(50)` | no | `String licenciaTipo` |
| `turno` | `varchar(20)` | no | `String turno` |
| `telefono` | `varchar(20)` | sí | `String telefono` |
| `estado_operativo` | `varchar(20)` | no | `String estadoOperativo` |
| `activo` | `boolean` | sí | `Boolean activo` |
| `fecha_registro` | `timestamp` | sí | `Timestamp fechaRegistro` |

Restricciones: `dui` es único.

Turnos permitidos: `DIURNO`, `NOCTURNO`, `ROTATIVO`.

Estados permitidos: `DISPONIBLE`, `EN_RUTA`, `DESCANSO`, `INACTIVO`.

### `operadores_certificaciones` → `OperadorCertificacion`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_certificacion` | `integer` | no | `int idCertificacion` |
| `id_operador` | `integer` | no | `int idOperador` |
| `id_categoria` | `integer` | no | `int idCategoria` |
| `numero_acreditacion` | `varchar(100)` | no | `String numeroAcreditacion` |
| `fecha_expedicion` | `date` | no | `Date fechaExpedicion` |
| `fecha_vencimiento` | `date` | no | `Date fechaVencimiento` |

Restricciones: `numero_acreditacion` es único; las fechas deben cumplir
`fecha_vencimiento >= fecha_expedicion`; referencia a operadores y categorías.

### `rutas_destinos` → `RutaDestino`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_ruta` | `integer` | no | `int idRuta` |
| `nombre_proyecto` | `varchar(150)` | no | `String nombreProyecto` |
| `punto_origen` | `varchar(255)` | no | `String puntoOrigen` |
| `punto_destino` | `varchar(255)` | no | `String puntoDestino` |
| `distancia_km` | `numeric(10,2)` | sí | `BigDecimal distanciaKm` |
| `estado` | `varchar(20)` | no | `String estado` |

Estados permitidos: `PLANIFICADA`, `EN_CURSO`, `FINALIZADA`, `CANCELADA`.

### `asignaciones` → `Asignacion`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_asignacion` | `integer` | no | `int idAsignacion` |
| `id_maquinaria` | `integer` | no | `int idMaquinaria` |
| `id_operador` | `integer` | no | `int idOperador` |
| `id_ruta` | `integer` | no | `int idRuta` |
| `id_usuario_registro` | `integer` | no | `int idUsuarioRegistro` |
| `fecha_asignacion` | `timestamp` | sí | `Timestamp fechaAsignacion` |
| `fecha_estimada_retorno` | `timestamp` | no | `Timestamp fechaEstimadaRetorno` |
| `fecha_retorno_real` | `timestamp` | sí | `Timestamp fechaRetornoReal` |
| `estado_asignacion` | `varchar(20)` | no | `String estadoAsignacion` |
| `observaciones` | `text` | sí | `String observaciones` |

Referencias: maquinaria, operador, ruta y usuario registrador. La fecha estimada
de retorno debe ser posterior a la fecha de asignación.

Estados permitidos: `PROGRAMADA`, `EN_CURSO`, `FINALIZADA`, `CANCELADA`.
Solo puede existir una asignación `EN_CURSO` por maquinaria y por operador.

### `mantenimientos` → `Mantenimiento`

| Columna PostgreSQL | Tipo | Nulo | Campo Java |
|---|---|---:|---|
| `id_mantenimiento` | `integer` | no | `int idMantenimiento` |
| `id_maquinaria` | `integer` | no | `int idMaquinaria` |
| `id_usuario_registro` | `integer` | no | `int idUsuarioRegistro` |
| `tipo_mantenimiento` | `varchar(20)` | no | `String tipoMantenimiento` |
| `fecha_ingreso` | `timestamp` | sí | `Timestamp fechaIngreso` |
| `fecha_salida_estimada` | `timestamp` | sí | `Timestamp fechaSalidaEstimada` |
| `fecha_salida_real` | `timestamp` | sí | `Timestamp fechaSalidaReal` |
| `diagnostico` | `text` | sí | `String diagnostico` |
| `costo` | `numeric(10,2)` | sí | `BigDecimal costo` |
| `estado_mantenimiento` | `varchar(20)` | no | `String estadoMantenimiento` |
| `taller_responsable` | `varchar(150)` | sí | `String tallerResponsable` |

Referencias: maquinaria y usuario registrador.

Tipos permitidos: `PREVENTIVO`, `CORRECTIVO`.

Estados permitidos: `EN_PROCESO`, `FINALIZADO`, `CANCELADO`.

## Relaciones

- `usuarios.id_rol` → `roles.id_rol`.
- `maquinaria.id_categoria` → `categorias_maquinaria.id_categoria`.
- `operadores_certificaciones.id_operador` → `operadores.id_operador`.
- `operadores_certificaciones.id_categoria` → `categorias_maquinaria.id_categoria`.
- `asignaciones.id_maquinaria` → `maquinaria.id_maquinaria`.
- `asignaciones.id_operador` → `operadores.id_operador`.
- `asignaciones.id_ruta` → `rutas_destinos.id_ruta`.
- `asignaciones.id_usuario_registro` → `usuarios.id_usuario`.
- `mantenimientos.id_maquinaria` → `maquinaria.id_maquinaria`.
- `mantenimientos.id_usuario_registro` → `usuarios.id_usuario`.

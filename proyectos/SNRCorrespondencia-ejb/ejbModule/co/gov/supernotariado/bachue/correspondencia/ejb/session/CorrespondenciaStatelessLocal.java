package co.gov.supernotariado.bachue.correspondencia.ejb.session;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.CondicionRegla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.EntidadBase;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.EntidadGenerica;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoria;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoSecuencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Regla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RestriccionEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaGlobal;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;

/**
 * Interface Local para definir los accesos a persistencia
 */
@Local
public interface CorrespondenciaStatelessLocal {
	/**
	 * Remueve una entidad del manejador de persistencia temporalmente para poder realizar operaciones de clonado en la entidad
	 * @param entidad Entidad a remover
	 */
	void detach(EntidadGenerica entidad);

	/**
	 * Remueve una entidad del manejador de persistencia temporalmente para poder realizar operaciones de clonado en la entidad
	 * @param entidad Entidad a remover
	 */
	void detach(EntidadBase entidad);
	
	/**
	 * Busca una entidad por su llave primaria
	 * @param entidad Entidad a consultar Entidad a consultar
	 * @param id Identificador del objeto a afectar
	 * @return Entidad obtenida
	 */
	EntidadGenerica buscarPorId(EntidadGenerica entidad, Object id);

	/**
	 * Guarda una entidad como un registro en la respectiva tabla de la base de datos
	 * Usado para entidades que manejan un atributo llamado "id" 
	 * @param entidad Entidad a consultar
	 * @return Identificador de la Entidad persistida
	 */
	long persistir(EntidadGenerica entidad);

	/**
	 * Guarda una entidad como un registro en la respectiva tabla de la base de datos.
	 * Usado para entidades que no manejan un atributo llamado "id" 
	 * @param entidad Entidad a consultar
	 */
	void persistir(EntidadBase entidad);

	/**
	 * Actualiza una entidad en la respectiva tabla de la base de datos
	 * @param entidad Entidad a consultar
	 */
	void actualizar(EntidadGenerica entidad);

	/**
	 * Actualiza una entidad en la respectiva tabla de la base de datos pero tomando una sentencia SQL
	 * @param sqlUpdate SQL a aplicar
	 */
	void actualizarSQL(String sqlUpdate);

	/**
	 * Elimina una entidad en la respectiva tabla de la base de datos
	 * Usado para entidades que manejan un atributo llamado "id" 
	 * @param entidad Entidad a consultar
	 */
	void eliminar(EntidadGenerica entidad);

	/**
	 * Elimina una entidad en la respectiva tabla de la base de datos
	 * Usado para entidades que no manejan un atributo llamado "id" 
	 * @param entidad Entidad a consultar
	 */
	void eliminar(EntidadBase entidad);

	/**
	 * Busca una lista de registros pasando como parámetro un Named Query
	 * @param namedQuery Nombre del named query
	 * @param args Argumentos a rellenar como parámetros
	 * @return Listado con las entidades encontradas
	 */
	List<EntidadGenerica> buscarPorNamedQuery(String namedQuery, Object... args);

	/**
	 * Busca todos los registros de una tabla en la base de datos
	 * @param type Clase Entity a buscar
	 * @return Listado con las entidades encontradas
	 */
	@SuppressWarnings("rawtypes")
	List<EntidadGenerica> findAll(Class type);

	/**
	 * Busca los parametros activo según un tipo de parámetro
	 * @param tipoParametro Tipo de Parámetro a consultar
	 * @return Listado con los parámetros encontrados
	 */
	List<Parametro> buscarParametrosActivosPorTipo(String tipoParametro);

	/**
	 * Busca los parametros activo según un tipo de parámetro
	 * @param tipoParametro Tipo de Parámetro a consultar
	 * @param nombre Nombre clave del parámetro
	 * @return Listado con los parámetros encontrados
	 */
	List<Parametro> buscarParametrosActivosPorTipoNombre(String tipoParametro, String nombre);

	
	/**
	 * Busca los parámetros guardados según los filtros. Se usa en el listado de parámetros
	 * @param activo Indica si buscar registros activos
	 * @param tipoParametro Tipo de Parámetro a consultar 
	 * @param ordenadoPor Orden a aplicar en la consulta Orden a aplicar en la consulta
	 * @param limite Límite de registros a aplicar en la consulta
	 * @param buscarDesde Inicio de registros en la búsqueda (paginación)
	 * @return Listado con los parámetros encontrados
	 */
	List<Parametro> buscarParametrosPor(Boolean activo, String tipoParametro, String ordenadoPor, int limite, int buscarDesde);

	/**
	 * Cuenta los parámetros guardados según los filtros. Se usa en el listado de parámetros
	 * @param activo Indica si buscar registros activos
	 * @param tipoParametro Tipo de Parámetro a consultar
	 * @return Número de registros encontrados
	 */
	int contarParametrosPor(Boolean activo, String tipoParametro);

	/**
	 * Busca todos los pasos activos por un proceso dado
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los Pasos encontrados
	 */
	List<Paso> buscarPasosActivosPorProceso(long procesoId);

	/**
	 * Busca todos los pasos activos por proceso en orden descendente
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los Pasos encontrados
	 */
	List<Paso> buscarPasosActivosPorProcesoOrdenDesc(long procesoId);

	/**
	 * Busca todos los pasos atrasados dad una fecha de finalización del paso
	 * @param fechaFinPaso Fecha límite a buscar los pasos
	 * @return Listado con los Pasos encontrados
	 */
	List<PasoEjecutado> buscarPasosAtrasados(Date fechaFinPaso);

	/**
	 * Busca los recordatorios activos por paso dada una fecha de finalización del paso
	 * @param fechaFinPaso Fecha límite a buscar los pasos
	 * @return Listado con los Pasos encontrados
	 */
	List<PasoEjecutado> buscarRecordatoriosPasos(Date fechaFinPaso);
	
	/**
	 * Busca los pasos ejecutados dados unos filtros.  Usado en la consulta principal de la bandeja de entrada de los usuarios
	 * @param filtros Mapa con los filtros a aplicar
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param entradas Lista de entradas con metadatos del paso
	 * @param grupos Lista de grupos asignados sobre los cuales consultar los pasos
	 * @param ordenadoPor Orden a aplicar en la consulta
	 * @param limite Límite de registros a aplicar en la consulta
	 * @param buscarDesde Inicio de registros en la búsqueda (paginación)
	 * @return Listado con los pasos encontrados
	 */
	List<PasoEjecutado> buscarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entradas, List <String> grupos, String ordenadoPor, int limite, int buscarDesde);

	/**
	 * Cuenta los pasos ejecutados dados unos filtros.  Usado en la consulta principal de la bandeja de entrada de los usuarios
	 * @param filtros Mapa con los filtros a aplicar
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param entradas Lista de entradas con metadatos del paso
	 * @param grupos Lista de grupos asignados sobre los cuales consultar los pasos
	 * @return Número de registros encontrados
	 */
	int contarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entradas, List <String> grupos);

	/**
	 * Busca un listado de procesos.  Usado en la lista de administración de procesos
	 * @param activo Indica si buscar registros activos
	 * @param filtro Cadena con filtro general a consultar
	 * @param ordenadoPor Orden a aplicar en la consulta
	 * @param limite Límite de registros a aplicar en la consulta
	 * @param buscarDesde Inicio de registros en la búsqueda (paginación)
	 * @return Listado con los procesos encontrados
	 */
	List<Proceso> buscarProcesosPor(Boolean activo, String filtro, String ordenadoPor, int limite, int buscarDesde);

	/**
	 * Cuenta los registros de un listado de procesos.  Usado en la lista de administración de procesos
	 * @param activo Indica si buscar registros activos
	 * @param filtro Cadena con filtro general a consultar
	 * @return Número de registros encontrados
	 */
	int contarProcesosPor(Boolean activo, String filtro);

	/**
	 * Busca las entradas activas de un paso dado
	 * @param pasoId Identificador del paso
	 * @return Listado con las entradas encontradas
	 */
	List<Entrada> buscarEntradasActivasPorPaso(long pasoId);

	/**
	 * Busca las reglas activas de un paso dado
	 * @param pasoId Identificador del paso
	 * @return Listado con las reglas encontradas
	 */
	List<Regla> buscarReglasActivasPorPaso(long pasoId);

	/**
	 * Busca las condiciones de una regla dada
	 * @param reglaEntradaId Identificador de la Regla a consultar
	 * @return Listado con las condiciones de la regla encontradas
	 */
	List<CondicionRegla> buscarCondicionReglaPorRegla(long reglaEntradaId);

	/**
	 * Busca los valores guardados para las entradas del formulario de un paso ya ejecutado
	 * @param pasoEjecutadoId Identificador del Paso Ejecutado
	 * @return Listado con las entradas encontradas
	 */
	List<ValorEntrada> buscarValorEntradaPorPasoEjecutado(long pasoEjecutadoId);

	/**
	 * Busca las categorías asociadas a un proceso
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los procesos categoria encontrados
	 */
	List<ProcesoCategoria> buscarCategoriasPorProceso(long procesoId);

	/**
	 * Obtiene el siguiente identificador interno para un proceso.  Este identificador es igual en versiones del mismo proceso.  Pero cambia en diferentes procesos.
	 * @return Secuencia del proceso encontrada
	 */
	long obtenerSecuenciaProceso();

	/**
	 * Obtiene el siguiente identificador para una entrada de tipo secuencia
	 * @return Identificador de la entrada tipo secuencia
	 */
	long obtenerIdentificadorSecuenciaEntrada();
	
	/** 
	 * Valida por ejecuciones de un proceso según una restricción de fecha
	 * @param proceso Proceso a validar
	 * @return Lista de procesos encontrados
	 */
	List<ProcesoEjecutado> validarProcesosEjecutadosPorPeriodo(Proceso proceso);

    /**
     * Guarda todos los detalles de un proceso
     * @param proceso Proceso a guardar
     * @param categoriasSeleccionadas Categorias asociadas al proceso
     * @param usuarioActualId Identificador del Usuario actual
     * @param direccionIp Dirección IP del usuario actual
     */
    public void guardarProceso(Proceso proceso, Long[] categoriasSeleccionadas, String usuarioActualId, String direccionIp);

	/**
	 * Guarda las secuencias asociadas a una entrada
	 * @param secuenciaEntrada Lista con las secuencias de entradas a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarSecuenciasEntrada(List<SecuenciaEntrada> secuenciaEntrada, String usuarioActualId, String direccionIp);

	/**
	 * Guarda las secuencias asociadas a un proceso
	 * @param secuenciaProceso Lista con las secuencias de proceso a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarSecuenciasProceso(List<ProcesoSecuencia> secuenciaProceso, String usuarioActualId, String direccionIp);

	/**
	 * Guarda las secuencias globales asociadas a un parametro
	 * @param secuencias Lista con las secuencias globales a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarSecuenciaGlobal(List<SecuenciaGlobal> secuencias, String usuarioActualId, String direccionIp);

	
	/**
	 * Guarda un paso asociado a un proceso
	 * @param paso Paso a guardar
	 * @param proceso Proceso asociado al paso
	 * @param pasosListSize Tamaño de lista de pasos actual para indicar el orden que se guarda para el paso
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarPaso(Paso paso, Proceso proceso, int pasosListSize, String usuarioActualId, String direccionIp);

	
	/**
	 * Guarda una entrada asociada a un paso
	 * @param entrada Entrada a guardar
	 * @param paso Paso asociado a la entrada
	 * @param entradasListSize Tamaño de lista de entradas actual
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarEntrada(Entrada entrada, Paso paso, int entradasListSize, String usuarioActualId, String direccionIp);
	
	/**
	 * Elimina una entrada y todas sus reglas asociadas
	 * @param entrada Entrada a eliminar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void eliminarEntrada(Entrada entrada, String usuarioActualId, String direccionIp);
	
	/**
	 * Guarda una regla y sus condiciones
	 * @param regla Regla a guardar
	 * @param paso Paso asociado a la regla
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarRegla(Regla regla, Paso paso, String usuarioActualId, String direccionIp);

	/**
	 * Guarda las restricciones asociadas a una entrada
	 * @param restricciones Lista de restricciones a guardar
	 * @param entrada Entrada asociada a las restricciones
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarRestriccionesEntrada(List<RestriccionEntrada> restricciones, Entrada entrada, String usuarioActualId, String direccionIp);

	/**
	 * Obtiene un listado de radicados para componentes autocomplete
	 * @param texto Texto con la consulta del radicado 
	 * @param filtros Mapa con los filtros de la consulta
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param grupos Lista de grupos asignados por los cuales filtrar
	 * @return Lista de cadenas con las sugerencias de radicados
	 */
	List<String> obtenerSugerenciasRadicados(String texto, Map<String, Object> filtros, int tipoConsulta, List<String> grupos);

}

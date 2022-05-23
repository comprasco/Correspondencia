package co.gov.supernotariado.bachue.correspondencia.ejb.negocio;

import java.util.List;

import javax.ejb.Local;

import org.primefaces.model.UploadedFile;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Archivo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RolGrupo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaGlobal;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;

/**
 * Interface Local para definir las operaciones para los parámetros del sistema
 */
@Local
public interface ParametrosStatelessLocal {

	/**
	 * Obtiene un listado de los tipos de datos que tienen validación de longitud
	 * @return Lista de los tipos de dato
	 */
	List<String> getTiposCamposLongitud();
	
	/**
	 * Permite obtener una instancia para la integración con el servicio web de catálogos
	 * @return Objeto IntegracionCatalogos inicializado
	 */
	IntegracionCatalogos obtenerIntegracionCatalogos();

	
	/**
	 * Permite obtener un listado de parámetros activos dado un tipo de parámetro
	 * 
	 * @param tipoParametro Cadena con el tipo de parámetro a buscar
	 * @return Lista con los parametros encontrados
	 */
	List<Parametro> obtenerParametrosActivosPorTipo(String tipoParametro);

	
	/**
	 * Busca los parámetros guardados según los filtros. Se usa en el listado de parámetros
	 * @param activo Indicador para buscar activos o inactivos
	 * @param tipoParametro Cadena con el tipo de parámetro a buscar
	 * @param ordenadoPor Campo a ordenar (paginación)
	 * @param limite Límite de registros (paginación)
	 * @param buscarDesde Comienzo de registros (paginación)
	 * @return Lista con los parámetros encontrados
	 */
	List<Parametro> buscarParametrosPor(Boolean activo, String tipoParametro, String ordenadoPor, int limite, int buscarDesde);
	
	
	/**
	 * Cuenta los parámetros guardados según los filtros. Se usa en el listado de parámetros
	 * @param activo Indicador para buscar activos o inactivos
	 * @param tipoParametro Cadena con el tipo de parámetro a buscar
	 * @return Contador de parámetros según los resultados
	 */
	int contarParametrosPor(Boolean activo, String tipoParametro);

	
	/**
	 * Inserta o actualiza un parametro en la base de datos
	 * @param parametro Objeto parámetro a guardar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Objeto parámetro guardado
	 */
	Parametro guardarParametro(Parametro parametro, String usuarioActualId, String direccionIp);
	
	
	/**
	 * Guarda un parámetro de tipo plantilla
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param parametro Objeto parámetro a guardar
	 * @param archivoCargado Archivo a guardar
	 * @param archivoGuardado Archivo actual guardado para la plantilla
	 * @param codigoOrip Código de la Orip que ejecuta
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Mensaje de error si hubo alguno
	 */
	String guardarPlantilla(IntegracionCatalogos integracionCatalogos, Parametro parametro, UploadedFile archivoCargado, Archivo archivoGuardado, String codigoOrip, String usuarioActualId, String direccionIp);

	
	/**
	 * Obtiene un archivo por su Identificador
	 * @param idArchivo Identificador del archivo
	 * @return Archivo obtenido
	 */
	Archivo obtenerArchivoPorId(long idArchivo);

	
	/**
	 * Obtiene una lista de grupos por rol interno
	 * @param idRol Identificador del Rol a Buscar
	 * @return Lista con los RolesGrupos encontrados
	 */
	List<RolGrupo> obtenerGruposPorRol(long idRol);
	

	/**
	 * Obtiene una lista de roles internos por grupo
	 * @param idGrupo Identificador del Grupo a Buscar
	 * @return Lista con los RolesGrupos encontrados
	 */
	List<RolGrupo> obtenerRolesPorGrupo(long idGrupo);

	
	/**
	 * Elimina un rol grupo
	 * @param rolGrupo Objeto rolgrupo a eliminar
	 */
	void eliminarRolGrupo(RolGrupo rolGrupo);
	
	
	/**
	 * Guarda una asogiación entre roles y grupos
	 * @param idRol Identificador del Rol a guardar
	 * @param idGrupo Identificador del Grupo a guardar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarRolGrupo(long idRol, long idGrupo, String usuarioActualId, String direccionIp);


	/**
	 * Obtiene un listado de secuencias globales asociadas a un parámetro
	 * @param idParametro Identificador del Parametro
	 * @return Listado de secuencias encontradas
	 */
	List<SecuenciaGlobal> obtenerSecuenciasGlobal(long idParametro);

	
	/**
	 * Elimina un trozo de secuencia global
	 * @param secuencia Secuencia global a eliminar
	 */
	void eliminarSecuenciaGlobal(SecuenciaGlobal secuencia);

	
	/**
	 * Guarda las secuencias asociadas a un parámetro
	 * @param secuencias Listado de secuencias a guardar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarSecuenciaGlobal(List<SecuenciaGlobal> secuencias, String usuarioActualId, String direccionIp);

	
	/**
	 * Obtiene el Rol Interno del Usuario según sus Grupos
	 * Deja el menor rol encontrado (mas permisos entre menor es el rol)
	 * @param grupos Lista con los identificadores del Grupo
	 * @return Identificador del Rol Interno a aplicar
	 */
	long obtenerRolInternoUsuario(List<GrupoTO> grupos);

	/**
	 * Lista con objetos de tipo JsonOpcion cuando el parámetro es tipo lista
	 * @param parametro Parámetro a evaluar sus opciones
	 * @return Lista con valores activos
	 */
	List<JsonOpcion> obtenerOpcionesJsonActivas(Parametro parametro);
	
	/**
	 * Permite consultar un parámetro por su ID
	 * @param parametroId Identificador del parámetro
	 */
	public Parametro buscarParametroPorId(long parametroId);

	/**
	 * Inicializa el valor del campo JSON del parámetro para las listas de opciones de datos
	 * @param parametro Parámetro a trabajar
	 */
	void inicializarJsonDataParametro(Parametro parametro);

}

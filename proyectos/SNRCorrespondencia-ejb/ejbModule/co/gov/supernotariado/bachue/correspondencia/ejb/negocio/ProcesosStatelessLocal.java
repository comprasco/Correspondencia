package co.gov.supernotariado.bachue.correspondencia.ejb.negocio;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Archivo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.EntidadGenerica;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoria;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoSecuencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Regla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RestriccionEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionUsuarios;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import net.sf.jasperreports.engine.JRException;

/**
 * Interface Local para definir las operaciones para los parámetros del sistema
 */
@Local
public interface ProcesosStatelessLocal {

	/**
	 * Actualiza una entidad en la base de datos
	 * @param entidad Entidad a buscar
	 * @return Entidad consultada
	 */
	EntidadGenerica buscarEntidadPorId(EntidadGenerica entidad, Object id);

	
	/**
	 * Actualiza una entidad en la base de datos
	 * @param entidad Entidad a afectar
	 */
	void actualizarEntidad(EntidadGenerica entidad);
	
	/**
	 * Obtiene un listado de los tipos de entradas que se admiten para reglas
	 * @return Listado de Cadenas con los tipos de entradas
	 */
	List<String> getTiposEntradasReglas();
	
	/**
	 * Obtiene un listado de los tipos de entradas que tienen validación de longitud
	 * @return Lista de Cadenas con los tipos de entradas
	 */
	List<String> getTiposEntradasLongitud();

	/**
	 * Obtiene un listado de los tipos de datos que tienen validación de longitud
	 * @return Lista de Cadenas con los tipos de entradas
	 */
	List<String> getTiposEntradasRestricciones();
	
	
	/**
	 * Obtiene un proceso activo por su identificador
	 * @param identificador Identificador del proceso
	 * @return Proceso obtenido
	 */
	Proceso obtenerProcesoActivoPorIdentificador(long identificador);

	
	/**
	 * Obtiene un proceso activo por su nombre clave
	 * @param nombreClave Cadena con el nombre clave a buscar
	 * @return Proceso obtenido
	 */
	Proceso obtenerProcesoActivoPorNombreClave(String nombreClave);

	
	/**
	 * Obtiene el paso actual de un proceso ejecutado
	 * @param idProcesoEjecutado Identificador del proceso
	 * @return Lista de Pasos encontrados
	 */
	List<PasoEjecutado> obtenerPasosEjecutadosPorProceso(long idProcesoEjecutado);
	
	
	/**
	 * Obtiene un listado de procesos ejecutados por radicado e identificador del proceso
	 * @param radicado Radicado a buscar
	 * @param identificadorProceso Identificador del proceso a buscar
	 * @return Proceso ejecutado encontrado
	 */
	ProcesoEjecutado obtenerProcesoEjecutadoPorSecuenciaIdentificador(String radicado, long identificadorProceso);
	

	/**
	 * Obtiene un listado de procesos ejecutados por radicado e identificador del proceso
	 * @param secuenciaSalidaProceso Secuencia de salida (consecutivo ee) a buscar 
	 * @param identificadorProceso Identificador de proceso a buscar
	 * @return Proceso Ejecutado obtenido
	 */
	ProcesoEjecutado obtenerProcesoEjecutadoPorSecuenciaSalidaIdentificador(String secuenciaSalidaProceso, long identificadorProceso);
	
	
	/**
	 * Revisa las reglas de las entradas para fijar el paso siguiente
	 * Se revisan las entradas en orden inverso, las siguientes reglas van sobreescribiendo las anteriores
	 * Esto por que también se tienen en cuenta entradas de pasos anteriores
	 * Devuelve el paso siguiente del proceso, comenzando por el paso 0 de la lista proximosPasos
	 * Sobreescribe la propiedad permitirparalelo de la configuración del paso, con la que tenga la regla
	 * @param pasoEjecutado Paso ejecutado sobre el cual validar
	 * @param entradasAnteriores Lista de entradas anteriores para evaluar su valor en las reglas a evaluar
	 * @param proximosPasos Listado de proximos pasos para seleccionar el siguiente paso basado en las reglas
	 * @return Paso con el siguiente en el proceso
	 */
	Paso validarReglas(PasoEjecutado pasoEjecutado, List<ValorEntrada> entradasAnteriores, List<Paso> proximosPasos);
	
	
	/**
	 * Permite obtener un List a partir de la lista de opciones de un entrada guardado en un campo tipo json
	 * Utiliza el objeto NodeTO a manera de objeto de key:value.
	 * Solo tiene en cuenta las opciones activas  
	 * @param entrada Entrada a validar
	 * @param entradaPadreId Indica el valor del campo padre en el caso de campos anidados 
	 * @return Lista de Opciones de una entrada
	 */
	List<NodoTO> getListaOpcionesEntrada(Entrada entrada, String entradaPadreId);
	

	/**
	 * Permite obtener un List a partir de la lista de opciones de un entrada guardado en un campo tipo json
	 * Utiliza el objeto NodeTO a manera de objeto de key:value.
	 * Tiene en cuenta tanto activos como inactivos, esto para recuperar valores que se guardaron anteriormente a la inactivación del dato  
	 * @param entrada Entrada sobre la que obtener las opciones
	 * @param entradaPadreId Indica el valor del campo padre en el caso de campos anidados 
	 * @return Lista de nodos con las opciones
	 */
	List<NodoTO> getListaOpcionesEntradaTodos(Entrada entrada, String entradaPadreId);

	
	/**
	 * Calcula el tiempo limite de el paso segun las horas laborales
	 * @param tiempoPaso Tiempo configurado para el paso
	 * @param hl Horas laborales
	 * @param tipoPeriodo Tipo de periodo configurado para el paso
	 * @return Calendario con la fecha de fin del paso
	 */
	Calendar calcularTiempoFinPaso(int tiempoPaso, String[] hl, String tipoPeriodo);
	
	
	/** Usado para calcular el tiempo entre horas laborales para finalizar una tarea
	 * @param hl Horas laborales 
	 * @param time Tiempo periodo calculado
	 * @param end Calendario para calcular 
	 * @param aux Calendario auxiliar para calcular
	 */
	void calculaHorasLaborales(String[] hl, int time, Calendar end, Calendar aux);

	
	
	/**
	 * Verifica todos los campos anidados contra los valores de los campos padre
	 * Recupera también la lista de opciones para todos los campos tipo selección
	 * No se puede buscar solamente por el valor del campo padre seleccionado ya que se debe buscar por campos anidados de mas de 1 nivel
	 * @param entradas campos a validar
	 */
	void validarEntradasAnidadas(List<ValorEntrada> entradas);


	/**
	 * Identifica entradas anidadas para validaciones en formulario
	 * Identifica si tiene reglas asociadas
	 * Usado al cargar el formulario para ser llenado, y sabe si debe validar en cada cambio de valor de campo las reglas y entradas anidadas.  No en consultas de detalle posteriores.
	 * @param entradas Lista de entradas con las cuales validar
	 */
	void validarReglasYEntradasAnidadas(List<ValorEntrada> entradas);

	
	/**
	 * Guarda la transaccion para avanzar una Etapa
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param pasoEjecutado Paso Ejecutado a guardar
	 * @param usuarioActual Usuario actual
	 * @param siguientePaso Siguiente paso dentro del proceso
	 * @param categoriasSeleccionadas Categorias asociadas al paso
	 * @param usuariosAsignados Listado de usuarios asignados
	 * @param gruposAsignados Listado de grupos asignados
	 * @param opcion 1-avanzar, 2-devolver
	 * @return Paso ejecutado guardado
	 */
	PasoEjecutado guardarTransaccionPasoEjecutado(IntegracionUsuarios integracionUsuarios, PasoEjecutado pasoEjecutado, UsuarioTO usuarioActual, Paso siguientePaso, Long categoriasSeleccionadas, String[] usuariosAsignados, String[] gruposAsignados, List<ValorEntrada> entradas, int opcion);


	/**
	 * Guarda un nuevo Paso Ejecutado o actualiza uno existente
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param pasoEjecutado Paso Ejecutado a guardar
	 * @param usuarioActual Usuario actual
	 * @param siguientePaso Siguiente paso dentro del proceso
	 * @param categoriasSeleccionadas Categorias asociadas al paso
	 * @param ipAddress Dirección IP del usuario actual
	 * @param oripEjecucion ORIP de ejecución de proceso
	 * @return Paso ejecutado guardado
	 */
	PasoEjecutado guardarPasoEjecutado(IntegracionUsuarios integracionUsuarios, PasoEjecutado pasoEjecutado, UsuarioTO usuarioActual, Paso siguientePaso, Long categoriasSeleccionadas, String ipAddress, String oripEjecucion);
	

	/**
	 * Guarda información sobre las asignaciones del paso
	 * @param pasoEjecutado Paso ejecutado a validar
	 * @param usuariosAsignados Listado de usuarios asignados
	 * @param gruposAsignados Listado de grupos asignados
	 * @return Paso Ejecutado con los usuarios o grupos asignados
	 */
	PasoEjecutado guardarAsignacionesSiguientePaso(PasoEjecutado pasoEjecutado, String[] usuariosAsignados, String[] gruposAsignados);


	/**
	 * Inicializa un Paso Ejecutado desde ceros.  No lo guarda
	 * @param paso Paso a asociar al paso ejecutado
	 * @param idUsuario Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual Dirección IP del usuario actual
	 * @param oripEjecucion ORIP de ejecución del proceso
	 * @return Paso Ejecutado inicializado
	 */
	PasoEjecutado inicializaPasoEjecutado(Paso paso, String idUsuario, String direccionIp, String oripEjecucion);


	/**
	 * Inicializa un paso ejecutado con la información básica en estado pendiente y lo persiste.  Este paso lo completará otro usuario más adelante
	 * @param proximoPaso Proximo paso a asignar
	 * @param pasoEjecutadoInicial Paso ejecutado base para inicializar el actual
	 * @param opcion 1-avanzar, 2-devolver
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Paso ejecutado inicializado
	 */
	PasoEjecutado inicializarNuevoPaso(Paso proximoPaso, PasoEjecutado pasoEjecutadoInicial, int opcion, String direccionIp);

	
	/**
	 * Permite generar un nuevo paso para un proceso 
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param usuariosAsignados Listado de usuarios asignados al nuevo paso
	 * @param gruposAsignados Listaod de grupos asignados al nuevo paso
	 * @param siguientePaso Paso que se asignará al paso ejecutado
	 * @param pasoEjecutadoActual Paso ejecutado actual dentro del proceso
	 * @param opcion 1-avanzar, 2-devolver
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Paso ejecutado inicializado
	 */
	PasoEjecutado generarNuevoPaso(IntegracionUsuarios integracionUsuarios, String[] usuariosAsignados, String[] gruposAsignados, Paso siguientePaso, PasoEjecutado pasoEjecutadoActual, int opcion, String direccionIp);

	
	/**
	 * Ejecuta un paso del proceso.  Guarda las entradas asociadas
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param pasoEjecutadoActual PasoEjecutado a ejecutar
	 * @param idProceso Identificador del proceso asociado
	 * @param entradasActuales Listado con las entradas a guardar
	 * @param usuarioActual Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @param opcion 1-avanzar, 2-devolver
	 * @param oripEjecucion ORIP de ejecución del proceso
	 * @return Paso Ejecutado que fue guardado
	 */
	PasoEjecutado ejecutarPasoProceso(IntegracionUsuarios integracionUsuarios, PasoEjecutado pasoEjecutadoActual, long idProceso, List<ValorEntrada> entradasActuales, UsuarioTO usuarioActual, String direccionIp, int opcion, String oripEjecucion);

	
	
	/**
	 * Establece los usuarios para el proximo paso
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param paso Paso sobre el cual evaluar los usuarios
	 * @param pasoEjecutadoAnterior Paso anterior
	 * @param usuarioActual Usuario actual
	 * @param nivelAcceso Indica el nivel de acceso que tiene el usuario actual
	 * @return Lista de identificadores de usuario
	 */
	List<String[]> estableceUsuariosProximoPaso(IntegracionUsuarios integracionUsuarios, Paso paso, PasoEjecutado pasoEjecutadoAnterior, UsuarioTO usuarioActual, int nivelAcceso);


	
	/**
	 * Establece el siguiente paso del proceso a partir de la evaluacion de las entradas
	 * @param entradasActuales Lista con entradas para validar
	 * @param pasoEjecutadoActual Paso Ejecutado actual
	 * @param proximosPasos Listado de los próximos pasos del proceso para determinar cual es el siguiente
	 * @return Próximo Paso
	 */
	Paso establecerSiguientePaso(List<ValorEntrada> entradasActuales, PasoEjecutado pasoEjecutadoActual, List<Paso> proximosPasos);

	
	/**
	 * Establece los valores de las entradas ya guardadas de un paso
 	 * @param pasoEjecutadoId Identificador del paso ejecutado
	 * @return Lista de las entradas obtenidas
	 */
	List<ValorEntrada> buscarValoresEntradas(long pasoEjecutadoId);


	/**
	 * Elimina un proceso sin ningun otro procedimiento adicional
	 * @param proceso Proceso a eliminar
	 */
	void borrarProcesoSimple(Proceso proceso);
	
	
	/**
	 * Borra el proceso y todas sus versiones si no tiene ejecuciones
	 * @param procesoActual Proceso sobre el cual efectuar la operación
	 * @return número de procesos ejecutados encontrados.  0 indica que se eliminó correctamente. mayor a 1 indica que hay procesos ejecutados y no se puede borrar el proceso
	 */
	int borrarProceso(Proceso procesoActual);

	
	/**
	 * Elimina versiones anteriores de los procesos para eliminar datos basura
	 * Solo las elimina si no tienen procesos ejecutados 
	 * @param procesoActual Proceso sobre el cual efectuar la operación
	 * @return número de procesos ejecutados encontrados.  0 indica que se eliminó correctamente. Mayor a 1 indica que hay procesos ejecutados y no se puede borrar el proceso
	 */
	int eliminarVersionActual(Proceso procesoActual);

	
	/**
	 * Elimina versiones anteriores de los procesos para eliminar datos basura
	 * Solo las elimina si no tienen procesos ejecutados 
	 * @param proceso Proceso sobre el cual efectuar la operación
	 * @return Mensaje de error si no se logró eliminar la versión
	 */
	String eliminaVersionesAnterioresProceso(Proceso proceso);

	
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
	 * Genera un nuevo proceso copiado de uno actual
	 *
	 * @param procesoId Proceso a generar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @param archivarActual Indica si el proceso actual se archiva o continua activo
	 * @return Proceso generado
	 */
	Proceso generarNuevoProceso(long procesoId, String usuarioActualId, String direccionIp, boolean archivarActual);


    /**
     * Guarda todos los detalles de un proceso
     * @param proceso Proceso a guardar
     * @param categoriasSeleccionadas Categorias asociadas al proceso
     * @param usuarioActualId Identificador del Usuario actual
     * @param direccionIp Dirección IP del usuario actual
     */
    void guardarProceso(Proceso proceso, Long[] categoriasSeleccionadas, String usuarioActualId, String direccionIp);

	
	/**
	 * Elimina una secuencia de una entrada
	 * @param secuenciaEntrada Secuencia a eliminar
	 */
	void eliminarSecuenciaEntrada(SecuenciaEntrada secuenciaEntrada);

	
	/**
	 * Busca las secuencias de una entrada de este tipo 
	 * @param entradaId Identificador de la entrada a consultar
	 * @return Lista de secuencias asociadas
	 */
	List<SecuenciaEntrada> buscarSecuenciaEntrada(long entradaId);


	
	/**
	 * Guarda las secuencias asociadas a una entrada
	 * @param secuenciaEntrada Lista con las secuencias de entradas a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarSecuenciasEntrada(List<SecuenciaEntrada> secuenciaEntrada, String usuarioActualId, String direccionIp);


	/**
	 * Elimina una secuencia de un proceso
	 * @param secuencia Secuencia a eliminar
	 */
	void eliminarSecuenciaProceso(ProcesoSecuencia secuencia);


	/**
	 * Busca las secuencia de proceso asociadas a un proceso
	 * @param procesoId Identificador del proceso a consultar
	 * @return Lista de secuencias obtenidas
	 */
	List<ProcesoSecuencia> buscarSecuenciaProceso(long procesoId);


	
	/**
	 * Guarda las secuencias asociadas a un proceso
	 * @param secuenciaProceso Lista con las secuencias de proceso a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarSecuenciasProceso(List<ProcesoSecuencia> secuenciaProceso, String usuarioActualId, String direccionIp);



	/**
	 * Busca los procesos ejecutados de un proceso
	 * @param procesoId Identificador del proceso a consultar
	 * @return Lista de Procesos ejecutados obtenidos
	 */
	List<ProcesoEjecutado> buscarProcesosEjecutadosProceso(long procesoId);


	/**
	 * Busca todos los pasos activos por un proceso dado
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los Pasos encontrados
	 */
	List<Paso> buscarPasosActivosProceso(long procesoId);


	/**
	 * Busca las entradas activas de un paso dado
	 * @param pasoId Identificador del paso
	 * @return Listado con las entradas encontradas
	 */
	List<Entrada> buscarEntradasActivasPaso(long pasoId);

	
	/**
	 * Busca las entradas activas de todos los pasos de un proceso
	 * @param procesoId Identificador del proceso al cual 
	 * @return Lista de entradas obtenidas
	 */
	List<Entrada> buscarEntradasActivasProceso(long procesoId);


	/**
	 * Busca todos los pasos ejecutados de un proceso ejecutado
	 * @param procesoEjecutadoId Identificador del proceso a consultar
	 * @return Lista con los Pasos Ejecutados obtenidos
	 */
	List<PasoEjecutado> buscarPasosEjecutadosAnteriores(long procesoEjecutadoId);


	/**
	 * Busca las categorías asociadas a un proceso
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los procesos categoria encontrados
	 */
	List<ProcesoCategoria> buscarCategoriasProceso(long procesoId);

	
	/**
	 * Busca las reglas asociadas a un paso
	 * @param pasoId Identificador del paso a consultar
	 * @return Lista de reglas obtenidas
	 */
	List<Regla> buscarReglasPaso(long pasoId);

	
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
	 * @param entradas Lista de entradas con metadatos del paso Lista de entradas con metadatos del pasoListSize Tamaño de lista de entradas actual
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
    void guardarEntrada(Entrada entrada, Paso paso, int entradas, String usuarioActualId, String direccionIp);

    
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
	 * Busca las restricciones de una entrada
	 * @param entradaId Identificador de la entrada a consultar
	 * @return Lista de restricciones de la entrada consultada
	 */
	List<RestriccionEntrada> buscarRestriccionesEntrada(long entradaId);


	/**
	 * Guarda las restricciones asociadas a una entrada
	 * @param restricciones Lista de restricciones a guardar
	 * @param entrada Entrada asociada a las restricciones
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarRestriccionesEntrada(List<RestriccionEntrada> restricciones, Entrada entrada, String usuarioActualId, String direccionIp);


	/**
	 * Elimina una restricción de una entrada
	 * @param restriccionEntrada Restriccion de entrada a eliminar
	 */
	void eliminarRestriccionEntrada(RestriccionEntrada restriccionEntrada);


	/**
	 * Obtiene un listado de radicados para componentes autocomplete
	 * @param texto Texto con la consulta del radicado 
	 * @param filtros Mapa con los filtros de la consulta
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param grupos Lista de grupos asignados por los cuales filtrar
	 * @return Lista de cadenas con las sugerencias de radicados
	 */
	List<String> obtenerSugerenciasRadicados(String texto, Map<String, Object> filtros, int tipoConsulta, List<String> grupos);

	
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
	List<PasoEjecutado> buscarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entradas, List<String> grupos, String ordenadoPor, int limite, int buscarDesde);


	
	/**
	 * Cuenta los pasos ejecutados dados unos filtros.  Usado en la consulta principal de la bandeja de entrada de los usuarios
	 * @param filtros Mapa con los filtros a aplicar
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param entradas Lista de entradas con metadatos del paso
	 * @param grupos Lista de grupos asignados sobre los cuales consultar los pasos
	 * @return Número de registros encontrados
	 */
	int contarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entradas, List<String> grupos);


    /**
     * Permite guardar un listado de entradas asociadas a un paso ejecutado
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
     * @param entradas Entradas a guardar
     * @param pasoEjecutado Paso ejecutado asociado
     * @param usuarioActualId Identificador del usuario
     * @param direccionIp Direccion IP del usuario
     */
    void guardarValorEntradas(IntegracionCatalogos integracionCatalogos, List<ValorEntrada> entradas, PasoEjecutado pasoEjecutado, String usuarioActualId, String direccionIp);

  
	/**
	 * Guarda un valor de entrada en la base de datos
	 * @param valorEntrada Valor de la entrada
	 * @param pasoEjecutado Paso Ejecutado asociado a la entrada
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	void guardarValorEntrada(ValorEntrada valorEntrada, PasoEjecutado pasoEjecutado, String usuarioActualId, String direccionIp);


	/** 
	 * Valida si un proceso puede ejecutarse si tiene activa la restricción de tiempo
	 * @param proceso Proceso a validar
	 * @return true si se permite ejecutar el paso
	 */
	boolean validarRestriccionTiempoEjecutado(Proceso proceso);


	/**
	 * Elimina un proceso ejecutado. También elimina por cascade todos sus pasos ejecutados y entradas asociadas
	 * @param procesoEjecutado Proceso Ejecutado a eliminar
	 */
	void eliminarProcesoEjecutado(ProcesoEjecutado procesoEjecutado);


	/**
	 * Reactiva la última etapa de un proceso ejecutado para que pueda seguirse ejecutando 
	 * @param pasoEjecutado Paso Ejecutado a afectar
	 */
	void reactivarProcesoEjecutado(PasoEjecutado pasoEjecutado);

	
	/**
	 * Guarda un archivo en base de datos (metadata) y en OWCC (fisico)
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param pasoEjecutado Paso Ejecutado actual
	 * @param entradas Lista de entradas con metadatos del proceso
	 * @param archivoBytes Bytes del archivo a guardar
	 * @param archivoNombre Nombre del archivo
	 * @param archivoTipo Tipo de archivo
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Identificador del archivo guardado
	 */
	String guardarArchivo(IntegracionCatalogos integracionCatalogos, PasoEjecutado pasoEjecutado, List<ValorEntrada> entradas, byte[] archivoBytes, String archivoNombre, String archivoTipo, String usuarioActualId, String direccionIp);

	
	/**
	 * Envia la notificacion de correspondencia al servicio web 
	 * @param identificador Identioficador del proceso
	 * @param guia Número de guía de envío
	 * @param fechaEnvio Fecha de envío de la guía
	 */
	void enviarNotificarCorrespondencia(IntegracionCatalogos integracionCatalogos, String identificador, String guia, String fechaEnvio);

	
	/**
	 * Genera los bytes de un pdf para una salida de reporte. 
	 * @param valorEntrada Puede estar guardado o no en la base de datos desde que tenga el resto de datos cargados
	 * @return Bytes con el PDF de salida
	 * @throws JRException
	 */
	ByteArrayOutputStream generarPDFSalidaReporte(ValorEntrada valorEntrada) throws JRException;


	/**
	 * Genera el reporte pdf para una salida de reporte.
	 * @param idValorEntrada Debe ser mayor a cero para buscar desde la base de datos el valor de la entrada
	 * @return Bytes con el reporte generado
	 * @throws JRException
	 */
	ByteArrayOutputStream generarSalidaReporte(long idValorEntrada) throws JRException;


	/**
	 * Genera la planilla de distribución para el rol Distribución
	 * @param pasos Lista con los pasos ejecutados para traer metadatos
	 * @param orip Descripción de la ORIP
	 * @return Bytes con el archivo generado
	 * @throws JRException
	 */
	ByteArrayOutputStream generarPlanillaDistribucion(List<PasoEjecutado> pasos, String orip) throws JRException;


	/**
	 * Toma una plantilla y genera un documento reemplazando datos de las variables
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param entradas Lista de entradas con metadatos
	 * @param plantillaId Identificador de la plantilla
	 * @return bytes del archivo con la plantilla generada
	 */
	byte[] generarPlantilla(IntegracionCatalogos integracionCatalogos, List<ValorEntrada> entradas, long plantillaId);


	/**
	 * Toma un documento y genera otro documento reemplazando datos de las variables
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param entradas Lista de entradas con metadatos
	 * @param archivoOrigen Archivo original
	 * @return bytes con el archivo nuevo
	 */
	byte[] generarDocumento(IntegracionCatalogos integracionCatalogos, List<ValorEntrada> entradas, Archivo archivoOrigen);

	/**
	 * Permite consultar un proceso por su ID
	 * @param procesoId Identificador del proceso
	 */
	Proceso buscarProcesoPorId(long procesoId);

	/**
	 * Inicializa el valor del campo JSON del proceso
	 * @param proceso Proceso a trabajar
	 */
	void inicializarJsonProcesoData(Proceso proceso);
	
	/**
	 * Permite consultar un paso por su ID
	 * @param pasoId Identificador del paso
	 */
	Paso buscarPasoPorId(long pasoId);

	/**
	 * Inicializa el valor del campo JSON del paso
	 * @param paso Paso a trabajar
	 */
	void inicializarJsonPasoData(Paso paso);

	/**
	 * Permite consultar una entrada por su ID
	 * @param entradaId Identificador de la entrada
	 */
	Entrada buscarEntradaPorId(long entradaId);

	/**
	 * Inicializa el valor del campo JSON de la entrada
	 * @param entrada Entrada a trabajar
	 */
	void inicializarJsonEntradaData(Entrada entrada);

	/**
	 * Busca todos los pasos atrasados dad una fecha de finalización del paso
	 * @param fechaFinPaso Fecha límite a buscar los pasos
	 * @return Listado con los Pasos encontrados
	 */
	List<PasoEjecutado> buscarPasosAtrasados(Date fechaFin);
	
	/**
	 * Busca los recordatorios activos por paso dada una fecha de finalización del paso
	 * @param fechaFinPaso Fecha límite a buscar los pasos
	 * @return Listado con los Pasos encontrados
	 */
	List<PasoEjecutado> buscarRecordatoriosPasos(Date fechaFinPaso);


}

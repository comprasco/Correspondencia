package co.gov.supernotariado.bachue.correspondencia.ejb.negocio;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.ConstantesCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposDatosCampos;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposParametros;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Archivo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RolGrupo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaGlobal;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.ParametrosSistemaTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionOWCC;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonDatosOpcionesParametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.session.CorrespondenciaStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.JsonUtil;
import https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoSalidaEnviarDocumento;

/**
 * Maneja la lógica de negocio para los parámetros del sistema
 */
@Stateless(name="ParametrosBusiness", mappedName="ejb/ParametrosBusiness")
@Local(ParametrosStatelessLocal.class)
public class ParametrosBusiness implements ParametrosStatelessLocal {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(ParametrosBusiness.class);

	/** Manejador de persistencia */
	@EJB(name = "CorrespondenciaStateless")
	private CorrespondenciaStatelessLocal persistencia;

	/**
	 * Tipos de entradas que requieren campo de restricción de longitud
	 */
	protected static final String[] TIPOS_CAMPOS_LONGITUD = {TiposDatosCampos.TEXT.name(), TiposDatosCampos.TEXTO_NUMERICO.name(), TiposDatosCampos.EMAIL.name()};
	
	/**
	 * Obtiene un listado de los tipos de datos que tienen validación de longitud
	 * @return Lista de los tipos de dato
	 */
	public List<String> getTiposCamposLongitud(){
		try {
			return Arrays.asList(TIPOS_CAMPOS_LONGITUD);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return new ArrayList<>();
	}
	
	
	/**
	 * Permite obtener una instancia para la integración con el servicio web de catálogos
	 * @return Objeto IntegracionCatalogos inicializado
	 */
	public IntegracionCatalogos obtenerIntegracionCatalogos() {
		ParametrosSistemaTO parametrosTO = new ParametrosSistemaTO();
		parametrosTO.setEndpoint(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_INTEGRACION_CATALOGOS_URL));
		parametrosTO.setModulo(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_INTEGRACION_CATALOGOS_MODULO));
		parametrosTO.setCatalogo(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_INTEGRACION_CATALOGOS_CATALOGO));
		parametrosTO.setCatalogoOrip(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_INTEGRACION_CATALOGOS_ORIPPORUSUARIO));

		parametrosTO.setParamEnvioDocumentos(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_JOB_ENVIO_GESTOR_DOCUMENTAL_ENDPOINT));
		parametrosTO.setParamConsultaDocumentos(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_CONSULTA_DOCUMENTOS_OWCC_ENDPOINT));
		parametrosTO.setParamDigitalizacion(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_URL_DIGITALIZACION_CAPTURE_CORRESPONDENCIA));
		parametrosTO.setParamNotificador(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_NOTIFICADOR_CORRESPONDENCIA_ENDPOINT));
		parametrosTO.setParamGestionUsuarios(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_INTEGRACION_CAWEB_URL));
		parametrosTO.setParamBioclient(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_INTEGRACION_BIOCLIENT_IMPRIMIRPDF));
		parametrosTO.setParamBioclientVerificar(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_INTEGRACION_BIOCLIENT_VERIFY));
		parametrosTO.setParamBioclientSegunda(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CATALOGO_PAD_DE_FIRMAS_ENDPOINT));

		parametrosTO.setParamCawebComponente(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CAWEB_COMPONENETE));
		parametrosTO.setParamCawebLogoutUrl(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CAWEB_LOGOUT_URL));

		parametrosTO.setParamOwccProfile(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PROFILE));
		parametrosTO.setParamOwccParamRadicado(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_RADICADO));
		parametrosTO.setParamOwccParamDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_DESTINATARIO));
		parametrosTO.setParamOwccParamCodigoOrip(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_CODIGO_ORIP));
		parametrosTO.setParamOwccParamDocTitle(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_DOC_TITLE));
		parametrosTO.setParamOwccParamDocType(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_DOC_TYPE));
		parametrosTO.setParamOwccParamDocTypeValue(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_DOC_TYPE_VALUE));
		parametrosTO.setParamOwccParamDocId(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_DOC_ID));
		parametrosTO.setParamOwccParamRepositorioValueFinal(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_REPOSITORIO_FINAL_VALUE));
		parametrosTO.setParamOwccParamRepositorioValueTemporal(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_OWCC_PARAM_REPOSITORIO_TEMPORAL_VALUE));

		parametrosTO.setParamCorreosUsuario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_USUARIO));
		parametrosTO.setParamCorreosClave(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_CLAVE));
		parametrosTO.setParamCorreosEstructura(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_ESTRUCTURA));
		parametrosTO.setParamCorreosRecordatorioPasoAsunto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_RECORDATORIO_PASO_ASUNTO));
		parametrosTO.setParamCorreosRecordatorioPasoTexto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_RECORDATORIO_PASO_TEXTO));
		parametrosTO.setParamCorreosProcesoFinalizadoAsunto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_FINAL_PROCESO_ASUNTO));
		parametrosTO.setParamCorreosProcesoFinalizadoTexto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_FINAL_PROCESO_TEXTO));
		parametrosTO.setParamCorreosPasoAtrasadoAsunto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_PASO_ATRASADO_ASUNTO));
		parametrosTO.setParamCorreosPasoAtrasadoTexto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_PASO_ATRASADO_TEXTO));
		parametrosTO.setParamCorreosPasoActualAsunto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_PASO_ACTUAL_ASUNTO));
		parametrosTO.setParamCorreosPasoActualTexto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_PASO_ACTUAL_TEXTO));
		parametrosTO.setParamCorreosPasoProximoAsunto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_PASO_PROXIMO_ASUNTO));
		parametrosTO.setParamCorreosPasoProximoTexto(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_CORREO_PASO_PROXIMO_TEXTO));

		parametrosTO.setParamGeneracioneeOrip(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_ORIP));
		parametrosTO.setParamGeneracioneeClasificacion(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_CLASIFICACION));
		parametrosTO.setParamGeneracioneeCanal(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_CANAL));
		parametrosTO.setParamGeneracioneeCanalElectronico(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_CANAL_ELECTRONICO));
		parametrosTO.setParamGeneracioneeCanalFisico(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_CANAL_FISICO));
		parametrosTO.setParamGeneracioneeNir(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_NIR));
		parametrosTO.setParamGeneracioneeTurno(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_TURNO));
		parametrosTO.setParamGeneracioneeNumeroFolios(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_NUMEROFOLIOS));
		parametrosTO.setParamGeneracioneeRadicacionManual(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_RADICACIONMANUAL));
		parametrosTO.setParamGeneracioneeNombreDocumento(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_NOMBREDOCUMENTO));
		parametrosTO.setParamGeneracioneeTipoDocumentoDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_TIPODOCUMENTODESTINATARIO));
		parametrosTO.setParamGeneracioneeNumeroDocumentoDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_NUMERODOCUMENTODESTINATARIO));
		parametrosTO.setParamGeneracioneePrimerNombreDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_PRIMERNOMBREDESTINATARIO));
		parametrosTO.setParamGeneracioneeSegundoNombreDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_SEGUNDONOMBREDESTINATARIO));
		parametrosTO.setParamGeneracioneePrimerApellidoDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_PRIMERAPELLIDODESTINATARIO));
		parametrosTO.setParamGeneracioneeSegundoApellidoDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_SEGUNDOAPELLIDODESTINATARIO));
		parametrosTO.setParamGeneracioneeCiudadDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_CIUDADDESTINATARIO));
		parametrosTO.setParamGeneracioneeDireccionDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_DIRECCIONDESTINATARIO));
		parametrosTO.setParamGeneracioneeTelefonoDestinatario(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_TELEFONODESTINATARIO));
		parametrosTO.setParamGeneracioneeLlave(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_LLAVE));
		parametrosTO.setParamGeneracioneeValor(obtenerDescripcionParametroTipoNombre(TiposParametros.SISTEMA.name(), ConstantesCorrespondencia.PARAMETRO_GENERACIONEE_CAMPO_VALOR));

		return new IntegracionCatalogos(parametrosTO);
	}
	
	
	/**
	 * Permite obtener un parámetro por su tipo y su nombre
	 * @param tipo Tipo de parámetro
	 * @param nombre Nombre clave del parámetro
	 * @return Cadena con la descripción del parámetro encontrado
	 */
	public String obtenerDescripcionParametroTipoNombre(String tipo, String nombre) {
		String dato = "";
		if(tipo!=null && !tipo.isEmpty() && nombre!=null && !nombre.isEmpty()) {
			List<Parametro> parametros = persistencia.buscarParametrosActivosPorTipoNombre(tipo, nombre);
			if(!parametros.isEmpty()) {
				dato = parametros.get(0).getDescripcion();
			} else {
				logger.error("No se encontró parámetro por los datos:"+tipo+" - "+nombre);
			}
		}
		return dato;
	}

	
	/**
	 * Permite obtener un listado de parámetros activos dado un tipo de parámetro
	 * 
	 * @param tipoParametro Cadena con el tipo de parámetro a buscar
	 * @return Lista con los parametros encontrados
	 */
	public List<Parametro> obtenerParametrosActivosPorTipo(String tipoParametro){
		List<Parametro> resultado = new ArrayList<>();
		try {
			if(tipoParametro!=null && !tipoParametro.isEmpty()) {
				resultado = persistencia.buscarParametrosActivosPorTipo(tipoParametro);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return resultado;
	}
	
	
	/**
	 * Busca los parámetros guardados según los filtros. Se usa en el listado de parámetros
	 * @param activo Indicador para buscar activos o inactivos
	 * @param tipoParametro Cadena con el tipo de parámetro a buscar
	 * @param ordenadoPor Campo a ordenar (paginación)
	 * @param limite Límite de registros (paginación)
	 * @param buscarDesde Comienzo de registros (paginación)
	 * @return Lista con los parámetros encontrados
	 */
	public List<Parametro> buscarParametrosPor(Boolean activo, String tipoParametro, String ordenadoPor, int limite, int buscarDesde) {
		List<Parametro> lista = persistencia.buscarParametrosPor(activo, tipoParametro, ordenadoPor, limite, buscarDesde);
		for(Parametro parametro:lista) {
			inicializarJsonDataParametro(parametro);
		}
		return lista;
	}
	
	
	/**
	 * Cuenta los parámetros guardados según los filtros. Se usa en el listado de parámetros
	 * @param activo Indicador para buscar activos o inactivos
	 * @param tipoParametro Cadena con el tipo de parámetro a buscar
	 * @return Contador de parámetros según los resultados
	 */
	public int contarParametrosPor(Boolean activo, String tipoParametro) {
		return persistencia.contarParametrosPor(activo, tipoParametro);
	}
	
	
	/**
	 * Inserta o actualiza un parametro en la base de datos
	 * @param parametro Objeto parámetro a guardar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Objeto parámetro guardado
	 */
	public Parametro guardarParametro(Parametro parametro, String usuarioActualId, String direccionIp) {
		if(parametro!=null) {
			parametro.setOpciones(JsonUtil.transformarAJson(parametro.getOpcionesJson()));
			parametro.setOpcionesDatos(JsonUtil.transformarAJson(parametro.getJsonDatosOpcionesParametro()));
	
	    	if(parametro.getId()==0){
	    		parametro.setIdUsuarioCreacion(usuarioActualId);
	    		parametro.setFechaCreacion(Calendar.getInstance().getTime());
	    		parametro.setIpCreacion(direccionIp);
	    		persistencia.persistir(parametro);
	    	} else{
	    		parametro.setIdUsuarioModificacion(usuarioActualId);
	    		parametro.setFechaModificacion(Calendar.getInstance().getTime());
	    		parametro.setIpModificacion(direccionIp);
	    		persistencia.actualizar(parametro);
	    	}
	    	inicializarJsonDataParametro(parametro);
		}
    	return parametro;
	}
	
	
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
	public String guardarPlantilla(IntegracionCatalogos integracionCatalogos, Parametro parametro, UploadedFile archivoCargado, Archivo archivoGuardado, String codigoOrip, String usuarioActualId, String direccionIp) {
		String mensaje = "";
		if(archivoCargado!=null) {
			parametro.setDescripcion(archivoCargado.getFileName());
			TipoSalidaEnviarDocumento response = new IntegracionOWCC(integracionCatalogos).enviarDocumento(parametro.getNombre(), TiposParametros.PLANTILLA_DOCUMENTO.name(), archivoCargado.getFileName(), archivoCargado.getContents(), codigoOrip, null, null);
			if (response != null) {
				if(response.getDID() != null && !response.getDID().isEmpty()) {
					// Guardar metadatos en BD
					if(archivoGuardado==null) {
						archivoGuardado = new Archivo();
						archivoGuardado.setFechaCreacion(Calendar.getInstance().getTime());
						archivoGuardado.setIdUsuarioCreacion(usuarioActualId);
						archivoGuardado.setIpCreacion(direccionIp);
					} else {
						archivoGuardado.setFechaModificacion(Calendar.getInstance().getTime());
						archivoGuardado.setIdUsuarioModificacion(usuarioActualId);
						archivoGuardado.setIpModificacion(direccionIp);
					}
					archivoGuardado.setActivo(true);
					archivoGuardado.setNombre(archivoCargado.getFileName());
					archivoGuardado.setTamano(archivoCargado.getSize());
					archivoGuardado.setTipoArchivo(archivoCargado.getContentType());
					archivoGuardado.setIdentificadorExterno(response.getDID());
					archivoGuardado.setPasoEjecutado(null);
					if(archivoGuardado.getId()==0) {
						persistencia.persistir(archivoGuardado);
					} else {
						persistencia.actualizar(archivoGuardado);
					}

					JsonOpcion json = new JsonOpcion();
					if(!parametro.getOpcionesJson().isEmpty()) {
						json = parametro.getOpcionesJson().get(0);
					} else {
						json.setActivo(true);
						json.setId(1);
						parametro.getOpcionesJson().add(json);
					}
					json.setNombre(""+archivoGuardado.getId());
		    		guardarParametro(parametro, usuarioActualId, direccionIp);
				} else {
					mensaje = response.getDescripcionMensaje();
				}
			} else {
				mensaje = "(response null)";
			}
		}
		return mensaje;
	}
	
	
	/**
	 * Obtiene un archivo por su Identificador
	 * @param idArchivo Identificador del archivo
	 * @return Archivo obtenido
	 */
	public Archivo obtenerArchivoPorId(long idArchivo) {
		return (Archivo) persistencia.buscarPorId(new Archivo(), Long.valueOf(idArchivo));
	}

	
	/**
	 * Obtiene una lista de grupos por rol interno
	 * @param idRol Identificador del Rol a Buscar
	 * @return Lista con los RolesGrupos encontrados
	 */
	@SuppressWarnings("unchecked")
	public List<RolGrupo> obtenerGruposPorRol(long idRol) {
		return (List<RolGrupo>) (Object) persistencia.buscarPorNamedQuery(RolGrupo.GRUPOS_POR_ROL, idRol);
	}
	
	
	/**
	 * Obtiene una lista de roles internos por grupo
	 * @param idGrupo Identificador del Grupo a Buscar
	 * @return Lista con los RolesGrupos encontrados
	 */
	@SuppressWarnings("unchecked")
	public List<RolGrupo> obtenerRolesPorGrupo(long idGrupo) {
		return (List<RolGrupo>) (Object) persistencia.buscarPorNamedQuery(RolGrupo.ROLES_POR_GRUPO, idGrupo);
	}

	
	/**
	 * Elimina un rol grupo
	 * @param rolGrupo Objeto rolgrupo a eliminar
	 */
	public void eliminarRolGrupo(RolGrupo rolGrupo) {
		persistencia.eliminar(rolGrupo);
	}
	
	
	/**
	 * Guarda una asogiación entre roles y grupos
	 * @param idRol Identificador del Rol a guardar
	 * @param idGrupo Identificador del Grupo a guardar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarRolGrupo(long idRol, long idGrupo, String usuarioActualId, String direccionIp) {
		RolGrupo rolGrupo = new RolGrupo();
		rolGrupo.setGrupoId(idGrupo);
		rolGrupo.setRolId(idRol);
		rolGrupo.setIpCreacion(direccionIp);
		rolGrupo.setFechaCreacion(Calendar.getInstance().getTime());
		rolGrupo.setIdUsuarioCreacion(usuarioActualId);
		persistencia.persistir(rolGrupo);
	}
	
	
	/**
	 * Obtiene un listado de secuencias globales asociadas a un parámetro
	 * @param idParametro Identificador del Parametro
	 * @return Listado de secuencias encontradas
	 */
	@SuppressWarnings("unchecked")
	public List<SecuenciaGlobal> obtenerSecuenciasGlobal(long idParametro) {
		return (List<SecuenciaGlobal>) (Object) persistencia.buscarPorNamedQuery(SecuenciaGlobal.SECUENCIAGLOBAL_POR_PARAMETRO_ORDEN_ID_ASC, idParametro);
	}
	
	/**
	 * Elimina un trozo de secuencia global
	 * @param secuencia Secuencia global a eliminar
	 */
	public void eliminarSecuenciaGlobal(SecuenciaGlobal secuencia) {
		if(secuencia.getId()!=0) {
			this.persistencia.eliminar(secuencia);
		}
	}

	/**
	 * Guarda las secuencias asociadas a un parámetro
	 * @param secuencias Listado de secuencias a guardar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarSecuenciaGlobal(List<SecuenciaGlobal> secuencias, String usuarioActualId, String direccionIp) {
		persistencia.guardarSecuenciaGlobal(secuencias, usuarioActualId, direccionIp);
	}

	
	/**
	 * Obtiene el Rol Interno del Usuario según sus Grupos
	 * Deja el menor rol encontrado (mas permisos entre menor es el rol)
	 * @param grupos Lista con los identificadores del Grupo
	 * @return Identificador del Rol Interno a aplicar
	 */
	public long obtenerRolInternoUsuario(List<GrupoTO> grupos) {
		String nombreParametroRolAdmin = "";
		List<Parametro> parametros = obtenerParametrosActivosPorTipo(TiposParametros.SISTEMA.name());
		for(Parametro parametro:parametros) {
			if(parametro.getNombre().equals(ConstantesCorrespondencia.PARAMETRO_NOMBRE_ROL_ADMINISTRADOR)) {
				nombreParametroRolAdmin = parametro.getDescripcion();
				break;
			}
		}

		String grupoAdminId = "";
		long ultimoRol = 10;
		for(GrupoTO grupo:grupos) {
			if(grupo.getNombre().equals(nombreParametroRolAdmin)) {
				grupoAdminId = grupo.getId();
			}
			List<RolGrupo> rolesGrupo = obtenerRolesPorGrupo(Long.valueOf(grupo.getId()));
			for(RolGrupo rolGrupo:rolesGrupo) {
				if(rolGrupo.getRolId() < ultimoRol) {
					ultimoRol = rolGrupo.getRolId();
				}
			}
		}
		if(ultimoRol==10) {
			ultimoRol = 0;
		}
		
		// Si es un usuario administrador pero no tiene el rol interno asociado, lo asocia y guarda para que pueda 
		// ingresar como admin
		if(!grupoAdminId.isEmpty() && ultimoRol != 1) {
			ultimoRol = 1;
			guardarRolGrupo(ultimoRol, Long.parseLong(grupoAdminId), "1", "");
		}

		return ultimoRol;
	}
	
	
	/**
	 * Lista con objetos de tipo JsonOpcion cuando el parámetro es tipo lista
	 * @param parametro Parámetro a evaluar sus opciones
	 * @return Lista con valores activos
	 */
	public List<JsonOpcion> obtenerOpcionesJsonActivas(Parametro parametro) {
		List<JsonOpcion> result = new ArrayList<>();
		try {
			for(JsonOpcion opcion:parametro.getOpcionesJson()) {
				if(opcion.isActivo()) {
					result.add(opcion);
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * Permite consultar un parámetro por su ID
	 * @param parametroId Identificador del parámetro
	 */
	public Parametro buscarParametroPorId(long parametroId) {
		Parametro parametro = (Parametro) persistencia.buscarPorId(new Parametro(), parametroId);
		if(parametro!=null) {
			inicializarJsonDataParametro(parametro);
		}
		return parametro;
	}

	
	/**
	 * Inicializa el valor del campo JSON del parámetro para las listas de opciones de datos
	 * @param parametro Parámetro a trabajar
	 */
	public void inicializarJsonDataParametro(Parametro parametro) {
		try {
			if(parametro.getJsonDatosOpcionesParametro()==null || parametro.getJsonDatosOpcionesParametro().isEmpty()) {
			    Type listType = new TypeToken<List<JsonDatosOpcionesParametro>>(){}.getType();
			    parametro.setJsonDatosOpcionesParametro(new Gson().fromJson(parametro.getOpcionesDatos(), listType));
			    if(parametro.getJsonDatosOpcionesParametro()==null) {
			    	parametro.setJsonDatosOpcionesParametro(new ArrayList<>());
			    }
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			if(parametro.getOpcionesJson()==null || parametro.getOpcionesJson().isEmpty()) {
			    Type listType = new TypeToken<List<JsonOpcion>>(){}.getType();
			    parametro.setOpcionesJson(new Gson().fromJson(parametro.getOpciones(), listType));
			    if(parametro.getOpcionesJson()==null) {
			    	parametro.setOpcionesJson(new ArrayList<>());
			    }
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}

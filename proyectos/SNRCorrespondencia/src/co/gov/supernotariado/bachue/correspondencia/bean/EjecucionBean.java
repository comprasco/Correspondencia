package co.gov.supernotariado.bachue.correspondencia.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.ConstantesCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.RolesSistema;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposParametros;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposRestriccionesEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposRestriccionesValor;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Archivo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoria;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RestriccionEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionOWCC;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonDatosOpcionesParametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ProcesosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.SecuenciasSingletonLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.session.CorrespondenciaStateless;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.NotificacionUtil;
import co.gov.supernotariado.bachue.correspondencia.exception.CorrespondenciaException;
import co.gov.supernotariado.bachue.correspondencia.to.SugerenciaTO;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.consultar.v1.TipoDocumento;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.consultar.v1.TipoSalidaConsultar;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Managed Bean que permite controlar la ejecución de un proceso
 */
@ManagedBean
@ViewScoped
public class EjecucionBean extends GenericBean {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(EjecucionBean.class);

	/** Manejador de lógica de negocio de procesos */
	@EJB(name = "ProcesoBusiness")
	private ProcesosStatelessLocal procesoControl;

	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;
	
	/** Manejador de lógica de negocio de secuencias */
	@EJB(name = "SecuenciasBusiness")
	private SecuenciasSingletonLocal secuenciasControl;

	/**
	 * Integracion con sistema owcc
	 */
	private IntegracionOWCC integracionOWCC;

	/**
	 * Maneja la sesión del usuario
	 */
	@ManagedProperty("#{gestorSesionBean}")
	private GestorSesionBean gestorSesionBean;

	/**
	 * Constante para localizar mensaje requerido en properties
	 */
	private static final String CONSTANTE_MENSAJE_REQUERIDO = "javax.faces.component.UIInput.REQUIRED";

	/**
	 * Lista principal de procesos
	 */
	private LazyDataModel<PasoEjecutado> lista;

	/**
	 * Indica si se muestran en pantalla los filtros adicionales
	 */
	private boolean mostrarFiltrosAdicionales = false;
	
	/**
	 * Filtro abierto aplicado
	 */
	private String filtro = "";

	/**
	 * Filtro por activos o inactivos
	 */
	private Boolean filtroActive = true;

	/**
	 * Filtro por usuario
	 */
	private SugerenciaTO filtroPorUsuario;

	/**
	 * Filtro de tipo consulta 1 todos, 2 pendientes
	 */
	private int filtroTipoConsulta = 1;

	/**
	 * Filtro coincidencias secuencia
	 */
	private String filtroSecuencia = "";

	/**
	 * Filtro rango secuencias inicial
	 */
	private String filtroSecuenciaIni = "";

	/**
	 * Filtro rango secuencias final
	 */
	private String filtroSecuenciaFin = "";

	/**
	 * Properties de mensajes
	 */
	private ResourceBundle mensajes;

	/**
	 * Etapa principal a ejecutar o detallar
	 */
	private PasoEjecutado data;

	/**
	 * Listado de pasos o etapas anteriores del proceso
	 */
	private List<PasoEjecutado> pasosAnteriores;

	/**
	 * Listado de proximos pasos en el proceso
	 */
	private List<Paso> proximosPasos;

	/**
	 * Detalle de la etapa siguiente en el proceso
	 */
	private Paso proximoPaso;

	/**
	 * Lista con los campos del formulario a rellenar
	 */
	private List<ValorEntrada> entradas;

	/**
	 * Lista con los procesos disponibles a iniciar
	 */
	private List<Proceso> procesos = new ArrayList<>();

	/**
	 * Proceso seleccionado a trabajar
	 */
	private long procesoSeleccionado;

	/**
	 * Listado de categorías de proceso
	 */
	private List<Parametro> categorias = new ArrayList<>();

	/**
	 * Categoría seleccionada del proceso
	 */
	private Long categoriasSeleccionadas;

	/**
	 * Filtros por categoría
	 */
	private long filtroCategoria;

	/**
	 * Niveles de acceso para permisos, según el rol
	 */
	private int nivelAcceso = RolesSistema.INICIAR_PROCESO.getId();

	/**
	 * Fecha actual a mostrar en pantalla
	 */
	private Date fechaActual;

	/**
	 * Permite deshabilitar el boton de guardar ante algun error inesperado que no
	 * permita continuar
	 */
	private boolean deshabilitaSiguientePaso = false;

	/** Indica si permite ejecutar el paso al entrar al detalle */
	private boolean permitirEjecutarPaso = false;

	/** Indica si cambia bandeja de entrada por grilla de envios */
	private boolean mostrarGrillaEnvios = false;
	
	/** Indica si muestra botón cargar envíos */
	private boolean mostrarSeccionCargarEnvios = false;

	/** Indica si cambia bandeja de entrada por grilla de envios */
	private boolean mostrarBotonDigitalizar = false;

	/**
	 * Listado de pasos seleccionados en la bandeja marcados para envío
	 */
	private List<Long> pasosEnviosSeleccionados = new ArrayList<>();

	/**
	 * Archivo a exportar 
	 */
	private StreamedContent archivoExportar;

	/**
	 * Entrada seleccionada en el caso de descargar archivo
	 */
	private long idValorEntradaSeleccionada;

	/** Usuarios seleccionados proximo paso */
	private String[] usuariosAsignados;

	/** Usuario seleccionado proximo paso en caso de que sea solo 1 */
	private String usuarioAsignado;

	/**
	 * Grupos seleccionados proximo paso
	 */
	private String[] gruposAsignados;

	/** Plantillas de documentos configuradas por parametros */
	private List<Parametro> plantillas = new ArrayList<>();

	/** Parametro a agregar un nuevo item en caso de listas predefinidas */
	private Parametro parametroAgregar;

	/** Nueva opción para el nuevo item en caso de listas predefinidas */
	private JsonOpcion parametroNuevaOpcion = new JsonOpcion();
	
	/** Indica si el campo observaciones es requerido */
	private boolean campoObservacionesRequerido;

	/** Url que inicializa oracle capture */
	private String urlOracleCapture;

	/** Titulo popup agregar elementos a lista parametros */
	private String tituloPopupAgregarListaParametros;

	/** Indica si se muestra boton exportar planilla distribución */
	private boolean mostrarExportarDistribuidor;
	
	/** Lista documentos recuperados de owcc para visualizar */
	private List<NodoTO> documentosVisualizar;

	/**Indica si la siguiente tarea es unificadora de procesos en paralelo muestra mensaje de advertencia */
	private boolean siguientePasoUnificaParalelo = false;

	/** Formato de fecha predefinido */
	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/** Lista temporal para filtrar por consecutivos ee */
	private List<String> filtroConsecutivosEE;
	

	/**
	 * Inicialización de los atributos del Bean
	 */
	@PostConstruct
	private void init() {
		try {
			this.fechaActual = Calendar.getInstance().getTime();
			if (gestorSesionBean != null && gestorSesionBean.getUsuarioActual() != null) {
				integracionOWCC = new IntegracionOWCC(gestorSesionBean.getIntegracionCatalogos());
				urlOracleCapture = gestorSesionBean.getIntegracionCatalogos().getCatalogoDigitalizacionCaptureURL();
				mensajes = ResourceBundle.getBundle("messages", gestorSesionBean.getLocale());

				nivelAcceso = gestorSesionBean.getUsuarioActual().getRol();

				if (gestorSesionBean.getUsuarioActual().getGrupos() != null) {
					buscar();

					if (nivelAcceso != RolesSistema.CONSULTA.getId()) {
						plantillas = parametrosControl.obtenerParametrosActivosPorTipo(TiposParametros.PLANTILLA_DOCUMENTO.name());
						establecerProcesosInicio();
						if (procesos != null) {
							procesos.sort((p1, p2) -> p1.getNombre().compareTo(p2.getNombre()));
						}
					}
					
					if (nivelAcceso == RolesSistema.EJECUCION_CONSULTA.getId()) {
						mostrarExportarDistribuidor = true;
					}
					
				} else {
					logger.error("El usuario no pertenece a ningún grupo");
				}
			} else {
				logger.error("GestorSesion nulo");
			}

		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	
	/**
	 * Busca los procesos que puede ejecutar el usuario actual
	 */
	private void establecerProcesosInicio() {
		procesos = procesoControl.buscarProcesosPor(true, null, null, 0, 0);
		Iterator<Proceso> iter = this.procesos.iterator();
		while (iter.hasNext()) {
			Proceso proceso = iter.next();
			List<Paso> pasos = procesoControl.buscarPasosActivosProceso(proceso.getId());
			if (!pasos.isEmpty()) {
				if (this.nivelAcceso > RolesSistema.SUPERADMIN.getId()) {
					Paso paso = pasos.get(0);
					estableceUsuariosProximoPaso(paso);

					if (establecerControlInicioProcesos(paso)) {
						estableceCategorias(proceso);
					} else {
						iter.remove();
					}
				}
			} else {
				iter.remove();
			}
		}
	}

	
	/**
	 * Agrega las categorias de los procesos que tenga permiso el usuario
	 * 
	 * @param proceso Proceso a evaluar
	 */
	private void estableceCategorias(Proceso proceso) {
		List<ProcesoCategoria> procesoCategorias = procesoControl.buscarCategoriasProceso(proceso.getId());
		for (ProcesoCategoria categoria : procesoCategorias) {
			boolean encontrado = false;
			for (Parametro cat : this.categorias) {
				if (cat.getId() == categoria.getCategoria().getId()) {
					encontrado = true;
					break;
				}
			}
			if (!encontrado) {
				this.categorias.add(categoria.getCategoria());
			}
		}
	}

	
	/**
	 * Encuentra un grupo en un listado
	 * 
	 * @param grupos Listado de grupos
	 * @param grupoId Id de grupo a encontrar
	 * @return true si encontró el grupo
	 */
	private boolean encontrarGrupoTO(List<GrupoTO> grupos, String grupoId) {
		boolean ok = false;
		for (GrupoTO grupo : grupos) {
			if (grupo.getId().equals(grupoId)) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	
	/**
	 * Evalua las condiciones que permiten al usuario iniciar el primer paso de un
	 * proceso
	 * 
	 * @param paso Paso a evaluar
	 * @return true si puede iniciar el proceso
	 */
	private boolean establecerControlInicioProcesos(Paso paso) {
		boolean ok = false;
		boolean controlGrupo = false;
		if (paso.getGrupoAsignadoId() == null || paso.getGrupoAsignadoId().isEmpty()) {
			for (GrupoTO grupo : gestorSesionBean.getUsuarioActual().getGrupos()) {
				ok = encontrarGrupoTO(paso.getJsonPasoData().getGruposSeleccionadosMostrar(), grupo.getId());
				if(ok) {
					break;
				}
			}
		}

		if (!ok && paso.getGrupoAsignadoId() != null && !paso.getGrupoAsignadoId().isEmpty()) {
			controlGrupo = true;
			ok = encontrarGrupoTO(gestorSesionBean.getUsuarioActual().getGrupos(), paso.getGrupoAsignadoId());
		}

		if (!ok && !controlGrupo && (paso.getUsuarioAsignadoId() == null || paso.getUsuarioAsignadoId().isEmpty())) {
			for (UsuarioTO usuarioTO : paso.getJsonPasoData().getUsuariosSeleccionadosMostrar()) {
				if (usuarioTO.getId().equals(gestorSesionBean.getUsuarioActual().getId())) {
					ok = true;
					break;
				}
			}
		}

		if (!ok && !controlGrupo && (paso.getUsuarioAsignadoId() != null && !paso.getUsuarioAsignadoId().isEmpty()) && paso.getUsuarioAsignadoId().equals(gestorSesionBean.getUsuarioActual().getId())) {
			ok = true;
		}
		return ok;
	}

	
	/**
	 * Reinicia todos los filtros de consulta
	 */
	public void reiniciarConsulta() {
		this.filtroSecuencia = "";
		this.filtroActive = true;
		this.filtro = "";
		this.filtroSecuenciaIni = "";
		this.filtroSecuenciaFin = "";
		this.mostrarFiltrosAdicionales = false;
		buscarConsultar();
	}

	
	/**
	 * Reinicia todos los datos del paso para preparar otra ejecución
	 */
	private void reiniciarPaso() {
		this.procesoSeleccionado = 0;
		this.data = new PasoEjecutado();
		this.data.setActivo(true);
		this.data.setFechaCreacion(Calendar.getInstance().getTime());
		this.data.setIdUsuarioCreacion(gestorSesionBean.getUsuarioActual().getId());
		this.data.setIpCreacion(gestorSesionBean.getDireccionIp());

		this.entradas = new ArrayList<>();
		this.proximosPasos = new ArrayList<>();
		this.proximoPaso = null;
		this.deshabilitaSiguientePaso = false;
		this.permitirEjecutarPaso = false;
	}
	

	/**
	 * Permite buscar por usuarios con un filtro de autocompletar
	 * @param consulta palabras a buscar
	 * @return Listado de sugerencias segun la consulta ingresada
	 */
	public List<SugerenciaTO> autocompletarBuscarUsuario(String consulta) {
		List<SugerenciaTO> results = new ArrayList<>();
		for (UsuarioTO user : gestorSesionBean.getUsuarios()) {
			if (user.getNombre().toLowerCase().contains(consulta.toLowerCase())) {
				results.add(new SugerenciaTO(user.getId() + "", user.getNombre()));
			}
		}
		return results;
	}

	
	/**
	 * Permite consultar por usuario
	 */
	public void buscarPorUsuario() {
		if (this.filtroPorUsuario != null) {
			this.buscar();
		}
	}

	
	/**
	 * Muestra mas campos de filtros
	 */
	public void mostrarFiltrosAdicionales() {
		mostrarFiltrosAdicionales = !mostrarFiltrosAdicionales;
	}
	
	
	/**
	 * Establece el tipo de consulta a realizar
	 * @return int con el tipo de consulta a realizar -1: no restringir nada (superadmin), 0-Consulta todos los procesos, 1-Consulta solo procesos en los que el usuario este asignado a el paso actual, 2-consulta procesos en los que el usuario haya participado
	*/
	private int establecerTipoConsulta() {
		int tipoConsulta = this.filtroTipoConsulta;

		// Si es rol consultas muestra todos los pasos
		if(gestorSesionBean.getMenuActual().getId() == 5) {
			tipoConsulta = -1;
		} else {
			// Si es superadmin al seleccionar todos los pasos no filtra por usuario ni grupo
			if (this.filtroTipoConsulta == 0) {
				if (this.nivelAcceso == RolesSistema.SUPERADMIN.getId()) {
					tipoConsulta = -1;
				} else {
					tipoConsulta = 0;
				}
			}
		}
		
		if (this.filtroPorUsuario != null) {
			tipoConsulta = 1;
		}
		return tipoConsulta;
	}

	
	/**
	 * Establece los datos para la consulta principal
	 * @param first Primera página de paginación de tabla
	 * @param pageSize Tamaño de resultados de paginación de tabla
	 * @param sortField Campo por el cual ordenar los resultados
	 * @param sortOrder Orden ascendente o descendente
	 * @param tipoConsultaEnvios 1=Indica si la consulta aplica los parámetros para la generación de archivo de envíos, 2=carga de archivo envíos 
	 * @return Lista de PasosEjecutados encontrados
	 */
	private List<PasoEjecutado> establecerFiltrosConsulta(int first, int pageSize, String sortField, SortOrder sortOrder, int tipoConsultaEnvios) {
		List<PasoEjecutado> listaPasos;

		if(tipoConsultaEnvios==0) {
			mostrarGrillaEnvios = false;
			mostrarBotonDigitalizar = false;
		}
		
		String usuarioAsignadoFiltro = null;
		categoriasSeleccionadas = 0l;
		List<String> gruposfiltro = null;
		proximoPaso = null;

		int tipoConsulta = establecerTipoConsulta();

		if ((nivelAcceso > RolesSistema.SUPERADMIN.getId() && gestorSesionBean.getMenuActual().getId() != 5) || ((nivelAcceso == RolesSistema.SUPERADMIN.getId()) && tipoConsulta > 0)) {
			usuarioAsignadoFiltro = gestorSesionBean.getUsuarioActual().getId();
			gruposfiltro = new ArrayList<>();
			for (GrupoTO org : gestorSesionBean.getUsuarioActual().getGrupos()) {
				gruposfiltro.add(org.getId());
			}
		} else {
			if (filtroPorUsuario != null) {
				usuarioAsignadoFiltro = filtroPorUsuario.getValue();
			}
		}

		Boolean ultimoPaso = true;
		if (filtroActive == null || filtroActive) {
			ultimoPaso = null;
		}

		Map<String, Object> filtros = new HashMap<>();
		if(mostrarFiltrosAdicionales) {
			filtros.put(CorrespondenciaStateless.FILTRO_ACTIVO, filtroActive);
		} else {
			filtros.put(CorrespondenciaStateless.FILTRO_ACTIVO, true);
		}
		
		if(nivelAcceso>1) {
			filtros.put(CorrespondenciaStateless.FILTRO_ORIP, gestorSesionBean.getUsuarioActual().getOficina().getCirculo());
		}
		
		// filtro para exportar envios
		if(tipoConsultaEnvios==1) {
			// agrega los que faltan
			for(PasoEjecutado pasoEjecutado:lista.getWrappedData()) {
				if(pasoEjecutado.isSeleccionadoEnvios() && !pasoEjecutado.isDeshabilitarSeleccionadoEnvios() && !pasosEnviosSeleccionados.contains(pasoEjecutado.getId())) {
					pasosEnviosSeleccionados.add(pasoEjecutado.getId());
				} else if(!pasoEjecutado.isSeleccionadoEnvios() && pasosEnviosSeleccionados.contains(pasoEjecutado.getId())) {
					pasosEnviosSeleccionados.remove(pasoEjecutado.getId());
				}
			}
			filtros.put(CorrespondenciaStateless.FILTRO_PASOSEJECUTADOSID, pasosEnviosSeleccionados);
		} else if(tipoConsultaEnvios==2) {
			filtros.put(CorrespondenciaStateless.FILTRO_CONSECUTIVOSEE, filtroConsecutivosEE);
		}
		
		filtros.put(CorrespondenciaStateless.FILTRO_ULTIMOPASO, ultimoPaso);
		filtros.put(CorrespondenciaStateless.FILTRO_USUARIOASIGNADO, usuarioAsignadoFiltro);
		if (filtroCategoria > 0) {
			filtros.put(CorrespondenciaStateless.FILTRO_CATEGORIA, filtroCategoria);
		}
		if (filtroSecuencia != null && !filtroSecuencia.isEmpty()) {
			filtros.put(CorrespondenciaStateless.FILTRO_SECUENCIA, filtroSecuencia);
		}
		if(mostrarFiltrosAdicionales) {
			if (filtro != null && !filtro.isEmpty()) {
				filtros.put(CorrespondenciaStateless.FILTRO_MULTIFILTRO, filtro);
			}
			if (filtroSecuenciaIni != null && !filtroSecuenciaIni.isEmpty()) {
				filtros.put(CorrespondenciaStateless.FILTRO_SECUENCIAINI, filtroSecuenciaIni);
			}
			if (filtroSecuenciaFin != null && !filtroSecuenciaFin.isEmpty()) {
				filtros.put(CorrespondenciaStateless.FILTRO_SECUENCIAFIN, filtroSecuenciaFin);
			}
		}

		if (pageSize > 0) {
			listaPasos = procesoControl.buscarPasosEjecutadosPor(filtros, tipoConsulta, null, gruposfiltro, estableceOrdenConsulta(sortField, sortOrder), pageSize, first);
			lista.setRowCount(procesoControl.contarPasosEjecutadosPor(filtros, tipoConsulta, null, gruposfiltro));
		} else {
			listaPasos = procesoControl.buscarPasosEjecutadosPor(filtros, tipoConsulta, null, gruposfiltro, sortField, 0, 0);
		}

		if(tipoConsultaEnvios==0) {
			if(listaPasos.isEmpty()) {
				mostrarGrillaEnvios = false;
			} else {
				for (PasoEjecutado ti : listaPasos) {
					ti.setEntradas(new ArrayList<>());
					ti.setSecuenciaTabla(ti.getProcesoEjecutado().getSecuenciaProceso());
	
					List<PasoEjecutado> anteriores = procesoControl.buscarPasosEjecutadosAnteriores(ti.getProcesoEjecutado().getId());
					for (PasoEjecutado paso : anteriores) {
						ti.getEntradas().addAll(buscarValoresEntradasBandeja(paso));
					}
		
					if(gestorSesionBean.getMenuActual().getId() != 5) {
						// Verifica si el paso contiene entradas para envios. si algún paso no lo tiene se desactiva grilla envios
						boolean ok = false;
						List<Entrada> listaEntradas = procesoControl.buscarEntradasActivasPaso(ti.getPaso().getId());
						for (Entrada entrada : listaEntradas) {
							if(entrada.getTipoEntrada().equals(TiposEntrada.DIGITALIZACION_CAPTURE.name())) {
								ti.setMostrarDigitalizar(true);
								mostrarBotonDigitalizar = true;
							} else if(entrada.getTipoEntrada().equals(TiposEntrada.GENERAR_ARCHIVO_472.name()) || entrada.getTipoEntrada().equals(TiposEntrada.CARGAR_ARCHIVO_472.name())) {
								ti.setMostrarSeleccion(true);
	
								String consecutivoee = "";
								for (ValorEntrada value : ti.getEntradas()) {
									if (value.getEntrada().getNombre() != null && !value.getEntrada().getNombre().isEmpty() && value.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_CONSECUTIVOEE)) {
										consecutivoee = value.getValor();
										break;
									}
								}
								if(consecutivoee!=null && !consecutivoee.isEmpty()) {
									ti.setSecuenciaTabla(consecutivoee);
									// Agrega secuencia entrada a lista entradas para tooltip datos adicionales
									ValorEntrada ve = new ValorEntrada();
									Entrada ent = new Entrada();
									procesoControl.inicializarJsonEntradaData(ent);
									
									ent.setNombreEntrada(mensajes.getString("etiqueta_secuencia"));
									ent.setTipoEntrada(TiposEntrada.TEXT.name());
									ve.setEntrada(ent);
									ve.setValor(ti.getProcesoEjecutado().getSecuenciaProceso());
									ti.getEntradas().add(ve);
								}
	
								ok = true;
							} 
							
							if(entrada.getTipoEntrada().equals(TiposEntrada.CARGAR_ARCHIVO_472.name())) {
								ti.setSeleccionadoEnvios(true);
								ti.setDeshabilitarSeleccionadoEnvios(true);
							}
	
							if(ok) {
								break;
							}
						}
						if(ok) {
							mostrarGrillaEnvios = true;
						}
					} else {
						mostrarExportarDistribuidor = false;
					}
		
					if (ti.getUsuarioAsignadoId() != null && !ti.getUsuarioAsignadoId().isEmpty()) {
						ti.setUsuarioAsignado(gestorSesionBean.obtenerUsuario(ti.getUsuarioAsignadoId()));
					} else if (ti.getGrupoAsignadoId() != null && !ti.getGrupoAsignadoId().isEmpty()) {
						ti.setGrupoAsignado(gestorSesionBean.obtenerGrupo(ti.getGrupoAsignadoId(), null));
					}
				}
			}
		}
		filtroConsecutivosEE = new ArrayList<>();

		return listaPasos;
	}
	
	
	
	/**
	 * Limpia lista de seleccionados para envio
	 */
	public void buscarConsultar() {
		try {
			pasosEnviosSeleccionados = new ArrayList<>();
			filtroConsecutivosEE = new ArrayList<>();
			if(filtroSecuenciaIni!=null && !filtroSecuenciaIni.isEmpty() && (filtroSecuenciaFin==null || filtroSecuenciaFin.isEmpty())){
				throw new CorrespondenciaException("error_filtrosecuencia_ejecucion", "");
			} 
			if(filtroSecuenciaFin!=null && !filtroSecuenciaFin.isEmpty() && (filtroSecuenciaIni==null || filtroSecuenciaIni.isEmpty())){
				throw new CorrespondenciaException("error_filtrosecuencia_ejecucion", "");
			} 
			buscar();
		} catch(Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), e.getLocalizedMessage()));
		}
	}

	
	/**
	 * Busca las etapas pendientes de ejecutar por el usuario
	 */
	public void buscar() {
		this.lista = new LazyDataModel<PasoEjecutado>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<PasoEjecutado> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				if(mostrarGrillaEnvios && lista!=null && lista.getWrappedData()!=null) {
					for(PasoEjecutado pasoEjecutado:lista.getWrappedData()) {
						if(pasoEjecutado.isSeleccionadoEnvios() && !pasoEjecutado.isDeshabilitarSeleccionadoEnvios() && !pasosEnviosSeleccionados.contains(pasoEjecutado.getId())) {
							pasosEnviosSeleccionados.add(pasoEjecutado.getId());
						} else if(!pasoEjecutado.isSeleccionadoEnvios() && pasosEnviosSeleccionados.contains(pasoEjecutado.getId())) {
							pasosEnviosSeleccionados.remove(pasoEjecutado.getId());
						}
					}
				}

				return establecerFiltrosConsulta(first, pageSize, sortField, sortOrder, 0);
			}
		};

		this.fechaActual = Calendar.getInstance().getTime();
		PrimeFaces.current().executeScript("refreshButtonList();");
	}

	
	/**
	 * Obtiene sugerencias para componente autocomplete de filtro radicados
	 * @param texto Consulta para buscar las sugerencias
	 * @return Lista con las sugerencias encontradas
	 */
	public List<String> sugerenciasRadicados(String texto) {
		String filtroUsuarioAsignado = null;
		List<String> gruposfiltro = null;

		int tipoConsulta = establecerTipoConsulta();

		if ((nivelAcceso > RolesSistema.SUPERADMIN.getId() && gestorSesionBean.getMenuActual().getId() != 5) || ((nivelAcceso == RolesSistema.SUPERADMIN.getId()) && tipoConsulta > 0)) {
			filtroUsuarioAsignado = gestorSesionBean.getUsuarioActual().getId();
			gruposfiltro = new ArrayList<>();
			for (GrupoTO org : gestorSesionBean.getUsuarioActual().getGrupos()) {
				gruposfiltro.add(org.getId());
			}
		} else {
			if (filtroPorUsuario != null) {
				filtroUsuarioAsignado = filtroPorUsuario.getValue();
			}
		}

		Map<String, Object> filtros = new HashMap<>();
		if(mostrarFiltrosAdicionales) {
			filtros.put(CorrespondenciaStateless.FILTRO_ACTIVO, filtroActive);
		} else {
			filtros.put(CorrespondenciaStateless.FILTRO_ACTIVO, true);
		}
		filtros.put(CorrespondenciaStateless.FILTRO_USUARIOASIGNADO, filtroUsuarioAsignado);
		
		if(nivelAcceso>1) {
			filtros.put(CorrespondenciaStateless.FILTRO_ORIP, gestorSesionBean.getUsuarioActual().getOficina().getCirculo());
		}

		return procesoControl.obtenerSugerenciasRadicados(texto, filtros, tipoConsulta, gruposfiltro);
    }
  
	  
	/**
	 * Permite exportar una planilla para el distribuidor
	 */
	public void exportarDistribuidor() {
		try {
			List<PasoEjecutado> listaReporte = establecerFiltrosConsulta(0, 0, null, null, 0);
			
			String fileName = "SNRCorrespondencia_" + Calendar.getInstance().getTimeInMillis() + ".pdf";
			ByteArrayOutputStream baos = procesoControl.generarPlanillaDistribucion(listaReporte, gestorSesionBean.getUsuarioActual().getOficina().getNombre());
			if (baos != null) {
				archivoExportar = new DefaultStreamedContent(new ByteArrayInputStream(baos.toByteArray()), "application/pdf", fileName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}

	
	/**
	 * Permite exportar la bandeja de entrada a un archivo excel
	 */
	public void exportar() {
		try {
			List<PasoEjecutado> listaReporte = establecerFiltrosConsulta(0, 0, null, null, 0);

			String fileName = "SNRCorrespondencia_" + Calendar.getInstance().getTimeInMillis() + ".xls";
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			WritableWorkbook workbook = Workbook.createWorkbook(baos);
			WritableSheet sheet = workbook.createSheet("SNRCorrespondencia_Reporte", 0);
			int i = 0;
			int row = 0;
			sheet.addCell(new Label(i++, row, mensajes.getString("etiqueta_proceso")));
			sheet.addCell(new Label(i++, row, mensajes.getString("etiqueta_secuencia")));
			sheet.addCell(new Label(i++, row, mensajes.getString("etiqueta_usuarioogrupo")));
			sheet.addCell(new Label(i++, row, mensajes.getString("etiqueta_fechainicio")));
			sheet.addCell(new Label(i, row, mensajes.getString("etiqueta_fechafin")));

			if (!listaReporte.isEmpty()) {
				// Verifica si dentro de los filtros solo viene información de 1 proceso para
				// dejar nombres de campos como cabeceras
				long procesoId = 0;
				boolean procesoUnico = true;
				for (PasoEjecutado ti : listaReporte) {
					if (procesoId == 0) {
						procesoId = ti.getProcesoEjecutado().getProceso().getIdentificador();
					}
					if (procesoId != ti.getProcesoEjecutado().getProceso().getIdentificador()) {
						procesoUnico = false;
						break;
					}
				}

				for (PasoEjecutado ti : listaReporte) {
					row++;
					i = 0;

					sheet.addCell(new Label(i++, row, ti.getPaso().getProceso().getNombre()));
					sheet.addCell(new Label(i++, row, ti.getProcesoEjecutado().getSecuenciaProceso()));
					if (ti.getUsuarioAsignado() != null) {
						sheet.addCell(new Label(i++, row, ti.getUsuarioAsignado().getNombre()));
					} else if (ti.getGrupoAsignado() != null) {
						sheet.addCell(new Label(i++, row, ti.getGrupoAsignado().getNombre()));
					}

					if (ti.getFechaCreacion() != null) {
						sheet.addCell(new Label(i++, row, formatoFecha.format(ti.getFechaCreacion())));
					}

					if (ti.getFechaFin() != null) {
						sheet.addCell(new Label(i++, row, formatoFecha.format(ti.getFechaFin())));
					} else {
						sheet.addCell(new Label(i++, row, ""));
					}

					ti.setEntradas(new ArrayList<>());
					List<PasoEjecutado> anteriores = procesoControl.buscarPasosEjecutadosAnteriores(ti.getProcesoEjecutado().getId());
					for (PasoEjecutado paso : anteriores) {
						ti.getEntradas().addAll(buscarValoresEntradasBandeja(paso));
					}
					if (ti.getUsuarioAsignadoId() != null && !ti.getUsuarioAsignadoId().isEmpty()) {
						ti.setUsuarioAsignado(gestorSesionBean.obtenerUsuario(ti.getUsuarioAsignadoId()));
					} else if (ti.getGrupoAsignadoId() != null && !ti.getGrupoAsignadoId().isEmpty()) {
						ti.setGrupoAsignado(gestorSesionBean.obtenerGrupo(ti.getGrupoAsignadoId(), null));
					}

					for (ValorEntrada value : ti.getEntradas()) {
						if (procesoUnico) {
							sheet.addCell(new Label(i, 0, value.getEntrada().getNombreEntrada()));
							if (value.getValorEntradaTexto() == null || value.getValorEntradaTexto().isEmpty()) {
								sheet.addCell(new Label(i++, row, value.getValor()));
							} else {
								sheet.addCell(new Label(i++, row, value.getValorEntradaTexto()));
							}
						} else {
							if (value.getValorEntradaTexto() == null || value.getValorEntradaTexto().isEmpty()) {
								sheet.addCell(new Label(i++, row, value.getEntrada().getNombreEntrada() + ":" + value.getValor()));
							} else {
								sheet.addCell(new Label(i++, row, value.getEntrada().getNombreEntrada() + ":" + value.getValorEntradaTexto()));
							}
						}
					}
				}
			}
			sheet.addCell(new Label(sheet.getColumns(), 0, mensajes.getString("etiqueta_firma")));
			workbook.write();
			workbook.close();
			if (baos != null) {
				archivoExportar = new DefaultStreamedContent(new ByteArrayInputStream(baos.toByteArray()), "application/vnd.ms-excel", fileName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}

	/**
	 * Exporta un archivo plano para la empresa transportadora
	 */
	public void exportarEnvios() {
		try {
			List<PasoEjecutado> listaReporte = establecerFiltrosConsulta(0, 0, null, null, 1);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			String fileName = "SNRCorrespondencia_Archivo472_" + Calendar.getInstance().getTimeInMillis() + ".csv";

			PrintWriter pw = new PrintWriter(baos);

			StringBuilder builder = new StringBuilder();
			String columns = "destinatario;ciudad;direccion;telefono;peso;referencia";
			builder.append(columns + "\n");

			if (pw != null) {
				for (PasoEjecutado ti : listaReporte) {
					// Busca los valores de las entradas de pasos anteriores
					List<ValorEntrada> listaEntradas = new ArrayList<>();
					List<PasoEjecutado> anteriores = procesoControl.buscarPasosEjecutadosAnteriores(ti.getProcesoEjecutado().getId());
					for (PasoEjecutado anterior : anteriores) {
						List<ValorEntrada> entradasAnteriores = procesoControl.buscarValoresEntradas(anterior.getId());
						listaEntradas.addAll(entradasAnteriores);
					}

					boolean devuelto = false;
					String destinatario = "";
					String primerNombre = "";
					String segundoNombre = "";
					String primerApellido = "";
					String segundoApellido = "";
					String ciudad = "";
					String direccion = "";
					String telefono = "";
					String peso = "";
					String consecutivo = "";

					for (ValorEntrada value : listaEntradas) {
						if (value.getEntrada().getNombre() != null && !value.getEntrada().getNombre().isEmpty()) {
							// Cuando ya se ha devuelto el paquete
							if (value.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_ENVIODEVUELTO)) {
								if(value.getValor().equals("2")) {
									devuelto = true;
								} else {
									devuelto = false;
								}
							}
							if(ti.getProcesoEjecutado().getProceso().getNombreClave().equals(ConstantesCorrespondencia.NOMBRE_PROCESO_CORRESPONDENCIA_RECIBIDA)) {
								if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamOwccParamDestinatario())) {
									Parametro parametro = parametrosControl.buscarParametroPorId(value.getEntrada().getParametroOpcionesId());
									if (parametro.getOpcionesJson() != null && !parametro.getOpcionesJson().isEmpty()) {
										for (JsonOpcion opcion : parametro.getOpcionesJson()) {
											if (opcion.getId() == Integer.valueOf(value.getValor())) {
												destinatario = opcion.getNombre();
												
												if(opcion.getDatosAdicionales()!=null) {
													for(Integer id:opcion.getDatosAdicionales().keySet()) {
														for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
															if(dato.getId()==id && dato.isActivo()) {
																if (dato.getNombreDato().equalsIgnoreCase(ConstantesCorrespondencia.NOMBRE_CAMPO_PARAMETRO_LISTA_CIUDAD)) {
																	ciudad = opcion.getDatosAdicionales().get(id);
																} else if (dato.getNombreDato().equalsIgnoreCase(ConstantesCorrespondencia.NOMBRE_CAMPO_PARAMETRO_LISTA_DIRECCION)) {
																	direccion = opcion.getDatosAdicionales().get(id);
																} else if (dato.getNombreDato().equalsIgnoreCase(ConstantesCorrespondencia.NOMBRE_CAMPO_PARAMETRO_LISTA_TELEFONO)) {
																	telefono = opcion.getDatosAdicionales().get(id);
																}
															}
														}
													}
												}
												break;
											}
										}
									}
								}
							} else if(ti.getProcesoEjecutado().getProceso().getNombreClave().equals(ConstantesCorrespondencia.NOMBRE_PROCESO_GENERACION_EE)){
								if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneePrimerNombreDestinatario())) {
									primerNombre = value.getValor();
								} else if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneeSegundoNombreDestinatario())) {
									segundoNombre = " " + value.getValor();
								} else if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneePrimerApellidoDestinatario())) {
									primerApellido = " " + value.getValor();
								} else if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneeSegundoApellidoDestinatario())) {
									segundoApellido = " " + value.getValor();
								} else if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneeCiudadDestinatario())) {
									ciudad = value.getValor();
								} else if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneeDireccionDestinatario())) {
									direccion = value.getValor();
								} else if (value.getEntrada().getNombre().equals(gestorSesionBean.getIntegracionCatalogos().getParametrosTO().getParamGeneracioneeTelefonoDestinatario())) {
									telefono = value.getValor();
								}
							}
							if (value.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_CONSECUTIVOEE)) {
								consecutivo = value.getValor();
							}
						}
					}
					
					// si viene del proceso de generacion ee
					if(destinatario.isEmpty()) {
						destinatario = primerNombre + segundoNombre + primerApellido + segundoApellido;
					}
					
					builder.append(destinatario + ";");
					builder.append(ciudad + ";");
					builder.append(direccion + ";");
					builder.append(telefono + ";");
					builder.append(peso + ";");
					builder.append(consecutivo);
					builder.append('\n');
					
					// Ejecuta cambio de etapa
					List<ValorEntrada> entradasPasoEjecutado = buscarEntradas(ti.getPaso().getId());
					for(ValorEntrada valorEntrada:entradasPasoEjecutado) {
						if(valorEntrada.getEntrada().getNombre()!=null) {
							if(valorEntrada.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_FECHAENVIO472)) {
								valorEntrada.setValor(formatoFecha.format(Calendar.getInstance().getTime()));
							} else if(valorEntrada.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_REENVIAR)) {
								if(devuelto) {
									valorEntrada.setValor("2");
								} else {
									valorEntrada.setValor("1");
								}
							}
						}
					}

					// Las entradas se guardan acá mismo
					procesoControl.ejecutarPasoProceso(gestorSesionBean.getIntegracionUsuarios(), ti, ti.getProcesoEjecutado().getProceso().getId(), entradasPasoEjecutado, gestorSesionBean.getUsuarioActual(), gestorSesionBean.getDireccionIp(), 1, gestorSesionBean.getUsuarioActual().getOficina().getCirculo());
				}

				pw.write(builder.toString());
				pw.close();
			}

			if (baos != null) {
				archivoExportar = new DefaultStreamedContent(new ByteArrayInputStream(baos.toByteArray()), "application/vnd.ms-excel", fileName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}

	
	/**
	 * Crea una nueva ejecución del proceso
	 */
	public void crear() {
		if (!procesos.isEmpty()) {
			gestorSesionBean.irAOpcion(41);
			this.reiniciarPaso();

			if (procesos.size() == 1) {
				this.procesoSeleccionado = procesos.get(0).getId();
				listenerCambiarProceso(null);
			}

		} else {
			String message = mensajes.getString("error_nohayprocesos");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Evento para cambiar la seleccion de proceso y recargar todos sus datos
	 * @param event Evento de cambio del valor del Componente
	 */
	public void listenerCambiarProceso(ValueChangeEvent event) {
		try {
			if (event != null) {
				this.reiniciarPaso();
				this.procesoSeleccionado = (Long) event.getNewValue();
			}
			if (this.procesoSeleccionado > 0) {
				permitirEjecutarPaso = true;

				// Consulta proceso para determinar restriccion de ejecución por periodos
				Proceso proceso = procesoControl.buscarProcesoPorId(procesoSeleccionado);
				if (proceso.getRestriccionTiempoEjecutado() > 0) {
					permitirEjecutarPaso = procesoControl.validarRestriccionTiempoEjecutado(proceso);
					if(!permitirEjecutarPaso) {
						String msg = mensajes.getString("error_procesorestringidoperiodo") + " " + mensajes.getString("etiqueta_proceso_restriccionTiempoEjecutado_" + proceso.getRestriccionTiempoEjecutado());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
					}

				}

				if (gestorSesionBean.getMenuActual().getId() == 5) {
					permitirEjecutarPaso = false;
				}

				if (permitirEjecutarPaso) {
					this.proximosPasos = procesoControl.buscarPasosActivosProceso(this.procesoSeleccionado);
					if (!this.proximosPasos.isEmpty()) {
						this.data = procesoControl.inicializaPasoEjecutado(this.proximosPasos.get(0), this.gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp(), gestorSesionBean.getUsuarioActual().getOficina().getCirculo());
						this.entradas = this.buscarEntradas(this.data.getPaso().getId());
						this.proximosPasos.remove(0);
						this.setDefaultProximoPaso();

						procesoControl.validarReglasYEntradasAnidadas(entradas);

						cambiarValorEntrada();
					}
					if (this.data.getPaso().getProceso().getCategorias() == null) {
						this.data.getPaso().getProceso().setCategorias(procesoControl.buscarCategoriasProceso(data.getPaso().getProceso().getId()));

						data.getPaso().getProceso().getCategorias().sort((obj1, obj2) -> obj1.getCategoria().getNombre().compareTo(obj2.getCategoria().getNombre()));
					}
				}
			} else {
				this.proximosPasos = new ArrayList<>();
			}

			if (proximoPaso != null) {
				estableceUsuariosProximoPaso(proximoPaso);
			}
		} catch (Exception e) {
			String message = "Error buscando datos del proceso: " + e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Establece los usuarios para el proximo paso
	 * @param paso Paso sobre el cual evaluar los usuarios
	 */
	private void estableceUsuariosProximoPaso(Paso paso) {
		usuariosAsignados = null;
		gruposAsignados = null;
		
		List<String[]> resultado = procesoControl.estableceUsuariosProximoPaso(gestorSesionBean.getIntegracionUsuarios(), paso, data, gestorSesionBean.getUsuarioActual(), nivelAcceso);
		if(resultado!=null && !resultado.isEmpty()) {
			usuariosAsignados = resultado.get(0);
			gruposAsignados = resultado.get(1);
		}

	}
	

	/**
	 * Entra a detallar una etapa permitiendo su ejecución o solamente su detalle
	 * @param data PasoEejecutado sobre el cual entrar a detallar
	 */
	public void detalles(PasoEjecutado data) {
		if(gestorSesionBean.getMenuActual().getId()==4) {
			gestorSesionBean.irAOpcion(41);
		} else if(gestorSesionBean.getMenuActual().getId()==5) {
			gestorSesionBean.irAOpcion(51);
		}
		gestorSesionBean.getBreadcrumb().clear();
		gestorSesionBean.getBreadcrumb().add(data.getProcesoEjecutado().getProceso().getNombre());
		gestorSesionBean.getBreadcrumb().add(data.getPaso().getNombre());
		this.reiniciarPaso();

		this.data = (PasoEjecutado) procesoControl.buscarEntidadPorId(new PasoEjecutado(), data.getId());
		procesoControl.inicializarJsonPasoData(this.data.getPaso());
		
		this.procesoSeleccionado = this.data.getPaso().getProceso().getId();

		if (this.data.getPaso().getProceso().getCategorias() == null) {
			this.data.getPaso().getProceso().setCategorias(procesoControl.buscarCategoriasProceso(data.getPaso().getProceso().getId()));
		}
		if (this.data.getProcesoEjecutado().getCategoria() != null) {
			this.categoriasSeleccionadas = this.data.getProcesoEjecutado().getCategoria().getId();
		} else {
			this.categoriasSeleccionadas = 0l;
		}

		this.data.setFechaCreacion(Calendar.getInstance().getTime());
		this.data.setIdUsuarioCreacion(gestorSesionBean.getUsuarioActual().getId());
		this.data.setIpCreacion(gestorSesionBean.getDireccionIp());

		this.pasosAnteriores = procesoControl.buscarPasosEjecutadosAnteriores(data.getProcesoEjecutado().getId());
		Iterator<PasoEjecutado> iter = this.pasosAnteriores.iterator();
		while (iter.hasNext()) {
			PasoEjecutado pasoEjecutado = iter.next();
			// Quita los pasos de la lista del historico de eventos si no tiene permisos y
			// solo deja las que el mismo usuario ejecutó
			if (this.nivelAcceso > RolesSistema.CONSULTA.getId() && pasoEjecutado.getIdUsuarioCreacion().equals(this.gestorSesionBean.getUsuarioActual().getId()) && Boolean.FALSE.equals(pasoEjecutado.getUltimoPaso())) {
				iter.remove();
			} else {
				pasoEjecutado.setEntradas(procesoControl.buscarValoresEntradas(pasoEjecutado.getId()));

				procesoControl.validarEntradasAnidadas(pasoEjecutado.getEntradas());

				// Encuentra el usuario que realizó el paso tiene prevalencia el updateuser
				if (pasoEjecutado.getIdUsuarioModificacion() != null && !pasoEjecutado.getIdUsuarioModificacion().isEmpty()) {
					pasoEjecutado.setUsuarioAsignado(gestorSesionBean.obtenerUsuario(pasoEjecutado.getIdUsuarioModificacion()));
				} else if (pasoEjecutado.getUsuarioAsignadoId() != null && !pasoEjecutado.getUsuarioAsignadoId().isEmpty()) {
					pasoEjecutado.setUsuarioAsignado(gestorSesionBean.obtenerUsuario(pasoEjecutado.getUsuarioAsignadoId()));
				}

				if (pasoEjecutado.getListaUsuariosAsignados() == null) {
					pasoEjecutado.setListaUsuariosAsignados(new ArrayList<>());
					if (pasoEjecutado.getUsuariosAsignados() != null && !pasoEjecutado.getUsuariosAsignados().isEmpty()) {
						String[] usus = pasoEjecutado.getUsuariosAsignados().split(",");
						for (String usu : usus) {
							pasoEjecutado.getListaUsuariosAsignados().add(gestorSesionBean.obtenerUsuario(usu));
						}
					}
				}
				if (pasoEjecutado.getListaGruposAsignados() == null) {
					pasoEjecutado.setListaGruposAsignados(new ArrayList<>());
					if (pasoEjecutado.getGruposAsignados() != null && !pasoEjecutado.getGruposAsignados().isEmpty()) {
						String[] grupos = pasoEjecutado.getGruposAsignados().split(",");
						for (String grupo : grupos) {
							pasoEjecutado.getListaGruposAsignados().add(gestorSesionBean.obtenerGrupo(grupo, null));
						}
					}
				}

			}
		}

		if (this.data.getProcesoEjecutado().isActivo()) {
			int order = this.data.getPaso().getOrdenPaso().intValue();
			this.proximosPasos = procesoControl.buscarPasosActivosProceso(this.procesoSeleccionado);
			Iterator<Paso> iter2 = this.proximosPasos.iterator();
			while (iter2.hasNext()) {
				Paso paso = iter2.next();
				if (paso.getOrdenPaso().intValue() <= order) {
					iter2.remove();
				}
			}
			this.setDefaultProximoPaso();

			this.entradas = this.buscarEntradas(this.data.getPaso().getId());

			procesoControl.validarReglasYEntradasAnidadas(entradas);

			cambiarValorEntrada();

			// Si es rol cliente no ve proximas pasos
			if (this.nivelAcceso > RolesSistema.CONSULTA.getId()) {
				this.proximosPasos = new ArrayList<>();
				this.entradas = new ArrayList<>();
			}

			// Verifica si el usuario actual es el usuario asignado o pertenece a un grupo
			// asignado para poder ejecutar el paso
			boolean okgroup = false;
			if (this.data.getGrupoAsignadoId() != null) {
				String[] grupos = this.data.getGrupoAsignadoId().split(",");
				for(String grupo:grupos) {
					for (GrupoTO group : this.gestorSesionBean.getUsuarioActual().getGrupos()) {
						if (grupo.equals(group.getId())) {
							okgroup = true;
							break;
						}
					}
					if(okgroup) {
						break;
					}
				}
			}

			boolean okusuario = false;
			if (this.data.getUsuarioAsignadoId() != null) {
				String[] usuarios = this.data.getUsuarioAsignadoId().split(",");
				for(String usuario:usuarios) {
					if(this.gestorSesionBean.getUsuarioActual().getId().equals(usuario)) {
						okusuario = true;
						break;
					}
				}
			}

			
			if (okgroup || ((this.data.getUsuarioAsignadoId() == null || this.data.getUsuarioAsignadoId().isEmpty()) && (this.data.getGrupoAsignadoId() == null || this.data.getGrupoAsignadoId().isEmpty()))
					|| (this.data.getUsuarioAsignadoId() != null && okusuario)) {
				this.permitirEjecutarPaso = true;
			}

			if (this.nivelAcceso == RolesSistema.SUPERADMIN.getId()) {
				this.permitirEjecutarPaso = true;
			}
			if (gestorSesionBean.getMenuActual().getId() == 5 || gestorSesionBean.getMenuActual().getId() == 51) {
				permitirEjecutarPaso = false;
			}

			if (proximoPaso != null) {
				estableceUsuariosProximoPaso(proximoPaso);
			}
		}
	}

	
	
	/**
	 * Solo administrador puede borrar un proceso
	 */
	public void borrarProcesoEjecutado() {
		try {
			if (this.data != null) {
				procesoControl.eliminarProcesoEjecutado(data.getProcesoEjecutado());
			}
			backList();
			String message = MessageFormat.format(mensajes.getString("mensaje_proceso_borrado"), this.data.getProcesoEjecutado().getId());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		} catch (Exception e) {
			String message = "Error: " + e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	

	/**
	 * Solo administrador puede reactivar un proceso después de finalizada su última etapa
	 */
	public void reactivateProcesoInstance() {
		try {
			if (this.data != null) {
				procesoControl.reactivarProcesoEjecutado(data);
				this.data = procesoControl.inicializarNuevoPaso(this.data.getPaso(), this.data, 2, gestorSesionBean.getDireccionIp());
				this.buscar();
				
				String radicado = data.getProcesoEjecutado().getId()+"";
				if (data.getProcesoEjecutado().getSecuenciaProceso() != null && !data.getProcesoEjecutado().getSecuenciaProceso().isEmpty()) {
					radicado = data.getProcesoEjecutado().getSecuenciaProceso();
				}

				String message = MessageFormat.format(mensajes.getString("mensaje_proceso_reactivar"), radicado);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
			}
		} catch (Exception e) {
			String message = "Error: " + e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Fija los datos del proximo paso a ejecutar
	 */
	private void setDefaultProximoPaso() {
		if (!this.proximosPasos.isEmpty()) {
			for (Paso paso : this.proximosPasos) {
				if (paso.getUsuarioAsignadoId() != null && !paso.getUsuarioAsignadoId().isEmpty()) {
					paso.setUsuarioAsignado(gestorSesionBean.obtenerUsuario(paso.getUsuarioAsignadoId()));
				} else if (paso.getGrupoAsignadoId() != null && !paso.getGrupoAsignadoId().isEmpty()) {
					paso.setGrupoAsignado(gestorSesionBean.obtenerGrupo(paso.getGrupoAsignadoId(), null));
				}
			}
			this.proximoPaso = this.proximosPasos.get(0);
		}
	}

	
	/**
	 * Busca las entradas de un paso
	 * 
	 * @param pasoId Paso asociado
	 * @return Lista con las entradas asociadas al Paso
	 */
	private List<ValorEntrada> buscarEntradas(long pasoId) {
		List<ValorEntrada> listaEntradas = new ArrayList<>();
		List<Entrada> meta = procesoControl.buscarEntradasActivasPaso(pasoId);
		for (Entrada entradaentrada : meta) {
			ValorEntrada value = new ValorEntrada();
			value.setEntrada(entradaentrada);
			if (entradaentrada.isEntradaAnidada()) {
				value.setMostrar(false);
			}
			if (entradaentrada.getTipoEntrada().equals(TiposEntrada.RADIO.name()) || entradaentrada.getTipoEntrada().equals(TiposEntrada.SELECT.name()) || entradaentrada.getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())
					|| entradaentrada.getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || entradaentrada.getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name()) ) {
				getListaOpcionesEntrada(entradaentrada);
				// Radio no coloca valor predefinido
				if ((entradaentrada.getTipoEntrada().equals(TiposEntrada.SELECT.name()) || entradaentrada.getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name())) && !entradaentrada.getListaOpciones().isEmpty()) {
					value.setValor(entradaentrada.getListaOpciones().get(0).getId());
				}
			} else if(entradaentrada.getTipoEntrada().equals(TiposEntrada.VISOR_ARCHIVO.name())) {
				// Coloca lupas para archivos cargados en pasos anteriores (solo cologa el último encontrado)
				List<Archivo> documentos = new ArrayList<>();
				Archivo documento = null;
				if(pasosAnteriores!=null) {
					for(PasoEjecutado pasoEjecutado:pasosAnteriores) {
						for(ValorEntrada valorEntrada:pasoEjecutado.getEntradas()) {
							if(valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.FILE.name())){
								String[] ids = valorEntrada.getValor().split(",");
								for (String id : ids) {
									documento = (Archivo) procesoControl.buscarEntidadPorId(new Archivo(), Long.valueOf(id));
								}
							}
						}
					}
				}
				if (documento != null) {
					documentos.add(documento);
				}
				value.setDocumentos(documentos);
			}
			
			// Restricciones de fecha
			if(procesoControl.getTiposEntradasRestricciones().contains(value.getEntrada().getTipoEntrada())) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				List<RestriccionEntrada> restricciones = procesoControl.buscarRestriccionesEntrada(value.getEntrada().getId());
				if(!restricciones.isEmpty()) {
					for(RestriccionEntrada restriccion:restricciones) {
						if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.DATE.name())) {
							Calendar calendar = Calendar.getInstance();
							calendar.clear(Calendar.HOUR_OF_DAY);
							calendar.clear(Calendar.MINUTE);
							calendar.clear(Calendar.SECOND);
							calendar.clear(Calendar.MILLISECOND);

							if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MAYOR.name())) {
								if(restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name())) {
									calendar.add(Calendar.DAY_OF_MONTH, 1);
									value.setValorMinimoEntrada(sdf.format(calendar.getTime()));
								}
							} else if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MAYOR_O_IGUAL.name())) {
								if(restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name())) {
									value.setValorMinimoEntrada(sdf.format(calendar.getTime()));
								}
							} else if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MENOR.name())) {
								if(restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name())) {
									calendar.add(Calendar.DAY_OF_MONTH, -1);
									value.setValorMaximoEntrada(sdf.format(calendar.getTime()));
								}
							} else if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MENOR_O_IGUAL.name()) && restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name())) {
								value.setValorMaximoEntrada(sdf.format(calendar.getTime()));
							}
						}
					}
				}
			}
			
			
			// Validar copiar valor entrada
			// Copia el valor de la entrada de la última ejecución de la etapa con el id de entrada referenciado
			if(value.getEntrada().getIdEntradaCopiar()!=null && value.getEntrada().getIdEntradaCopiar()>0 && pasosAnteriores!=null) {
				for(PasoEjecutado pasoAnterior:pasosAnteriores){
					if(pasoAnterior.getEntradas()!=null) {
						for(ValorEntrada entradaAnterior:pasoAnterior.getEntradas()) {
							if(entradaAnterior.getEntrada().getId()==value.getEntrada().getIdEntradaCopiar()) {
								value.setValor(entradaAnterior.getValor());
								break;
							}
						}
					}
				}
			}

			listaEntradas.add(value);
		}
		return listaEntradas;
	}

	
	/**
	 * Obtiene los valores de entradas para un paso ejecutado para mostrar en la bandeja
	 * 
	 * @param paso PasoEjecutado sobre el cual consultar los valores a mostrar en la bandeja
	 * @return Lista de entradas encontradas
	 */
	private List<ValorEntrada> buscarValoresEntradasBandeja(PasoEjecutado paso) {
		List<ValorEntrada> listaEntradas = procesoControl.buscarValoresEntradas(paso.getId());
		Iterator<ValorEntrada> iter = listaEntradas.iterator();
		while (iter.hasNext()) {
			ValorEntrada value = iter.next();
			// Las secuencias siempre se muestran así estén ocultas
			if ((!value.getEntrada().isIncluirBandeja() || value.getEntrada().isEntradaOculta()) && 
					!value.getEntrada().getTipoEntrada().equals(TiposEntrada.SECUENCIA_GLOBAL.name()) && !value.getEntrada().getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())) {
				iter.remove();
			}
		}
		return listaEntradas;
	}

	
	/**
	 * Guarda el paso ejecutado
	 * 
	 * @param opcion 1 avanzar - 0 devolver
	 */
	public void guardar(int opcion) {
		String message = "";
		try {
			if (this.validarGuardarPaso(opcion)) {
				logger.info("Guardando paso:" + this.data.getPaso().getId());

				Proceso proceso = procesoControl.buscarProcesoPorId(this.procesoSeleccionado);
				if (proceso != null) {
					Paso siguientePaso = null;
					if (opcion == 1) {
						siguientePaso = this.proximoPaso;
					} else {
						// devolver paso
						siguientePaso = this.data.getPasoAnterior().getPaso();
						procesoControl.inicializarJsonPasoData(siguientePaso);
						
						data.setPasoDevuelto(true);
						// Fija el usuario anterior como usuario asignado
						usuariosAsignados = new String[1];
						usuariosAsignados[0] = data.getPasoAnterior().getUsuarioAsignadoId();
					}

					boolean nuevo = false;
					
					// Fija la dirección IP en el objeto pasoEjecutado para que se tome internamente al momento de guardar
					if(data.getId()==0) {
						data.setIpCreacion(gestorSesionBean.getDireccionIp());
						nuevo = true;
					} else {
						data.setIpModificacion(gestorSesionBean.getDireccionIp());
					}
					

					// En caso de que el usuario asignado sea solo 1
					if((usuariosAsignados==null || usuariosAsignados.length==0) && usuarioAsignado!=null) {
						usuariosAsignados = new String[1];
						usuariosAsignados[0] = usuarioAsignado;
					}
					
					data = procesoControl.guardarTransaccionPasoEjecutado(gestorSesionBean.getIntegracionUsuarios(), data, gestorSesionBean.getUsuarioActual(), siguientePaso, categoriasSeleccionadas, usuariosAsignados, gruposAsignados, entradas, opcion);
					
					enviarNotificacionesEjecucion();

					String radicado = data.getProcesoEjecutado().getId()+"";
					if (data.getProcesoEjecutado().getSecuenciaProceso() != null && !data.getProcesoEjecutado().getSecuenciaProceso().isEmpty()) {
						radicado = data.getProcesoEjecutado().getSecuenciaProceso();
					}
					if (!nuevo) {
						message = MessageFormat.format(mensajes.getString("mensaje_proceso_actualizar"), radicado);
					} else {
						message = MessageFormat.format(mensajes.getString("mensaje_ejecucion_guardar"), radicado);
					}

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
					this.detalles(data);
					permitirEjecutarPaso = false;
				}
			}
		} catch (Exception e) {
			logger.error("Error guardando proceso", e);
			message = mensajes.getString("error_proceso_guardar") + ":" + e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Envia notificaciones de ejecución paso actual
	 */
	private void enviarNotificacionesEjecucion() {
		try {
			if (this.data.getPaso().getAvisoEjecucion() > 0 && this.data.getIdUsuarioCreacion() != null && !data.getIdUsuarioCreacion().isEmpty()) {
				if (this.data.getPaso().getAvisoEjecucion() == 1 || this.data.getPaso().getAvisoEjecucion() == 3) {
					new NotificacionUtil(gestorSesionBean.getIntegracionCatalogos()).enviarCorreoPasoActual(this.data);
				}
				if (this.data.getPaso().getAvisoEjecucion() == 2 || this.data.getPaso().getAvisoEjecucion() == 3) {
					new NotificacionUtil(gestorSesionBean.getIntegracionCatalogos()).enviarSMSPasoActual(this.data);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	
	/**
	 * Valida los datos obligatorios de guardar paso
	 * @param opcion 1 avanzar - 0 devolver
	 * @return true si las validaciones fueron satisfactorias
	 */
	private boolean validarGuardarPaso(int opcion) {
		boolean ok = true;
		List<String> errors = new ArrayList<>();
		
		// Valida usuario asignado solo para opcion=1 paso siguiente
		if (opcion == 1 && this.proximoPaso != null) {
			if (!proximoPaso.getJsonPasoData().isOcultarSeleccionUsuarios() && ((usuariosAsignados == null || usuariosAsignados.length == 0) && (usuarioAsignado==null || usuarioAsignado.isEmpty()) && proximoPaso.getJsonPasoData().isMostrarUsuariosGrupos())) {
				errors.add(mensajes.getString("error_paso_usuario"));
			} else if (!proximoPaso.getJsonPasoData().isOcultarSeleccionGrupos() && ((usuariosAsignados == null || usuariosAsignados.length == 0) && (gruposAsignados == null || gruposAsignados.length == 0) && !proximoPaso.getJsonPasoData().isMostrarUsuariosGrupos())) {
				errors.add(mensajes.getString("error_paso_grupo"));
			}
		}

		// Valida campos obligatorios entradas
		for (ValorEntrada value : this.entradas) {
			if (value.getEntrada().isRequerido() && value.isMostrar()) {
				if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.FILE.name())) {
					if (value.getArchivoCargado() == null || value.getArchivoCargado().getSize()==0) {
						errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(CONSTANTE_MENSAJE_REQUERIDO));
					}
				} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.DATE.name())) {
					if (value.getValorEntradaFecha() == null) {
						errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(CONSTANTE_MENSAJE_REQUERIDO));
					}
				} else if (value.getValor() == null || value.getValor().isEmpty()) {
					errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(CONSTANTE_MENSAJE_REQUERIDO));
				}
			}
			
			// Restricciones de fecha
			String mensajeErrorRestriccion = "error_entradarestriccion";
			if(procesoControl.getTiposEntradasRestricciones().contains(value.getEntrada().getTipoEntrada())) {
				List<RestriccionEntrada> restricciones = procesoControl.buscarRestriccionesEntrada(value.getEntrada().getId());
				if(!restricciones.isEmpty()) {
					for(RestriccionEntrada restriccion:restricciones) {
						if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.DATE.name()) && value.getValorEntradaFecha() != null) {
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.HOUR_OF_DAY, 0);
							calendar.set(Calendar.MINUTE, 0);
							calendar.set(Calendar.SECOND, 0);
							calendar.set(Calendar.MILLISECOND, 0);

							if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MAYOR.name())) {
								calendar.add(Calendar.DAY_OF_MONTH, 1);
								if(restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name()) && value.getValorEntradaFecha().before(calendar.getTime())) {
									errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(mensajeErrorRestriccion));
								}
							} else if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MAYOR_O_IGUAL.name())) {
								if(restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name()) && value.getValorEntradaFecha().compareTo(calendar.getTime()) < 0) {
									errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(mensajeErrorRestriccion));
								}
							} else if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MENOR.name())) {
								calendar.add(Calendar.DAY_OF_MONTH, -1);
								if(restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name()) && value.getValorEntradaFecha().after(calendar.getTime())) {
									errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(mensajeErrorRestriccion));
								}
							} else if(restriccion.getTipo().equals(TiposRestriccionesEntrada.MENOR_O_IGUAL.name()) && restriccion.getValor().equals(TiposRestriccionesValor.SYSDATE.name()) && value.getValorEntradaFecha().compareTo(calendar.getTime()) > 0) {
								errors.add(value.getEntrada().getNombreEntrada() + ": " + mensajes.getString(mensajeErrorRestriccion));
							}
						}
					}
				}
			}
		}

		if (!errors.isEmpty()) {
			ok = false;
			for (String error : errors) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, error, error));
			}
		}
		return ok;
	}

	
	/**
	 * Vuelve a la bandeja principal
	 */
	public void backList() {
		this.data = null;
		this.proximosPasos = null;
		this.pasosAnteriores = null;
		this.entradas = null;
		gestorSesionBean.irAOpcion(gestorSesionBean.getMenuActual().getIdPadre());
		this.buscar();
	}

	
	/**
	 * Cargar el archivo de una entrada
	 */
	public void cargarArchivos(FileUploadEvent event) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, event.getFile().getFileName() + " cargado.", event.getFile().getFileName() + " cargado."));

		Object valorEntrada = event.getComponent().getAttributes().get("valorEntrada");
		if (valorEntrada != null) {
			ValorEntrada valor = (ValorEntrada) valorEntrada;
			valor.setArchivoCargado(event.getFile());
		}
	}

	
	/**
	 * Borra el archivo cargado de una entrada
	 * 
	 * @param valorEntrada Entrada afectada
	 */
	public void borrarArchivos(ValorEntrada valorEntrada) {
		valorEntrada.setArchivoCargado(null);
	}


	/**
	 * Obtiene un estilo para las filas de la bandeja de entrada
	 * @return Cadena con el estilo de la fila de la tabla
	 */
	public String getRowClass() {
		StringBuilder rowClasses = new StringBuilder();
		for (PasoEjecutado paso : this.lista) {
			if (rowClasses.length() > 0)
				rowClasses.append(",");
			if (paso.getFechaFin() != null && paso.getFechaFin().before(this.fechaActual) && paso.getFechaFin().after(paso.getFechaCreacion()) && paso.getProcesoEjecutado().isActivo()) {
				rowClasses.append("row_atradado");
			} else if (paso.getProcesoEjecutado() != null && !paso.getProcesoEjecutado().isActivo()) {
				rowClasses.append("row_completado");
			} else {
				rowClasses.append("row_normal");
			}
		}
		return rowClasses.toString();
	}

	
	/**
	 * Se ejecuta al cambiar de opción un entrada tipo lista
	 */
	public void cambiarValorEntrada() {
		siguientePasoUnificaParalelo = false;
		this.proximoPaso = procesoControl.establecerSiguientePaso(entradas, data, proximosPasos);
		if(proximoPaso!=null) {
			campoObservacionesRequerido = proximoPaso.isCampoObservacionesRequerido();
			if(proximoPaso.getId()==-1l) {
				proximoPaso = null;
			}
		}
		
		if (proximoPaso != null) {
			estableceUsuariosProximoPaso(proximoPaso);
			
			// Valida si es una ejecución paralela y tiene etapas pendientes muestra mensaje advertencia.
			// Solamente tiene en cuenta si esta encendida la casilla de unificar pasos proceso
			if(proximoPaso.getJsonPasoData().isUnificarPasosProceso()) {
				List<PasoEjecutado> pasosActuales = procesoControl.obtenerPasosEjecutadosPorProceso(data.getProcesoEjecutado().getId());
				if(pasosActuales.size()>1) {
					siguientePasoUnificaParalelo = true;
				}
			}

		}
	}


	/**
	 * Obtiene una lista de opciones a partir de las configuradas para la entrada
	 * @param entrada Entrada sobre la cual verificar las opciones
	 * @param seleccionadas Opciones seleccionadas previamente
	 * @return Lista de Nodos para mostrar en la página
	 */
	public List<NodoTO> getListaOpcionesEntradaSelected(Entrada entrada, List<String> seleccionadas) {
		List<NodoTO> result = procesoControl.getListaOpcionesEntrada(entrada, null);
		Iterator<NodoTO> iter = result.iterator();
		while (iter.hasNext()) {
			NodoTO nodo = iter.next();
			if (!seleccionadas.contains(nodo.getId())) {
				iter.remove();
			}
		}
		return result;
	}

	
	/**
	 * Obtiene una lista de opciones segun un valor predefinido.  Solamente devuelve el item que es igual al valor del parametro
	 * @param opciones Listado de opciones original
	 * @param value Valor a buscar dentro de la lista de opciones
	 * @return Lista de Nodos para mostrar en la página con el valor encontrado
	 */
	public List<NodoTO> getListaOpcionesEntradavalue(List<NodoTO> opciones, String value) {
		List<NodoTO> result = new ArrayList<>();
		for (NodoTO option : opciones) {
			if (option.getId().equals(value)) {
				result.add(option);
				break;
			}
		}
		return result;
	}

	
	/**
	 * Obtiene el listado de opciones de una entrada Utiliza el objeto NodeTO a
	 * manera de objeto de key:value
	 * 
	 * @param entrada Entrada a evaluar las opciones
	 * @return Lista de opciones de la entrada encontradas
	 */
	public List<NodoTO> getListaOpcionesEntrada(Entrada entrada) {
		List<NodoTO> result = null;
		if (entrada == null) {
			result = null;
		} else {
			result = procesoControl.getListaOpcionesEntrada(entrada, null);
		}
		return result;
	}

	
	/**
	 * Obtiene una lista de opciones de una entrada Tiene en cuenta opciones tanto
	 * activas como inactivas
	 * 
	 * @param entrada Entrada a evaluar las opciones
	 * @return Lista de opciones de la entrada encontradas
	 */
	public List<NodoTO> getListaOpcionesEntradaTodos(Entrada entrada) {
		List<NodoTO> result = null;
		if (entrada == null) {
			result = null;
		} else {
			result = procesoControl.getListaOpcionesEntradaTodos(entrada, null);
		}
		return result;
	}

	
	/**
	 * Abre un documento de tipo Salida Reporte (Sticker radicación)
	 * @param valorEntrada Entrada con los datos para abrir el reporte
	 */
	public void abrirSalidaReporte(ValorEntrada valorEntrada) {
		idValorEntradaSeleccionada = valorEntrada.getId();
		
		String urlOracleWCCImprimirPDF = gestorSesionBean.getIntegracionCatalogos().getCatalogoBioclientImprimir();

		String scriptImprimir = "";
		try {
			Object[] args = new Object[5];
			
			Archivo documento = null; 
			String[] ids = valorEntrada.getValor().split(",");
			for (String id : ids) {
				if(!id.equals("0")) {
					documento = (Archivo) procesoControl.buscarEntidadPorId(new Archivo(), Long.valueOf(id));
				}
			}
			
			String idpdfowcc = ""; 
			if(documento!=null) {
				idpdfowcc = documento.getIdentificadorExterno();
	
				args[0] = idpdfowcc;
				args[1] = valorEntrada.getValorEntradaTexto();
				args[2] = gestorSesionBean.getUsuarioActual().getNombreUsuario();
				args[3] = valorEntrada.getPasoEjecutado().getProcesoEjecutado().getSecuenciaProceso();
				args[4] = 1;
				
				urlOracleWCCImprimirPDF = MessageFormat.format(urlOracleWCCImprimirPDF, args);
				scriptImprimir = "connect('"+urlOracleWCCImprimirPDF+"');";
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		PrimeFaces.current().executeScript("PF('dialogovisorpdf').show();"+scriptImprimir);
	}

	
	/**
	 * Abre una ventana nueva para la digitalización con capture cuando se está en el detalle de la etapa
	 */
	public void abrirDigitalizacion() {
		StringBuilder url = new StringBuilder();
		if (data != null && data.getProcesoEjecutado() != null && data.getProcesoEjecutado().getSecuenciaProceso() != null) {
			url.append(urlOracleCapture).append(data.getProcesoEjecutado().getSecuenciaProceso());
		}
		PrimeFaces.current().executeScript("verOracleCapture('" + url + "');");
	}

	
	/**
	 * Abre una ventana nueva para la digitalización con capture cuando se está en la lista principal (bandeja)
	 * @param pasoEjecutado PasoEjecutado que contiene la secuencia del proceso para mandar a capture
	 */
	public void abrirDigitalizacionDirecto(PasoEjecutado pasoEjecutado) {
		StringBuilder url = new StringBuilder();
		if (pasoEjecutado != null && pasoEjecutado.getProcesoEjecutado() != null && pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso() != null) {
			url.append(urlOracleCapture).append(pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso());
		}
		PrimeFaces.current().executeScript("verOracleCapture('" + url + "');");
	}

	
	/**
	 * Obtiene la siguiente secuencia de una entrada de este tipo
	 * @param entrada Entrada a validar
	 * @param aumentarValor Si debe aumentar (persistir) el valor encontrado
	 * @return Cadena con el valor de ls secuencia
	 */
	public String getEntradaValorSecuencia(Entrada entrada, boolean aumentarValor) {
		return secuenciasControl.obtenerEntradaValorSecuencia(entrada.getId(), aumentarValor);
	}


	/**
	 * Obtiene la siguiente secuencia de una entrada de este tipo.  Usada para obtener el valor Global de una secuencia tipo parametro 
	 * @param entrada Entrada a validar
	 * @param aumentarValor Si debe aumentar (persistir) el valor encontrado
	 * @return Cadena con el valor de ls secuencia
	 */
	public String obtenerValorSecuenciaGlobal(Entrada entrada, boolean aumentarValor) {
		return secuenciasControl.obtenerValorSecuenciaGlobal(entrada.getParametroOpcionesId(), aumentarValor);
	}

	
	/**
	 * Obtiene el nombre de la etapa actual
	 * @param pasoEjecutado PasoEjecutado a evaluar
	 * @return Cadena con el orden de la etapa mas el nombre
	 */
	public String getNombrePaso(PasoEjecutado pasoEjecutado) {
		String name = "";
		if (pasoEjecutado != null) {
			name = pasoEjecutado.getPaso().getOrdenPaso() + " - " + pasoEjecutado.getPaso().getNombre();
			if (pasoEjecutado.isPasoDevuelto()) {
				name += " " + mensajes.getString("etiqueta_procesodetalles_devuelto");
			}
		}
		return name;
	}

	/**
	 * Time zone del servidor por defecto
	 * @return TimeZone por defecto
	 */
	public TimeZone getTimeZone() {
		return TimeZone.getDefault();
	}

	
	/**
	 * Obtiene la URL para integración con oracle capture agregando la secuencia del proceso (radicado)
	 * @return Cadena con la URL para conectar a Capture
	 */
	public String getUrlOracleCaptureCompleto() {
		StringBuilder url = new StringBuilder();
		if (data != null && data.getProcesoEjecutado() != null && data.getProcesoEjecutado().getSecuenciaProceso() != null) {
			url.append(urlOracleCapture).append(data.getProcesoEjecutado().getSecuenciaProceso());
		}
		return url.toString();
	}

	
	/**
	 * Abre el visor de documentos de oracle capture
	 * @param archivo Archivo con un ID de OWCC
	 * @param valorEntrada Valor de entrada asociada al archivo
	 */
	public void descargarArchivoEntrada(Archivo archivo, ValorEntrada valorEntrada) {
		String url = "";
		String mensaje = "";
		documentosVisualizar = new ArrayList<>();
		if(data.getProcesoEjecutado()!=null && data.getProcesoEjecutado().getSecuenciaProceso()!=null && !data.getProcesoEjecutado().getSecuenciaProceso().isEmpty()) {
			String repositorio = integracionOWCC.getIntegracionCatalogos().getParametrosTO().getParamOwccParamRepositorioValueFinal();
			if(valorEntrada!=null && valorEntrada.getEntrada().getArchivoTipoRepositorio()==1) {
				repositorio = integracionOWCC.getIntegracionCatalogos().getParametrosTO().getParamOwccParamRepositorioValueTemporal();
			}
			TipoSalidaConsultar salida = integracionOWCC.busquedaDocumentosPorIdDocumento(archivo.getIdentificadorExterno(), repositorio);
			if(salida!=null) {
				if(salida.getDocumentos()!=null) {
					List<TipoDocumento> documentos = salida.getDocumentos().getDocumento();
					for(TipoDocumento documento:documentos) {
						NodoTO nodo = new NodoTO();
						nodo.setId(documento.getDID());
						nodo.setText(documento.getDocName());
						nodo.setClave(documento.getUrlVisor());
						documentosVisualizar.add(nodo);
						url = documento.getUrlVisor();
					}
				} else {
					mensaje = "No se encontró documento";
				}
			} else {
				mensaje = "Response es nulo.";
			}
		}

		if(url!=null && !url.isEmpty()) {
			PrimeFaces.current().executeScript("verOracleWCCVisor('" + url + "');");
		} else {
			String message = mensajes.getString("error_descargar_archivo_visor") + ":" + mensaje;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	

	/**
	 * Permite descargar una plantilla para elaborar un documento
	 * 
	 * @param valorEntrada Entrada de tipo Plantilla para descargar
	 */
	public void descargarPlantilla(ValorEntrada valorEntrada) {
		try {
			HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			sesion.setAttribute("valorEntrada", valorEntrada);

			// Lista temporal con todos los valores de entradas de pasos pasadas + la actual
			List<ValorEntrada> list = new ArrayList<>();
			if (this.pasosAnteriores != null) {
				for (PasoEjecutado ti : this.pasosAnteriores) {
					list.addAll(ti.getEntradas());
				}
			}

			// Fija el valor de los checks
			for (ValorEntrada value : entradas) {
				// Fija valor para los checkbox para poder evaluar su regla y sus campos
				// anidados
				if (value.isMostrar() && value.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())) {
					value.setValor("");
					if (value.getListaEntradas() != null && !value.getListaEntradas().isEmpty()) {
						for (String objeto : value.getListaEntradas()) {
							value.setValor(value.getValor() + objeto + ",");
						}
						value.setValor(value.getValor().substring(0, value.getValor().lastIndexOf(',')));
					}
				}
			}

			List<ValorEntrada> allEntradas = new ArrayList<>();
			allEntradas.addAll(list);
			allEntradas.addAll(entradas);
			sesion.setAttribute("entradasPlantilla", allEntradas);

			PrimeFaces.current().executeScript("descargarArchivo(3);");
		} catch (Exception e) {
			logger.error("Error descargarPlantilla", e);
		}
	}

	
	/**
	 * Obtiene el nombre completo de una entrada (si es requerido con un *)
	 * 
	 * @param entrada Entrada a la cual obtener el nombre
	 * @return Cadena con el nombre de la entrada
	 */
	public String obtenerNombreEntrada(Entrada entrada) {
		StringBuilder resultado = new StringBuilder();
		if (entrada != null) {
			if (entrada.isRequerido()) {
				resultado.append(mensajes.getString("etiqueta_requerido")).append(' ');
			}
			resultado.append(entrada.getNombreEntrada());
		}
		return resultado.toString();
	}

	
	/**
	 * Valida si un parametro permite agregar items en el formulario
	 * @param entrada Entrada sobre la cual evaluar
	 * @return true si permite agregar items
	 */
	public boolean validarParametroNuevoItemLista(Entrada entrada) {
		boolean result = false;
		Parametro param = parametrosControl.buscarParametroPorId(entrada.getParametroOpcionesId());
		if (param.isPermiteAgregarFormulario()) {
			result = true;
		}
		return result;
	}

	
	/**
	 * Agrega un nuevo item a la lista de parametros
	 * @param entrada Entrada sobre la cual operar
	 */
	public void agregarNuevoItemListaParametro(Entrada entrada) {
		parametroAgregar = parametrosControl.buscarParametroPorId(entrada.getParametroOpcionesId());

		// Encuentra el maximo id para asignar a la nueva opción
		int maxid = 0;
		for (JsonOpcion opcion : parametroAgregar.getOpcionesJson()) {
			if (opcion.getId() > maxid) {
				maxid = opcion.getId();
			}
		}

		parametroNuevaOpcion = new JsonOpcion();
		parametroNuevaOpcion.setId(maxid + 1);
		parametroNuevaOpcion.setActivo(true);
		parametroNuevaOpcion.setNuevo(true);
		if (parametroNuevaOpcion.getDatosAdicionales() == null) {
			parametroNuevaOpcion.setDatosAdicionales(new LinkedHashMap<Integer, String>());
		}

		for (JsonDatosOpcionesParametro dato : parametroAgregar.getJsonDatosOpcionesParametro()) {
			if(dato.isActivo()) {
				parametroNuevaOpcion.getDatosAdicionales().put(dato.getId(), null);
			}
		}
		parametroAgregar.getOpcionesJson().add(parametroNuevaOpcion);

		tituloPopupAgregarListaParametros = mensajes.getString("boton_agregar");
		PrimeFaces.current().executeScript("PF('dialogolistaparametro').show();");
	}
	
	
	/**
	 * Edita los datos de un item de lista de parametro
	 * @param valorEntrada Entrada sobre la cual editar el item
	 */
	public void editarItemListaParametro(ValorEntrada valorEntrada) {
		parametroAgregar = parametrosControl.buscarParametroPorId(valorEntrada.getEntrada().getParametroOpcionesId());

		for (JsonOpcion opcion : parametroAgregar.getOpcionesJson()) {
			if(String.valueOf(opcion.getId()).equals(valorEntrada.getValor())) {
				parametroNuevaOpcion = opcion;
				break;
			}
		}

		if(parametroNuevaOpcion!=null) {
			if(parametroNuevaOpcion.getDatosAdicionales()==null){
				parametroNuevaOpcion.setDatosAdicionales(new LinkedHashMap<Integer, String>());
			}

			// agrega nuevos datos que se hayan podido incluir como datos adicionales
			for(JsonDatosOpcionesParametro dato:parametroAgregar.getJsonDatosOpcionesParametro()) {
				if(dato.isActivo() && !parametroNuevaOpcion.getDatosAdicionales().containsKey(dato.getId())){
					parametroNuevaOpcion.getDatosAdicionales().put(dato.getId(), null);
				}
			}
		}

		tituloPopupAgregarListaParametros = mensajes.getString("boton_modificar");
		PrimeFaces.current().executeScript("PF('dialogolistaparametro').show();");
	}

	
	/**
	 * Obtiene lista de datos adicionales de un parámetro. Usado al agregar un nuevo dato a una lista predefinida
	 * @return Lista con los datos adicionales del parametro
	 */
	public List<Integer> getListaDatosAdicionales() {
		List<Integer> datos = new ArrayList<>();
		if(parametroNuevaOpcion!=null && parametroNuevaOpcion.getDatosAdicionales()!=null) {
			for(Integer id:parametroNuevaOpcion.getDatosAdicionales().keySet()) {
				for(JsonDatosOpcionesParametro dato:parametroAgregar.getJsonDatosOpcionesParametro()) {
					if(dato.getId()==id && dato.isActivo()) {
						datos.add(id);
		    			break;
					}
				}
			}
		}
		return datos;
	}


	/**
	 * Guarda el nuevo parametro de tipo lista
	 */
	public void guardarOpcionParametro() {
		try {
			boolean ok = true;
			for (JsonOpcion opcion : parametroAgregar.getOpcionesJson()) {
				if (opcion.isActivo() && opcion.getId() != parametroNuevaOpcion.getId()) {
					if (parametroAgregar.isOpcionesClaveValor() && opcion.getClave()!=null && opcion.getClave().trim().equalsIgnoreCase(parametroNuevaOpcion.getClave().trim())) {
						ok = false;
					}
					if (opcion.getNombre()!=null && opcion.getNombre().trim().equalsIgnoreCase(parametroNuevaOpcion.getNombre().trim())) {
						ok = false;
					}
				}
			}

			if (ok) {
				parametrosControl.guardarParametro(parametroAgregar, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());

				// Refresca lista en las entradas y asigna valor por defecto el nuevo
				for (ValorEntrada entrada : entradas) {
					if (entrada.getEntrada().getParametroOpcionesId() != null && entrada.getEntrada().getParametroOpcionesId() == parametroAgregar.getId()) {
						getListaOpcionesEntrada(entrada.getEntrada());
						entrada.setValor(parametroNuevaOpcion.getId() + "");
						break;
					}
				}

				String message = mensajes.getString("mensaje_actualizarparametro");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
				PrimeFaces.current().executeScript("PF('dialogolistaparametro').hide();");
			} else {
				String message = mensajes.getString("mensaje_parametroitemlistaexiste");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		} catch (Exception e) {
			logger.error("Error guardar", e);
			String message = mensajes.getString("error") + ": " + e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Cierra la opción de un nuevo parametro de tipo lista
	 */
	public void cerrarOpcionParametro() {
		try {
			parametroAgregar = null;
			parametroNuevaOpcion = null;
		} catch (Exception e) {
			logger.error("Error cerrar", e);
			String message = mensajes.getString("error") + ": " + e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Obtiene el nombre de un dato adicional de un parámetro
	 * 
	 * @param id Identificador de la opción del dato
	 * @return Cadena con el nombre del dato solicitado
	 */
	public String getNombreDato(int id) {
		String nombre = "";
		for (JsonDatosOpcionesParametro dato : parametroAgregar.getJsonDatosOpcionesParametro()) {
			if (dato.getId() == id) {
				nombre = dato.getNombreDato();
				break;
			}
		}
		return nombre;
	}

	
	/**
	 * Obtiene el tipo de un dato adicional de un parámetro
	 * 
	 * @param id Identificador de la opción del dato
	 * @return Cadena con el tipo del dato solicitado
	 */
	public String getTipoDato(int id) {
		String nombre = "";
		for (JsonDatosOpcionesParametro dato : parametroAgregar.getJsonDatosOpcionesParametro()) {
			if (dato.getId() == id) {
				nombre = dato.getTipoDato();
				break;
			}
		}
		if(nombre==null || nombre.isEmpty()) {
			nombre = "TEXT";
		}
		return nombre;
	}

	
	/**
	 * Obtiene la longitud de un dato adicional de un parámetro
	 * 
	 * @param id Identificador de la opción del dato
	 * @return Longitud del dato solicitado
	 */
	public int getLongitudDato(int id) {
		int nombre = 0;
		try {
			for (JsonDatosOpcionesParametro dato : parametroAgregar.getJsonDatosOpcionesParametro()) {
				if (dato.getId() == id) {
					nombre = Integer.parseInt(dato.getLongitud());
					break;
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return nombre;
	}

	
	/**
	 * Obtiene una lista definitiva de entradas ocultando las que no se deben
	 * mostrar.  Para entradas diferentes a TextArea
	 * 
	 * @return Lista de entradas procesada
	 */
	public List<ValorEntrada> getListaEntradas() {
		List<ValorEntrada> result = new ArrayList<>();
		for (ValorEntrada entrada : entradas) {
			if (!entrada.getEntrada().isEntradaOculta() && entrada.isMostrar() && !entrada.getEntrada().getTipoEntrada().equals(TiposEntrada.TEXTAREA.name())) {
				result.add(entrada);
			}
		}
		return result;
	}
	
	
	/**
	 * Obtiene una lista definitiva de entradas ocultando las que no se deben
	 * mostrar.  Solamente para entradas tipo TextArea
	 * 
	 * @return Lista de entradas
	 */
	public List<ValorEntrada> getListaEntradasTextArea() {
		List<ValorEntrada> result = new ArrayList<>();
		for (ValorEntrada entrada : entradas) {
			if (!entrada.getEntrada().isEntradaOculta() && entrada.isMostrar() && entrada.getEntrada().getTipoEntrada().equals(TiposEntrada.TEXTAREA.name())) {
				result.add(entrada);
			}
		}
		return result;
	}

	
	/**
	 * Cambia opcion de mostrar sección cargar archivo envios
	 */
	public void mostrarCargaEnvios() {
		mostrarSeccionCargarEnvios = !mostrarSeccionCargarEnvios;
	}

	
	/**
	 * Cargar el archivo de envios 
	 * @param event Evento con el archivo cargado
	 */
	public void cargarArchivoEnvios(FileUploadEvent event) {
		filtroConsecutivosEE = new ArrayList<>();
		if(event.getFile()!=null) {
			BufferedReader csvReader = null;
			try {
				csvReader = new BufferedReader(new InputStreamReader(event.getFile().getInputstream()));
				String row = null;
				Map<String, String[]> datos = new HashMap<>();
				int index = 0;
				while ((row = csvReader.readLine()) != null) {
					String[] fila = row.split(";");
					if(index++==0 && fila.length != 31) {
						throw new CorrespondenciaException("error_formatoarchivo", "");
					} else {
						if(fila.length>20) {
							for(int i=0;i<fila.length;i++) {
								if(fila[i]!=null && fila[i].startsWith("\"") && fila[i].endsWith("\"")){
									fila[i] = fila[i].substring(1, fila[i].length()-1);
								}
							}
							
							if(fila[20]!=null) {
								datos.put(fila[20], fila);
								if(index>1) {
									filtroConsecutivosEE.add(fila[20]);
								}
							}
						}
					}
				}
				csvReader.close();
				
				List<PasoEjecutado> listaReporte = establecerFiltrosConsulta(0, 0, null, null, 2);

				int actualizados = 0;
				for (PasoEjecutado ti : listaReporte) {
					// Solamente actualiza el estado de los radicados que se encuentren en la etapa ENVÍO GENERADO
					if(ti.getPaso().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ETAPA_ENVIO_GENERADO)) {
						// Busca los valores de las entradas de pasos anteriores
						List<ValorEntrada> listaEntradas = new ArrayList<>();
						List<PasoEjecutado> pasos = procesoControl.buscarPasosEjecutadosAnteriores(ti.getProcesoEjecutado().getId());
						for (PasoEjecutado anterior : pasos) {
							List<ValorEntrada> entradasAnteriores = procesoControl.buscarValoresEntradas(anterior.getId());
							listaEntradas.addAll(entradasAnteriores);
						}
	
						String consecutivoee = "";
						String guia = "";
						String fechaenvio = "";
						for (ValorEntrada value : listaEntradas) {
							if (value.getEntrada().getNombre() != null && !value.getEntrada().getNombre().isEmpty()) {
								if(value.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_CONSECUTIVOEE)) {
									consecutivoee = value.getValor();
								} else if(value.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_FECHAENVIO472)) {
									fechaenvio = value.getValor();
								}
							}
							
							if(!consecutivoee.isEmpty() && !fechaenvio.isEmpty()) {
								break;
							}
						}
	
						
						if(consecutivoee!=null && !consecutivoee.isEmpty()) {
							String[] fila = datos.get(consecutivoee);
							if(fila!=null) {
								boolean devuelto = false;
								List<ValorEntrada> entradasPasoEjecutado = buscarEntradas(ti.getPaso().getId());
								for(ValorEntrada valorEntrada:entradasPasoEjecutado) {
									if(valorEntrada.getEntrada().getNombre()!=null && valorEntrada.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_FECHAGUIA472)) {
										valorEntrada.setValor(formatoFecha.format(Calendar.getInstance().getTime()));
									} else if(valorEntrada.getEntrada().getNombre()!=null && valorEntrada.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_NUMEROGUIA) && fila[1] != null && !fila[1].isEmpty()) {
										valorEntrada.setValor(fila[1]);
										guia = fila[1];
									} else if(valorEntrada.getEntrada().getNombre()!=null && valorEntrada.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_CAUSARECHAZO) && fila.length > 30 && fila[30] != null && !fila[30].isEmpty()) {
										valorEntrada.setValor(fila[30]);
										devuelto = true;
									}
								}
								
								// Coloca check de devuelto
								for(ValorEntrada valorEntrada:entradasPasoEjecutado) {
									if(valorEntrada.getEntrada().getNombre()!=null && valorEntrada.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_ENVIODEVUELTO)) {
										if(devuelto) {
											valorEntrada.setValor("2");
										} else {
											valorEntrada.setValor("1");
										}
										break;
									}
								}
								
								procesoControl.ejecutarPasoProceso(gestorSesionBean.getIntegracionUsuarios(), ti, ti.getProcesoEjecutado().getProceso().getId(), entradasPasoEjecutado, gestorSesionBean.getUsuarioActual(), gestorSesionBean.getDireccionIp(), 1, gestorSesionBean.getUsuarioActual().getOficina().getCirculo());

								// Envía notificar correspondencia solo para este proceso, solo se notifican los recibidos
								if(!devuelto && ti.getProcesoEjecutado().getProceso().getNombreClave().equals(ConstantesCorrespondencia.NOMBRE_PROCESO_GENERACION_EE)) {
									procesoControl.enviarNotificarCorrespondencia(gestorSesionBean.getIntegracionCatalogos(), consecutivoee, guia, fechaenvio);
								}
								actualizados++;
							}
						}
					}
				}
				String message = event.getFile().getFileName() + " procesado. "+actualizados+" registros actualizados.";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

			} catch(Exception e) {
				String message = e.getLocalizedMessage();
				logger.error(message, e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		}
	}
	
	
	/**
	 * Obtiene la etiqueta de comentarios u observaciones dependiendo si es requerido o no
	 * @return Cadena con el mensaje de etiqueta
	 */
	public String getEtiquetaComentarios() {
		String etiqueta = "";
		if(campoObservacionesRequerido) {
			etiqueta = mensajes.getString("etiqueta_requerido")+" ";
		}
		etiqueta += mensajes.getString("etiqueta_proceso_comentarios");
		return etiqueta;
	}
	
	
	
	/**
	 * Permite consultar los documentos asociados al radicado
	 */
	public void consultarArchivosOWCC() {
		if(data.getProcesoEjecutado()!=null && data.getProcesoEjecutado().getSecuenciaProceso()!=null && !data.getProcesoEjecutado().getSecuenciaProceso().isEmpty()) {
			TipoSalidaConsultar salidaConsultar = integracionOWCC.busquedaDocumentosPorRadicado(data.getProcesoEjecutado().getSecuenciaProceso(), integracionOWCC.getIntegracionCatalogos().getParametrosTO().getParamOwccParamRepositorioValueFinal());
			if(salidaConsultar!=null && salidaConsultar.getDocumentos()!=null) {
				documentosVisualizar = new ArrayList<>();
				List<TipoDocumento> documentos = salidaConsultar.getDocumentos().getDocumento();
				for(TipoDocumento documento:documentos) {
					logger.info("docid "+documento.getDID());
					logger.info("docName "+documento.getDocName());
					if(documento.getUrlVisor()!=null) {
						logger.info("urlVisor "+documento.getUrlVisor());
					}

					NodoTO nodo = new NodoTO();
					nodo.setId(documento.getDID());
					nodo.setText(documento.getDocName());
					nodo.setClave(documento.getUrlVisor());
					documentosVisualizar.add(nodo);
				}
			}
		}
		PrimeFaces.current().executeScript("PF('dialogovisualizardocumentos').show();");
	}

	
	/**
	 * Obtiene el valor del atributo logger
	 * @return El valor del atributo logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Establece el valor del atributo logger
	 * @param logger con el valor del atributo logger a establecer
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Obtiene el valor del atributo gestorSesionBean
	 * @return El valor del atributo gestorSesionBean
	 */
	public GestorSesionBean getGestorSesionBean() {
		return gestorSesionBean;
	}

	/**
	 * Establece el valor del atributo gestorSesionBean
	 * @param gestorSesionBean con el valor del atributo gestorSesionBean a establecer
	 */
	public void setGestorSesionBean(GestorSesionBean gestorSesionBean) {
		this.gestorSesionBean = gestorSesionBean;
	}

	/**
	 * Obtiene el valor del atributo lista
	 * @return El valor del atributo lista
	 */
	public LazyDataModel<PasoEjecutado> getLista() {
		return lista;
	}

	/**
	 * Establece el valor del atributo lista
	 * @param lista con el valor del atributo lista a establecer
	 */
	public void setLista(LazyDataModel<PasoEjecutado> lista) {
		this.lista = lista;
	}

	/**
	 * Obtiene el valor del atributo mostrarFiltrosAdicionales
	 * @return El valor del atributo mostrarFiltrosAdicionales
	 */
	public boolean isMostrarFiltrosAdicionales() {
		return mostrarFiltrosAdicionales;
	}

	/**
	 * Establece el valor del atributo mostrarFiltrosAdicionales
	 * @param mostrarFiltrosAdicionales con el valor del atributo mostrarFiltrosAdicionales a establecer
	 */
	public void setMostrarFiltrosAdicionales(boolean mostrarFiltrosAdicionales) {
		this.mostrarFiltrosAdicionales = mostrarFiltrosAdicionales;
	}

	/**
	 * Obtiene el valor del atributo filtro
	 * @return El valor del atributo filtro
	 */
	public String getFiltro() {
		return filtro;
	}

	/**
	 * Establece el valor del atributo filtro
	 * @param filtro con el valor del atributo filtro a establecer
	 */
	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	/**
	 * Obtiene el valor del atributo filtroActive
	 * @return El valor del atributo filtroActive
	 */
	public Boolean getFiltroActive() {
		return filtroActive;
	}

	/**
	 * Establece el valor del atributo filtroActive
	 * @param filtroActive con el valor del atributo filtroActive a establecer
	 */
	public void setFiltroActive(Boolean filtroActive) {
		this.filtroActive = filtroActive;
	}

	/**
	 * Obtiene el valor del atributo filtroPorUsuario
	 * @return El valor del atributo filtroPorUsuario
	 */
	public SugerenciaTO getFiltroPorUsuario() {
		return filtroPorUsuario;
	}

	/**
	 * Establece el valor del atributo filtroPorUsuario
	 * @param filtroPorUsuario con el valor del atributo filtroPorUsuario a establecer
	 */
	public void setFiltroPorUsuario(SugerenciaTO filtroPorUsuario) {
		this.filtroPorUsuario = filtroPorUsuario;
	}

	/**
	 * Obtiene el valor del atributo filtroTipoConsulta
	 * @return El valor del atributo filtroTipoConsulta
	 */
	public int getFiltroTipoConsulta() {
		return filtroTipoConsulta;
	}

	/**
	 * Establece el valor del atributo filtroTipoConsulta
	 * @param filtroTipoConsulta con el valor del atributo filtroTipoConsulta a establecer
	 */
	public void setFiltroTipoConsulta(int filtroTipoConsulta) {
		this.filtroTipoConsulta = filtroTipoConsulta;
	}

	/**
	 * Obtiene el valor del atributo filtroSecuencia
	 * @return El valor del atributo filtroSecuencia
	 */
	public String getFiltroSecuencia() {
		return filtroSecuencia;
	}

	/**
	 * Establece el valor del atributo filtroSecuencia
	 * @param filtroSecuencia con el valor del atributo filtroSecuencia a establecer
	 */
	public void setFiltroSecuencia(String filtroSecuencia) {
		this.filtroSecuencia = filtroSecuencia;
	}

	/**
	 * Obtiene el valor del atributo filtroSecuenciaIni
	 * @return El valor del atributo filtroSecuenciaIni
	 */
	public String getFiltroSecuenciaIni() {
		return filtroSecuenciaIni;
	}

	/**
	 * Establece el valor del atributo filtroSecuenciaIni
	 * @param filtroSecuenciaIni con el valor del atributo filtroSecuenciaIni a establecer
	 */
	public void setFiltroSecuenciaIni(String filtroSecuenciaIni) {
		this.filtroSecuenciaIni = filtroSecuenciaIni;
	}

	/**
	 * Obtiene el valor del atributo filtroSecuenciaFin
	 * @return El valor del atributo filtroSecuenciaFin
	 */
	public String getFiltroSecuenciaFin() {
		return filtroSecuenciaFin;
	}

	/**
	 * Establece el valor del atributo filtroSecuenciaFin
	 * @param filtroSecuenciaFin con el valor del atributo filtroSecuenciaFin a establecer
	 */
	public void setFiltroSecuenciaFin(String filtroSecuenciaFin) {
		this.filtroSecuenciaFin = filtroSecuenciaFin;
	}

	/**
	 * Obtiene el valor del atributo mensajes
	 * @return El valor del atributo mensajes
	 */
	public ResourceBundle getMensajes() {
		return mensajes;
	}

	/**
	 * Establece el valor del atributo mensajes
	 * @param mensajes con el valor del atributo mensajes a establecer
	 */
	public void setMensajes(ResourceBundle mensajes) {
		this.mensajes = mensajes;
	}

	/**
	 * Obtiene el valor del atributo data
	 * @return El valor del atributo data
	 */
	public PasoEjecutado getData() {
		return data;
	}

	/**
	 * Establece el valor del atributo data
	 * @param data con el valor del atributo data a establecer
	 */
	public void setData(PasoEjecutado data) {
		this.data = data;
	}

	/**
	 * Obtiene el valor del atributo pasosAnteriores
	 * @return El valor del atributo pasosAnteriores
	 */
	public List<PasoEjecutado> getPasosAnteriores() {
		return pasosAnteriores;
	}

	/**
	 * Establece el valor del atributo pasosAnteriores
	 * @param pasosAnteriores con el valor del atributo pasosAnteriores a establecer
	 */
	public void setPasosAnteriores(List<PasoEjecutado> pasosAnteriores) {
		this.pasosAnteriores = pasosAnteriores;
	}

	/**
	 * Obtiene el valor del atributo proximosPasos
	 * @return El valor del atributo proximosPasos
	 */
	public List<Paso> getProximosPasos() {
		return proximosPasos;
	}

	/**
	 * Establece el valor del atributo proximosPasos
	 * @param proximosPasos con el valor del atributo proximosPasos a establecer
	 */
	public void setProximosPasos(List<Paso> proximosPasos) {
		this.proximosPasos = proximosPasos;
	}

	/**
	 * Obtiene el valor del atributo proximoPaso
	 * @return El valor del atributo proximoPaso
	 */
	public Paso getProximoPaso() {
		return proximoPaso;
	}

	/**
	 * Establece el valor del atributo proximoPaso
	 * @param proximoPaso con el valor del atributo proximoPaso a establecer
	 */
	public void setProximoPaso(Paso proximoPaso) {
		this.proximoPaso = proximoPaso;
	}

	/**
	 * Obtiene el valor del atributo entradas
	 * @return El valor del atributo entradas
	 */
	public List<ValorEntrada> getEntradas() {
		return entradas;
	}

	/**
	 * Establece el valor del atributo entradas
	 * @param entradas con el valor del atributo entradas a establecer
	 */
	public void setEntradas(List<ValorEntrada> entradas) {
		this.entradas = entradas;
	}

	/**
	 * Obtiene el valor del atributo procesos
	 * @return El valor del atributo procesos
	 */
	public List<Proceso> getProcesos() {
		return procesos;
	}

	/**
	 * Establece el valor del atributo procesos
	 * @param procesos con el valor del atributo procesos a establecer
	 */
	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	/**
	 * Obtiene el valor del atributo procesoSeleccionado
	 * @return El valor del atributo procesoSeleccionado
	 */
	public long getProcesoSeleccionado() {
		return procesoSeleccionado;
	}

	/**
	 * Establece el valor del atributo procesoSeleccionado
	 * @param procesoSeleccionado con el valor del atributo procesoSeleccionado a establecer
	 */
	public void setProcesoSeleccionado(long procesoSeleccionado) {
		this.procesoSeleccionado = procesoSeleccionado;
	}

	/**
	 * Obtiene el valor del atributo categorias
	 * @return El valor del atributo categorias
	 */
	public List<Parametro> getCategorias() {
		return categorias;
	}

	/**
	 * Establece el valor del atributo categorias
	 * @param categorias con el valor del atributo categorias a establecer
	 */
	public void setCategorias(List<Parametro> categorias) {
		this.categorias = categorias;
	}

	/**
	 * Obtiene el valor del atributo categoriasSeleccionadas
	 * @return El valor del atributo categoriasSeleccionadas
	 */
	public Long getCategoriasSeleccionadas() {
		return categoriasSeleccionadas;
	}

	/**
	 * Establece el valor del atributo categoriasSeleccionadas
	 * @param categoriasSeleccionadas con el valor del atributo categoriasSeleccionadas a establecer
	 */
	public void setCategoriasSeleccionadas(Long categoriasSeleccionadas) {
		this.categoriasSeleccionadas = categoriasSeleccionadas;
	}

	/**
	 * Obtiene el valor del atributo filtroCategoria
	 * @return El valor del atributo filtroCategoria
	 */
	public long getFiltroCategoria() {
		return filtroCategoria;
	}

	/**
	 * Establece el valor del atributo filtroCategoria
	 * @param filtroCategoria con el valor del atributo filtroCategoria a establecer
	 */
	public void setFiltroCategoria(long filtroCategoria) {
		this.filtroCategoria = filtroCategoria;
	}

	/**
	 * Obtiene el valor del atributo nivelAcceso
	 * @return El valor del atributo nivelAcceso
	 */
	public int getNivelAcceso() {
		return nivelAcceso;
	}

	/**
	 * Establece el valor del atributo nivelAcceso
	 * @param nivelAcceso con el valor del atributo nivelAcceso a establecer
	 */
	public void setNivelAcceso(int nivelAcceso) {
		this.nivelAcceso = nivelAcceso;
	}

	/**
	 * Obtiene el valor del atributo fechaActual
	 * @return El valor del atributo fechaActual
	 */
	public Date getFechaActual() {
		return fechaActual;
	}

	/**
	 * Establece el valor del atributo fechaActual
	 * @param fechaActual con el valor del atributo fechaActual a establecer
	 */
	public void setFechaActual(Date fechaActual) {
		this.fechaActual = fechaActual;
	}

	/**
	 * Obtiene el valor del atributo deshabilitaSiguientePaso
	 * @return El valor del atributo deshabilitaSiguientePaso
	 */
	public boolean isDeshabilitaSiguientePaso() {
		return deshabilitaSiguientePaso;
	}

	/**
	 * Establece el valor del atributo deshabilitaSiguientePaso
	 * @param deshabilitaSiguientePaso con el valor del atributo deshabilitaSiguientePaso a establecer
	 */
	public void setDeshabilitaSiguientePaso(boolean deshabilitaSiguientePaso) {
		this.deshabilitaSiguientePaso = deshabilitaSiguientePaso;
	}

	/**
	 * Obtiene el valor del atributo permitirEjecutarPaso
	 * @return El valor del atributo permitirEjecutarPaso
	 */
	public boolean isPermitirEjecutarPaso() {
		return permitirEjecutarPaso;
	}

	/**
	 * Establece el valor del atributo permitirEjecutarPaso
	 * @param permitirEjecutarPaso con el valor del atributo permitirEjecutarPaso a establecer
	 */
	public void setPermitirEjecutarPaso(boolean permitirEjecutarPaso) {
		this.permitirEjecutarPaso = permitirEjecutarPaso;
	}

	/**
	 * Obtiene el valor del atributo mostrarGrillaEnvios
	 * @return El valor del atributo mostrarGrillaEnvios
	 */
	public boolean isMostrarGrillaEnvios() {
		return mostrarGrillaEnvios;
	}

	/**
	 * Establece el valor del atributo mostrarGrillaEnvios
	 * @param mostrarGrillaEnvios con el valor del atributo mostrarGrillaEnvios a establecer
	 */
	public void setMostrarGrillaEnvios(boolean mostrarGrillaEnvios) {
		this.mostrarGrillaEnvios = mostrarGrillaEnvios;
	}

	/**
	 * Obtiene el valor del atributo mostrarSeccionCargarEnvios
	 * @return El valor del atributo mostrarSeccionCargarEnvios
	 */
	public boolean isMostrarSeccionCargarEnvios() {
		return mostrarSeccionCargarEnvios;
	}

	/**
	 * Establece el valor del atributo mostrarSeccionCargarEnvios
	 * @param mostrarSeccionCargarEnvios con el valor del atributo mostrarSeccionCargarEnvios a establecer
	 */
	public void setMostrarSeccionCargarEnvios(boolean mostrarSeccionCargarEnvios) {
		this.mostrarSeccionCargarEnvios = mostrarSeccionCargarEnvios;
	}

	/**
	 * Obtiene el valor del atributo pasosEnviosSeleccionados
	 * @return El valor del atributo pasosEnviosSeleccionados
	 */
	public List<Long> getPasosEnviosSeleccionados() {
		return pasosEnviosSeleccionados;
	}

	/**
	 * Establece el valor del atributo pasosEnviosSeleccionados
	 * @param pasosEnviosSeleccionados con el valor del atributo pasosEnviosSeleccionados a establecer
	 */
	public void setPasosEnviosSeleccionados(List<Long> pasosEnviosSeleccionados) {
		this.pasosEnviosSeleccionados = pasosEnviosSeleccionados;
	}

	/**
	 * Obtiene el valor del atributo archivoExportar
	 * @return El valor del atributo archivoExportar
	 */
	public StreamedContent getArchivoExportar() {
		return archivoExportar;
	}

	/**
	 * Establece el valor del atributo archivoExportar
	 * @param archivoExportar con el valor del atributo archivoExportar a establecer
	 */
	public void setArchivoExportar(StreamedContent archivoExportar) {
		this.archivoExportar = archivoExportar;
	}

	/**
	 * Obtiene el valor del atributo idValorEntradaSeleccionada
	 * @return El valor del atributo idValorEntradaSeleccionada
	 */
	public long getIdValorEntradaSeleccionada() {
		return idValorEntradaSeleccionada;
	}

	/**
	 * Establece el valor del atributo idValorEntradaSeleccionada
	 * @param idValorEntradaSeleccionada con el valor del atributo idValorEntradaSeleccionada a establecer
	 */
	public void setIdValorEntradaSeleccionada(long idValorEntradaSeleccionada) {
		this.idValorEntradaSeleccionada = idValorEntradaSeleccionada;
	}

	/**
	 * Obtiene el valor del atributo usuariosAsignados
	 * @return El valor del atributo usuariosAsignados
	 */
	public String[] getUsuariosAsignados() {
		return usuariosAsignados;
	}

	/**
	 * Establece el valor del atributo usuariosAsignados
	 * @param usuariosAsignados con el valor del atributo usuariosAsignados a establecer
	 */
	public void setUsuariosAsignados(String[] usuariosAsignados) {
		this.usuariosAsignados = usuariosAsignados;
	}

	/**
	 * Obtiene el valor del atributo gruposAsignados
	 * @return El valor del atributo gruposAsignados
	 */
	public String[] getGruposAsignados() {
		return gruposAsignados;
	}

	/**
	 * Establece el valor del atributo gruposAsignados
	 * @param gruposAsignados con el valor del atributo gruposAsignados a establecer
	 */
	public void setGruposAsignados(String[] gruposAsignados) {
		this.gruposAsignados = gruposAsignados;
	}

	/**
	 * Obtiene el valor del atributo plantillas
	 * @return El valor del atributo plantillas
	 */
	public List<Parametro> getPlantillas() {
		return plantillas;
	}

	/**
	 * Establece el valor del atributo plantillas
	 * @param plantillas con el valor del atributo plantillas a establecer
	 */
	public void setPlantillas(List<Parametro> plantillas) {
		this.plantillas = plantillas;
	}

	/**
	 * Obtiene el valor del atributo parametroAgregar
	 * @return El valor del atributo parametroAgregar
	 */
	public Parametro getParametroAgregar() {
		return parametroAgregar;
	}

	/**
	 * Establece el valor del atributo parametroAgregar
	 * @param parametroAgregar con el valor del atributo parametroAgregar a establecer
	 */
	public void setParametroAgregar(Parametro parametroAgregar) {
		this.parametroAgregar = parametroAgregar;
	}

	/**
	 * Obtiene el valor del atributo parametroNuevaOpcion
	 * @return El valor del atributo parametroNuevaOpcion
	 */
	public JsonOpcion getParametroNuevaOpcion() {
		return parametroNuevaOpcion;
	}

	/**
	 * Establece el valor del atributo parametroNuevaOpcion
	 * @param parametroNuevaOpcion con el valor del atributo parametroNuevaOpcion a establecer
	 */
	public void setParametroNuevaOpcion(JsonOpcion parametroNuevaOpcion) {
		this.parametroNuevaOpcion = parametroNuevaOpcion;
	}

	/**
	 * Obtiene el valor del atributo campoObservacionesRequerido
	 * @return El valor del atributo campoObservacionesRequerido
	 */
	public boolean isCampoObservacionesRequerido() {
		return campoObservacionesRequerido;
	}

	/**
	 * Establece el valor del atributo campoObservacionesRequerido
	 * @param campoObservacionesRequerido con el valor del atributo campoObservacionesRequerido a establecer
	 */
	public void setCampoObservacionesRequerido(boolean campoObservacionesRequerido) {
		this.campoObservacionesRequerido = campoObservacionesRequerido;
	}

	/**
	 * Obtiene el valor del atributo urlOracleCapture
	 * @return El valor del atributo urlOracleCapture
	 */
	public String getUrlOracleCapture() {
		return urlOracleCapture;
	}

	/**
	 * Establece el valor del atributo urlOracleCapture
	 * @param urlOracleCapture con el valor del atributo urlOracleCapture a establecer
	 */
	public void setUrlOracleCapture(String urlOracleCapture) {
		this.urlOracleCapture = urlOracleCapture;
	}

	/**
	 * Obtiene el valor del atributo tituloPopupAgregarListaParametros
	 * @return El valor del atributo tituloPopupAgregarListaParametros
	 */
	public String getTituloPopupAgregarListaParametros() {
		return tituloPopupAgregarListaParametros;
	}

	/**
	 * Establece el valor del atributo tituloPopupAgregarListaParametros
	 * @param tituloPopupAgregarListaParametros con el valor del atributo tituloPopupAgregarListaParametros a establecer
	 */
	public void setTituloPopupAgregarListaParametros(String tituloPopupAgregarListaParametros) {
		this.tituloPopupAgregarListaParametros = tituloPopupAgregarListaParametros;
	}

	/**
	 * Obtiene el valor del atributo mostrarExportarDistribuidor
	 * @return El valor del atributo mostrarExportarDistribuidor
	 */
	public boolean isMostrarExportarDistribuidor() {
		return mostrarExportarDistribuidor;
	}

	/**
	 * Establece el valor del atributo mostrarExportarDistribuidor
	 * @param mostrarExportarDistribuidor con el valor del atributo mostrarExportarDistribuidor a establecer
	 */
	public void setMostrarExportarDistribuidor(boolean mostrarExportarDistribuidor) {
		this.mostrarExportarDistribuidor = mostrarExportarDistribuidor;
	}

	/**
	 * Obtiene el valor del atributo documentosVisualizar
	 * @return El valor del atributo documentosVisualizar
	 */
	public List<NodoTO> getDocumentosVisualizar() {
		return documentosVisualizar;
	}

	/**
	 * Establece el valor del atributo documentosVisualizar
	 * @param documentosVisualizar con el valor del atributo documentosVisualizar a establecer
	 */
	public void setDocumentosVisualizar(List<NodoTO> documentosVisualizar) {
		this.documentosVisualizar = documentosVisualizar;
	}

	/**
	 * Obtiene el valor del atributo mostrarBotonDigitalizar
	 * @return El valor del atributo mostrarBotonDigitalizar
	 */
	public boolean isMostrarBotonDigitalizar() {
		return mostrarBotonDigitalizar;
	}

	/**
	 * Establece el valor del atributo mostrarBotonDigitalizar
	 * @param mostrarBotonDigitalizar con el valor del atributo mostrarBotonDigitalizar a establecer
	 */
	public void setMostrarBotonDigitalizar(boolean mostrarBotonDigitalizar) {
		this.mostrarBotonDigitalizar = mostrarBotonDigitalizar;
	}

	/**
	 * Obtiene el valor del atributo usuarioAsignado
	 * @return El valor del atributo usuarioAsignado
	 */
	public String getUsuarioAsignado() {
		return usuarioAsignado;
	}

	/**
	 * Establece el valor del atributo usuarioAsignado
	 * @param usuarioAsignado con el valor del atributo usuarioAsignado a establecer
	 */
	public void setUsuarioAsignado(String usuarioAsignado) {
		this.usuarioAsignado = usuarioAsignado;
	}

	/**
	 * Obtiene el valor del atributo siguientePasoUnificaParalelo
	 * @return El valor del atributo siguientePasoUnificaParalelo
	 */
	public boolean isSiguientePasoUnificaParalelo() {
		return siguientePasoUnificaParalelo;
	}

	/**
	 * Establece el valor del atributo siguientePasoUnificaParalelo
	 * @param siguientePasoUnificaParalelo con el valor del atributo siguientePasoUnificaParalelo a establecer
	 */
	public void setSiguientePasoUnificaParalelo(boolean siguientePasoUnificaParalelo) {
		this.siguientePasoUnificaParalelo = siguientePasoUnificaParalelo;
	}

	/**
	 * Obtiene el valor del atributo formatoFecha
	 * @return El valor del atributo formatoFecha
	 */
	public SimpleDateFormat getFormatoFecha() {
		return formatoFecha;
	}

	/**
	 * Establece el valor del atributo formatoFecha
	 * @param formatoFecha con el valor del atributo formatoFecha a establecer
	 */
	public void setFormatoFecha(SimpleDateFormat formatoFecha) {
		this.formatoFecha = formatoFecha;
	}

	/**
	 * Obtiene el valor del atributo filtroConsecutivosEE
	 * @return El valor del atributo filtroConsecutivosEE
	 */
	public List<String> getFiltroConsecutivosEE() {
		return filtroConsecutivosEE;
	}

	/**
	 * Establece el valor del atributo filtroConsecutivosEE
	 * @param filtroConsecutivosEE con el valor del atributo filtroConsecutivosEE a establecer
	 */
	public void setFiltroConsecutivosEE(List<String> filtroConsecutivosEE) {
		this.filtroConsecutivosEE = filtroConsecutivosEE;
	}


	/**
	 * Obtiene el valor del atributo integracionOWCC
	 * @return El valor del atributo integracionOWCC
	 */
	public IntegracionOWCC getIntegracionOWCC() {
		return integracionOWCC;
	}


	/**
	 * Establece el valor del atributo integracionOWCC
	 * @param integracionOWCC con el valor del atributo integracionOWCC a establecer
	 */
	public void setIntegracionOWCC(IntegracionOWCC integracionOWCC) {
		this.integracionOWCC = integracionOWCC;
	}

}

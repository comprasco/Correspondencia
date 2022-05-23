package co.gov.supernotariado.bachue.correspondencia.bean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposParametros;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposPeriodosPasos;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposRestriccionesEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposRestriccionesValor;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.CondicionRegla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoria;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoSecuencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Regla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RestriccionEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ProcesosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.SecuenciasSingletonLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.JsonUtil;
import co.gov.supernotariado.bachue.correspondencia.exception.CorrespondenciaException;

/**
 * Managed Bean que permite controlar la adninistración de procesos 
 */
@ManagedBean
@ViewScoped
public class ProcesosBean extends GenericBean {
	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(ProcesosBean.class);

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
	 * Manejo de sesión del usuario
	 */
	@ManagedProperty("#{gestorSesionBean}")
    private GestorSesionBean gestorSesionBean;

	/**
	 * Error general para excepciones
	 */
	private static final String ERROR_GENERAL_KEY = "error_general";
	
	/**
	 * Mensaje para guardar proceso
	 */
	private static final String INFO_MENSAJE_GUARDAR_PROCESO = "mensaje_proceso_guardar";
	
	/**
	 * Lista paginada con resultados de procesos
	 */
	private LazyDataModel<Proceso> lista;
	
	/**
	 * Campo para filtrado en lista
	 */
	private String filtroGeneral = "";

	/**
	 * Properties con los mensajes localizados
	 */
	private ResourceBundle mensajes = ResourceBundle.getBundle("messages");
	
	/**
	 * Objeto proceso para controlar los datos del proceso
	 */
	private Proceso data;
	
	/**
	 * Objeto paso para los detalles del paso
	 */
	private Paso paso;

	/**
	 * Objeto entrada para los detalles de la entrada
	 */
	private Entrada entrada;

	/**
	 * Objeto regla con los detalles de la regla
	 */
	private Regla regla;

	/**
	 * Listado de pasos asociados al proceso
	 */
	private List<Paso> pasos;

	/**
	 * Listado de entradas asociadas al paso
	 */
	private List<Entrada> entradas;

	/**
	 * Listado de reglas asociadas al paso
	 */
	private List<Regla> reglas;

	/**
	 * Listado con las opciones de una entrada anidada
	 */
	private List<NodoTO> opcionesEntradasAnidadas = new ArrayList<>();

	/**
	 * Listado de Parametros tipo de proceso a asociar
	 */
	private List<Parametro> tiposProceso;
	
	/**
	 * Lista de tipos de duración de las tareas
	 */
	private List<SelectItem> tiposDuracion = new ArrayList<>();
	
	/**
	 * Tipos de entrada permitidos para las entradas
	 */
	private List<SelectItem> tiposEntrada = new ArrayList<>();

	/**
	 * Listado de parámetros de categorías
	 */
	private List<Parametro> categorias;
	
	/**
	 * Categorías seleccionadas
	 */
	private Long[] categoriasSeleccionadas;
	
	/**
	 * Listado con las secuencias del proceso
	 */
	private List<ProcesoSecuencia> secuenciaProceso = new ArrayList<>();

	/**
	 * Listado con las secuencias de la entrada seleccionada
	 */
	private List<SecuenciaEntrada> secuenciaentrada = new ArrayList<>();
	
	/** Si aplican horas laborales */
	private boolean mostrarHorasLaborales = false;
	/** Hora laboral 1 ingresada */
	private String horasLaborales1; 
	/** Hora laboral 2 ingresada */
	private String horasLaborales2;
	/** Listado de horas laborales a seleccionar */
	private List<String> listaHorasLaborales = new ArrayList<>(); 

	/** Indica si el proceso ya tiene ejecuciones.  No permite borrar si ya tiene ejecuciones */
	private boolean procesoTieneEjecuciones = false;
	
	/** Lista de listas predefinidas por parámetros */
	private List<Parametro> listaPredefinida = new ArrayList<>();
	
	/** Lista de secuencias globales disponibles a usar */
	private List<Parametro> secuenciasGlobales = new ArrayList<>();

	/** Lista de restricciones para una entrada */
	private List<RestriccionEntrada> restriccionesEntrada = new ArrayList<>();
	
	/**
	 * Tipos de restriccion de entradas
	 */
	private List<SelectItem> tiposRestriccionEntradas = new ArrayList<>();

	/**
	 * Tipos de valores de restriccion de entradas
	 */
	private List<SelectItem> tiposRestriccionValores = new ArrayList<>();

	/**
	 * Constructor
	 */
	public ProcesosBean(){
		for (int i=0;i<24;i++) {
			this.listaHorasLaborales.add(i+"");
		}
	}
	
	/**
	 * Inicializa listas y variables
	 */
	@PostConstruct
	private void init(){
		try {
			mensajes = ResourceBundle.getBundle("messages", gestorSesionBean.getLocale());

			listaPredefinida = parametrosControl.obtenerParametrosActivosPorTipo(TiposParametros.LISTA_PREDEFINIDA.name());

			secuenciasGlobales = parametrosControl.obtenerParametrosActivosPorTipo(TiposParametros.SECUENCIA_GLOBAL.name());

			this.tiposProceso = this.parametrosControl.obtenerParametrosActivosPorTipo(TiposParametros.TIPO_PROCESO.name());

			for (TiposPeriodosPasos datos:TiposPeriodosPasos.values()) {
				this.tiposDuracion.add(new SelectItem(datos, this.mensajes.getString("etiqueta_DURACIONPASOS_"+datos.name())));
			}
			for (TiposEntrada tipos:TiposEntrada.values()) {
				this.tiposEntrada.add(new SelectItem(tipos, this.mensajes.getString("etiqueta_CAMPO_"+tipos.name())));
			}
			for (TiposRestriccionesEntrada datos:TiposRestriccionesEntrada.values()) {
				this.tiposRestriccionEntradas.add(new SelectItem(datos, this.mensajes.getString("etiqueta_TIPORESTRICCIONENTRADA_"+datos.name())));
			}
			this.buscar();
		} catch(Exception e){
			logger.error("Error", e);
		}
	}
	
	/**
	 * Limpia variables
	 */
	private void resetearPaso(){
		this.paso = null;
		this.entradas = null;
		this.reglas = null;
		this.regla = null;
		this.entrada = null;
		this.mostrarHorasLaborales = false;
		this.horasLaborales1 = null;
		this.horasLaborales2 = null;
	}
	
	
	/**
	 * Elimina versiones anteriores de los procesos para eliminar datos basura
	 * Solo las elimina si no tienen procesos ejecutados 
	 */
	public void eliminaVersionesAnteriores() {
		try {
			String mensaje = procesoControl.eliminaVersionesAnterioresProceso(data);
			if(!mensaje.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, mensaje));
			} else {
				agregarMensajeFaces(FacesMessage.SEVERITY_INFO, INFO_MENSAJE_GUARDAR_PROCESO);
			}
			detalles(data);
		} catch(Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
			logger.error(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Elimina la versión actual y devuelve a la anterior si no tiene procesos ejecutados
	 * Solo las elimina si no tienen procesos ejecutados 
	 */
	public void eliminaVersionActual() {
		try {
			String mensaje = procesoControl.eliminaVersionesAnterioresProceso(data);
			if(!mensaje.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, mensaje));
			} else {
				agregarMensajeFaces(FacesMessage.SEVERITY_INFO, INFO_MENSAJE_GUARDAR_PROCESO);
			}
			detalles(data);
		} catch(Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
			logger.error(e.getMessage(), e);
		}
	}

	
	/**
	 * Borra el proceso y todas sus versiones si no tiene ejecuciones
	 */
	public void borrarProceso() {
		try {
			int numeroProcesos = procesoControl.borrarProceso(data);
			if(numeroProcesos>0) {
				String mensaje = MessageFormat.format(mensajes.getString("mensaje_proceso_eliminarprocesoversiones"), data.getId(), data.getNumeroMayorVersion(), numeroProcesos);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, mensaje));
			} else {
				volverALista();
				agregarMensajeFaces(FacesMessage.SEVERITY_INFO, "mensaje_proceso_eliminarproceso");
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	


	/**
	 * Busca un listado paginado de procesos
	 */
	public void buscar(){
    	gestorSesionBean.irAOpcion(3);
		this.resetearPaso();
		this.categoriasSeleccionadas = null;
		
    	this.lista = new LazyDataModel<Proceso>(){
            private static final long serialVersionUID = 1L;
            
            @Override
            public List<Proceso> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        		List<Proceso> listaProcesos = procesoControl.buscarProcesosPor(null, filtroGeneral, estableceOrdenConsulta(sortField, sortOrder), pageSize, first);
    			lista.setRowCount(procesoControl.contarProcesosPor(null, filtroGeneral));
            	return listaProcesos;
            }
        };

	}
	
	
	/**
	 * Permite generar una nueva version del proceso duplicando el proceso, pero dejandolo con el mismo id secundario
	 */
	public void generarNuevaVersion() {
		long versionanterior = 0;
		try {
			if(data!=null) {
				versionanterior = data.getNumeroMayorVersion();
				logger.info("Nueva version proceso:"+data.getId()+" identificador:"+data.getIdentificador()+" version:"+data.getNumeroMayorVersion());
				data = procesoControl.generarNuevoProceso(data.getId(), gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp(), true);
				detalles(data);
				resetearPaso();
				agregarMensajeFaces(FacesMessage.SEVERITY_INFO, INFO_MENSAJE_GUARDAR_PROCESO);
				logger.info("Version generada proceso:"+data.getId()+" identificador:"+data.getIdentificador()+" version:"+data.getNumeroMayorVersion());
			}
		} catch(Exception e) {
			logger.info("Error generarNuevaVersion()", e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
			// Elimina el proceso generado para dejar la version anterior
			if(versionanterior!=data.getNumeroMayorVersion()) {
				eliminarVersionActual();
			}
			volverALista();
		}
	}
	
	
	/**
	 * Elimina version actual del proceso
	 * Solo las elimina si no tienen procesos ejecutados 
	 */
	public void eliminarVersionActual() {
		try {
			if(data!=null) {
				int numeroProcesos = procesoControl.eliminarVersionActual(this.data);
			
				if(numeroProcesos>0) {
					String mensaje = MessageFormat.format(mensajes.getString("mensaje_proceso_eliminarprocesoversiones"), data.getId(), data.getNumeroMayorVersion(), numeroProcesos);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, mensaje));
				} else {
					volverALista();
					agregarMensajeFaces(FacesMessage.SEVERITY_INFO, "mensaje_proceso_eliminarversion");
				}
			}
		} catch(Exception e) {
			logger.info("Error eliminarVersionActual", e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}


	
	/**
	 * Crea los datos para un nuevo proceso
	 */
	public void crear(){
		this.data = new Proceso();
		this.data.setTipoProceso(new Parametro());
		this.data.setNumeroMayorVersion(1);
		this.data.setNombreCategoria("Categoría");
		this.data.setCategorias(new ArrayList<ProcesoCategoria>());
		procesoControl.inicializarJsonProcesoData(data);

		this.secuenciaProceso = new ArrayList<>();
    	gestorSesionBean.irAOpcion(31);
	}
	
	
	/**
	 * Obtiene todos los detalles de un proceso para su modificación
	 * @param proceso Proceso para mostrar sus detalles
	 */
	public void detalles(Proceso proceso){
    	gestorSesionBean.irAOpcion(32);
		this.data = procesoControl.buscarProcesoPorId(proceso.getId());
		
		establecerNombresUsuarios();
		
		// Evalua si el proceso tiene ya instancias creadas para mostrar mensajes de advertencia
		List<ProcesoEjecutado> procesos = procesoControl.buscarProcesosEjecutadosProceso(proceso.getId());
		if(!procesos.isEmpty()) {
			procesoTieneEjecuciones = true;
		} else {
			procesoTieneEjecuciones = false;
		}
		
		this.pasos = procesoControl.buscarPasosActivosProceso(proceso.getId());
		if(this.data.getCategorias()==null){
			this.data.setCategorias(procesoControl.buscarCategoriasProceso(proceso.getId()));
			if(!this.data.getCategorias().isEmpty()){
				this.categoriasSeleccionadas = new Long[this.data.getCategorias().size()];
				int i=0;
				for(ProcesoCategoria cat:this.data.getCategorias()){
					this.categoriasSeleccionadas[i++] = cat.getId().getCategoriaId();
				}
			}
		}

		this.secuenciaProceso = procesoControl.buscarSecuenciaProceso(proceso.getId());
	}
	
	
    /**
     * Establece los nombres de los usuarios de creación y modificación
     */
    private void establecerNombresUsuarios() {
		if(data.getIdUsuarioCreacion()!=null && !data.getIdUsuarioCreacion().isEmpty()){
			data.setUsuarioCreacion(gestorSesionBean.obtenerUsuario(data.getIdUsuarioCreacion()));
		}
		if(data.getIdUsuarioModificacion()!=null && !data.getIdUsuarioModificacion().isEmpty()){
			data.setUsuarioModificacion(gestorSesionBean.obtenerUsuario(data.getIdUsuarioModificacion()));
		}
    }

    
    /**
     * Guarda el proceso
     */
    public void guardar(){
		try{
			procesoControl.guardarProceso(data, categoriasSeleccionadas, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
			String message = mensajes.getString(INFO_MENSAJE_GUARDAR_PROCESO);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
			
			data.setCategorias(null);
			detalles(data);
			resetearPaso();
		} catch(Exception e){
			logger.error("Error guardando proceso", e);
			String message = mensajes.getString("error_proceso_guardar")+":"+e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	
	
	/**
	 * Regresa al listado de procesos
	 */
	public void volverALista(){
		this.data = null;
		this.buscar();
	}
	
	
	/**
	 * Agrega un nuevo paso a la lista
	 */
	public void agregarPaso(){
		this.paso = new Paso();
		this.paso.setDuracion(0l);
		procesoControl.inicializarJsonPasoData(paso);

		// Deja preseleccionado para ocultar el combo de Funcionarios
		this.paso.getJsonPasoData().setOcultarSeleccionUsuarios(true);
		
		this.mostrarHorasLaborales = false;
		this.horasLaborales1 = null;
		this.horasLaborales2 = null;
	}
	

	/**
	 * Edita un paso de la lista
	 * @param paso Paso a modificar
	 */
	public void editarPaso(Paso paso){
		try{
			resetearPaso();
			// Refresca info de objeto
			this.paso = procesoControl.buscarPasoPorId(paso.getId());
			
			this.mostrarHorasLaborales = false;
			this.horasLaborales1 = null;
			this.horasLaborales2 = null;
			if(this.paso.getHorarioLaboral()!=null && !this.paso.getHorarioLaboral().isEmpty()) {
				this.mostrarHorasLaborales = true;
				String[] work = this.paso.getHorarioLaboral().split("-");
				if(work.length==2) {
					this.horasLaborales1 = work[0];
					this.horasLaborales2 = work[1];
				}
			}

			this.entradas = procesoControl.buscarEntradasActivasPaso(this.paso.getId());
			
			this.buscarReglas();
			
			listenerCambiarGrupo();
			listenerCambiarUsuario();
		} catch(Exception e){
			this.cancelarAgregarPaso();
			String message = "Error: "+e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	
	
	/**
	 * Elimina un paso de la lista
	 * @param paso Paso a eliminar
	 */
	public void eliminarPaso(Paso paso){
		paso.setActivo(false);
		procesoControl.actualizarEntidad(paso);
		
		// Ajusta orden pasos
		this.pasos = procesoControl.buscarPasosActivosProceso(this.data.getId());
		long i = 1;
		for(Paso t:pasos){
			t.setOrdenPaso(i++);
			procesoControl.actualizarEntidad(t);
		}

		this.cancelarAgregarPaso();
	}

	
	/**
	 * Busca las reglas asociadas al paso actual en edición
	 */
	private void buscarReglas(){
		this.regla = null;
		this.reglas = procesoControl.buscarReglasPaso(paso.getId());
	}
	

	/**
	 * Guarda el paso en edición
	 */
	public void guardarPaso(){
		try{
			if(!this.mostrarHorasLaborales) {
				this.paso.setHorarioLaboral(null);
			} else {
				if(Integer.valueOf(this.horasLaborales1)>=Integer.valueOf(this.horasLaborales2)) {
    				String message = mensajes.getString("error_paso_rangohoras");
    				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    				this.setHorasLaborales1(null);
    				this.setHorasLaborales2(null);
    				return;
				} else {
					this.paso.setHorarioLaboral(this.horasLaborales1+"-"+this.horasLaborales2);
				}
			}
			
			procesoControl.guardarPaso(paso, data, pasos==null?0:pasos.size(), gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
			
			this.pasos = procesoControl.buscarPasosActivosProceso(data.getId());
			String message = mensajes.getString("mensaje_paso_guardar");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		} catch(Exception e){
			logger.error("Error saving proceso", e);
			String message = mensajes.getString("error_proceso_guardar")+":"+e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	
	/**
	 * Cancela el proceso de agregar un nuevo paso
	 */
	public void cancelarAgregarPaso(){
		this.resetearPaso();
	}


	/**
	 * Agrega una nueva entrada de formulario para su edición
	 */
	public void agregarEntrada(){
		entrada = new Entrada();
		procesoControl.inicializarJsonEntradaData(entrada);
		
		entrada.setLongitud(32);
		if(!tiposEntrada.isEmpty()) {
			entrada.setTipoEntrada(tiposEntrada.get(0).getValue().toString());
		}

		List<Entrada> meta = getEntradasParaEntradaAnidada();
		if(!meta.isEmpty()) {
			entrada.setEntradaAnidadaId(meta.get(0).getId());
			actualizaOpcionesEntradaAnidada(false);
		}
		
		secuenciaentrada = new ArrayList<>();
		restriccionesEntrada = new ArrayList<>();
		
	}

	/**
	 * Inicializa una entrada para su edición
	 * @param entrada Entrada a editar
	 */
	public void editarEntrada(Entrada entrada){
		// Refresca campos que pudieron modificarse temporalmente sin guardar
		this.entradas = procesoControl.buscarEntradasActivasPaso(this.paso.getId());
		this.entrada = entrada;
		
		if(this.entrada.getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())) {
			this.secuenciaentrada = procesoControl.buscarSecuenciaEntrada(entrada.getId());
		}
		
		this.tiposRestriccionValores = new ArrayList<>();
		for (String datos:TiposRestriccionesValor.obtenerValoresRestricciones(entrada.getTipoEntrada())) {
			this.tiposRestriccionValores.add(new SelectItem(datos, this.mensajes.getString("etiqueta_TIPORESTRICCIONVALORENTRADA_"+datos)));
		}

		if(this.entrada.getEntradaAnidadaId()==null) {
			List<Entrada> meta = getEntradasReglas();
			if(!meta.isEmpty()) {
				this.entrada.setEntradaAnidadaId(meta.get(0).getId());
			}
		}
		this.actualizaOpcionesEntradaAnidada(false);
		
		restriccionesEntrada = procesoControl.buscarRestriccionesEntrada(entrada.getId());
		
	}

	/**
	 * Cancela la modificación de una entrada
	 */
	public void cancelarAgregarEntrada(){
		// Refresca campos que pudieron modificarse temporalmente sin guardar
		this.entradas = procesoControl.buscarEntradasActivasPaso(this.paso.getId());
		this.entrada = null;
	}

	
	/**
	 * Guarda la entrada del formulario
	 */
	public void guardarEntrada(){
		try{
			procesoControl.guardarEntrada(entrada, paso, entradas==null?0:entradas.size(), gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());

			if(this.entrada.getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())) {
				this.guardarSecuenciaEntrada();
			}
			
			procesoControl.guardarRestriccionesEntrada(restriccionesEntrada, entrada, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());

			this.entrada = null;
			this.entradas = procesoControl.buscarEntradasActivasPaso(this.paso.getId());
			String message = mensajes.getString("mensaje_entrada_guardar");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		} catch(Exception e){
			String message = mensajes.getString("error_entrada_guardar")+":"+e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	
	
	/**
	 * Agrega un nuevo campo de opción a la entrada
	 */
	public void agregarOpcionEntrada() {
		JsonUtil.agregarCampoOpciones(entrada, null);
	}

	/**
	 * Agrega un nuevo campo de opción a la entrada cuando es tipo anidado
	 * @param node Nodo a agregar a las opciones
	 */
	public void agregarOpcionEntrada(NodoTO node) {
		JsonUtil.agregarCampoOpciones(entrada, node.getId());
	}

	/**
	 * Elimina un nuevo campo de opción a la entrada
	 * Lo marca en falso para no borrarlo definitivamente y conservar la integridad referencial del dato para procesos ya creados
	 * @param opcion Opcion a remover
	 */
	public void removerOpcionEntrada(JsonOpcion opcion) {
		opcion.setActivo(false);
	}
	
	/**
	 * Elimina una entrada, si tiene reglas asociadas se elimina la regla
	 * @param entrada Entrada a evaluar
	 */
	public void eliminarEntrada(Entrada entrada){
		try{
			procesoControl.eliminarEntrada(entrada, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());

			this.entradas = procesoControl.buscarEntradasActivasPaso(this.paso.getId());

			this.buscarReglas();
			
			agregarMensajeFaces(FacesMessage.SEVERITY_INFO, "mensaje_entrada_eliminar");
		} catch(Exception e){
			logger.error(e);
			agregarMensajeFaces(FacesMessage.SEVERITY_ERROR, "error_entrada_eliminar", e.getMessage());
		}
	}
	

	/**
	 * Afecta el orden de los pasos
	 * @param paso Paso a mover
	 * @param opcion 1 arriba 2 abajo
	 */
	public void moverPaso(Paso paso, int opcion){
		try{
			if(paso!=null){
				if(opcion==1){
					paso.setOrdenPaso(paso.getOrdenPaso()-1);
					if(paso.getOrdenPaso()<=0){
						paso.setOrdenPaso(1l);
					}
					for(Paso t:pasos){
						if(t.getId()!=paso.getId() && t.getOrdenPaso().equals(paso.getOrdenPaso())){
							t.setOrdenPaso(t.getOrdenPaso()+1);
							procesoControl.actualizarEntidad(t);
						}
					}
				} else{
					paso.setOrdenPaso(paso.getOrdenPaso()+1);
					if(paso.getOrdenPaso()>pasos.size()){
						paso.setOrdenPaso(Long.valueOf(pasos.size()));
					}
					for(Paso t:pasos){
						if(t.getId()!=paso.getId() && t.getOrdenPaso().equals(paso.getOrdenPaso())){
							t.setOrdenPaso(t.getOrdenPaso()-1);
							procesoControl.actualizarEntidad(t);
						}
					}
				}
				procesoControl.actualizarEntidad(paso);
			}
			pasos = procesoControl.buscarPasosActivosProceso(data.getId());
		} catch(Exception e){
			String message = e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Mueve el orden de la entrada hacia arriba o abajo
	 * @param entrada Entrada a mover
	 * @param opcion 1-arriba 2-abajo
	 */
	public void moverEntrada(Entrada entrada, int opcion){
		try{
			if(entrada!=null){
				if(opcion==1){
					entrada.setOrdenEntrada(entrada.getOrdenEntrada()-1);
					if(entrada.getOrdenEntrada()<=0){
						entrada.setOrdenEntrada(1);
					}
					// encuentra el siguiente elemento en la lista para afectar su orden
					for(Entrada md:this.entradas){
						if(md.getId()!=entrada.getId() && md.getOrdenEntrada()==entrada.getOrdenEntrada()){
							md.setOrdenEntrada(md.getOrdenEntrada()+1);
							procesoControl.actualizarEntidad(md);
						}
					}
				} else{

					entrada.setOrdenEntrada(entrada.getOrdenEntrada()+1);
					if(entrada.getOrdenEntrada()>this.entradas.size()){
						entrada.setOrdenEntrada(this.entradas.size());
					}
					// encuentra el siguiente elemento en la lista para afectar su orden
					for(Entrada md:this.entradas){
						if(md.getId()!=entrada.getId() && md.getOrdenEntrada()==entrada.getOrdenEntrada()){
							md.setOrdenEntrada(md.getOrdenEntrada()-1);
							procesoControl.actualizarEntidad(md);
						}
					}
				}
				procesoControl.actualizarEntidad(entrada);
			}
			this.entradas = procesoControl.buscarEntradasActivasPaso(this.paso.getId());
		} catch(Exception e){
			String message = e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}

	
	/**
	 * Listener cuando se cambia el tipo de entrada
	 */
	public void cambiarTipoEntrada(){
		if(!this.entrada.getTipoEntrada().equals(this.getEntradaEntradaValue(TiposEntrada.SELECT)) && !this.entrada.getTipoEntrada().equals(this.getEntradaEntradaValue(TiposEntrada.RADIO)) && !this.entrada.getTipoEntrada().equals(this.getEntradaEntradaValue(TiposEntrada.CHECKBOX))){
			entrada.setOpciones("");
		}
		this.tiposRestriccionValores = new ArrayList<>();
		for (String datos:TiposRestriccionesValor.obtenerValoresRestricciones(entrada.getTipoEntrada())) {
			this.tiposRestriccionValores.add(new SelectItem(datos, this.mensajes.getString("etiqueta_TIPORESTRICCIONVALORENTRADA_"+datos)));
		}

		actualizaOpcionesEntradaAnidada(false);
		
		if(entrada.getTipoEntrada().equals(TiposEntrada.SECUENCIA_GLOBAL.name()) && !secuenciasGlobales.isEmpty()) {
			entrada.setParametroOpcionesId(secuenciasGlobales.get(0).getId());
		}
	}
	
	
	/**
	 * Actualiza el listado de opciones de un combo padre para seleccionar los valores que van a aplicar para mostrar una entrada hija
	 * @param reseteaSeleccion Indica si se deben resetear los valores seleccionados en el combo de entradas anidadas
	 */
	public void actualizaOpcionesEntradaAnidada(boolean reseteaSeleccion) {
		opcionesEntradasAnidadas.clear();
		
		if(reseteaSeleccion) {
			entrada.getOpcionesJson().clear();
		}
		
		if(entrada.isEntradaAnidada() && entrada.getEntradaAnidadaId()!=null && entrada.getEntradaAnidadaId()>0) {
			opcionesEntradasAnidadas = getListaOpcionesEntrada(procesoControl.buscarEntradaPorId(entrada.getEntradaAnidadaId()));
			if(opcionesEntradasAnidadas!=null) {
				for(NodoTO node:opcionesEntradasAnidadas) {
					// Valida si tiene guardado valor para el parent para que salga el check seleccionado
					for(JsonOpcion option:entrada.getOpcionesJson()) {
						if(option.getOpcionPadreId()!=null && option.getOpcionPadreId().equals(node.getId())) {
							node.setSelected(true);
							break;
						}
					}
				}
			}
		}
	}
	

	/**
	 * Actualiza un campo de opciones para fijar si es requerido o no, dependiendo si se selecciona en pantalla
	 * Borra cualquier dato ingresado si el campo no fue seleccionado
	 * @param node Nodo a modificar
	 */
	public void actualizarRequeridosOpcionesEntradasAnidadas(NodoTO node) {
		if(node.isSelected()) {
			boolean found = false;
			for(JsonOpcion option:entrada.getOpcionesJson()) {
				if(option.getOpcionPadreId() != null && option.getOpcionPadreId().equals(node.getId())) {
					found = true;
					option.setSeleccionado(node.isSelected());
					option.setRequerido(node.isSelected());
				}
			}
			if(!found) {
				JsonUtil.agregarCampoOpciones(entrada, node.getId());
			}
		} else {
			Iterator<JsonOpcion> iter = entrada.getOpcionesJson().iterator();
			while(iter.hasNext()) {
				JsonOpcion option = iter.next();
				if(option.getOpcionPadreId().equals(node.getId())) {
					iter.remove();
				}
			}
		}
	}


	/**
	 * Agrega una regla al paso actual
	 * Verifica si las entradas tienen campos de lista para establecer una regla
	 */
	public void agregarRegla(){
		try{
			if(!this.getEntradasReglas().isEmpty()) {
				this.regla = new Regla();
				this.agregarReglaCombinada();
				if(pasos!=null) {
					// Se hace la asignación de esta manera en lugar de reglaentrada.setSiguientePaso(pasos.get(0)) para prevenir que se afecte la primera paso de la lista por referencia
					// en el momento de guardar la condición
					Paso pasoSiguiente = new Paso();
					procesoControl.inicializarJsonPasoData(pasoSiguiente);

					pasoSiguiente.setId(pasos.get(0).getId());
					// ---
					regla.setSiguientePasoId(pasos.get(0).getId());
					regla.setSiguientePaso(pasoSiguiente);
				}
			} else{
				String message = mensajes.getString("error_regla_noentradas");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		} catch(Exception e){
			String message = "Error: "+(e.getMessage()!=null?e.getMessage():e.toString());
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	
	
	/**
	 * Ingreso a modificar una regla
	 * @param regla Regla a modificar
	 */ 
	public void modificarRegla(Regla regla){
		this.regla = regla;
	}
	

	/**
	 * Agrega una regla combinada con varias condiciones
	 */
	public void agregarReglaCombinada(){
		if(this.regla!=null){
			if(this.regla.getDetalles()==null){
				this.regla.setDetalles(new ArrayList<CondicionRegla>());
			}
			
			CondicionRegla detalles = new CondicionRegla();
			detalles.setContenido("1");
			detalles.setEntrada(new Entrada());
			procesoControl.inicializarJsonEntradaData(detalles.getEntrada());
			
			List<Entrada> md = this.getEntradasReglas();
			if(md!=null && !md.isEmpty()){
				detalles.setEntrada(md.get(0));
			}
			this.regla.getDetalles().add(detalles);
		}
	}

	
	/**
	 * Obtiene todas las entradas de un proceso
	 * @return Lista de entradas 
	 */
	public List<Entrada> getEntradasTodasFormato(){
		List<Entrada> list = new ArrayList<>();
		List<Entrada> listaEntradas = procesoControl.buscarEntradasActivasProceso(data.getId());
		for(Entrada objeto:listaEntradas){
			objeto.setNombreEntrada(objeto.getNombreEntrada()+" ("+objeto.getPaso().getNombre()+")");
			list.add(objeto);
		}
		return list;
	}

	
	
	/**
	 * Obtiene los entradas de un paso que permiten agregar reglas select,radio
	 * @return Lista de entradas con reglas
	 */
	public List<Entrada> getEntradasReglas(){
		List<Entrada> list = new ArrayList<>();
		List<Entrada> listaEntradas = procesoControl.buscarEntradasActivasProceso(data.getId());
		for(Entrada objeto:listaEntradas){
			if(procesoControl.getTiposEntradasReglas().contains(objeto.getTipoEntrada())){
				objeto.setNombreEntrada(objeto.getNombreEntrada()+" ("+objeto.getPaso().getNombre()+")");
				procesoControl.inicializarJsonEntradaData(objeto);
				list.add(objeto);
			}
		}
		return list;
	}

	
	/**
	 * Obtiene las entradas de un paso cuando se tratan de entradas anidadas
	 * @return Lista de entradas anidadas
	 */
	public List<Entrada> getEntradasParaEntradaAnidada(){
		List<Entrada> list = new ArrayList<>();
		List<Entrada> listaEntradas = procesoControl.buscarEntradasActivasProceso(data.getId());
		for(Entrada objeto:listaEntradas){
			if(procesoControl.getTiposEntradasReglas().contains(objeto.getTipoEntrada()) && (objeto.getId()==0 || this.entrada.getId()!=objeto.getId())) {
				objeto.setNombreEntrada(objeto.getNombreEntrada()+" ("+objeto.getPaso().getNombre()+")");
				list.add(objeto);
			}
		}
		return list;
	}
	
	
	/**
	 * Cancela agregar una regla
	 */
	public void cancelarAgregarRegla(){
		this.regla = null;
		this.buscarReglas();
	}
	

	/**
	 * Guarda una regla
	 */
	public void guardarRegla(){
		try{
			procesoControl.guardarRegla(regla, paso, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());

			this.regla = null;
			this.buscarReglas();
			String message = mensajes.getString("mensaje_regla_guardar");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		} catch(Exception e){
			String message = mensajes.getString("error_regla_guardar")+":"+e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}
	
	
	/**
	 * Elimina una regla
	 * @param regla Regla a eliminar
	 */
	public void eliminarRegla(Regla regla){
		try{
			regla.setActivo(false);
			procesoControl.actualizarEntidad(regla);
			this.buscarReglas();
			String message = mensajes.getString("mensaje_regla_eliminar");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		} catch(Exception e){
			String message = mensajes.getString("error_regla_eliminar")+":"+e.getMessage();
			logger.error(message, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
	}


	/**
	 * Obtiene una lista de opciones a partir de una condicion de regla seleccionada
	 * @param condicionRegla Condicion de la regla a evaluar 
	 * @return Lista de opciones según la condición evaluada
	 */
	public List<NodoTO> getOptionsList(CondicionRegla condicionRegla){
		List<NodoTO> result = new ArrayList<>();
		if(condicionRegla.getEntrada()!=null && condicionRegla.getEntrada().getId()>0){
			condicionRegla.setEntrada(procesoControl.buscarEntradaPorId(condicionRegla.getEntrada().getId()));
			result = this.getListaOpcionesEntrada(condicionRegla.getEntrada());
		}
		return result;
	}
	
	
	/**
	 * Obtiene el listado de opciones de una entrada
	 * Utiliza el objeto NodeTO a manera de objeto de key:value
	 * @param entrada Entrada a evaluar
	 * @return Lista de opciones de la entrada
	 */
	public List<NodoTO> getListaOpcionesEntrada(Entrada entrada){
		List<NodoTO> result = null;
		if(entrada==null) {
			result = null;
		} else {
			result = procesoControl.getListaOpcionesEntrada(entrada, null);
		}
		return result;
	}
	
	
	/**
	 * Obtiene una cadena con los detalles de la regla
	 * @param regla Regla a evaluar
	 * @return Cadena con el detalle de la regla
	 */
	public String getReglaDetallesString(Regla regla){
		StringBuilder det = new StringBuilder();
		if(regla.getDetalles()!=null){
			int i = 1;
			for(CondicionRegla con:regla.getDetalles()){
				det.append(con.getEntrada().getNombreEntrada() +" ("+con.getEntrada().getPaso().getNombre()+")" + " = ");
				// Verifica la posicion guardada en el campo regla para traer el campo correspondiente en la lista de opciones del entrada
				List<NodoTO> param = this.getListaOpcionesEntrada(con.getEntrada());
				// Verifica por el ID de la opción seleccionada
				int index = 0;
				try{
					index = Integer.parseInt(con.getContenido());
				} catch(Exception e){
					logger.error("Regla no es entero");
				}
				if(index > 0) {
					for(NodoTO p:param) {
						if(Integer.parseInt(p.getId()) == index) {
							det.append(p.getText());
							break;
						}
					}
				} else {
					index--;
					if(param.size()>index){
						det.append(param.get(index).getText());
					}
				}

				if(i++<regla.getDetalles().size()){
					det.append(", ");
				}
			}
		}
		return det.toString();
	}
	

	/**
	 * Listener cuando se cambia el usuario asignado al paso
	 */
	public void validarCambiarUsuarioAsignado(){
		if(this.paso.getUsuarioAsignadoId()!=null && !paso.getUsuarioAsignadoId().isEmpty()){
			this.paso.setGrupoAsignadoId(null);
		}
	}

	/**
	 * Listener cuando se cambia el grupo asignado al paso
	 */
	public void validarCambiarGrupoAsignado(){
		if(this.paso.getGrupoAsignadoId()!=null && !paso.getGrupoAsignadoId().isEmpty()){
			this.paso.setUsuarioAsignadoId(null);
		}
	}

	/**
	 * Obtiene el nombre de un tipo de entrada
	 * @param tipoEntrada Tipo de Entrada a evaluar
	 * @return Cadena con el nombre del tipo de entrada
	 */
	public String getEntradaEntradaValue(TiposEntrada tipoEntrada) {
        return tipoEntrada.name();
    }
	
	
	/**
	 * Permite agregar un trozo de secuencia al proceso
	 */
	public void agregarSecuenciaProceso() {
		if(this.secuenciaProceso==null) {
			this.secuenciaProceso = new ArrayList<>();
		}
		ProcesoSecuencia fs = new ProcesoSecuencia();
		fs.setProceso(this.data);
		fs.setActivo(true);
		this.secuenciaProceso.add(fs);
	}
	
	
	/**
	 * Permite obtener una cadena con todas las secuencias asociadas a un proceso
	 * @return Cadena con el valor de la secuencia del proceso
	 */
	public String getSecuenciaProcesoCadena() {
		StringBuilder proceso = new StringBuilder();
		try {
			for(ProcesoSecuencia procesoseq:this.secuenciaProceso) {
				if(procesoseq.isActivo() && procesoseq.getValorSecuencia()!=null) {
					if(procesoseq.getTamanoSecuencia()>procesoseq.getValorSecuencia().length()) {
						proceso.append(StringUtils.leftPad(procesoseq.getValorSecuencia(), procesoseq.getTamanoSecuencia(), "0"));
					} else {
						proceso.append(procesoseq.getValorSecuencia());
					}
				}
			}
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
		return proceso.toString();
	}
	
	
	/**
	 * Permite guardar las secuencias asociadas al proceso
	 */
	public void guardarSecuenciaProceso() {
		try {
			String message = "";
			for(ProcesoSecuencia procesoseq:this.secuenciaProceso) {
				if(procesoseq.isAutoNumerico()) {
					Integer sec = obtenerEnteroSecuencia(procesoseq.getValorSecuencia());
					if(sec==null) {
						message = this.mensajes.getString("etiqueta_secuenciaproceso_errorautonumerico")+" : "+procesoseq.getValorSecuencia();
						procesoseq.setValorSecuencia("");
						break;
					}
				}
			}
	
			if(message.isEmpty()) {
				procesoControl.guardarSecuenciasProceso(secuenciaProceso, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
	
				this.secuenciaProceso = procesoControl.buscarSecuenciaProceso(data.getId());
				String mensaje = this.mensajes.getString("etiqueta_secuenciaproceso_guardarok");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, mensaje));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}
	
	
	/**
	 * Reinicia la secuencia del proceso a la que se tenga guardada
	 */
	public void cancelarSecuenciaProceso() {
		try {
			this.secuenciaProceso = procesoControl.buscarSecuenciaProceso(data.getId());
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}
	
	/**
	 * Permite borrar un trozo de secuencia de un proceso
	 * @param secuenciaProceso Secuencia de proceso a borrar
	 */
	public void borrarSecuenciaProceso(ProcesoSecuencia secuenciaProceso) {
		try {
			procesoControl.eliminarSecuenciaProceso(secuenciaProceso);
			this.secuenciaProceso.remove(secuenciaProceso);
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}

	
	/**
	 * Permite agregar un trozo de secuencia a la entrada
	 */
	public void agregarSecuenciaEntrada() {
		if(this.secuenciaentrada==null) {
			this.secuenciaentrada = new ArrayList<>();
		}
		SecuenciaEntrada fs = new SecuenciaEntrada();
		fs.setEntrada(this.entrada);
		fs.setActivo(true);
		this.secuenciaentrada.add(fs);
	}
	

	/**
	 * Arma un string con la secuencia de la entrada cuando es de este tipo de entrada
	 * @param entrada Entrada a obtener su secuencia
	 * @return Cadena con la secuencia
	 */
	public String getEntradaValorSecuencia(Entrada entrada) {
		return secuenciasControl.obtenerEntradaValorSecuencia(entrada.getId(), false);
	}
	
	
    /**
     * Obtiene la secuencia en formato Integer
     * @param secuencia Secuencia a parsear
     * @return Entero con el valor de la secuencia
     */
    private Integer obtenerEnteroSecuencia(String secuencia) {
    	Integer resultado = null;
		try {
			resultado = Integer.valueOf(secuencia);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return resultado;
    }
    
    
	/**
	 * Guarda las secuencias asociadas a una entrada
	 */
	public void guardarSecuenciaEntrada() {
		try {
			String message = "";
			for(SecuenciaEntrada secuenciaEntrada:this.secuenciaentrada) {
				if(secuenciaEntrada.isAutonumerico()) {
					Integer sec = obtenerEnteroSecuencia(secuenciaEntrada.getValorSecuencia());
					if(sec==null) {
						message = this.mensajes.getString("etiqueta_secuenciaentrada_errorautonumerico")+" : "+secuenciaEntrada.getValorSecuencia();
						secuenciaEntrada.setValorSecuencia("");
						break;
					}
				}
			}
	
			if(message.isEmpty()) {
				procesoControl.guardarSecuenciasEntrada(secuenciaentrada, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
	
				this.secuenciaentrada = procesoControl.buscarSecuenciaEntrada(entrada.getId());
				String mensaje = this.mensajes.getString("etiqueta_secuenciaentrada_guardarok");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, mensaje));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}
	
	/**
	 * Reinicia la secuencia de la entrada
	 */
	public void cancelarSecuenciaEntrada() {
		try {
			this.secuenciaentrada = procesoControl.buscarSecuenciaEntrada(entrada.getId());
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}
	
	/**
	 * Elimina un trozo de la secuencia de una entrada
	 * @param secuenciaEntrada Secuencia de Entrada a eliminar
	 */
	public void eliminarSecuenciaEntrada(SecuenciaEntrada secuenciaEntrada) {
		try {
			procesoControl.eliminarSecuenciaEntrada(secuenciaEntrada);
			this.secuenciaentrada.remove(secuenciaEntrada);
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}
	
	
	/**
	 * Valida si una entrada de formulario permite agregar opciones
	 * @param entrada Entrada a validar
	 * @return true si la entrada permite opciones
	 */
	public boolean validarOpcionesEntrada(Entrada entrada) {
		boolean result = false;
		if(entrada.getTipoEntrada().equals(TiposEntrada.SELECT.name()) || entrada.getTipoEntrada().equals(TiposEntrada.RADIO.name()) || entrada.getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())) {
			result = true;
		}
		return result;
	}
	

	/**
	 * Valida si una opción tiene entradas hijas o anidadas
	 * @param idPadre Identificador de la opción padre
	 * @param id Identificador de la opción actual
	 * @return Posición en la que se encontró la opción dentro de la lista de opciones de la entrada actual
	 */
	public int validarIndiceOpcionesAnidadasEntradas(String idPadre, int id) {
		int result = 0;
		for(JsonOpcion option:entrada.getOpcionesJson()) {
			if(option.getOpcionPadreId()!=null && option.getOpcionPadreId().equals(idPadre) && option.isActivo()) {
				if(option.getId() == id) {
					break;
				}
				result++;
			}
		}
		return result;
	}

	
	
	/**
	 * Listener que se ejecuta cuando se cambia de asignación un grupo a un paso
	 */
	public void listenerCambiarGrupo() {
		paso.getJsonPasoData().getGruposSeleccionadosMostrar().clear();

		if(paso.getJsonPasoData()!=null && paso.getJsonPasoData().getGruposSeleccionados()!=null && !paso.getJsonPasoData().getGruposSeleccionados().isEmpty()) {
			for (Object grupoId:paso.getJsonPasoData().getGruposSeleccionados()) {
				for (GrupoTO grupo:gestorSesionBean.getGrupos()) {
					if (grupo.getId().equals(String.valueOf(grupoId))) {
						paso.getJsonPasoData().getGruposSeleccionadosMostrar().add(grupo);
						break;
					}
				}
			}
		}
	}
	

	/**
	 * Listener que se ejecuta cuando se cambia de asignación un usuario a un paso
	 */
	public void listenerCambiarUsuario() {
		paso.getJsonPasoData().getUsuariosSeleccionadosMostrar().clear();

		if(paso.getJsonPasoData()!=null && paso.getJsonPasoData().getUsuariosSeleccionados()!=null && !paso.getJsonPasoData().getUsuariosSeleccionados().isEmpty()) {
			for (Object id:paso.getJsonPasoData().getUsuariosSeleccionados()) {
				for (UsuarioTO usuario:gestorSesionBean.getUsuarios()) {
					if (usuario.getId().equals(String.valueOf(id))) {
						paso.getJsonPasoData().getUsuariosSeleccionadosMostrar().add(usuario);
						break;
					}
				}
			}
		}
	}
	
	
	/**
	 * Obtiene el nombre completo de una entrada (si es requerido con un *)
	 * 
	 * @param entrada Entrada a obtener el nombre
	 * @return Cadena con el nombre de la entrada
	 */
	public String obtenerNombreEntrada(Entrada entrada) {
		StringBuilder resultado = new StringBuilder();
		if (entrada != null) {
			if (entrada.isRequerido()) {
				resultado.append(mensajes.getString("etiqueta_requerido")).append(' ');
			}
			resultado.append(entrada.getNombreEntrada());
			if(entrada.isEntradaOculta()) {
				resultado.append(" ("+mensajes.getString("etiqueta_entrada_oculta")).append(") ");
			}
		}
		return resultado.toString();
	}

	
	/**
	 * Valida si la entrada puede enviarse como parámetro a checkin de owcc
	 * @param entrada Entrada a evaluar
	 * @return true si la entrada puede enviarse como parámetro a checkin de owcc
	 */
	public boolean isEntradaEnvioCheckin(Entrada entrada) {
		boolean ok = false;
		if(entrada.getTipoEntrada().equals(TiposEntrada.TEXT.name()) || entrada.getTipoEntrada().equals(TiposEntrada.TEXTAREA.name()) || entrada.getTipoEntrada().equals(TiposEntrada.TEXTO_NUMERICO.name()) 
				|| entrada.getTipoEntrada().equals(TiposEntrada.CHECKBOX.name()) || entrada.getTipoEntrada().equals(TiposEntrada.DATE.name()) || entrada.getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name())
				|| entrada.getTipoEntrada().equals(TiposEntrada.EMAIL.name()) || entrada.getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || entrada.getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())
				|| entrada.getTipoEntrada().equals(TiposEntrada.SELECT.name())|| entrada.getTipoEntrada().equals(TiposEntrada.RADIO.name())) {
			ok = true;
		}
		return ok;
	}
	
	
	/**
	 * Indica si la entrada tiene campo de longitud
	 * @param entrada Entrada a evaluar
	 * @return true si la entrada tiene opción de longitud
	 */
	public boolean isTipoEntradaLongitud(Entrada entrada) {
		return procesoControl.getTiposEntradasLongitud().contains(entrada.getTipoEntrada());
	}
	
	
	/**
	 * Agrega una restricción a una entrada
	 * @param entrada Entrada a agregar la restricción
	 */
	public void agregarRestriccionEntrada(Entrada entrada) {
		if(restriccionesEntrada==null) {
			restriccionesEntrada = new ArrayList<>();
		}
		RestriccionEntrada re = new RestriccionEntrada();
		re.setEntrada(entrada);
		re.setTipo(TiposRestriccionesEntrada.obtenerTodos().get(0));
		restriccionesEntrada.add(re);
	}


	/**
	 * Elimina una restricción de una entrada
	 * @param restriccionEntrada Restricción de entrada a eliminar
	 */
	public void eliminarRestriccionEntrada(RestriccionEntrada restriccionEntrada) {
		procesoControl.eliminarRestriccionEntrada(restriccionEntrada);
		restriccionesEntrada.remove(restriccionEntrada);
	}
	
	
	/**
	 * Arma un string con la secuencia global del parametro
	 * @param idParametro Parametro a consultar
	 * @return Cadena con el valor de la secuencia global
	 */
	public String obtenerValorSecuenciaGlobal(long idParametro) {
		return secuenciasControl.obtenerValorSecuenciaGlobal(idParametro, false);
	}

	
	/**
	 * Obtiene el Timezone por defecto
	 * @return Timezone por defecto
	 */
	public TimeZone getTimeZone(){
		return TimeZone.getDefault();
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
	public LazyDataModel<Proceso> getLista() {
		return lista;
	}

	/**
	 * Establece el valor del atributo lista
	 * @param lista con el valor del atributo lista a establecer
	 */
	public void setLista(LazyDataModel<Proceso> lista) {
		this.lista = lista;
	}

	/**
	 * Obtiene el valor del atributo filtroGeneral
	 * @return El valor del atributo filtroGeneral
	 */
	public String getFiltroGeneral() {
		return filtroGeneral;
	}

	/**
	 * Establece el valor del atributo filtroGeneral
	 * @param filtroGeneral con el valor del atributo filtroGeneral a establecer
	 */
	public void setFiltroGeneral(String filtroGeneral) {
		this.filtroGeneral = filtroGeneral;
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
	public Proceso getData() {
		return data;
	}

	/**
	 * Establece el valor del atributo data
	 * @param data con el valor del atributo data a establecer
	 */
	public void setData(Proceso data) {
		this.data = data;
	}

	/**
	 * Obtiene el valor del atributo paso
	 * @return El valor del atributo paso
	 */
	public Paso getPaso() {
		return paso;
	}

	/**
	 * Establece el valor del atributo paso
	 * @param paso con el valor del atributo paso a establecer
	 */
	public void setPaso(Paso paso) {
		this.paso = paso;
	}

	/**
	 * Obtiene el valor del atributo entrada
	 * @return El valor del atributo entrada
	 */
	public Entrada getEntrada() {
		return entrada;
	}

	/**
	 * Establece el valor del atributo entrada
	 * @param entrada con el valor del atributo entrada a establecer
	 */
	public void setEntrada(Entrada entrada) {
		this.entrada = entrada;
	}

	/**
	 * Obtiene el valor del atributo regla
	 * @return El valor del atributo regla
	 */
	public Regla getRegla() {
		return regla;
	}

	/**
	 * Establece el valor del atributo regla
	 * @param regla con el valor del atributo regla a establecer
	 */
	public void setRegla(Regla regla) {
		this.regla = regla;
	}

	/**
	 * Obtiene el valor del atributo pasos
	 * @return El valor del atributo pasos
	 */
	public List<Paso> getPasos() {
		return pasos;
	}

	/**
	 * Establece el valor del atributo pasos
	 * @param pasos con el valor del atributo pasos a establecer
	 */
	public void setPasos(List<Paso> pasos) {
		this.pasos = pasos;
	}

	/**
	 * Obtiene el valor del atributo entradas
	 * @return El valor del atributo entradas
	 */
	public List<Entrada> getEntradas() {
		return entradas;
	}

	/**
	 * Establece el valor del atributo entradas
	 * @param entradas con el valor del atributo entradas a establecer
	 */
	public void setEntradas(List<Entrada> entradas) {
		this.entradas = entradas;
	}

	/**
	 * Obtiene el valor del atributo reglas
	 * @return El valor del atributo reglas
	 */
	public List<Regla> getReglas() {
		return reglas;
	}

	/**
	 * Establece el valor del atributo reglas
	 * @param reglas con el valor del atributo reglas a establecer
	 */
	public void setReglas(List<Regla> reglas) {
		this.reglas = reglas;
	}

	/**
	 * Obtiene el valor del atributo opcionesEntradasAnidadas
	 * @return El valor del atributo opcionesEntradasAnidadas
	 */
	public List<NodoTO> getOpcionesEntradasAnidadas() {
		return opcionesEntradasAnidadas;
	}

	/**
	 * Establece el valor del atributo opcionesEntradasAnidadas
	 * @param opcionesEntradasAnidadas con el valor del atributo opcionesEntradasAnidadas a establecer
	 */
	public void setOpcionesEntradasAnidadas(List<NodoTO> opcionesEntradasAnidadas) {
		this.opcionesEntradasAnidadas = opcionesEntradasAnidadas;
	}

	/**
	 * Obtiene el valor del atributo tiposProceso
	 * @return El valor del atributo tiposProceso
	 */
	public List<Parametro> getTiposProceso() {
		return tiposProceso;
	}

	/**
	 * Establece el valor del atributo tiposProceso
	 * @param tiposProceso con el valor del atributo tiposProceso a establecer
	 */
	public void setTiposProceso(List<Parametro> tiposProceso) {
		this.tiposProceso = tiposProceso;
	}

	/**
	 * Obtiene el valor del atributo tiposDuracion
	 * @return El valor del atributo tiposDuracion
	 */
	public List<SelectItem> getTiposDuracion() {
		return tiposDuracion;
	}

	/**
	 * Establece el valor del atributo tiposDuracion
	 * @param tiposDuracion con el valor del atributo tiposDuracion a establecer
	 */
	public void setTiposDuracion(List<SelectItem> tiposDuracion) {
		this.tiposDuracion = tiposDuracion;
	}

	/**
	 * Obtiene el valor del atributo tiposEntrada
	 * @return El valor del atributo tiposEntrada
	 */
	public List<SelectItem> getTiposEntrada() {
		return tiposEntrada;
	}

	/**
	 * Establece el valor del atributo tiposEntrada
	 * @param tiposEntrada con el valor del atributo tiposEntrada a establecer
	 */
	public void setTiposEntrada(List<SelectItem> tiposEntrada) {
		this.tiposEntrada = tiposEntrada;
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
	public Long[] getCategoriasSeleccionadas() {
		return categoriasSeleccionadas;
	}

	/**
	 * Establece el valor del atributo categoriasSeleccionadas
	 * @param categoriasSeleccionadas con el valor del atributo categoriasSeleccionadas a establecer
	 */
	public void setCategoriasSeleccionadas(Long[] categoriasSeleccionadas) {
		this.categoriasSeleccionadas = categoriasSeleccionadas;
	}

	/**
	 * Obtiene el valor del atributo secuenciaProceso
	 * @return El valor del atributo secuenciaProceso
	 */
	public List<ProcesoSecuencia> getSecuenciaProceso() {
		return secuenciaProceso;
	}

	/**
	 * Establece el valor del atributo secuenciaProceso
	 * @param secuenciaProceso con el valor del atributo secuenciaProceso a establecer
	 */
	public void setSecuenciaProceso(List<ProcesoSecuencia> secuenciaProceso) {
		this.secuenciaProceso = secuenciaProceso;
	}

	/**
	 * Obtiene el valor del atributo secuenciaentrada
	 * @return El valor del atributo secuenciaentrada
	 */
	public List<SecuenciaEntrada> getSecuenciaentrada() {
		return secuenciaentrada;
	}

	/**
	 * Establece el valor del atributo secuenciaentrada
	 * @param secuenciaentrada con el valor del atributo secuenciaentrada a establecer
	 */
	public void setSecuenciaentrada(List<SecuenciaEntrada> secuenciaentrada) {
		this.secuenciaentrada = secuenciaentrada;
	}

	/**
	 * Obtiene el valor del atributo mostrarHorasLaborales
	 * @return El valor del atributo mostrarHorasLaborales
	 */
	public boolean isMostrarHorasLaborales() {
		return mostrarHorasLaborales;
	}

	/**
	 * Establece el valor del atributo mostrarHorasLaborales
	 * @param mostrarHorasLaborales con el valor del atributo mostrarHorasLaborales a establecer
	 */
	public void setMostrarHorasLaborales(boolean mostrarHorasLaborales) {
		this.mostrarHorasLaborales = mostrarHorasLaborales;
	}

	/**
	 * Obtiene el valor del atributo horasLaborales1
	 * @return El valor del atributo horasLaborales1
	 */
	public String getHorasLaborales1() {
		return horasLaborales1;
	}

	/**
	 * Establece el valor del atributo horasLaborales1
	 * @param horasLaborales1 con el valor del atributo horasLaborales1 a establecer
	 */
	public void setHorasLaborales1(String horasLaborales1) {
		this.horasLaborales1 = horasLaborales1;
	}

	/**
	 * Obtiene el valor del atributo horasLaborales2
	 * @return El valor del atributo horasLaborales2
	 */
	public String getHorasLaborales2() {
		return horasLaborales2;
	}

	/**
	 * Establece el valor del atributo horasLaborales2
	 * @param horasLaborales2 con el valor del atributo horasLaborales2 a establecer
	 */
	public void setHorasLaborales2(String horasLaborales2) {
		this.horasLaborales2 = horasLaborales2;
	}

	/**
	 * Obtiene el valor del atributo listaHorasLaborales
	 * @return El valor del atributo listaHorasLaborales
	 */
	public List<String> getListaHorasLaborales() {
		return listaHorasLaborales;
	}

	/**
	 * Establece el valor del atributo listaHorasLaborales
	 * @param listaHorasLaborales con el valor del atributo listaHorasLaborales a establecer
	 */
	public void setListaHorasLaborales(List<String> listaHorasLaborales) {
		this.listaHorasLaborales = listaHorasLaborales;
	}

	/**
	 * Obtiene el valor del atributo procesoTieneEjecuciones
	 * @return El valor del atributo procesoTieneEjecuciones
	 */
	public boolean isProcesoTieneEjecuciones() {
		return procesoTieneEjecuciones;
	}

	/**
	 * Establece el valor del atributo procesoTieneEjecuciones
	 * @param procesoTieneEjecuciones con el valor del atributo procesoTieneEjecuciones a establecer
	 */
	public void setProcesoTieneEjecuciones(boolean procesoTieneEjecuciones) {
		this.procesoTieneEjecuciones = procesoTieneEjecuciones;
	}

	/**
	 * Obtiene el valor del atributo listaPredefinida
	 * @return El valor del atributo listaPredefinida
	 */
	public List<Parametro> getListaPredefinida() {
		return listaPredefinida;
	}

	/**
	 * Establece el valor del atributo listaPredefinida
	 * @param listaPredefinida con el valor del atributo listaPredefinida a establecer
	 */
	public void setListaPredefinida(List<Parametro> listaPredefinida) {
		this.listaPredefinida = listaPredefinida;
	}

	/**
	 * Obtiene el valor del atributo logger
	 * @return El valor del atributo logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Obtiene el valor del atributo restriccionesEntrada
	 * @return El valor del atributo restriccionesEntrada
	 */
	public List<RestriccionEntrada> getRestriccionesEntrada() {
		return restriccionesEntrada;
	}

	/**
	 * Establece el valor del atributo restriccionesEntrada
	 * @param restriccionesEntrada con el valor del atributo restriccionesEntrada a establecer
	 */
	public void setRestriccionesEntrada(List<RestriccionEntrada> restriccionesEntrada) {
		this.restriccionesEntrada = restriccionesEntrada;
	}

	/**
	 * Obtiene el valor del atributo tiposRestriccionEntradas
	 * @return El valor del atributo tiposRestriccionEntradas
	 */
	public List<SelectItem> getTiposRestriccionEntradas() {
		return tiposRestriccionEntradas;
	}

	/**
	 * Establece el valor del atributo tiposRestriccionEntradas
	 * @param tiposRestriccionEntradas con el valor del atributo tiposRestriccionEntradas a establecer
	 */
	public void setTiposRestriccionEntradas(List<SelectItem> tiposRestriccionEntradas) {
		this.tiposRestriccionEntradas = tiposRestriccionEntradas;
	}

	/**
	 * Obtiene el valor del atributo tiposRestriccionValores
	 * @return El valor del atributo tiposRestriccionValores
	 */
	public List<SelectItem> getTiposRestriccionValores() {
		return tiposRestriccionValores;
	}

	/**
	 * Establece el valor del atributo tiposRestriccionValores
	 * @param tiposRestriccionValores con el valor del atributo tiposRestriccionValores a establecer
	 */
	public void setTiposRestriccionValores(List<SelectItem> tiposRestriccionValores) {
		this.tiposRestriccionValores = tiposRestriccionValores;
	}

	/**
	 * Obtiene el valor del atributo secuenciasGlobales
	 * @return El valor del atributo secuenciasGlobales
	 */
	public List<Parametro> getSecuenciasGlobales() {
		return secuenciasGlobales;
	}

	/**
	 * Establece el valor del atributo secuenciasGlobales
	 * @param secuenciasGlobales con el valor del atributo secuenciasGlobales a establecer
	 */
	public void setSecuenciasGlobales(List<Parametro> secuenciasGlobales) {
		this.secuenciasGlobales = secuenciasGlobales;
	}

}

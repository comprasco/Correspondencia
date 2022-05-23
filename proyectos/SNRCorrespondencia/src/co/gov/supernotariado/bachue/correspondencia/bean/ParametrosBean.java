package co.gov.supernotariado.bachue.correspondencia.bean;

import java.util.ArrayList;
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
import javax.faces.model.SelectItem;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposDatosCampos;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposParametros;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Archivo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaGlobal;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonDatosOpcionesParametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.exception.CorrespondenciaException;

/**
 * Managed Bean que permite controlar los parámetros a registrar en la aplicación
 */
@ManagedBean
@ViewScoped
public class ParametrosBean extends GenericBean {
	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(ParametrosBean.class);
	
	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;
	
	/**
	 * Bean de sesión mara manejar la sesión
	 */
	@ManagedProperty("#{gestorSesionBean}")
    private GestorSesionBean gestorSesionBean;

	/**
	 * Error general para excepciones
	 */
	private static final String ERROR_GENERAL_KEY = "error_general";

	/**
	 * Lista principal de parámetros
	 */
	private LazyDataModel<Parametro> list;

	/**
	 * Mensajes de la aplicación
	 */
	private ResourceBundle mensajes = ResourceBundle.getBundle("messages");

	/**
	 * Lista con tipos de parámetros
	 */
	private List<SelectItem> tiposParametros = new ArrayList<>(); 
	
	/**
	 * Tipo de parámetro seleccionado
	 */
	private String tipoParametroSeleccionado;
	
	/**
	 * Filtro por estado
	 */
	private Boolean filtroStatus = true;
	
	/**
	 * Objeto Parametro a crear o modificar 
	 */
	private Parametro parametro;
	
	/**
	 * Tipos de datos permitidos para los datos adicionales de las opciones
	 */
	private List<SelectItem> tiposDatos = new ArrayList<>();

	/**
	 * Archivo cargado para un parametro tipo plantilla
	 */
	@Transient
	private UploadedFile archivoCargado;

	/**
	 * Archivo guardado para un parametro tipo plantilla
	 */
	@Transient
	private Archivo archivoGuardado;

	/**
	 * Listado de las partes de una secuencia global
	 */
	private List<SecuenciaGlobal> secuenciaGlobal = new ArrayList<>();

	
	/**
     * Inicializa variables del bean
     */
    @PostConstruct
    public void init() {
		mensajes = ResourceBundle.getBundle("messages", gestorSesionBean.getLocale());

    	for(TiposParametros param:TiposParametros.values()){
    		this.tiposParametros.add(new SelectItem(param.name(), mensajes.getString("etiqueta_tipoparametro_"+param.name())));
    	}
		for (TiposDatosCampos tipos:TiposDatosCampos.values()) {
			this.tiposDatos.add(new SelectItem(tipos, this.mensajes.getString("etiqueta_CAMPO_"+tipos.name())));
		}

    	this.buscar();
    }
    
    /**
     * Buscador para la lista de parámetros
     */
    public void buscar(){
    	archivoCargado = null;
    	gestorSesionBean.irAOpcion(2);
    	
    	this.list = new LazyDataModel<Parametro>(){
            private static final long serialVersionUID = 1L;
            
            @Override
            public List<Parametro> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        		List<Parametro> lista = parametrosControl.buscarParametrosPor(filtroStatus, tipoParametroSeleccionado, estableceOrdenConsulta(sortField, sortOrder), pageSize, first);
    			list.setRowCount(parametrosControl.contarParametrosPor(filtroStatus, tipoParametroSeleccionado));
            	return lista;
            }
        };

        
	}
    
    /**
     * Incializa opción para crear un parámetro
     */
    public void crear(){
    	this.parametro = new Parametro();
    	parametrosControl.inicializarJsonDataParametro(this.parametro);
    	
    	this.parametro.setActivo(true);
    	parametro.setLongitudItem(50);
    	gestorSesionBean.irAOpcion(21);
    }
    
    
    /**
     * Establece los nombres de los usuarios de creación y modificación
     */
    private void establecerNombresUsuarios() {
		if(parametro.getIdUsuarioCreacion()!=null && !parametro.getIdUsuarioCreacion().isEmpty()){
			parametro.setUsuarioCreacion(gestorSesionBean.obtenerUsuario(parametro.getIdUsuarioCreacion()));
		}
		if(parametro.getIdUsuarioModificacion()!=null && !parametro.getIdUsuarioModificacion().isEmpty()){
			parametro.setUsuarioModificacion(gestorSesionBean.obtenerUsuario(parametro.getIdUsuarioModificacion()));
		}
    }
    
    /**
     * Incializa opción para modificar un parámetro
     * @param param Parámetro a modificar
     */
    public void modificar(Parametro param){
    	this.parametro = param;
    	archivoGuardado = null;
    	archivoCargado = null;
    	gestorSesionBean.irAOpcion(22);
    	if(parametro.getLongitudItem()==0) {
    		parametro.setLongitudItem(50);
    	}
    	
    	establecerNombresUsuarios();

    	if(parametro.getOpcionesJson()!=null) {
	    	// Valida las opciones anteriores para que tengan los datos adicionales
	    	for(JsonOpcion op:parametro.getOpcionesJson()) {
	    		if(parametro.getTipoParametro().equals(TiposParametros.PLANTILLA_DOCUMENTO.name())) {
	    			String idArchivo = op.getNombre();
	    			archivoGuardado = parametrosControl.obtenerArchivoPorId(Long.valueOf(idArchivo));
	    		} else {
		    		for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
		    			if(op.getDatosAdicionales()==null){
		    				op.setDatosAdicionales(new LinkedHashMap<Integer, String>());
		    			}
		    			if(!op.getDatosAdicionales().containsKey(dato.getId())){
		    				op.getDatosAdicionales().put(dato.getId(), null);
		    			}
		    		}
	    		}
	    	}
    	}
    	
    	if(parametro.getTipoParametro().equals(TiposParametros.SECUENCIA_GLOBAL.name())) {
    		secuenciaGlobal = parametrosControl.obtenerSecuenciasGlobal(parametro.getId());
    	}
    	
    }
    
    /**
     * Guarda el parámetro
     */
    public void guardar(){
    	try{
    		if(parametro.getId()==0 && parametro.getTipoParametro().equals(TiposParametros.SISTEMA.name())) {
				String message = mensajes.getString("mensaje_parametronocrear");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    		} else {
	    		parametro = parametrosControl.guardarParametro(parametro, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
	
	    		if(parametro.getTipoParametro().equals(TiposParametros.SECUENCIA_GLOBAL.name())) {
					guardarSecuenciaGlobal();
				}
	    		
	    		if(parametro.getTipoParametro().equals(TiposParametros.PLANTILLA_DOCUMENTO.name()) && archivoCargado!=null) {
	    			String mensaje = parametrosControl.guardarPlantilla(gestorSesionBean.getIntegracionCatalogos(), parametro, archivoCargado, archivoGuardado, gestorSesionBean.getUsuarioActual().getOficina().getCirculo(), gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
	    			if(mensaje!=null && !mensaje.isEmpty()) {
						String msg = mensajes.getString("mensaje_errorguardarplantilla") + mensaje;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
					}
	    		}
	
				String message = mensajes.getString("mensaje_guardarparametro");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
				this.buscar();
    		}
    	} catch(Exception e){
			logger.error("Error guardar", e);
			String message = mensajes.getString("error")+": "+e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    	}
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
	 * Guarda las secuencias asociadas
	 */
	public void guardarSecuenciaGlobal() {
		try {
			String message = "";
			for(SecuenciaGlobal secuencia:secuenciaGlobal) {
				secuencia.setParametro(parametro);
				if(secuencia.isAutonumerico()) {
					Integer sec = obtenerEnteroSecuencia(secuencia.getValorSecuencia());
					if(sec==null) {
						message = this.mensajes.getString("etiqueta_secuenciaentrada_errorautonumerico")+" : "+secuencia.getValorSecuencia();
						secuencia.setValorSecuencia("");
						break;
					}
				}
			}
	
			if(message.isEmpty()) {
				parametrosControl.guardarSecuenciaGlobal(secuenciaGlobal, gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
	    		secuenciaGlobal = parametrosControl.obtenerSecuenciasGlobal(parametro.getId());
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}

    
    /**
     * Busca el nombre de un tipo de parámetro en el properties de mensajes
     * @param tipoParametro Tipo de parametro a concatenar para buscar su etiqueta
     * @return Cadena con el nombre de parámetro a mostrar
     */
    public String buscarTipoParametroNombre(String tipoParametro){
    	String nombre = "";
    	if(tipoParametro!=null && !tipoParametro.isEmpty()){
    		nombre = this.mensajes.getString("etiqueta_tipoparametro_"+tipoParametro);
    	}
    	return nombre;
    }
    
	
    /**
	 * Agrega una nueva opción de lista
	 */
	public void agregarOpcion() {
    	// Encuentra el maximo id para asignar a la nueva opción
    	int maxid = 0;
    	for(JsonOpcion opcion:parametro.getOpcionesJson()) {
    		if(opcion.getId() > maxid) {
    			maxid = opcion.getId();
    		}
    	}
    		
    	JsonOpcion opcion = new JsonOpcion();
    	opcion.setId(maxid+1);
    	opcion.setActivo(true);
    	for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
			if(opcion.getDatosAdicionales()==null){
				opcion.setDatosAdicionales(new LinkedHashMap<Integer, String>());
			}
    		opcion.getDatosAdicionales().put(dato.getId(), null);
    	}
    	parametro.getOpcionesJson().add(opcion);
    	

    	// Valida las opciones anteriores para que tengan los datos adicionales
    	for(JsonOpcion op:parametro.getOpcionesJson()) {
    		for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
    			if(op.getDatosAdicionales()==null){
    				op.setDatosAdicionales(new LinkedHashMap<Integer, String>());
    			}
    			if(!op.getDatosAdicionales().containsKey(dato.getId())){
        			op.getDatosAdicionales().put(dato.getId(), null);
    			}
    		}
    	}
	}

	
	/**
	 * Elimina una opción de lista
	 * Lo marca en falso para no borrarlo definitivamente y conservar la integridad referencial del dato para procesos ya creados
	 * @param opcion Opcion a remover de la lista
	 */
	public void removerOpcion(JsonOpcion opcion) {
		opcion.setActivo(false);
	}
	
	
	/**
	 * Obtiene el nombre de un dato adicional de un parámetro
	 * @param id Identificador del dato a buscar
	 * @return Cadena con el nombre del dato
	 */
	public String getNombreDato(int id) {
		String nombre = "";
		for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
			if(dato.getId()==id){
    			nombre = dato.getNombreDato();
    			break;
			}
		}
		return nombre;
	}
	
	
	/**
	 * Obtiene el tipo de un dato adicional de un parámetro
	 * @param id Identificador con el tipo de dato adicional a buscar
	 * @return String con el tipo de dato
	 */
	public String getTipoDato(int id) {
		String nombre = "";
		for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
			if(dato.getId()==id){
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
	 * @param id Identificador del dato adicional a buscar
	 * @return Entero con la longitud del dato consultado
	 */
	public int getLongitudDato(int id) {
		int nombre = 0;
		try {
			for (JsonDatosOpcionesParametro dato : parametro.getJsonDatosOpcionesParametro()) {
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
	 * Obtiene una lista de datos adicionales activos
	 * @return Lista con los datos adicionales configurados para el parámetro actual
	 */
	public List<JsonDatosOpcionesParametro> getListaDatosAdicionales() {
		List<JsonDatosOpcionesParametro> result = new ArrayList<>();
		for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
			if(dato.isActivo()){
    			result.add(dato);
			}
		}
		return result;
	}
	
	
	/**
	 * Obtiene lista de los identificadores de los datos adicionales de un parámetro
	 * @param opcion Opcion a consultar
	 * @return Lista de los identificadores de los datos adicionales de un parámetro
	 */
	public List<Integer> getListaDatosAdicionales(JsonOpcion opcion) {
		List<Integer> datos = new ArrayList<>();
		if(opcion.getDatosAdicionales()!=null) {
			for(Integer id:opcion.getDatosAdicionales().keySet()) {
				for(JsonDatosOpcionesParametro dato:parametro.getJsonDatosOpcionesParametro()) {
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
	 * Agrega un dato adicional a las opciones del parametro
	 */
	public void agregarDatoOpcion() {
    	// Encuentra el maximo id para asignar a la nueva opción
    	int maxid = 0;
    	for(JsonDatosOpcionesParametro opcion:parametro.getJsonDatosOpcionesParametro()) {
    		if(opcion.getId() > maxid) {
    			maxid = opcion.getId();
    		}
    	}

    	JsonDatosOpcionesParametro opcion = new JsonDatosOpcionesParametro();
    	opcion.setId(maxid+1);
    	opcion.setLongitud("32");
    	opcion.setActivo(true);
    	opcion.setTipoDato(TiposDatosCampos.TEXT.name());
    	parametro.getJsonDatosOpcionesParametro().add(opcion);
	}

	
    /**
	 * Remueve un dato adicional a las opciones del parametro
	 * @param opcion Opcion dentro del parámetro a remover
	 */
	public void removerDatoOpcion(JsonDatosOpcionesParametro opcion) {
		opcion.setActivo(false);
	}
	
	
	/**
	 * Cargar el archivo de una plantilla
	 * @param event Evento de carga de archivo
	 */
	public void cargarArchivos(FileUploadEvent event) {
		if(event!=null) {
			this.archivoCargado = event.getFile();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, event.getFile().getFileName() + " cargado.",  event.getFile().getFileName() + " cargado."));
		}
	}
	
	
	
	/**
	 * Permite agregar un trozo de secuencia a la entrada
	 */
	public void agregarSecuenciaGlobal() {
		if(this.secuenciaGlobal==null) {
			this.secuenciaGlobal = new ArrayList<>();
		}
		SecuenciaGlobal secuencia = new SecuenciaGlobal();
		secuencia.setActivo(true);
		secuenciaGlobal.add(secuencia);
	}

	/**
	 * Elimina un trozo de la secuencia global
	 * @param secuencia Secuencia global a eliminar
	 */
	public void eliminarSecuenciaGlobal(SecuenciaGlobal secuencia) {
		try {
			parametrosControl.eliminarSecuenciaGlobal(secuencia);
			secuenciaGlobal.remove(secuencia);
		} catch(Exception e){
			CorrespondenciaException ex = new CorrespondenciaException(ERROR_GENERAL_KEY, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessageOriginalEx(), ex.getLocalizedMessageOriginalEx()));
		}
	}


	/**
	 * Lista con objetos de tipo JsonOpcion cuando el parámetro es tipo lista
	 * @param parametro Parámetro a evaluar sus opciones
	 * @return Lista con valores activos
	 */
	public List<JsonOpcion> obtenerOpcionesJsonActivas(Parametro parametro) {
		return parametrosControl.obtenerOpcionesJsonActivas(parametro);
	}
	
	
	/**
	 * Borra el archivo cargado de un parámetro
	 */
	public void borrarArchivos(){
		archivoCargado = null;
	}
	
	
	/**
	 * Verifica si el tipo de campo debe validarse para longitud
	 * @param tipoCampo Tipo de campo a validar
	 * @return true si el tipo de campo debe validar longitud
	 */
	public boolean isTipoCampoLongitud(String tipoCampo) {
		if(tipoCampo==null || tipoCampo.isEmpty()) {
			tipoCampo = "TEXT";
		}
		return parametrosControl.getTiposCamposLongitud().contains(tipoCampo);
	}

	
	/**
	 * Obtiene el Time zone por defecto
	 * @return Time zone por defecto
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
	 * Obtiene el valor del atributo list
	 * @return El valor del atributo list
	 */
	public LazyDataModel<Parametro> getList() {
		return list;
	}

	/**
	 * Establece el valor del atributo list
	 * @param list con el valor del atributo list a establecer
	 */
	public void setList(LazyDataModel<Parametro> list) {
		this.list = list;
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
	 * Obtiene el valor del atributo tiposParametros
	 * @return El valor del atributo tiposParametros
	 */
	public List<SelectItem> getTiposParametros() {
		return tiposParametros;
	}

	/**
	 * Establece el valor del atributo tiposParametros
	 * @param tiposParametros con el valor del atributo tiposParametros a establecer
	 */
	public void setTiposParametros(List<SelectItem> tiposParametros) {
		this.tiposParametros = tiposParametros;
	}

	/**
	 * Obtiene el valor del atributo tipoParametroSeleccionado
	 * @return El valor del atributo tipoParametroSeleccionado
	 */
	public String getTipoParametroSeleccionado() {
		return tipoParametroSeleccionado;
	}

	/**
	 * Establece el valor del atributo tipoParametroSeleccionado
	 * @param tipoParametroSeleccionado con el valor del atributo tipoParametroSeleccionado a establecer
	 */
	public void setTipoParametroSeleccionado(String tipoParametroSeleccionado) {
		this.tipoParametroSeleccionado = tipoParametroSeleccionado;
	}

	/**
	 * Obtiene el valor del atributo filtroStatus
	 * @return El valor del atributo filtroStatus
	 */
	public Boolean getFiltroStatus() {
		return filtroStatus;
	}

	/**
	 * Establece el valor del atributo filtroStatus
	 * @param filtroStatus con el valor del atributo filtroStatus a establecer
	 */
	public void setFiltroStatus(Boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	/**
	 * Obtiene el valor del atributo parametro
	 * @return El valor del atributo parametro
	 */
	public Parametro getParametro() {
		return parametro;
	}

	/**
	 * Establece el valor del atributo parametro
	 * @param parametro con el valor del atributo parametro a establecer
	 */
	public void setParametro(Parametro parametro) {
		this.parametro = parametro;
	}

	/**
	 * Obtiene el valor del atributo tiposDatos
	 * @return El valor del atributo tiposDatos
	 */
	public List<SelectItem> getTiposDatos() {
		return tiposDatos;
	}

	/**
	 * Establece el valor del atributo tiposDatos
	 * @param tiposDatos con el valor del atributo tiposDatos a establecer
	 */
	public void setTiposDatos(List<SelectItem> tiposDatos) {
		this.tiposDatos = tiposDatos;
	}

	/**
	 * Obtiene el valor del atributo archivoCargado
	 * @return El valor del atributo archivoCargado
	 */
	public UploadedFile getArchivoCargado() {
		return archivoCargado;
	}

	/**
	 * Establece el valor del atributo archivoCargado
	 * @param archivoCargado con el valor del atributo archivoCargado a establecer
	 */
	public void setArchivoCargado(UploadedFile archivoCargado) {
		this.archivoCargado = archivoCargado;
	}

	/**
	 * Obtiene el valor del atributo logger
	 * @return El valor del atributo logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Obtiene el valor del atributo secuenciaGlobal
	 * @return El valor del atributo secuenciaGlobal
	 */
	public List<SecuenciaGlobal> getSecuenciaGlobal() {
		return secuenciaGlobal;
	}

	/**
	 * Establece el valor del atributo secuenciaGlobal
	 * @param secuenciaGlobal con el valor del atributo secuenciaGlobal a establecer
	 */
	public void setSecuenciaGlobal(List<SecuenciaGlobal> secuenciaGlobal) {
		this.secuenciaGlobal = secuenciaGlobal;
	}

	/**
	 * Obtiene el valor del atributo archivoGuardado
	 * @return El valor del atributo archivoGuardado
	 */
	public Archivo getArchivoGuardado() {
		return archivoGuardado;
	}

	/**
	 * Establece el valor del atributo archivoGuardado
	 * @param archivoGuardado con el valor del atributo archivoGuardado a establecer
	 */
	public void setArchivoGuardado(Archivo archivoGuardado) {
		this.archivoGuardado = archivoGuardado;
	}

}

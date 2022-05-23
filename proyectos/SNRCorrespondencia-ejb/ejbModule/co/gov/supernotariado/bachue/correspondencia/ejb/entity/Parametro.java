package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonDatosOpcionesParametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;

/**
 * Entidad para mapear la tabla SDB_PGN_CORRESPONDENCIA_PARAMETRO
 */
@Entity
@Table(name="SDB_PGN_CORRESPONDENCIA_PARAMETRO")
@NamedQueries({
	@NamedQuery(name = Parametro.PARAMETRO_ACTIVOS_POR_TIPO, query = "SELECT t FROM Parametro t WHERE t.tipoParametro = :tipoParametro AND t.activo = true"),
	@NamedQuery(name = Parametro.PARAMETRO_ACTIVOS_POR_TIPO_NOMBRE, query = "SELECT t FROM Parametro t WHERE t.tipoParametro = :tipoParametro AND t.nombre = :nombreParametro AND t.activo = true")
})
public class Parametro implements EntidadGenerica, Serializable {
	
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String PARAMETRO_ACTIVOS_POR_TIPO = "PARAMETRO_ACTIVOS_POR_TIPO";

	/** Constante nombre consulta */
	public static final String PARAMETRO_ACTIVOS_POR_TIPO_NOMBRE = "PARAMETRO_ACTIVOS_POR_TIPO_NOMBRE";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_PARAMETRO", sequenceName = "SEC_PGN_CORRESPONDENCIA_PARAMETRO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_PARAMETRO")
	private long id;

	/** Indica si el registro esta activo o inactivo */
	private boolean activo;

	/** Nombre de parámetro */
	private String nombre;

	/** Descripción del parámetro */
	private String descripcion;

	/** Tipo de parámetro */
	@Column(name="TIPO_PARAMETRO")
	private String tipoParametro;

	/** Fecha de creación del registro */
	@Column(name="FECHA_CREACION")
	private Date fechaCreacion;

	/** Identificador del usuario de creación del registro */
	@Column(name="ID_USUARIO_CREACION")
	private String idUsuarioCreacion;

	/** Dirección IP del usuario de creación del registro */
	@Column(name="IP_CREACION")
	private String ipCreacion;

	/** Fecha de modificación del registro */
	@Column(name="FECHA_MODIFICACION")
	private Date fechaModificacion;

	/** Identificador del usuario de modificación del registro */
	@Column(name="ID_USUARIO_MODIFICACION")
	private String idUsuarioModificacion;
	
	/** Dirección IP del usuario de modificación del registro */
	@Column(name="IP_MODIFICACION")
	private String ipModificacion;

	/** Indica si permite agregar items a la lista en la vista de ejecución del proceso */
	@Column(name="PERMITE_AGREGAR_FORMULARIO")
	private boolean permiteAgregarFormulario;

	/** Guarda la longitud que permite para los nombres de los items en las listas predefinidas */
	@Column(name="LONGITUD_ITEM")
	private int longitudItem;

	/** Lista de opciones cuando el parámetro es tipo lista */
	private String opciones;

	/** Indica si la Lista de opciones tiene datos de tipo clave valor */
	@Column(name="OPCIONES_CLAVE_VALOR")
	private boolean opcionesClaveValor;

	/** Lista con datos adicionales que se necesiten para parámetros tipo lista */
	@Column(name="OPCIONES_DATOS")
	private String opcionesDatos;

	/** Lista con objetos de tipo JsonDatosOpcionesParametro que se necesiten para parámetros tipo lista */
	@Transient
	private transient List<JsonDatosOpcionesParametro> jsonDatosOpcionesParametro;

	/** Objeto del usuario de creación del registro */
	@Transient
	private transient UsuarioTO usuarioCreacion;

	/** Objeto del usuario de modificación del registro */
	@Transient
	private transient UsuarioTO usuarioModificacion;

	/** Lista con objetos de tipo JsonOpcion cuando el parámetro es tipo lista */
	@Transient
	private transient List<JsonOpcion> opcionesJson;

	/** Constructor */
	public Parametro() {
		
	}
	
	/**
	 * Constructor que recibe el id del parámetro
	 * @param id
	 */
	public Parametro(long id){
		this.id = id;
	}
	
	/**
	 * Obtiene el valor del atributo jsonDatosOpcionesParametro
	 * @return El valor del atributo jsonDatosOpcionesParametro
	 */
	public List<JsonDatosOpcionesParametro> getJsonDatosOpcionesParametro() {
		return jsonDatosOpcionesParametro;
	}

	/**
	 * Obtiene el valor del atributo opcionesJson
	 * @return El valor del atributo opcionesJson
	 */
	public List<JsonOpcion> getOpcionesJson() {
		return opcionesJson;
	}
		
	/**
	 * Obtiene el valor del atributo id
	 * @return El valor del atributo id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el valor del atributo id
	 * @param id con el valor del atributo id a establecer
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Obtiene el valor del atributo activo
	 * @return El valor del atributo activo
	 */
	public boolean isActivo() {
		return activo;
	}

	/**
	 * Establece el valor del atributo activo
	 * @param activo con el valor del atributo activo a establecer
	 */
	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/**
	 * Obtiene el valor del atributo nombre
	 * @return El valor del atributo nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el valor del atributo nombre
	 * @param nombre con el valor del atributo nombre a establecer
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el valor del atributo descripcion
	 * @return El valor del atributo descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece el valor del atributo descripcion
	 * @param descripcion con el valor del atributo descripcion a establecer
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene el valor del atributo tipoParametro
	 * @return El valor del atributo tipoParametro
	 */
	public String getTipoParametro() {
		return tipoParametro;
	}

	/**
	 * Establece el valor del atributo tipoParametro
	 * @param tipoParametro con el valor del atributo tipoParametro a establecer
	 */
	public void setTipoParametro(String tipoParametro) {
		this.tipoParametro = tipoParametro;
	}

	/**
	 * Obtiene el valor del atributo fechaCreacion
	 * @return El valor del atributo fechaCreacion
	 */
	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * Establece el valor del atributo fechaCreacion
	 * @param fechaCreacion con el valor del atributo fechaCreacion a establecer
	 */
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * Obtiene el valor del atributo idUsuarioCreacion
	 * @return El valor del atributo idUsuarioCreacion
	 */
	public String getIdUsuarioCreacion() {
		return idUsuarioCreacion;
	}

	/**
	 * Establece el valor del atributo idUsuarioCreacion
	 * @param idUsuarioCreacion con el valor del atributo idUsuarioCreacion a establecer
	 */
	public void setIdUsuarioCreacion(String idUsuarioCreacion) {
		this.idUsuarioCreacion = idUsuarioCreacion;
	}

	/**
	 * Obtiene el valor del atributo ipCreacion
	 * @return El valor del atributo ipCreacion
	 */
	public String getIpCreacion() {
		return ipCreacion;
	}

	/**
	 * Establece el valor del atributo ipCreacion
	 * @param ipCreacion con el valor del atributo ipCreacion a establecer
	 */
	public void setIpCreacion(String ipCreacion) {
		this.ipCreacion = ipCreacion;
	}

	/**
	 * Obtiene el valor del atributo fechaModificacion
	 * @return El valor del atributo fechaModificacion
	 */
	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	/**
	 * Establece el valor del atributo fechaModificacion
	 * @param fechaModificacion con el valor del atributo fechaModificacion a establecer
	 */
	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	/**
	 * Obtiene el valor del atributo idUsuarioModificacion
	 * @return El valor del atributo idUsuarioModificacion
	 */
	public String getIdUsuarioModificacion() {
		return idUsuarioModificacion;
	}

	/**
	 * Establece el valor del atributo idUsuarioModificacion
	 * @param idUsuarioModificacion con el valor del atributo idUsuarioModificacion a establecer
	 */
	public void setIdUsuarioModificacion(String idUsuarioModificacion) {
		this.idUsuarioModificacion = idUsuarioModificacion;
	}

	/**
	 * Obtiene el valor del atributo ipModificacion
	 * @return El valor del atributo ipModificacion
	 */
	public String getIpModificacion() {
		return ipModificacion;
	}

	/**
	 * Establece el valor del atributo ipModificacion
	 * @param ipModificacion con el valor del atributo ipModificacion a establecer
	 */
	public void setIpModificacion(String ipModificacion) {
		this.ipModificacion = ipModificacion;
	}

	/**
	 * Obtiene el valor del atributo permiteAgregarFormulario
	 * @return El valor del atributo permiteAgregarFormulario
	 */
	public boolean isPermiteAgregarFormulario() {
		return permiteAgregarFormulario;
	}

	/**
	 * Establece el valor del atributo permiteAgregarFormulario
	 * @param permiteAgregarFormulario con el valor del atributo permiteAgregarFormulario a establecer
	 */
	public void setPermiteAgregarFormulario(boolean permiteAgregarFormulario) {
		this.permiteAgregarFormulario = permiteAgregarFormulario;
	}

	/**
	 * Obtiene el valor del atributo opciones
	 * @return El valor del atributo opciones
	 */
	public String getOpciones() {
		return opciones;
	}

	/**
	 * Establece el valor del atributo opciones
	 * @param opciones con el valor del atributo opciones a establecer
	 */
	public void setOpciones(String opciones) {
		this.opciones = opciones;
	}

	/**
	 * Obtiene el valor del atributo opcionesClaveValor
	 * @return El valor del atributo opcionesClaveValor
	 */
	public boolean isOpcionesClaveValor() {
		return opcionesClaveValor;
	}

	/**
	 * Establece el valor del atributo opcionesClaveValor
	 * @param opcionesClaveValor con el valor del atributo opcionesClaveValor a establecer
	 */
	public void setOpcionesClaveValor(boolean opcionesClaveValor) {
		this.opcionesClaveValor = opcionesClaveValor;
	}

	/**
	 * Obtiene el valor del atributo opcionesDatos
	 * @return El valor del atributo opcionesDatos
	 */
	public String getOpcionesDatos() {
		return opcionesDatos;
	}

	/**
	 * Establece el valor del atributo opcionesDatos
	 * @param opcionesDatos con el valor del atributo opcionesDatos a establecer
	 */
	public void setOpcionesDatos(String opcionesDatos) {
		this.opcionesDatos = opcionesDatos;
	}

	/**
	 * Obtiene el valor del atributo usuarioCreacion
	 * @return El valor del atributo usuarioCreacion
	 */
	public UsuarioTO getUsuarioCreacion() {
		return usuarioCreacion;
	}

	/**
	 * Establece el valor del atributo usuarioCreacion
	 * @param usuarioCreacion con el valor del atributo usuarioCreacion a establecer
	 */
	public void setUsuarioCreacion(UsuarioTO usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}

	/**
	 * Obtiene el valor del atributo usuarioModificacion
	 * @return El valor del atributo usuarioModificacion
	 */
	public UsuarioTO getUsuarioModificacion() {
		return usuarioModificacion;
	}

	/**
	 * Establece el valor del atributo usuarioModificacion
	 * @param usuarioModificacion con el valor del atributo usuarioModificacion a establecer
	 */
	public void setUsuarioModificacion(UsuarioTO usuarioModificacion) {
		this.usuarioModificacion = usuarioModificacion;
	}

	/**
	 * Establece el valor del atributo jsonDatosOpcionesParametro
	 * @param jsonDatosOpcionesParametro con el valor del atributo jsonDatosOpcionesParametro a establecer
	 */
	public void setJsonDatosOpcionesParametro(List<JsonDatosOpcionesParametro> jsonDatosOpcionesParametro) {
		this.jsonDatosOpcionesParametro = jsonDatosOpcionesParametro;
	}

	/**
	 * Establece el valor del atributo opcionesJson
	 * @param opcionesJson con el valor del atributo opcionesJson a establecer
	 */
	public void setOpcionesJson(List<JsonOpcion> opcionesJson) {
		this.opcionesJson = opcionesJson;
	}

	/**
	 * Obtiene el valor del atributo longitudItem
	 * @return El valor del atributo longitudItem
	 */
	public int getLongitudItem() {
		return longitudItem;
	}

	/**
	 * Establece el valor del atributo longitudItem
	 * @param longitudItem con el valor del atributo longitudItem a establecer
	 */
	public void setLongitudItem(int longitudItem) {
		this.longitudItem = longitudItem;
	}

}

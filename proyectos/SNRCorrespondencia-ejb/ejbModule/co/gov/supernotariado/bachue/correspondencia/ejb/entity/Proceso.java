package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonProcesoData;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_PROCESO
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_PROCESO")
@NamedQueries({
	@NamedQuery(name = Proceso.PROCESOS_POR_IDENTIFICADOR, query = "SELECT t FROM Proceso t WHERE t.identificador = ?1 ORDER BY t.id "),
	@NamedQuery(name = Proceso.PROCESOS_POR_IDENTIFICADOR_ORDERDESC, query = "SELECT t FROM Proceso t WHERE t.identificador = ?1 ORDER BY t.id DESC "),
	@NamedQuery(name = Proceso.PROCESO_ACTIVO_POR_IDENTIFICADOR_ORDERDESC, query = "SELECT t FROM Proceso t WHERE t.identificador = ?1 AND t.activo = true ORDER BY t.id DESC "),
	@NamedQuery(name = Proceso.PROCESO_ACTIVO_POR_NOMBRECLAVE_ORDERDESC, query = "SELECT t FROM Proceso t WHERE t.nombreClave = ?1 AND t.activo = true ORDER BY t.id DESC ")
})
public class Proceso implements EntidadGenerica, Serializable {

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String PROCESOS_POR_IDENTIFICADOR = "PROCESOS_POR_IDENTIFICADOR";

	/** Constante nombre consulta */
	public static final String PROCESOS_POR_IDENTIFICADOR_ORDERDESC = "PROCESOS_POR_IDENTIFICADOR_ORDERDESC";

	/** Constante nombre consulta */
	public static final String PROCESO_ACTIVO_POR_IDENTIFICADOR_ORDERDESC = "PROCESO_ACTIVO_POR_IDENTIFICADOR_ORDERDESC";

	/** Constante nombre consulta */
	public static final String PROCESO_ACTIVO_POR_NOMBRECLAVE_ORDERDESC = "PROCESO_ACTIVO_POR_NOMBRECLAVE_ORDERDESC";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_PROCESO", sequenceName = "SEC_PNG_CORRESPONDENCIA_PROCESO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_PROCESO")
	private long id;

	/** Identificador del proceso"."  Al generar versiones este identificador permanece igual"."  */
	private long identificador;

	/** Indica si el registro esta activo o inactivo */
	private Boolean activo;

	/** Descripción del proceso */
	private String descripcion;

	/** Nombre del proceso */
	private String nombre;

	/** Nombre clave para el proceso */
	@Column(name="NOMBRE_CLAVE")
	private String nombreClave;

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

	/** Indica si se envía notificación al finalizar el proceso al usuario que lo inició */
	@Column(name="NOTIFICACION_FINALIZACION")
	private int notificacionFinalizacion;

	/** Indica si hay restricción por periodo de tiempo para ejecutar el proceso.
	 *  1-anual,2-semestral,3-trimestral,4-bimestral-5-mensual,6-quincenal,7-semanal,8-diario */
	@Column(name="RESTRICCION_TIEMPO_EJECUTADO")
	private int restriccionTiempoEjecutado;
	
	/** Versión actual del proceso */
	@Column(name="NUMERO_MAYOR_VERSION")
	private int numeroMayorVersion;

	/** Datos adicionales de configuración del proceso */
	@Column(name="DATOS_ADICIONALES")
	private String datosAdicionales;

	/** Indicador auxiliar para las versiones del proceso. 
	 * El valor es verdadero cuando se cambia la versión o cuando se elimina el proceso*/
	private boolean archivado;

	/** Tipo de proceso según parametro seleccionado */
	@ManyToOne
	@JoinColumn(name = "ID_TIPO_PROCESO", insertable = false, updatable = false)
	private Parametro tipoProceso;

	/** Identificador Tipo de proceso según parametro seleccionado */
	@Column(name = "ID_TIPO_PROCESO")
	private long idTipoProceso;

	/** Etiqueta para el campo categoría */
	@Column(name="NOMBRE_CATEGORIA")
	private String nombreCategoria;
	
	/** Temporal para obtener las categorias asociadas al proceso */
	@Transient
	private List<ProcesoCategoria> categorias;

	/** Atributo temporal para mantener el usuario que crea el proceso */
	@Transient
	private transient UsuarioTO usuarioCreacion;

	/** Atributo temporal para mantener el usuario que modifica el proceso */
	@Transient
	private transient UsuarioTO usuarioModificacion;

	/** Atributo temporal para mantener los datos adicionales de configuración del proceso */
	@Transient
	private transient JsonProcesoData jsonProcesoData;

	/**
	 * Obtiene el valor del atributo jsonProcesoData
	 * @return El valor del atributo jsonProcesoData
	 */
	public JsonProcesoData getJsonProcesoData() {
		return jsonProcesoData;
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
	 * Obtiene el valor del atributo identificador
	 * @return El valor del atributo identificador
	 */
	public long getIdentificador() {
		return identificador;
	}

	/**
	 * Establece el valor del atributo identificador
	 * @param identificador con el valor del atributo identificador a establecer
	 */
	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}

	/**
	 * Obtiene el valor del atributo activo
	 * @return El valor del atributo activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Establece el valor del atributo activo
	 * @param activo con el valor del atributo activo a establecer
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
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
	 * Obtiene el valor del atributo notificacionFinalizacion
	 * @return El valor del atributo notificacionFinalizacion
	 */
	public int getNotificacionFinalizacion() {
		return notificacionFinalizacion;
	}

	/**
	 * Establece el valor del atributo notificacionFinalizacion
	 * @param notificacionFinalizacion con el valor del atributo notificacionFinalizacion a establecer
	 */
	public void setNotificacionFinalizacion(int notificacionFinalizacion) {
		this.notificacionFinalizacion = notificacionFinalizacion;
	}

	/**
	 * Obtiene el valor del atributo restriccionTiempoEjecutado
	 * @return El valor del atributo restriccionTiempoEjecutado
	 */
	public int getRestriccionTiempoEjecutado() {
		return restriccionTiempoEjecutado;
	}

	/**
	 * Establece el valor del atributo restriccionTiempoEjecutado
	 * @param restriccionTiempoEjecutado con el valor del atributo restriccionTiempoEjecutado a establecer
	 */
	public void setRestriccionTiempoEjecutado(int restriccionTiempoEjecutado) {
		this.restriccionTiempoEjecutado = restriccionTiempoEjecutado;
	}

	/**
	 * Obtiene el valor del atributo numeroMayorVersion
	 * @return El valor del atributo numeroMayorVersion
	 */
	public int getNumeroMayorVersion() {
		return numeroMayorVersion;
	}

	/**
	 * Establece el valor del atributo numeroMayorVersion
	 * @param numeroMayorVersion con el valor del atributo numeroMayorVersion a establecer
	 */
	public void setNumeroMayorVersion(int numeroMayorVersion) {
		this.numeroMayorVersion = numeroMayorVersion;
	}

	/**
	 * Obtiene el valor del atributo datosAdicionales
	 * @return El valor del atributo datosAdicionales
	 */
	public String getDatosAdicionales() {
		return datosAdicionales;
	}

	/**
	 * Establece el valor del atributo datosAdicionales
	 * @param datosAdicionales con el valor del atributo datosAdicionales a establecer
	 */
	public void setDatosAdicionales(String datosAdicionales) {
		this.datosAdicionales = datosAdicionales;
	}

	/**
	 * Obtiene el valor del atributo archivado
	 * @return El valor del atributo archivado
	 */
	public boolean isArchivado() {
		return archivado;
	}

	/**
	 * Establece el valor del atributo archivado
	 * @param archivado con el valor del atributo archivado a establecer
	 */
	public void setArchivado(boolean archivado) {
		this.archivado = archivado;
	}

	/**
	 * Obtiene el valor del atributo tipoProceso
	 * @return El valor del atributo tipoProceso
	 */
	public Parametro getTipoProceso() {
		return tipoProceso;
	}

	/**
	 * Establece el valor del atributo tipoProceso
	 * @param tipoProceso con el valor del atributo tipoProceso a establecer
	 */
	public void setTipoProceso(Parametro tipoProceso) {
		this.tipoProceso = tipoProceso;
	}

	/**
	 * Obtiene el valor del atributo nombreCategoria
	 * @return El valor del atributo nombreCategoria
	 */
	public String getNombreCategoria() {
		return nombreCategoria;
	}

	/**
	 * Establece el valor del atributo nombreCategoria
	 * @param nombreCategoria con el valor del atributo nombreCategoria a establecer
	 */
	public void setNombreCategoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}

	/**
	 * Obtiene el valor del atributo categorias
	 * @return El valor del atributo categorias
	 */
	public List<ProcesoCategoria> getCategorias() {
		return categorias;
	}

	/**
	 * Establece el valor del atributo categorias
	 * @param categorias con el valor del atributo categorias a establecer
	 */
	public void setCategorias(List<ProcesoCategoria> categorias) {
		this.categorias = categorias;
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
	 * Establece el valor del atributo jsonProcesoData
	 * @param jsonProcesoData con el valor del atributo jsonProcesoData a establecer
	 */
	public void setJsonProcesoData(JsonProcesoData jsonProcesoData) {
		this.jsonProcesoData = jsonProcesoData;
	}

	/**
	 * Obtiene el valor del atributo nombreClave
	 * @return El valor del atributo nombreClave
	 */
	public String getNombreClave() {
		return nombreClave;
	}

	/**
	 * Establece el valor del atributo nombreClave
	 * @param nombreClave con el valor del atributo nombreClave a establecer
	 */
	public void setNombreClave(String nombreClave) {
		this.nombreClave = nombreClave;
	}

	/**
	 * Obtiene el valor del atributo idTipoProceso
	 * @return El valor del atributo idTipoProceso
	 */
	public long getIdTipoProceso() {
		return idTipoProceso;
	}

	/**
	 * Establece el valor del atributo idTipoProceso
	 * @param idTipoProceso con el valor del atributo idTipoProceso a establecer
	 */
	public void setIdTipoProceso(long idTipoProceso) {
		this.idTipoProceso = idTipoProceso;
	}

}

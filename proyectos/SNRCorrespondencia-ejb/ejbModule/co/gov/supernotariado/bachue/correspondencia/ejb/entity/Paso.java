package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.io.Serializable;
import java.util.Date;

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

import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonPasoData;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_PASO
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_PASO")
@NamedQueries({
	@NamedQuery(name = Paso.PASO_ACTIVOS_POR_PROCESO_ORDEN, query = "SELECT t FROM Paso t WHERE t.activo = true AND t.proceso.id = :procesoId ORDER BY t.ordenPaso"),
	@NamedQuery(name = Paso.PASO_ACTIVOS_POR_PROCESO_ORDEN_DESC, query = "SELECT t FROM Paso t WHERE t.activo = true AND t.proceso.id = :procesoId ORDER BY t.ordenPaso DESC")
})
public class Paso implements EntidadGenerica, Serializable {

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String PASO_ACTIVOS_POR_PROCESO_ORDEN = "PASO_ACTIVOS_POR_PROCESO_ORDEN";

	/** Constante nombre consulta */
	public static final String PASO_ACTIVOS_POR_PROCESO_ORDEN_DESC = "PASO_ACTIVOS_POR_PROCESO_ORDEN_DESC";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_PASO", sequenceName = "SEC_PNG_CORRESPONDENCIA_PASO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_PASO")
	private long id;

	/** Indica si el registro esta activo o inactivo */
	private boolean activo;

	/** Descripción del paso */
	private String descripcion;

	/** Nombre del paso */
	private String nombre;

	/** Orden del paso dentro del proceso */
	@Column(name="ORDEN_PASO")
	private Long ordenPaso;

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

	/** Periodo de duración del paso: días, semanas, etc */
	@Column(name="DURACION_PERIODO")
	private String duracionPeriodo;
	
	/** Duración del paso */
	@Column(name="DURACION_MAGNITUD")
	private Long duracion;

	/** Grupo asignado para el paso */
	@Column(name="ID_GRUPO_ASIGNADO")
	private String grupoAsignadoId;

	/** Usuario asignado para el paso */
	@Column(name="ID_USUARIO_ASIGNADO")
	private String usuarioAsignadoId;

	/** Enviar correo de asignación.
	 * 0-No enviar notificaciones, 1-Correo electronico, 2-SMS, 3-SMS y Correo electronico */
	@Column(name="ENVIAR_CORREO_ELECTRONICO")
	private int enviarEmail;

	/** Datos adicionales de configuración del paso en formato JSON */
	@Column(name="DATOS_ADICIONALES")
	private String datosAdicionales;

	/** Identificador del proceso */
	@ManyToOne
	@JoinColumn(name = "ID_PROCESO")
	private Proceso proceso;

	/** Enviar mensajes de recordatorio */
	@Column(name="ENVIAR_RECORDATORIO")
	private int enviarRecordatorio;

	/** Enviar mensaje de vencimiento según duración del paso */
	@Column(name="AVISO_VENCIMIENTO")
	private int avisoVencimiento;

	/** Enviar correo cuando se ejecuta el paso */
	@Column(name="AVISO_EJECUCION")
	private int avisoEjecucion;

	/** Frecuencia de recordatorio para calcular proximo envio de recordatorio */
	@Column(name="FRECUENCIA_RECORDATORIO_MAGNITUD")
	private Integer frecuenciaRecordatorio;
	
	/** Tipo de Frecuencia de recordatorio segun TiposDuracion*/
	@Column(name="FRECUENCIA_RECORDATORIO_PERIODO")
	private String frecuenciaRecordatorioPeriodo;

	/** Indica si se tienen cuenta horas laborales. ej 8-18 */
	@Column(name="HORARIO_LABORAL")
	private String horarioLaboral;

	/** Temporal para obtener el grupo asignado al paso  */
	@Transient
	private transient GrupoTO grupoAsignado;

	/** Temporal para obtener el usuarios asignado al paso  */
	@Transient
	private transient UsuarioTO usuarioAsignado;

	/** Temporal para saber si es un salto de paso, no secuencial dentro del proceso  */
	@Transient
	private boolean saltoPaso = false;
	
	/** Temporal para mantener los datos adicionales de configuración del paso  */
	@Transient
	private transient JsonPasoData jsonPasoData;
	
	/**
	 * Indica si el campo observaciones es requerido
	 */
	@Transient
	private transient boolean campoObservacionesRequerido;

	/**
	 * Obtiene el valor del atributo jsonPasoData
	 * @return El valor del atributo jsonPasoData
	 */
	public JsonPasoData getJsonPasoData() {
		return jsonPasoData;
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
	 * Obtiene el valor del atributo ordenPaso
	 * @return El valor del atributo ordenPaso
	 */
	public Long getOrdenPaso() {
		return ordenPaso;
	}


	/**
	 * Establece el valor del atributo ordenPaso
	 * @param ordenPaso con el valor del atributo ordenPaso a establecer
	 */
	public void setOrdenPaso(Long ordenPaso) {
		this.ordenPaso = ordenPaso;
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
	 * Obtiene el valor del atributo duracionPeriodo
	 * @return El valor del atributo duracionPeriodo
	 */
	public String getDuracionPeriodo() {
		return duracionPeriodo;
	}


	/**
	 * Establece el valor del atributo duracionPeriodo
	 * @param duracionPeriodo con el valor del atributo duracionPeriodo a establecer
	 */
	public void setDuracionPeriodo(String duracionPeriodo) {
		this.duracionPeriodo = duracionPeriodo;
	}


	/**
	 * Obtiene el valor del atributo duracion
	 * @return El valor del atributo duracion
	 */
	public Long getDuracion() {
		return duracion;
	}


	/**
	 * Establece el valor del atributo duracion
	 * @param duracion con el valor del atributo duracion a establecer
	 */
	public void setDuracion(Long duracion) {
		this.duracion = duracion;
	}


	/**
	 * Obtiene el valor del atributo grupoAsignadoId
	 * @return El valor del atributo grupoAsignadoId
	 */
	public String getGrupoAsignadoId() {
		return grupoAsignadoId;
	}


	/**
	 * Establece el valor del atributo grupoAsignadoId
	 * @param grupoAsignadoId con el valor del atributo grupoAsignadoId a establecer
	 */
	public void setGrupoAsignadoId(String grupoAsignadoId) {
		this.grupoAsignadoId = grupoAsignadoId;
	}


	/**
	 * Obtiene el valor del atributo usuarioAsignadoId
	 * @return El valor del atributo usuarioAsignadoId
	 */
	public String getUsuarioAsignadoId() {
		return usuarioAsignadoId;
	}


	/**
	 * Establece el valor del atributo usuarioAsignadoId
	 * @param usuarioAsignadoId con el valor del atributo usuarioAsignadoId a establecer
	 */
	public void setUsuarioAsignadoId(String usuarioAsignadoId) {
		this.usuarioAsignadoId = usuarioAsignadoId;
	}


	/**
	 * Obtiene el valor del atributo enviarEmail
	 * @return El valor del atributo enviarEmail
	 */
	public int getEnviarEmail() {
		return enviarEmail;
	}


	/**
	 * Establece el valor del atributo enviarEmail
	 * @param enviarEmail con el valor del atributo enviarEmail a establecer
	 */
	public void setEnviarEmail(int enviarEmail) {
		this.enviarEmail = enviarEmail;
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
	 * Obtiene el valor del atributo proceso
	 * @return El valor del atributo proceso
	 */
	public Proceso getProceso() {
		return proceso;
	}


	/**
	 * Establece el valor del atributo proceso
	 * @param proceso con el valor del atributo proceso a establecer
	 */
	public void setProceso(Proceso proceso) {
		this.proceso = proceso;
	}


	/**
	 * Obtiene el valor del atributo enviarRecordatorio
	 * @return El valor del atributo enviarRecordatorio
	 */
	public int getEnviarRecordatorio() {
		return enviarRecordatorio;
	}


	/**
	 * Establece el valor del atributo enviarRecordatorio
	 * @param enviarRecordatorio con el valor del atributo enviarRecordatorio a establecer
	 */
	public void setEnviarRecordatorio(int enviarRecordatorio) {
		this.enviarRecordatorio = enviarRecordatorio;
	}


	/**
	 * Obtiene el valor del atributo avisoVencimiento
	 * @return El valor del atributo avisoVencimiento
	 */
	public int getAvisoVencimiento() {
		return avisoVencimiento;
	}


	/**
	 * Establece el valor del atributo avisoVencimiento
	 * @param avisoVencimiento con el valor del atributo avisoVencimiento a establecer
	 */
	public void setAvisoVencimiento(int avisoVencimiento) {
		this.avisoVencimiento = avisoVencimiento;
	}


	/**
	 * Obtiene el valor del atributo avisoEjecucion
	 * @return El valor del atributo avisoEjecucion
	 */
	public int getAvisoEjecucion() {
		return avisoEjecucion;
	}


	/**
	 * Establece el valor del atributo avisoEjecucion
	 * @param avisoEjecucion con el valor del atributo avisoEjecucion a establecer
	 */
	public void setAvisoEjecucion(int avisoEjecucion) {
		this.avisoEjecucion = avisoEjecucion;
	}


	/**
	 * Obtiene el valor del atributo frecuenciaRecordatorio
	 * @return El valor del atributo frecuenciaRecordatorio
	 */
	public Integer getFrecuenciaRecordatorio() {
		return frecuenciaRecordatorio;
	}


	/**
	 * Establece el valor del atributo frecuenciaRecordatorio
	 * @param frecuenciaRecordatorio con el valor del atributo frecuenciaRecordatorio a establecer
	 */
	public void setFrecuenciaRecordatorio(Integer frecuenciaRecordatorio) {
		this.frecuenciaRecordatorio = frecuenciaRecordatorio;
	}


	/**
	 * Obtiene el valor del atributo frecuenciaRecordatorioPeriodo
	 * @return El valor del atributo frecuenciaRecordatorioPeriodo
	 */
	public String getFrecuenciaRecordatorioPeriodo() {
		return frecuenciaRecordatorioPeriodo;
	}


	/**
	 * Establece el valor del atributo frecuenciaRecordatorioPeriodo
	 * @param frecuenciaRecordatorioPeriodo con el valor del atributo frecuenciaRecordatorioPeriodo a establecer
	 */
	public void setFrecuenciaRecordatorioPeriodo(String frecuenciaRecordatorioPeriodo) {
		this.frecuenciaRecordatorioPeriodo = frecuenciaRecordatorioPeriodo;
	}


	/**
	 * Obtiene el valor del atributo horarioLaboral
	 * @return El valor del atributo horarioLaboral
	 */
	public String getHorarioLaboral() {
		return horarioLaboral;
	}


	/**
	 * Establece el valor del atributo horarioLaboral
	 * @param horarioLaboral con el valor del atributo horarioLaboral a establecer
	 */
	public void setHorarioLaboral(String horarioLaboral) {
		this.horarioLaboral = horarioLaboral;
	}


	/**
	 * Obtiene el valor del atributo grupoAsignado
	 * @return El valor del atributo grupoAsignado
	 */
	public GrupoTO getGrupoAsignado() {
		return grupoAsignado;
	}


	/**
	 * Establece el valor del atributo grupoAsignado
	 * @param grupoAsignado con el valor del atributo grupoAsignado a establecer
	 */
	public void setGrupoAsignado(GrupoTO grupoAsignado) {
		this.grupoAsignado = grupoAsignado;
	}


	/**
	 * Obtiene el valor del atributo usuarioAsignado
	 * @return El valor del atributo usuarioAsignado
	 */
	public UsuarioTO getUsuarioAsignado() {
		return usuarioAsignado;
	}


	/**
	 * Establece el valor del atributo usuarioAsignado
	 * @param usuarioAsignado con el valor del atributo usuarioAsignado a establecer
	 */
	public void setUsuarioAsignado(UsuarioTO usuarioAsignado) {
		this.usuarioAsignado = usuarioAsignado;
	}


	/**
	 * Obtiene el valor del atributo saltoPaso
	 * @return El valor del atributo saltoPaso
	 */
	public boolean isSaltoPaso() {
		return saltoPaso;
	}


	/**
	 * Establece el valor del atributo saltoPaso
	 * @param saltoPaso con el valor del atributo saltoPaso a establecer
	 */
	public void setSaltoPaso(boolean saltoPaso) {
		this.saltoPaso = saltoPaso;
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
	 * Establece el valor del atributo jsonPasoData
	 * @param jsonPasoData con el valor del atributo jsonPasoData a establecer
	 */
	public void setJsonPasoData(JsonPasoData jsonPasoData) {
		this.jsonPasoData = jsonPasoData;
	}
	
}

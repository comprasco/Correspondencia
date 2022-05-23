package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

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

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_PROCESO_SECUENCIA
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_PROCESO_SECUENCIA")
@NamedQueries({
	@NamedQuery(name = ProcesoSecuencia.PROCESOSECUENCIA_POR_PROCESO, query = "SELECT t FROM ProcesoSecuencia t WHERE t.proceso.id = ?1 ORDER BY t.id")
})
public class ProcesoSecuencia implements EntidadGenerica {
	
	/** Constante nombre consulta */
	public static final String PROCESOSECUENCIA_POR_PROCESO = "PROCESOSECUENCIA_POR_PROCESO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_PROCESOSECUENCIA", sequenceName = "SEC_PNG_CORRESPONDENCIA_PROCESO_SECUENCIA", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_PROCESOSECUENCIA")
	private long id;

	/** Proceso asociado a la secuencia */
	@ManyToOne
	@JoinColumn(name = "ID_PROCESO")
	private Proceso proceso;

	/** Valor actual de la secuencia */
	@Column(name="VALOR_SECUENCIA")
	private String valorSecuencia;

	/** Indica si la secuencia es autonumérica */
	@Column(name="AUTO_NUMERICO")
	private boolean autoNumerico;

	/** Indica si la secuencia es tipo fecha */
	@Column(name="TIPO_FECHA")
	private boolean tipoFecha;

	/** Indica si la secuencia es tipo fecha */
	@Column(name="REINICIAR_CADA")
	private int reiniciarCada;

	/** Indica el tamaño de la secuencia */
	@Column(name="TAMANO_SECUENCIA")
	private int tamanoSecuencia;

	/** Indica la fecha del último reinicio automático de la secuencia  */
	@Column(name="FECHA_ULTIMO_REINICIO")
	private Date fechaUltimoReinicio;

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

	/** Indica si el registro esta activo o inactivo */
	private boolean activo;

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
	 * Obtiene el valor del atributo valorSecuencia
	 * @return El valor del atributo valorSecuencia
	 */
	public String getValorSecuencia() {
		return valorSecuencia;
	}

	/**
	 * Establece el valor del atributo valorSecuencia
	 * @param valorSecuencia con el valor del atributo valorSecuencia a establecer
	 */
	public void setValorSecuencia(String valorSecuencia) {
		this.valorSecuencia = valorSecuencia;
	}

	/**
	 * Obtiene el valor del atributo autoNumerico
	 * @return El valor del atributo autoNumerico
	 */
	public boolean isAutoNumerico() {
		return autoNumerico;
	}

	/**
	 * Establece el valor del atributo autoNumerico
	 * @param autoNumerico con el valor del atributo autoNumerico a establecer
	 */
	public void setAutoNumerico(boolean autoNumerico) {
		this.autoNumerico = autoNumerico;
	}

	/**
	 * Obtiene el valor del atributo tipoFecha
	 * @return El valor del atributo tipoFecha
	 */
	public boolean isTipoFecha() {
		return tipoFecha;
	}

	/**
	 * Establece el valor del atributo tipoFecha
	 * @param tipoFecha con el valor del atributo tipoFecha a establecer
	 */
	public void setTipoFecha(boolean tipoFecha) {
		this.tipoFecha = tipoFecha;
	}

	/**
	 * Obtiene el valor del atributo reiniciarCada
	 * @return El valor del atributo reiniciarCada
	 */
	public int getReiniciarCada() {
		return reiniciarCada;
	}

	/**
	 * Establece el valor del atributo reiniciarCada
	 * @param reiniciarCada con el valor del atributo reiniciarCada a establecer
	 */
	public void setReiniciarCada(int reiniciarCada) {
		this.reiniciarCada = reiniciarCada;
	}

	/**
	 * Obtiene el valor del atributo tamanoSecuencia
	 * @return El valor del atributo tamanoSecuencia
	 */
	public int getTamanoSecuencia() {
		return tamanoSecuencia;
	}

	/**
	 * Establece el valor del atributo tamanoSecuencia
	 * @param tamanoSecuencia con el valor del atributo tamanoSecuencia a establecer
	 */
	public void setTamanoSecuencia(int tamanoSecuencia) {
		this.tamanoSecuencia = tamanoSecuencia;
	}

	/**
	 * Obtiene el valor del atributo fechaUltimoReinicio
	 * @return El valor del atributo fechaUltimoReinicio
	 */
	public Date getFechaUltimoReinicio() {
		return fechaUltimoReinicio;
	}

	/**
	 * Establece el valor del atributo fechaUltimoReinicio
	 * @param fechaUltimoReinicio con el valor del atributo fechaUltimoReinicio a establecer
	 */
	public void setFechaUltimoReinicio(Date fechaUltimoReinicio) {
		this.fechaUltimoReinicio = fechaUltimoReinicio;
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


}

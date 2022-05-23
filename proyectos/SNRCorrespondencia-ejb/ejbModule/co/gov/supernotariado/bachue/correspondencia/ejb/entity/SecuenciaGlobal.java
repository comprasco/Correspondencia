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
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_SECUENCIA_GLOBAL
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_SECUENCIA_GLOBAL")
@NamedQueries({
	@NamedQuery(name = SecuenciaGlobal.SECUENCIAGLOBAL_POR_PARAMETRO_ORDEN_ID_ASC, query = "SELECT t FROM SecuenciaGlobal t WHERE t.parametro.id = ?1 ORDER BY t.id ASC")
})
public class SecuenciaGlobal implements EntidadGenerica {
	
	/** Constante nombre consulta */
	public static final String SECUENCIAGLOBAL_POR_PARAMETRO_ORDEN_ID_ASC = "SECUENCIAGLOBAL_POR_PARAMETRO_ORDEN_ID_ASC";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_SECUENCIA_GLOBAL", sequenceName = "SEC_PNG_CORRESPONDENCIA_SECUENCIA_GLOBAL", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_SECUENCIA_GLOBAL")
	private long id;
	
	/**
	 * Parametro asociado
	 */
	@ManyToOne
	@JoinColumn(name = "ID_PARAMETRO")
	private Parametro parametro;

	/**
	 * Valor de la secuencia actual
	 */
	@Column(name = "VALOR_SECUENCIA")
	private String valorSecuencia;

	/**
	 * Indica si el campo es autonumerico
	 */
	@Column(name = "AUTO_NUMERICO")
	private boolean autonumerico;

	/**
	 * Tamaño de la secuencia en caracteres
	 */
	@Column(name = "TAMANO_SECUENCIA")
	private int tamanoSecuencia;

	/**
	 * Indica si la secuencia se declara como tipo fecha, YYYY MM DD
	 */
	@Column(name="TIPO_FECHA")
	private boolean tipoFecha;

	/**
	 * Indica si la secuencia debe reiniciarse cuando es autonumerico cada 1-ano
	 */
	@Column(name="REINICIAR_CADA")
	private int reiniciarCada;
	
	/**
	 * Indica la fecha en que la secuencia autonumerica fue reiniciada
	 */
	@Column(name="FECHA_ULTIMO_REINICIO")
	private Date fechaUltimoReinicio;

	/** Indica si el registro esta activo o inactivo */
	private boolean activo;

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
	 * Obtiene el valor del atributo autonumerico
	 * @return El valor del atributo autonumerico
	 */
	public boolean isAutonumerico() {
		return autonumerico;
	}

	/**
	 * Establece el valor del atributo autonumerico
	 * @param autonumerico con el valor del atributo autonumerico a establecer
	 */
	public void setAutonumerico(boolean autonumerico) {
		this.autonumerico = autonumerico;
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

	
}

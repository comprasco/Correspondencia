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

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_CONDICION_REGLA
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_CONDICION_REGLA")
@NamedQueries({
	@NamedQuery(name = CondicionRegla.CONDICIONREGLA_POR_REGLA, query = "SELECT t FROM CondicionRegla t WHERE t.regla.id = ?1 AND t.regla.activo = true order by t.id" ),
	@NamedQuery(name = CondicionRegla.CONDICIONREGLA_POR_ENTRADA_PASO, query = "SELECT t FROM CondicionRegla t WHERE t.entrada.id = ?1 AND t.regla.paso.id = ?2 AND t.regla.activo = true order by t.id")
})
public class CondicionRegla implements EntidadGenerica, Serializable{

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String CONDICIONREGLA_POR_REGLA = "CONDICIONREGLA_POR_REGLA";

	/** Constante nombre consulta */
	public static final String CONDICIONREGLA_POR_ENTRADA_PASO = "CONDICIONREGLA_POR_ENTRADA_PASO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_CONDICIONREGLA", sequenceName = "SEC_PNG_CORRESPONDENCIA_CONDICION_REGLA", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_CONDICIONREGLA")
	private long id;

	/** Valor de la condición */
	private String contenido;

	/** Identificador de la regla */
	@ManyToOne
	@JoinColumn(name = "ID_REGLA")
	private Regla regla;

	/** Identificador de la entrada sobre la cual se aplica la condición */
	@ManyToOne
	@JoinColumn(name = "ID_ENTRADA")
	private Entrada entrada;
	
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
	 * Obtiene el valor del atributo contenido
	 * @return El valor del atributo contenido
	 */
	public String getContenido() {
		return contenido;
	}

	/**
	 * Establece el valor del atributo contenido
	 * @param contenido con el valor del atributo contenido a establecer
	 */
	public void setContenido(String contenido) {
		this.contenido = contenido;
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

	

}

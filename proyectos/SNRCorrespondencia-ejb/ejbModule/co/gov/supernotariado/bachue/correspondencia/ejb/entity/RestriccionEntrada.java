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
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_ENTRADA_RESTRICCION
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_ENTRADA_RESTRICCION")
@NamedQueries({
	@NamedQuery(name = RestriccionEntrada.RESTRICCION_POR_ENTRADA, query = "SELECT t FROM RestriccionEntrada t WHERE t.entrada.id = ?1 ORDER BY t.id")
})
public class RestriccionEntrada implements EntidadGenerica, Serializable {
	
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String RESTRICCION_POR_ENTRADA = "RESTRICCION_POR_ENTRADA";
	
	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_RESTRICCIONENTRADA", sequenceName = "SEC_PNG_CORRESPONDENCIA_ENTRADA_RESTRICCION", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_RESTRICCIONENTRADA")
	private long id;

	/** Tipo de restricción */
	@Column(name="TIPO")
	private String tipo;

	/** Valor de la restricción */
	@Column(name="VALOR")
	private String valor;

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

	/** Objeto Entrada asociado */
	@ManyToOne
	@JoinColumn(name = "ID_ENTRADA")
	private Entrada entrada;

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
	 * Obtiene el valor del atributo tipo
	 * @return El valor del atributo tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Establece el valor del atributo tipo
	 * @param tipo con el valor del atributo tipo a establecer
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene el valor del atributo valor
	 * @return El valor del atributo valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * Establece el valor del atributo valor
	 * @param valor con el valor del atributo valor a establecer
	 */
	public void setValor(String valor) {
		this.valor = valor;
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

}

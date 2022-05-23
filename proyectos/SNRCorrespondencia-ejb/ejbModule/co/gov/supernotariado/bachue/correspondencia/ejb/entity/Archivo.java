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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entidad para mapear la tabla SDB_ACC_CORRESPONDENCIA_ARCHIVO
 */
@Entity
@Table(name = "SDB_ACC_CORRESPONDENCIA_ARCHIVO")
public class Archivo implements EntidadGenerica, Serializable {
	private static final long serialVersionUID = 1L;

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_ARCHIVO", sequenceName = "SEC_ACC_CORRESPONDENCIA_ARCHIVO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_ARCHIVO")
	private long id;

	/** Nombre del archivo */
	private String nombre;

	/** Identificador con el que queda almacenado el documento en un sistema externo de almacenamiento */
	@Column(name="IDENTIFICADOR_EXTERNO")
	private String identificadorExterno;
	
	/** Tipo de archivo */
	private String tipoArchivo;

	/** Tamaño del archivo */
	private long tamano;

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

	/** Identificador del paso ejecutado */
	@ManyToOne
	@JoinColumn(name = "ID_PASO_EJECUTADO")
	private PasoEjecutado pasoEjecutado;

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
	 * Obtiene el valor del atributo identificadorExterno
	 * @return El valor del atributo identificadorExterno
	 */
	public String getIdentificadorExterno() {
		return identificadorExterno;
	}

	/**
	 * Establece el valor del atributo identificadorExterno
	 * @param identificadorExterno con el valor del atributo identificadorExterno a establecer
	 */
	public void setIdentificadorExterno(String identificadorExterno) {
		this.identificadorExterno = identificadorExterno;
	}

	/**
	 * Obtiene el valor del atributo tipoArchivo
	 * @return El valor del atributo tipoArchivo
	 */
	public String getTipoArchivo() {
		return tipoArchivo;
	}

	/**
	 * Establece el valor del atributo tipoArchivo
	 * @param tipoArchivo con el valor del atributo tipoArchivo a establecer
	 */
	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}

	/**
	 * Obtiene el valor del atributo tamano
	 * @return El valor del atributo tamano
	 */
	public long getTamano() {
		return tamano;
	}

	/**
	 * Establece el valor del atributo tamano
	 * @param tamano con el valor del atributo tamano a establecer
	 */
	public void setTamano(long tamano) {
		this.tamano = tamano;
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
	 * Obtiene el valor del atributo pasoEjecutado
	 * @return El valor del atributo pasoEjecutado
	 */
	public PasoEjecutado getPasoEjecutado() {
		return pasoEjecutado;
	}

	/**
	 * Establece el valor del atributo pasoEjecutado
	 * @param pasoEjecutado con el valor del atributo pasoEjecutado a establecer
	 */
	public void setPasoEjecutado(PasoEjecutado pasoEjecutado) {
		this.pasoEjecutado = pasoEjecutado;
	}

	
}

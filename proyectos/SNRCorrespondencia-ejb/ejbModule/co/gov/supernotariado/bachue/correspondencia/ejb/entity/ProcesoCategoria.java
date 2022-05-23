package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_PROCESO_CATEGORIA
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_PROCESO_CATEGORIA")
@NamedQueries({
	@NamedQuery(name = ProcesoCategoria.CATEGORIAS_POR_PROCESO, query = "SELECT t FROM ProcesoCategoria t WHERE t.id.procesoId = :procesoId")
})
public class ProcesoCategoria extends EntidadBase {

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String CATEGORIAS_POR_PROCESO = "CATEGORIAS_POR_PROCESO";

	/** Constante nombre consulta */
	@EmbeddedId
	private ProcesoCategoriaPK id;
	
	/** Identificador del proceso */
	@ManyToOne
	@JoinColumn(name="ID_PROCESO", insertable = false, updatable = false)
	private transient Proceso proceso;

	/** Identificador de la categoría */
	@ManyToOne
	@JoinColumn(name="ID_CATEGORIA", insertable = false, updatable = false)
	private Parametro categoria;

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
	public ProcesoCategoriaPK getId() {
		return id;
	}

	/**
	 * Establece el valor del atributo id
	 * @param id con el valor del atributo id a establecer
	 */
	public void setId(ProcesoCategoriaPK id) {
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
	 * Obtiene el valor del atributo categoria
	 * @return El valor del atributo categoria
	 */
	public Parametro getCategoria() {
		return categoria;
	}

	/**
	 * Establece el valor del atributo categoria
	 * @param categoria con el valor del atributo categoria a establecer
	 */
	public void setCategoria(Parametro categoria) {
		this.categoria = categoria;
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

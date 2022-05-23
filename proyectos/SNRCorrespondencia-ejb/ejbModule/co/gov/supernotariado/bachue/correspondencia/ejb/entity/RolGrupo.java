package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entidad para mapear la tabla SDB_PGN_CORRESPONDENCIA_ROL_GRUPO
 *
 */
@Entity
@Table(name="SDB_PGN_CORRESPONDENCIA_ROL_GRUPO")
@NamedQueries({
	@NamedQuery(name = RolGrupo.GRUPOS_POR_ROL, query = "SELECT t FROM RolGrupo t WHERE t.rolId = ?1"),
	@NamedQuery(name = RolGrupo.ROLES_POR_GRUPO, query = "SELECT t FROM RolGrupo t WHERE t.grupoId = ?1")
})
public class RolGrupo extends EntidadBase {

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String GRUPOS_POR_ROL = "GRUPOS_POR_ROL";

	/** Constante nombre consulta */
	public static final String ROLES_POR_GRUPO = "ROLES_POR_GRUPO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_ROL_GRUPO", sequenceName = "SEC_PGN_CORRESPONDENCIA_ROL_GRUPO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_ROL_GRUPO")
	private long id;

	/** Identificador del rol */
	@Column(name="ID_ROL")
	private long rolId;

	/** Identificador del grupo */
	@Column(name="ID_GRUPO")
	private long grupoId;

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
	 * Obtiene el valor del atributo rolId
	 * @return El valor del atributo rolId
	 */
	public long getRolId() {
		return rolId;
	}

	/**
	 * Establece el valor del atributo rolId
	 * @param rolId con el valor del atributo rolId a establecer
	 */
	public void setRolId(long rolId) {
		this.rolId = rolId;
	}

	/**
	 * Obtiene el valor del atributo grupoId
	 * @return El valor del atributo grupoId
	 */
	public long getGrupoId() {
		return grupoId;
	}

	/**
	 * Establece el valor del atributo grupoId
	 * @param grupoId con el valor del atributo grupoId a establecer
	 */
	public void setGrupoId(long grupoId) {
		this.grupoId = grupoId;
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

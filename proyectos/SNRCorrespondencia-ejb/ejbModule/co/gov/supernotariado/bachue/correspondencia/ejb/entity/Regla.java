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

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_REGLA
 *
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_REGLA")
@NamedQueries({
	@NamedQuery(name = Regla.REGLA_ACTIVAS_POR_PASO, query = "SELECT t FROM Regla t WHERE t.activo = true AND t.paso.id = ?1 order by t.id")
})
public class Regla implements EntidadGenerica, Serializable{
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String REGLA_ACTIVAS_POR_PASO = "REGLA_ACTIVAS_POR_PASO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_REGLA", sequenceName = "SEC_PNG_CORRESPONDENCIA_REGLA", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_REGLA")
	private long id;

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

	/** Indica si la accion a realizar es finalizar el paso */
	@Column(name="FINALIZA_PROCESO")
	private boolean finalizaProceso;

	/** Indica si el campo observaciones es requerido */
	@Column(name="CAMPO_OBSERVACIONES_REQUERIDO")
	private boolean campoObservacionesRequerido;

	/** Indica si al validar la regla permite que la siguiente tarea se asigna de forma paralela */
	@Column(name="PERMITE_PARALELO")
	private boolean permiteParalelo;

	/** Identificador del paso asociado */
	@ManyToOne
	@JoinColumn(name = "ID_PASO")
	private Paso paso;

	/** Identificador del siguiente paso asociado a la regla */
	@Column(name = "ID_SIGUIENTE_PASO")
	private Long siguientePasoId;

	/**
	 * Temporal para mantener el siguiente paso que se configura para la regla
	 */
	@Transient
	private Paso siguientePaso;
	
	/**
	 * Temporal para tener la lista de condiciones que se configuran para la regla
	 */
	@Transient
	private List<CondicionRegla> detalles;

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
	 * Obtiene el valor del atributo finalizaProceso
	 * @return El valor del atributo finalizaProceso
	 */
	public boolean isFinalizaProceso() {
		return finalizaProceso;
	}

	/**
	 * Establece el valor del atributo finalizaProceso
	 * @param finalizaProceso con el valor del atributo finalizaProceso a establecer
	 */
	public void setFinalizaProceso(boolean finalizaProceso) {
		this.finalizaProceso = finalizaProceso;
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
	 * Obtiene el valor del atributo paso
	 * @return El valor del atributo paso
	 */
	public Paso getPaso() {
		return paso;
	}

	/**
	 * Establece el valor del atributo paso
	 * @param paso con el valor del atributo paso a establecer
	 */
	public void setPaso(Paso paso) {
		this.paso = paso;
	}

	/**
	 * Obtiene el valor del atributo siguientePasoId
	 * @return El valor del atributo siguientePasoId
	 */
	public Long getSiguientePasoId() {
		return siguientePasoId;
	}

	/**
	 * Establece el valor del atributo siguientePasoId
	 * @param siguientePasoId con el valor del atributo siguientePasoId a establecer
	 */
	public void setSiguientePasoId(Long siguientePasoId) {
		this.siguientePasoId = siguientePasoId;
	}

	/**
	 * Obtiene el valor del atributo siguientePaso
	 * @return El valor del atributo siguientePaso
	 */
	public Paso getSiguientePaso() {
		return siguientePaso;
	}

	/**
	 * Establece el valor del atributo siguientePaso
	 * @param siguientePaso con el valor del atributo siguientePaso a establecer
	 */
	public void setSiguientePaso(Paso siguientePaso) {
		this.siguientePaso = siguientePaso;
	}

	/**
	 * Obtiene el valor del atributo detalles
	 * @return El valor del atributo detalles
	 */
	public List<CondicionRegla> getDetalles() {
		return detalles;
	}

	/**
	 * Establece el valor del atributo detalles
	 * @param detalles con el valor del atributo detalles a establecer
	 */
	public void setDetalles(List<CondicionRegla> detalles) {
		this.detalles = detalles;
	}

	/**
	 * Obtiene el valor del atributo permiteParalelo
	 * @return El valor del atributo permiteParalelo
	 */
	public boolean isPermiteParalelo() {
		return permiteParalelo;
	}

	/**
	 * Establece el valor del atributo permiteParalelo
	 * @param permiteParalelo con el valor del atributo permiteParalelo a establecer
	 */
	public void setPermiteParalelo(boolean permiteParalelo) {
		this.permiteParalelo = permiteParalelo;
	}

}

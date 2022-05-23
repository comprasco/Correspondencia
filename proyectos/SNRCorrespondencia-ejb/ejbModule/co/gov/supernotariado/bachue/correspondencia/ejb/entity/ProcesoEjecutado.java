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
 * Entidad para mapear la tabla SDB_ACC_CORRESPONDENCIA_PROCESO_EJECUTADO
 *
 */
@Entity
@Table(name="SDB_ACC_CORRESPONDENCIA_PROCESO_EJECUTADO")
@NamedQueries({
	@NamedQuery(name = ProcesoEjecutado.PROCESOEJECUTADO_POR_SECUENCIA_IDENTIFICADORPROCESO, query = "SELECT t FROM ProcesoEjecutado t WHERE t.secuenciaProceso = ?1 AND t.proceso.identificador = ?2 "),
	@NamedQuery(name = ProcesoEjecutado.PROCESOEJECUTADO_POR_SECUENCIASALIDA_IDENTIFICADORPROCESO, query = "SELECT t FROM ProcesoEjecutado t WHERE t.secuenciaProcesoSalida = ?1 AND t.proceso.identificador = ?2 "),
	@NamedQuery(name = ProcesoEjecutado.PROCESOEJECUTADO_ACTIVOS_POR_PROCESO, query = "SELECT t FROM ProcesoEjecutado t WHERE t.activo = true AND t.proceso.id = ?1 "),
	@NamedQuery(name = ProcesoEjecutado.PROCESOEJECUTADO_POR_PROCESO, query = "SELECT t FROM ProcesoEjecutado t WHERE t.proceso.id = ?1 ")
})
public class ProcesoEjecutado implements EntidadGenerica, Serializable {
	
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String PROCESOEJECUTADO_POR_SECUENCIA_IDENTIFICADORPROCESO = "PROCESOEJECUTADO_POR_SECUENCIA_IDENTIFICADORPROCESO";

	/** Constante nombre consulta */
	public static final String PROCESOEJECUTADO_POR_SECUENCIASALIDA_IDENTIFICADORPROCESO = "PROCESOEJECUTADO_POR_SECUENCIASALIDA_IDENTIFICADORPROCESO";

	/** Constante nombre consulta */
	public static final String PROCESOEJECUTADO_ACTIVOS_POR_PROCESO = "PROCESOEJECUTADO_ACTIVOS_POR_PROCESO";
	public static final String PROCESOEJECUTADO_POR_PROCESO = "PROCESOEJECUTADO_POR_PROCESO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_PROCESOEJECUTADO", sequenceName = "SEC_ACC_CORRESPONDENCIA_PROCESO_EJECUTADO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_PROCESOEJECUTADO")
	private long id;

	/** Indica si el registro esta activo o inactivo */
	private boolean activo;

	/** Identificador de la categoría */
	@ManyToOne
	@JoinColumn(name = "ID_CATEGORIA")
	private Parametro categoria;

	/** Identificador del proceso */
	@ManyToOne
	@JoinColumn(name = "ID_PROCESO")
	private Proceso proceso;

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

	/** Objeto que guarda el paso actual del proceso */
	@ManyToOne
	@JoinColumn(name = "ID_PASO_ACTUAL")
	private Paso pasoActual;

	/** Guarda la secuencia definida para el proceso  */
	@Column(name="SECUENCIA_PROCESO")
	private String secuenciaProceso;

	/** Guarda la secuencia de salida definida para el proceso  */
	@Column(name="SECUENCIA_PROCESO_SALIDA")
	private String secuenciaProcesoSalida;

	/** Indica la ORIP que ejecutó el proceso */
	@Column(name="ID_ORIP_EJECUCION")
	private String idOripEjecucion;

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
	 * Obtiene el valor del atributo pasoActual
	 * @return El valor del atributo pasoActual
	 */
	public Paso getPasoActual() {
		return pasoActual;
	}

	/**
	 * Establece el valor del atributo pasoActual
	 * @param pasoActual con el valor del atributo pasoActual a establecer
	 */
	public void setPasoActual(Paso pasoActual) {
		this.pasoActual = pasoActual;
	}

	/**
	 * Obtiene el valor del atributo secuenciaProceso
	 * @return El valor del atributo secuenciaProceso
	 */
	public String getSecuenciaProceso() {
		return secuenciaProceso;
	}

	/**
	 * Establece el valor del atributo secuenciaProceso
	 * @param secuenciaProceso con el valor del atributo secuenciaProceso a establecer
	 */
	public void setSecuenciaProceso(String secuenciaProceso) {
		this.secuenciaProceso = secuenciaProceso;
	}

	/**
	 * Obtiene el valor del atributo idOripEjecucion
	 * @return El valor del atributo idOripEjecucion
	 */
	public String getIdOripEjecucion() {
		return idOripEjecucion;
	}

	/**
	 * Establece el valor del atributo idOripEjecucion
	 * @param idOripEjecucion con el valor del atributo idOripEjecucion a establecer
	 */
	public void setIdOripEjecucion(String idOripEjecucion) {
		this.idOripEjecucion = idOripEjecucion;
	}

	/**
	 * Obtiene el valor del atributo secuenciaProcesoSalida
	 * @return El valor del atributo secuenciaProcesoSalida
	 */
	public String getSecuenciaProcesoSalida() {
		return secuenciaProcesoSalida;
	}

	/**
	 * Establece el valor del atributo secuenciaProcesoSalida
	 * @param secuenciaProcesoSalida con el valor del atributo secuenciaProcesoSalida a establecer
	 */
	public void setSecuenciaProcesoSalida(String secuenciaProcesoSalida) {
		this.secuenciaProcesoSalida = secuenciaProcesoSalida;
	}


}

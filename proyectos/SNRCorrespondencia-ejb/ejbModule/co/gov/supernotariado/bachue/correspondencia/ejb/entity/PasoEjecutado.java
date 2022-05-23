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

import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;

/**
 * Entidad para mapear la tabla SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO
 *
 */
@Entity
@Table(name="SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO")
@NamedQueries({
	@NamedQuery(name = PasoEjecutado.PASOEJECUTADO_POR_VENCIDAS, query = "SELECT t FROM PasoEjecutado t WHERE t.activo = true AND t.fechaFin < :fechaFin AND t.fechaUltimoRecordatorio is null "),
	@NamedQuery(name = PasoEjecutado.PASOEJECUTADO_ANTERIORES, query = "SELECT t FROM PasoEjecutado t WHERE t.activo = false AND t.procesoEjecutado.id = ?1 ORDER BY t.id "),
	@NamedQuery(name = PasoEjecutado.PASOEJECUTADO_ACTUAL_POR_PROCESO, query = "SELECT t FROM PasoEjecutado t WHERE t.activo = true AND t.procesoEjecutado.id = ?1 ORDER BY t.id "),
	@NamedQuery(name = PasoEjecutado.PASOEJECUTADO_POR_RECORDATORIO, query = "SELECT t FROM PasoEjecutado t WHERE t.activo = true AND t.estado <> 'EJECUTADO' AND t.fechaSiguienteRecordatorio <= :fechaProximoRecordatorio AND t.paso.enviarRecordatorio > 0 ")
})
public class PasoEjecutado implements EntidadGenerica, Serializable {

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String PASOEJECUTADO_POR_VENCIDAS = "PASOEJECUTADO_POR_VENCIDAS";

	/** Constante nombre consulta */
	public static final String PASOEJECUTADO_ACTUAL_POR_PROCESO = "PASOEJECUTADO_ACTUAL_POR_PROCESO";
	
	/** Constante nombre consulta */
	public static final String PASOEJECUTADO_POR_RECORDATORIO = "PASOEJECUTADO_POR_RECORDATORIO";
	
	/** Constante nombre consulta */
	public static final String PASOEJECUTADO_ANTERIORES = "PASOEJECUTADO_ANTERIORES";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_PASOEJECUTADO", sequenceName = "SEC_ACC_CORRESPONDENCIA_PASO_EJECUTADO", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_PASOEJECUTADO")
	private long id;

	/** Indica si el registro esta activo o inactivo */
	private Boolean activo;

	/** Indica si es el último paso del proceso */
	@Column(name="ULTIMO_PASO")
	private Boolean ultimoPaso;

	/** Comentario de ejecución del paso */
	private String comentario;

	/** Identificador del usuario asignado a ejecutar este paso */
	@Column(name="ID_USUARIO_ASIGNADO")
	private String usuarioAsignadoId;

	/** Identificador del grupo asignado a ejecutar este paso */
	@Column(name="ID_GRUPO_ASIGNADO")
	private String grupoAsignadoId;

	/** Fecha marcada como límite de finalización del paso */
	@Column(name="FECHA_FIN")
	private Date fechaFin;
	
	/** Fecha de ejecución del paso */
	@Column(name="FECHA_EJECUCION")
	private Date fechaEjecucion;
	
	/** Fecha en la que fue enviado el último recordatorio */
	@Column(name="FECHA_ULTIMO_RECORDATORIO")
	private Date fechaUltimoRecordatorio;

	/** Estado del paso, ejecutado, atrasado, etc */
	private String estado;
	
	/** Identificador del paso ejecutado anterior */
	@ManyToOne
	@JoinColumn(name = "ID_PASOEJECUTADO_ANTERIOR")
	private PasoEjecutado pasoAnterior;

	/** Identificador del proceso ejecutado */
	@ManyToOne
	@JoinColumn(name = "ID_PROCESO_EJECUTADO")
	private ProcesoEjecutado procesoEjecutado;
	
	/** Identificador del paso */
	@ManyToOne
	@JoinColumn(name = "ID_PASO")
	private Paso paso;
	
	/** Fecha programada para el envío del siguiente recordatorio */
	@Column(name="FECHA_SIGUIENTE_RECORDATORIO")
	private Date fechaSiguienteRecordatorio;

	/** Indica si fue un paso devuelto */
	@Column(name="PASO_DEVUELTO")
	private boolean pasoDevuelto;

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

	/** Lista con los usuarios asignados a este paso */
	@Column(name="USUARIOS_ASIGNADOS")
	private String usuariosAsignados;

	/** Lista con los grupos asignados a este paso */
	@Column(name="GRUPOS_ASIGNADOS")
	private String gruposAsignados;

	/** Indica la ORIP que ejecutó el Paso */
	@Column(name="ID_ORIP_EJECUCION")
	private String idOripEjecucion;

	/** Atributo temporal para mantener el usuario asignado al paso */
	@Transient
	private transient UsuarioTO usuarioAsignado;

	/** Atributo temporal para mantener el usuario que ejecuta el paso */
	@Transient
	private transient UsuarioTO usuarioEjecutado;

	/** Atributo temporal para mantener el grupo asignado al paso */
	@Transient
	private transient GrupoTO grupoAsignado;

	/** Atributo temporal para mantener la lista de campos a diligenciar en el paso */
	@Transient
	private transient List<ValorEntrada> campos;

	/** Atributo temporal para mantener la lista de usuarios asignados al paso */
	@Transient
	private transient List<UsuarioTO> listaUsuariosAsignados;

	/** Atributo temporal para mantener la lista de grupos asignados al paso */
	@Transient
	private transient List<GrupoTO> listaGruposAsignados;
	
	/** Atributo temporal para mantener si el paso fue seleccionado para enviar (paso envios correspondencia) */
	@Transient
	private transient boolean seleccionadoEnvios;

	/** Atributo temporal para mantener si el paso debe mostrarse como tipo seleccion (checkbox) (paso envios correspondencia) */
	@Transient
	private transient boolean mostrarSeleccion;

	/** Atributo temporal para mantener si el paso debe mostrarse como tipo seleccion (checkbox) (paso envios correspondencia) pero deshabilitado */
	@Transient
	private transient boolean deshabilitarSeleccionadoEnvios;

	/** Atributo temporal para mantener la secuencia autonumerica que se configuró para el paso */
	@Transient 
	private transient String secuenciaTabla;
	/**
	 * Indica si debe mostrar en la bandeja el boton de digitalizar
	 */
	@Transient
	private transient boolean mostrarDigitalizar;

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
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Establece el valor del atributo activo
	 * @param activo con el valor del atributo activo a establecer
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Obtiene el valor del atributo ultimoPaso
	 * @return El valor del atributo ultimoPaso
	 */
	public Boolean getUltimoPaso() {
		return ultimoPaso;
	}

	/**
	 * Establece el valor del atributo ultimoPaso
	 * @param ultimoPaso con el valor del atributo ultimoPaso a establecer
	 */
	public void setUltimoPaso(Boolean ultimoPaso) {
		this.ultimoPaso = ultimoPaso;
	}

	/**
	 * Obtiene el valor del atributo comentario
	 * @return El valor del atributo comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * Establece el valor del atributo comentario
	 * @param comentario con el valor del atributo comentario a establecer
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
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
	 * Obtiene el valor del atributo fechaFin
	 * @return El valor del atributo fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * Establece el valor del atributo fechaFin
	 * @param fechaFin con el valor del atributo fechaFin a establecer
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	/**
	 * Obtiene el valor del atributo fechaEjecucion
	 * @return El valor del atributo fechaEjecucion
	 */
	public Date getFechaEjecucion() {
		return fechaEjecucion;
	}

	/**
	 * Establece el valor del atributo fechaEjecucion
	 * @param fechaEjecucion con el valor del atributo fechaEjecucion a establecer
	 */
	public void setFechaEjecucion(Date fechaEjecucion) {
		this.fechaEjecucion = fechaEjecucion;
	}

	/**
	 * Obtiene el valor del atributo fechaUltimoRecordatorio
	 * @return El valor del atributo fechaUltimoRecordatorio
	 */
	public Date getFechaUltimoRecordatorio() {
		return fechaUltimoRecordatorio;
	}

	/**
	 * Establece el valor del atributo fechaUltimoRecordatorio
	 * @param fechaUltimoRecordatorio con el valor del atributo fechaUltimoRecordatorio a establecer
	 */
	public void setFechaUltimoRecordatorio(Date fechaUltimoRecordatorio) {
		this.fechaUltimoRecordatorio = fechaUltimoRecordatorio;
	}

	/**
	 * Obtiene el valor del atributo estado
	 * @return El valor del atributo estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * Establece el valor del atributo estado
	 * @param estado con el valor del atributo estado a establecer
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene el valor del atributo pasoAnterior
	 * @return El valor del atributo pasoAnterior
	 */
	public PasoEjecutado getPasoAnterior() {
		return pasoAnterior;
	}

	/**
	 * Establece el valor del atributo pasoAnterior
	 * @param pasoAnterior con el valor del atributo pasoAnterior a establecer
	 */
	public void setPasoAnterior(PasoEjecutado pasoAnterior) {
		this.pasoAnterior = pasoAnterior;
	}

	/**
	 * Obtiene el valor del atributo procesoEjecutado
	 * @return El valor del atributo procesoEjecutado
	 */
	public ProcesoEjecutado getProcesoEjecutado() {
		return procesoEjecutado;
	}

	/**
	 * Establece el valor del atributo procesoEjecutado
	 * @param procesoEjecutado con el valor del atributo procesoEjecutado a establecer
	 */
	public void setProcesoEjecutado(ProcesoEjecutado procesoEjecutado) {
		this.procesoEjecutado = procesoEjecutado;
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
	 * Obtiene el valor del atributo fechaSiguienteRecordatorio
	 * @return El valor del atributo fechaSiguienteRecordatorio
	 */
	public Date getFechaSiguienteRecordatorio() {
		return fechaSiguienteRecordatorio;
	}

	/**
	 * Establece el valor del atributo fechaSiguienteRecordatorio
	 * @param fechaSiguienteRecordatorio con el valor del atributo fechaSiguienteRecordatorio a establecer
	 */
	public void setFechaSiguienteRecordatorio(Date fechaSiguienteRecordatorio) {
		this.fechaSiguienteRecordatorio = fechaSiguienteRecordatorio;
	}

	/**
	 * Obtiene el valor del atributo pasoDevuelto
	 * @return El valor del atributo pasoDevuelto
	 */
	public boolean isPasoDevuelto() {
		return pasoDevuelto;
	}

	/**
	 * Establece el valor del atributo pasoDevuelto
	 * @param pasoDevuelto con el valor del atributo pasoDevuelto a establecer
	 */
	public void setPasoDevuelto(boolean pasoDevuelto) {
		this.pasoDevuelto = pasoDevuelto;
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
	 * Obtiene el valor del atributo usuariosAsignados
	 * @return El valor del atributo usuariosAsignados
	 */
	public String getUsuariosAsignados() {
		return usuariosAsignados;
	}

	/**
	 * Establece el valor del atributo usuariosAsignados
	 * @param usuariosAsignados con el valor del atributo usuariosAsignados a establecer
	 */
	public void setUsuariosAsignados(String usuariosAsignados) {
		this.usuariosAsignados = usuariosAsignados;
	}

	/**
	 * Obtiene el valor del atributo gruposAsignados
	 * @return El valor del atributo gruposAsignados
	 */
	public String getGruposAsignados() {
		return gruposAsignados;
	}

	/**
	 * Establece el valor del atributo gruposAsignados
	 * @param gruposAsignados con el valor del atributo gruposAsignados a establecer
	 */
	public void setGruposAsignados(String gruposAsignados) {
		this.gruposAsignados = gruposAsignados;
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
	 * Obtiene el valor del atributo usuarioEjecutado
	 * @return El valor del atributo usuarioEjecutado
	 */
	public UsuarioTO getUsuarioEjecutado() {
		return usuarioEjecutado;
	}

	/**
	 * Establece el valor del atributo usuarioEjecutado
	 * @param usuarioEjecutado con el valor del atributo usuarioEjecutado a establecer
	 */
	public void setUsuarioEjecutado(UsuarioTO usuarioEjecutado) {
		this.usuarioEjecutado = usuarioEjecutado;
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
	 * Obtiene el valor del atributo campos
	 * @return El valor del atributo campos
	 */
	public List<ValorEntrada> getEntradas() {
		return campos;
	}

	/**
	 * Establece el valor del atributo campos
	 * @param campos con el valor del atributo campos a establecer
	 */
	public void setEntradas(List<ValorEntrada> campos) {
		this.campos = campos;
	}

	/**
	 * Obtiene el valor del atributo listaUsuariosAsignados
	 * @return El valor del atributo listaUsuariosAsignados
	 */
	public List<UsuarioTO> getListaUsuariosAsignados() {
		return listaUsuariosAsignados;
	}

	/**
	 * Establece el valor del atributo listaUsuariosAsignados
	 * @param listaUsuariosAsignados con el valor del atributo listaUsuariosAsignados a establecer
	 */
	public void setListaUsuariosAsignados(List<UsuarioTO> listaUsuariosAsignados) {
		this.listaUsuariosAsignados = listaUsuariosAsignados;
	}

	/**
	 * Obtiene el valor del atributo listaGruposAsignados
	 * @return El valor del atributo listaGruposAsignados
	 */
	public List<GrupoTO> getListaGruposAsignados() {
		return listaGruposAsignados;
	}

	/**
	 * Establece el valor del atributo listaGruposAsignados
	 * @param listaGruposAsignados con el valor del atributo listaGruposAsignados a establecer
	 */
	public void setListaGruposAsignados(List<GrupoTO> listaGruposAsignados) {
		this.listaGruposAsignados = listaGruposAsignados;
	}

	/**
	 * Obtiene el valor del atributo seleccionadoEnvios
	 * @return El valor del atributo seleccionadoEnvios
	 */
	public boolean isSeleccionadoEnvios() {
		return seleccionadoEnvios;
	}

	/**
	 * Establece el valor del atributo seleccionadoEnvios
	 * @param seleccionadoEnvios con el valor del atributo seleccionadoEnvios a establecer
	 */
	public void setSeleccionadoEnvios(boolean seleccionadoEnvios) {
		this.seleccionadoEnvios = seleccionadoEnvios;
	}

	/**
	 * Obtiene el valor del atributo mostrarSeleccion
	 * @return El valor del atributo mostrarSeleccion
	 */
	public boolean isMostrarSeleccion() {
		return mostrarSeleccion;
	}

	/**
	 * Establece el valor del atributo mostrarSeleccion
	 * @param mostrarSeleccion con el valor del atributo mostrarSeleccion a establecer
	 */
	public void setMostrarSeleccion(boolean mostrarSeleccion) {
		this.mostrarSeleccion = mostrarSeleccion;
	}

	/**
	 * Obtiene el valor del atributo deshabilitarSeleccionadoEnvios
	 * @return El valor del atributo deshabilitarSeleccionadoEnvios
	 */
	public boolean isDeshabilitarSeleccionadoEnvios() {
		return deshabilitarSeleccionadoEnvios;
	}

	/**
	 * Establece el valor del atributo deshabilitarSeleccionadoEnvios
	 * @param deshabilitarSeleccionadoEnvios con el valor del atributo deshabilitarSeleccionadoEnvios a establecer
	 */
	public void setDeshabilitarSeleccionadoEnvios(boolean deshabilitarSeleccionadoEnvios) {
		this.deshabilitarSeleccionadoEnvios = deshabilitarSeleccionadoEnvios;
	}

	/**
	 * Obtiene el valor del atributo secuenciaTabla
	 * @return El valor del atributo secuenciaTabla
	 */
	public String getSecuenciaTabla() {
		return secuenciaTabla;
	}

	/**
	 * Establece el valor del atributo secuenciaTabla
	 * @param secuenciaTabla con el valor del atributo secuenciaTabla a establecer
	 */
	public void setSecuenciaTabla(String secuenciaTabla) {
		this.secuenciaTabla = secuenciaTabla;
	}

	/**
	 * Obtiene el valor del atributo mostrarDigitalizar
	 * @return El valor del atributo mostrarDigitalizar
	 */
	public boolean isMostrarDigitalizar() {
		return mostrarDigitalizar;
	}

	/**
	 * Establece el valor del atributo mostrarDigitalizar
	 * @param mostrarDigitalizar con el valor del atributo mostrarDigitalizar a establecer
	 */
	public void setMostrarDigitalizar(boolean mostrarDigitalizar) {
		this.mostrarDigitalizar = mostrarDigitalizar;
	}


}

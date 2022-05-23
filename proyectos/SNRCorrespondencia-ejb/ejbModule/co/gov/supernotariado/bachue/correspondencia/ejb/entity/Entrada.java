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

import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;

/**
 * Entidad para mapear la tabla SDB_PNG_CORRESPONDENCIA_ENTRADA
 */
@Entity
@Table(name="SDB_PNG_CORRESPONDENCIA_ENTRADA")
@NamedQueries({
	@NamedQuery(name = Entrada.ENTRADA_ACTIVAS_POR_PROCESO, query = "SELECT t FROM Entrada t WHERE t.activo = true AND t.paso.proceso.id = ?1 ORDER BY t.paso.ordenPaso, t.ordenEntrada, t.id"),
	@NamedQuery(name = Entrada.ENTRADA_ACTIVAS_POR_PASO, query = "SELECT t FROM Entrada t WHERE t.activo = true AND t.paso.id = ?1 ORDER BY t.ordenEntrada, t.id"),
})
public class Entrada implements EntidadGenerica, Serializable{

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String ENTRADA_ACTIVAS_POR_PROCESO = "ENTRADA_ACTIVAS_POR_PROCESO";

	/** Constante nombre consulta */
	public static final String ENTRADA_ACTIVAS_POR_PASO = "ENTRADA_ACTIVAS_POR_PASO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_ENTRADA", sequenceName = "SEC_PNG_CORRESPONDENCIA_ENTRADA", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_ENTRADA")
	private long id;

	/** Indica si el registro esta activo o inactivo */
	private Boolean activo;

	/** Lista de opciones en formato JSON */
	private String opciones;

	/** nombre clave */
	private String nombre;

	/** Nombre de la entrada */
	@Column(name="NOMBRE_ENTRADA")
	private String nombreEntrada;

	/** Identificador del tipo de entrada */
	@Column(name="TIPO_ENTRADA")
	private String tipoEntrada;

	/** Longitud del tipo de entrada */
	@Column(name="LONGITUD")
	private int longitud;

	/** Indica si la entrada es obligatoria */
	private boolean requerido;
	
	/** Indica si la entrada es oculta y no se muestra en el formulario */
	@Column(name="ENTRADA_OCULTA")
	private boolean entradaOculta;
	
	/** Orden de la entrada */
	@Column(name="ORDEN_ENTRADA")
	private int ordenEntrada;

	/** Indica si la entrada es una lista de tipo clave valor */
	@Column(name="ENTRADA_CLAVE_VALOR")
	private boolean entradaClaveValor;

	/** Identificador del paso asociado */
	@ManyToOne
	@JoinColumn(name = "ID_PASO")
	private Paso paso;

	/** Indica si la entrada es de tipo anidado */
	@Column(name="ENTRADA_ANIDADA")
	private boolean entradaAnidada;

	/** Identificador de la entrada padre cuando es anidada */
	@Column(name = "ID_ENTRADA_ANIDADA")
	private Long entradaAnidadaId;

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

	/** Identificador del parámetro cuando es lista predefinida */
	@Column(name="ID_PARAMETRO_OPCIONES")
	private Long parametroOpcionesId;
	
	/** Indica si la entrada se incluye en la bandeja prinicipal */
	@Column(name="INCLUIR_BANDEJA")
	private boolean incluirBandeja;

	/** Indica si la entrada es de tipo archivo, en que repositorio se alojará */
	@Column(name="ARCHIVO_TIPO_REPOSITORIO")
	private int archivoTipoRepositorio;

	/** Indica si la entrada debe enviarse como parametro en el envio de documentos */
	@Column(name="ENVIAR_CHECKIN")
	private boolean enviarCheckin;

	/** Indica un ID de entrada al cual copiar su valor de entrada anterior si existe */
	@Column(name="ID_ENTRADA_COPIAR")
	private Long idEntradaCopiar;

	/** Temporal para guardar la lista de opciones de la entrada */
	@Transient
	private List<NodoTO> listaOpciones;
	
	/** Temporal para guardar la lista de opciones de la entrada */
	@Transient
	private List<JsonOpcion> opcionesJson;

	/**
	 * Obtiene el valor del atributo opcionesJson
	 * @return El valor del atributo opcionesJson
	 */
	public List<JsonOpcion> getOpcionesJson() {
		return opcionesJson;
	}

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
	 * Obtiene el valor del atributo opciones
	 * @return El valor del atributo opciones
	 */
	public String getOpciones() {
		return opciones;
	}


	/**
	 * Establece el valor del atributo opciones
	 * @param opciones con el valor del atributo opciones a establecer
	 */
	public void setOpciones(String opciones) {
		this.opciones = opciones;
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
	 * Obtiene el valor del atributo nombreEntrada
	 * @return El valor del atributo nombreEntrada
	 */
	public String getNombreEntrada() {
		return nombreEntrada;
	}


	/**
	 * Establece el valor del atributo nombreEntrada
	 * @param nombreEntrada con el valor del atributo nombreEntrada a establecer
	 */
	public void setNombreEntrada(String nombreEntrada) {
		this.nombreEntrada = nombreEntrada;
	}


	/**
	 * Obtiene el valor del atributo tipoEntrada
	 * @return El valor del atributo tipoEntrada
	 */
	public String getTipoEntrada() {
		return tipoEntrada;
	}


	/**
	 * Establece el valor del atributo tipoEntrada
	 * @param tipoEntrada con el valor del atributo tipoEntrada a establecer
	 */
	public void setTipoEntrada(String tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}


	/**
	 * Obtiene el valor del atributo requerido
	 * @return El valor del atributo requerido
	 */
	public boolean isRequerido() {
		return requerido;
	}


	/**
	 * Establece el valor del atributo requerido
	 * @param requerido con el valor del atributo requerido a establecer
	 */
	public void setRequerido(boolean requerido) {
		this.requerido = requerido;
	}


	/**
	 * Obtiene el valor del atributo entradaOculta
	 * @return El valor del atributo entradaOculta
	 */
	public boolean isEntradaOculta() {
		return entradaOculta;
	}


	/**
	 * Establece el valor del atributo entradaOculta
	 * @param entradaOculta con el valor del atributo entradaOculta a establecer
	 */
	public void setEntradaOculta(boolean entradaOculta) {
		this.entradaOculta = entradaOculta;
	}


	/**
	 * Obtiene el valor del atributo ordenEntrada
	 * @return El valor del atributo ordenEntrada
	 */
	public int getOrdenEntrada() {
		return ordenEntrada;
	}


	/**
	 * Establece el valor del atributo ordenEntrada
	 * @param ordenEntrada con el valor del atributo ordenEntrada a establecer
	 */
	public void setOrdenEntrada(int ordenEntrada) {
		this.ordenEntrada = ordenEntrada;
	}


	/**
	 * Obtiene el valor del atributo entradaClaveValor
	 * @return El valor del atributo entradaClaveValor
	 */
	public boolean isEntradaClaveValor() {
		return entradaClaveValor;
	}


	/**
	 * Establece el valor del atributo entradaClaveValor
	 * @param entradaClaveValor con el valor del atributo entradaClaveValor a establecer
	 */
	public void setEntradaClaveValor(boolean entradaClaveValor) {
		this.entradaClaveValor = entradaClaveValor;
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
	 * Obtiene el valor del atributo entradaAnidada
	 * @return El valor del atributo entradaAnidada
	 */
	public boolean isEntradaAnidada() {
		return entradaAnidada;
	}


	/**
	 * Establece el valor del atributo entradaAnidada
	 * @param entradaAnidada con el valor del atributo entradaAnidada a establecer
	 */
	public void setEntradaAnidada(boolean entradaAnidada) {
		this.entradaAnidada = entradaAnidada;
	}


	/**
	 * Obtiene el valor del atributo entradaAnidadaId
	 * @return El valor del atributo entradaAnidadaId
	 */
	public Long getEntradaAnidadaId() {
		return entradaAnidadaId;
	}


	/**
	 * Establece el valor del atributo entradaAnidadaId
	 * @param entradaAnidadaId con el valor del atributo entradaAnidadaId a establecer
	 */
	public void setEntradaAnidadaId(Long entradaAnidadaId) {
		this.entradaAnidadaId = entradaAnidadaId;
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
	 * Obtiene el valor del atributo parametroOpcionesId
	 * @return El valor del atributo parametroOpcionesId
	 */
	public Long getParametroOpcionesId() {
		return parametroOpcionesId;
	}


	/**
	 * Establece el valor del atributo parametroOpcionesId
	 * @param parametroOpcionesId con el valor del atributo parametroOpcionesId a establecer
	 */
	public void setParametroOpcionesId(Long parametroOpcionesId) {
		this.parametroOpcionesId = parametroOpcionesId;
	}


	/**
	 * Obtiene el valor del atributo incluirBandeja
	 * @return El valor del atributo incluirBandeja
	 */
	public boolean isIncluirBandeja() {
		return incluirBandeja;
	}


	/**
	 * Establece el valor del atributo incluirBandeja
	 * @param incluirBandeja con el valor del atributo incluirBandeja a establecer
	 */
	public void setIncluirBandeja(boolean incluirBandeja) {
		this.incluirBandeja = incluirBandeja;
	}


	/**
	 * Obtiene el valor del atributo archivoTipoRepositorio
	 * @return El valor del atributo archivoTipoRepositorio
	 */
	public int getArchivoTipoRepositorio() {
		return archivoTipoRepositorio;
	}


	/**
	 * Establece el valor del atributo archivoTipoRepositorio
	 * @param archivoTipoRepositorio con el valor del atributo archivoTipoRepositorio a establecer
	 */
	public void setArchivoTipoRepositorio(int archivoTipoRepositorio) {
		this.archivoTipoRepositorio = archivoTipoRepositorio;
	}


	/**
	 * Obtiene el valor del atributo enviarCheckin
	 * @return El valor del atributo enviarCheckin
	 */
	public boolean isEnviarCheckin() {
		return enviarCheckin;
	}


	/**
	 * Establece el valor del atributo enviarCheckin
	 * @param enviarCheckin con el valor del atributo enviarCheckin a establecer
	 */
	public void setEnviarCheckin(boolean enviarCheckin) {
		this.enviarCheckin = enviarCheckin;
	}


	/**
	 * Obtiene el valor del atributo listaOpciones
	 * @return El valor del atributo listaOpciones
	 */
	public List<NodoTO> getListaOpciones() {
		return listaOpciones;
	}


	/**
	 * Establece el valor del atributo listaOpciones
	 * @param listaOpciones con el valor del atributo listaOpciones a establecer
	 */
	public void setListaOpciones(List<NodoTO> listaOpciones) {
		this.listaOpciones = listaOpciones;
	}


	/**
	 * Establece el valor del atributo opcionesJson
	 * @param opcionesJson con el valor del atributo opcionesJson a establecer
	 */
	public void setOpcionesJson(List<JsonOpcion> opcionesJson) {
		this.opcionesJson = opcionesJson;
	}

	/**
	 * Obtiene el valor del atributo longitud
	 * @return El valor del atributo longitud
	 */
	public int getLongitud() {
		return longitud;
	}

	/**
	 * Establece el valor del atributo longitud
	 * @param longitud con el valor del atributo longitud a establecer
	 */
	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	/**
	 * Obtiene el valor del atributo idEntradaCopiar
	 * @return El valor del atributo idEntradaCopiar
	 */
	public Long getIdEntradaCopiar() {
		return idEntradaCopiar;
	}

	/**
	 * Establece el valor del atributo idEntradaCopiar
	 * @param idEntradaCopiar con el valor del atributo idEntradaCopiar a establecer
	 */
	public void setIdEntradaCopiar(Long idEntradaCopiar) {
		this.idEntradaCopiar = idEntradaCopiar;
	}

}

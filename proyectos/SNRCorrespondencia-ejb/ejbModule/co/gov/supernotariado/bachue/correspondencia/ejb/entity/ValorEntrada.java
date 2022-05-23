package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.primefaces.model.UploadedFile;

/**
 * Entidad para mapear la tabla SDB_ACC_CORRESPONDENCIA_VALOR_ENTRADA
 *
 */
@Entity
@Table(name="SDB_ACC_CORRESPONDENCIA_VALOR_ENTRADA")
@NamedQueries({
	@NamedQuery(name = ValorEntrada.VALORENTRADA_POR_PASOEJECUTADO, query = "SELECT t FROM ValorEntrada t WHERE t.pasoEjecutado.id = ?1 ORDER BY t.id asc, t.entrada.ordenEntrada"),
	@NamedQuery(name = ValorEntrada.VALORENTRADA_POR_TIPOENTRADA_PASOEJECUTADO, query = "SELECT t FROM ValorEntrada t WHERE t.entrada.tipoEntrada = :tipoEntrada AND t.pasoEjecutado.id = :pasoEjecutadoId")
})
public class ValorEntrada implements EntidadGenerica, Serializable {
	
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Constante nombre consulta */
	public static final String VALORENTRADA_POR_PASOEJECUTADO = "VALORENTRADA_POR_PASOEJECUTADO";
	
	/** Constante nombre consulta */
	public static final String VALORENTRADA_POR_TIPOENTRADA_PASOEJECUTADO = "VALORENTRADA_POR_ENTRADA_PASOEJECUTADO";

	/** Identificador de la tabla */
	@Id
	@SequenceGenerator(name = "SECUENCIA_VALORENTRADA", sequenceName = "SEC_ACC_CORRESPONDENCIA_VALOR_ENTRADA", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_VALORENTRADA")
	private long id;

	/** Identificador de la entrada asociada */
	@Column(name="VALOR_ENTRADA")
	private String valor;

	/** Valor de la entrada en texto */
	@Column(name="VALOR_ENTRADA_TEXTO")
	private String valorEntradaTexto;

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

	/** Objeto PasoEjecutado asociado */
	@ManyToOne
	@JoinColumn(name = "ID_PASO_EJECUTADO")
	private PasoEjecutado pasoEjecutado;
	
	/** Auxiliar para tener una lista de archivos */
	@Transient
	private List<Archivo> documentos;

	/** Auxiliar para el manejo del valor de entrada tipo fecha */
	@Transient
	private Date valorEntradaFecha;

	/** Auxiliar para el manejo del valor de entrada */
	@Transient
	private String valorEntradaAux;

	/** Auxiliar para la lista de Entradas */
	@Transient
	private List<String> listaEntradas = new ArrayList<>();

	/** Auxiliar para mostrar u ocultar la entrada */
	@Transient
	private boolean mostrar = true;
	
	/** Auxiliar para determinar si este campo tiene campos anidados y debe validarse en cada seleccion de campo en el formulario */
	@Transient
	private boolean verificarAnidados;

	/** Auxiliar para determinar si este campo tiene reglas y debe validarse en cada seleccion de campo en el formulario */
	@Transient
	private boolean verificarReglas;

	/** Auxiliar para guardar un archivo asociado a la entrada */
	@Transient
	private transient UploadedFile archivoCargado;

	/** Auxiliar para validar restricciones de entradas */
	@Transient
	private transient String valorMinimoEntrada;

	/** Auxiliar para validar restricciones de entradas */
	@Transient
	private transient String valorMaximoEntrada;

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
	 * Obtiene el valor del atributo valorEntradaTexto
	 * @return El valor del atributo valorEntradaTexto
	 */
	public String getValorEntradaTexto() {
		return valorEntradaTexto;
	}

	/**
	 * Establece el valor del atributo valorEntradaTexto
	 * @param valorEntradaTexto con el valor del atributo valorEntradaTexto a establecer
	 */
	public void setValorEntradaTexto(String valorEntradaTexto) {
		this.valorEntradaTexto = valorEntradaTexto;
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

	/**
	 * Obtiene el valor del atributo documentos
	 * @return El valor del atributo documentos
	 */
	public List<Archivo> getDocumentos() {
		return documentos;
	}

	/**
	 * Establece el valor del atributo documentos
	 * @param documentos con el valor del atributo documentos a establecer
	 */
	public void setDocumentos(List<Archivo> documentos) {
		this.documentos = documentos;
	}

	/**
	 * Obtiene el valor del atributo valorEntradaFecha
	 * @return El valor del atributo valorEntradaFecha
	 */
	public Date getValorEntradaFecha() {
		return valorEntradaFecha;
	}

	/**
	 * Establece el valor del atributo valorEntradaFecha
	 * @param valorEntradaFecha con el valor del atributo valorEntradaFecha a establecer
	 */
	public void setValorEntradaFecha(Date valorEntradaFecha) {
		this.valorEntradaFecha = valorEntradaFecha;
	}

	/**
	 * Obtiene el valor del atributo valorEntradaAux
	 * @return El valor del atributo valorEntradaAux
	 */
	public String getValorEntradaAux() {
		return valorEntradaAux;
	}

	/**
	 * Establece el valor del atributo valorEntradaAux
	 * @param valorEntradaAux con el valor del atributo valorEntradaAux a establecer
	 */
	public void setValorEntradaAux(String valorEntradaAux) {
		this.valorEntradaAux = valorEntradaAux;
	}

	/**
	 * Obtiene el valor del atributo listaEntradas
	 * @return El valor del atributo listaEntradas
	 */
	public List<String> getListaEntradas() {
		return listaEntradas;
	}

	/**
	 * Establece el valor del atributo listaEntradas
	 * @param listaEntradas con el valor del atributo listaEntradas a establecer
	 */
	public void setListaEntradas(List<String> listaEntradas) {
		this.listaEntradas = listaEntradas;
	}

	/**
	 * Obtiene el valor del atributo mostrar
	 * @return El valor del atributo mostrar
	 */
	public boolean isMostrar() {
		return mostrar;
	}

	/**
	 * Establece el valor del atributo mostrar
	 * @param mostrar con el valor del atributo mostrar a establecer
	 */
	public void setMostrar(boolean mostrar) {
		this.mostrar = mostrar;
	}

	/**
	 * Obtiene el valor del atributo verificarAnidados
	 * @return El valor del atributo verificarAnidados
	 */
	public boolean isVerificarAnidados() {
		return verificarAnidados;
	}

	/**
	 * Establece el valor del atributo verificarAnidados
	 * @param verificarAnidados con el valor del atributo verificarAnidados a establecer
	 */
	public void setVerificarAnidados(boolean verificarAnidados) {
		this.verificarAnidados = verificarAnidados;
	}

	/**
	 * Obtiene el valor del atributo verificarReglas
	 * @return El valor del atributo verificarReglas
	 */
	public boolean isVerificarReglas() {
		return verificarReglas;
	}

	/**
	 * Establece el valor del atributo verificarReglas
	 * @param verificarReglas con el valor del atributo verificarReglas a establecer
	 */
	public void setVerificarReglas(boolean verificarReglas) {
		this.verificarReglas = verificarReglas;
	}

	/**
	 * Obtiene el valor del atributo archivoCargado
	 * @return El valor del atributo archivoCargado
	 */
	public UploadedFile getArchivoCargado() {
		return archivoCargado;
	}

	/**
	 * Establece el valor del atributo archivoCargado
	 * @param archivoCargado con el valor del atributo archivoCargado a establecer
	 */
	public void setArchivoCargado(UploadedFile archivoCargado) {
		this.archivoCargado = archivoCargado;
	}

	/**
	 * Obtiene el valor del atributo valorMinimoEntrada
	 * @return El valor del atributo valorMinimoEntrada
	 */
	public String getValorMinimoEntrada() {
		return valorMinimoEntrada;
	}

	/**
	 * Establece el valor del atributo valorMinimoEntrada
	 * @param valorMinimoEntrada con el valor del atributo valorMinimoEntrada a establecer
	 */
	public void setValorMinimoEntrada(String valorMinimoEntrada) {
		this.valorMinimoEntrada = valorMinimoEntrada;
	}

	/**
	 * Obtiene el valor del atributo valorMaximoEntrada
	 * @return El valor del atributo valorMaximoEntrada
	 */
	public String getValorMaximoEntrada() {
		return valorMaximoEntrada;
	}

	/**
	 * Establece el valor del atributo valorMaximoEntrada
	 * @param valorMaximoEntrada con el valor del atributo valorMaximoEntrada a establecer
	 */
	public void setValorMaximoEntrada(String valorMaximoEntrada) {
		this.valorMaximoEntrada = valorMaximoEntrada;
	}


}

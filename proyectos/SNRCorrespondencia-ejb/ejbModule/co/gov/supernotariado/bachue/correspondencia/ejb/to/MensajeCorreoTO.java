package co.gov.supernotariado.bachue.correspondencia.ejb.to;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la información de un mensaje de correo para una notificación
 *
 */
public class MensajeCorreoTO {
	/**
	 * Asunto del correo
	 */
	private String asunto;
	/**
	 * Contenido del correo
	 */
	private String contenido;

	/**
	 * Remitente del correo
	 */
	private String remitente;
	/**
	 * Lista de destinatarios del correo
	 */
	private List<String> destinatario = new ArrayList<>();
	/**
	 * Lista de destinatarios cc del correo
	 */
	private List<String> copia = new ArrayList<>();
	/**
	 * Lista de destinatarios bcc del correo
	 */
	private List<String> copiaOculta = new ArrayList<>();

	/**
	 * Lista de adjuntos
	 */
	private List<AdjuntoTO> adjuntos = new ArrayList<>();

	/**
	 * Indica si el formato del contenido es HTML
	 */
	private boolean formatoHTML = false;

	/**
	 * Constructor
	 * @param remitente Remitente del correo
	 * @param asunto Asunto del correo
	 * @param contenido Contenido del correo
	 * @param formatoHTML Indica si tiene formato HTML
	 */
	public MensajeCorreoTO(String remitente, String asunto, String contenido, boolean formatoHTML){
		this.remitente = remitente; 
		this.asunto = asunto;
		this.contenido = contenido;
		this.formatoHTML = formatoHTML;
	}

	/**
	 * Obtiene el valor del atributo asunto
	 * @return El valor del atributo asunto
	 */
	public String getAsunto() {
		return asunto;
	}

	/**
	 * Establece el valor del atributo asunto
	 * @param asunto con el valor del atributo asunto a establecer
	 */
	public void setAsunto(String asunto) {
		this.asunto = asunto;
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
	 * Obtiene el valor del atributo remitente
	 * @return El valor del atributo remitente
	 */
	public String getRemitente() {
		return remitente;
	}

	/**
	 * Establece el valor del atributo remitente
	 * @param remitente con el valor del atributo remitente a establecer
	 */
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	/**
	 * Obtiene el valor del atributo destinatario
	 * @return El valor del atributo destinatario
	 */
	public List<String> getDestinatario() {
		return destinatario;
	}

	/**
	 * Establece el valor del atributo destinatario
	 * @param destinatario con el valor del atributo destinatario a establecer
	 */
	public void setDestinatario(List<String> destinatario) {
		this.destinatario = destinatario;
	}

	/**
	 * Obtiene el valor del atributo copia
	 * @return El valor del atributo copia
	 */
	public List<String> getCopia() {
		return copia;
	}

	/**
	 * Establece el valor del atributo copia
	 * @param copia con el valor del atributo copia a establecer
	 */
	public void setCopia(List<String> copia) {
		this.copia = copia;
	}

	/**
	 * Obtiene el valor del atributo copiaOculta
	 * @return El valor del atributo copiaOculta
	 */
	public List<String> getCopiaOculta() {
		return copiaOculta;
	}

	/**
	 * Establece el valor del atributo copiaOculta
	 * @param copiaOculta con el valor del atributo copiaOculta a establecer
	 */
	public void setCopiaOculta(List<String> copiaOculta) {
		this.copiaOculta = copiaOculta;
	}

	/**
	 * Obtiene el valor del atributo adjuntos
	 * @return El valor del atributo adjuntos
	 */
	public List<AdjuntoTO> getAdjuntos() {
		return adjuntos;
	}

	/**
	 * Establece el valor del atributo adjuntos
	 * @param adjuntos con el valor del atributo adjuntos a establecer
	 */
	public void setAdjuntos(List<AdjuntoTO> adjuntos) {
		this.adjuntos = adjuntos;
	}

	/**
	 * Obtiene el valor del atributo formatoHTML
	 * @return El valor del atributo formatoHTML
	 */
	public boolean isFormatoHTML() {
		return formatoHTML;
	}

	/**
	 * Establece el valor del atributo formatoHTML
	 * @param formatoHTML con el valor del atributo formatoHTML a establecer
	 */
	public void setFormatoHTML(boolean formatoHTML) {
		this.formatoHTML = formatoHTML;
	}

	
}

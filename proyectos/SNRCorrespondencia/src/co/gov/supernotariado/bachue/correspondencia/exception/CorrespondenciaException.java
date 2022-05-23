package co.gov.supernotariado.bachue.correspondencia.exception;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * Clase para manejar excepciones del sistema obteniendo un mensaje de error desde errores.properties
 */
public class CorrespondenciaException extends Exception {
	/** Identificador serializable */
    private static final long serialVersionUID = 1L;

	/** Logger de impresión de mensajes en los logs del servidor */
	private final transient Logger logger = Logger.getLogger(CorrespondenciaException.class);
	
	/** Clave en el archivo properties para buscar el mensaje */
	private final String messageKey;

    /**
     * Constructor
     * @param messageKey Clave de mensaje de properties
     * @param e Excepción generada
     */
    public CorrespondenciaException(String messageKey, Throwable e) {
        super(e);
    	this.messageKey = messageKey;
    }

    /**
     * Constructor
     * @param messageKey Clave de mensaje de properties
     * @param error Error generado
     */
    public CorrespondenciaException(String messageKey, String error) {
        super(error);
    	this.messageKey = messageKey;
    }
    
    /**
     * Obtiene el mensaje de error desde un properties localizado
     * @return Cadena con el mensaje de error localizado
     */
    @Override
    public String getLocalizedMessage() {
    	ResourceBundle mensajes = ResourceBundle.getBundle("errores", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    	String error = getMessage();
		try {
			error = mensajes.getString(messageKey);
		} catch(Exception e) {
			logger.error("'"+messageKey+"' falta mensaje", e);
		}
		logger.error(error);
        return error;
    }

    /**
     * Obtiene el mensaje de error desde un properties localizado y le agrega la excepción original
     * @return Cadena con el mensaje de error localizado
     */
    public String getLocalizedMessageOriginalEx() {
    	ResourceBundle mensajes = ResourceBundle.getBundle("errores", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    	String error = getMessage();
		try {
			error = mensajes.getString(messageKey)+" "+getMessage();
		} catch(Exception e) {
			logger.error("'"+messageKey+"' falta mensaje", e);
		}
		logger.error(error);
        return error;
    }

    
    /**
	 * Obtiene el valor del atributo messageKey
	 * @return El valor del atributo messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}

}

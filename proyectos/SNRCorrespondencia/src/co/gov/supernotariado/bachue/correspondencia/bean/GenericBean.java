package co.gov.supernotariado.bachue.correspondencia.bean;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.SortOrder;

/**
 * Managed Bean Generico del que extienden los demás Beans
 */
public abstract class GenericBean{
	/**
	 * Properties de mensajes
	 */
	private ResourceBundle mensajes = ResourceBundle.getBundle("messages");

	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(GenericBean.class);

	/**
	 * Establece un campo de ordenamiento para enviar a la consulta en base de datos 
	 * @param sortField Campo por el cual ordenar los resultados
	 * @param sortOrder Orden ascendente o descendente
	 * @return String con el ordenamiento a aplicar
	 */
	protected String estableceOrdenConsulta(String sortField, SortOrder sortOrder) {
		String order = "";
		if(sortField==null) {
			order = "id";
		} else {
			order = sortField;
		}

		if (sortOrder != null){
			if (sortOrder.equals(SortOrder.ASCENDING)){
				order += " ASC";
			} else if (sortOrder.equals(SortOrder.DESCENDING)){
				order += " DESC";
			}
        }
		return order;
	}
	
	/**
	 * Agrega mensaje de faces con una severidad paramétrica
	 * El mensaje se toma desde el properties de mensajes
	 * @param severity Severidad a aplicar
	 * @param mensajeClave Mensaje a agregar
	 */
	protected void agregarMensajeFaces(Severity severity, String mensajeClave) {
		try {
			String message = mensajes.getString(mensajeClave);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, message));
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Agrega mensaje de faces con una severidad paramétrica
	 * El mensaje se toma desde el properties de mensajes.  Se agrega un mensaje adicional
	 * @param severity Severidad a aplicar
	 * @param mensajeClave Mensaje a agregar
	 * @param adicional Mensaje adicional a agregar
	 */
	protected void agregarMensajeFaces(Severity severity, String mensajeClave, String adicional) {
		try {
			String message = mensajes.getString(mensajeClave)+": "+adicional;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, message));
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	
    /**
     * Permite encontrar un objeto managed bean en la sesión
     * @param beanName Nombre del ManagedBean a encontrar
     * @return el objeto ManagedBean encontrado
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    
    
}

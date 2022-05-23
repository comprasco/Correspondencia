package co.gov.supernotariado.bachue.correspondencia.faces;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.bean.GenericBean;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;

/**
 * Converter usado en el pick list de grupos y roles
 *
 */
@FacesConverter("pickListGruposConverter")
public class PickListGruposConverter implements Converter {
 
	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(PickListGruposConverter.class);

    
    /**
     * Obtiene el valor de objeto de un componente UI
     * @param context Contexto de Faces
     * @param component componente sobre el cual evaluar
     * @param value valor seleccionado en el component 
     */
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Object result = null;
        if(value != null && value.trim().length() > 0) {
            try {
            	GenericBean bean = GenericBean.findBean("gestorSesionBean");
        		if(bean!=null){
        			result = bean.getClass().getMethod("obtenerGrupo", String.class, String.class).invoke(bean, value, null);
        		}
            } catch(Exception e) {
            	String msg = "Conversion Error. Not a valid data object. "+e.getMessage(); 
            	logger.error(msg, e);
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            }
        }
        return result;
    }
 
    /**
     * Obtiene el valor plano del valor de un componente UI
     * @param context Contexto de Faces
     * @param component componente sobre el cual evaluar
     * @param object Objeto al cual mapear
     */
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if(object != null) {
        	try {
        		return ((GrupoTO) object).getId();
        	} catch(Exception e) {
            	String msg = "Conversion Error. Method getId not found. "+e.getMessage(); 
            	logger.error(msg, e);
        	}
        }
        return null;
    }
        
}

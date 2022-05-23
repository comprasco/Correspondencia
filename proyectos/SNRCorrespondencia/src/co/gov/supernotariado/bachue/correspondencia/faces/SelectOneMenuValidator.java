package co.gov.supernotariado.bachue.correspondencia.faces;

import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;

/**
 * Validaciones para componentes tipo SelectOneMenu
 */
@FacesValidator("co.gov.supernotariado.bachue.correspondencia.util.SelectOneMenuValidator")
public class SelectOneMenuValidator implements Validator {
	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(SelectOneMenuValidator.class);

	/**
	 * Valida si la opción debe ser requerida
     * @param context Contexto de Faces
     * @param component componente sobre el cual evaluar
     * @param valor valor del componente
	 */
	public void validate(FacesContext context, UIComponent component, Object valor) {
		if(valor != null) {
			String value = (String) valor;

			boolean validate = false;
			Entrada entrada = (Entrada) component.getAttributes().get("entrada");
			if(entrada!=null && entrada.isRequerido()) {
				List<NodoTO> options = entrada.getListaOpciones();
				for(NodoTO option:options) {
					if(option.getId().equals(value) && option.isMarkedNull()) {
						validate = true;
						break;
					}
				}
			}
			
			if(validate) {
				ResourceBundle messages = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

				String error = "Valor Requerido";
				try {
					error = messages.getString("javax.faces.component.UIInput.REQUIRED");
				} catch(Exception e) {
					logger.error("'javax.faces.component.UIInput.REQUIRED' falta mensaje", e);
				}

				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, error, error));
			}
         }

    }
}

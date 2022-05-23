package co.gov.supernotariado.bachue.correspondencia.faces;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;

/**
 * Validador de correos electrónicos
 */
@FacesValidator("co.gov.supernotariado.bachue.correspondencia.util.EmailValidator")
public class EmailValidator implements Validator {
	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(EmailValidator.class);

	/**
	 * Patrón a validar
	 */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})(((,| , | ,){1}[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))*)$";

	/**
	 * Objeto para inicializar el patrón
	 */
	private Pattern pattern;

	/**
	 * Constructor
	 */
	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

	/**
	 * Validador del patrón de correo
	 * @param context Contexto de Faces
	 * @param component Componente sobre el cual validar
	 * @param value Valor del componente a validar
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		ResourceBundle mensajes = ResourceBundle.getBundle("messages");
		if(value!=null && !value.toString().isEmpty()){
			Matcher matcher = pattern.matcher(value.toString());
			if (!matcher.matches()) {
				String error = "Correo Electrónico Inválido";
				try {
					error = mensajes.getString("error_correo");
				} catch(Exception e) {
					logger.error("'error_correo' mensaje no encontrado. "+ e.getMessage());
				}
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, error, error));
			}
		}
	}
}

package co.gov.supernotariado.bachue.correspondencia.faces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.exception.CorrespondenciaException;

/**
 * Muestra un mensaje de error global si alguna validación falló
 */
public class ValidationFailedListener implements PhaseListener {
	/** Logger de impresión de mensajes en los logs del servidor */
	private transient Logger logger = Logger.getLogger(ValidationFailedListener.class);

	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Evento ejecutado después de la fase de faces
	 * @param event Evento de cambio de fase
	 */
	@Override
	public void afterPhase(PhaseEvent event) {
		// override
	}

	/**
	 * Ejecuta la validación y muestra un mensaje de error si alguna validación de algún componente falló
	 * @param event Evento de cambio de fase
	 */
	@Override
	public void beforePhase(PhaseEvent event) {
		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			try {
				logger.error("ValidationFailedListener "+event.getPhaseId()+" - "+event.getSource());
				throw new CorrespondenciaException("error_datos", "ValidationFailedListener "+event.getPhaseId()+" - "+event.getSource());
			} catch(Exception e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getLocalizedMessage(), e.getLocalizedMessage()));
			}
		}
	}

	/**
	 * Obtiene el ID de la fase
	 * @return ID de la fase
	 */
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
package co.gov.supernotariado.bachue.correspondencia.faces;

import java.util.Iterator;
import java.util.Map;

import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * Clase de tipo ExceptionHandler de JSF que permite validar las excepciones de sesión expirada ViewExpiredException.
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    /**
     * Manejador de la excepción
     */
    private ExceptionHandler wrapped;

    /**
     * Constructor
     * @param exception Manejador de excepción
     */
    CustomExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    /**
     * Evento Manejador de la excepción
     */
    @Override
    public void handle() {
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            Throwable t = context.getException();

            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();

            if (t instanceof ViewExpiredException) {
                requestMap.put("javax.servlet.error.message", "Session expired, try again!");
                String errorPageLocation = "/views/login.jsf";
                fc.setViewRoot(fc.getApplication().getViewHandler().createView(fc, errorPageLocation));
                fc.getPartialViewContext().setRenderAll(true);
                fc.renderResponse();
                i.remove();
            } else {
                requestMap.put("javax.servlet.error.message", t.getMessage());
                nav.handleNavigation(fc, null, "/views/login.jsf");
            }

            fc.renderResponse();
        }
        //parent hanle
        getWrapped().handle();
    }

	/**
	 * Obtiene el valor del atributo wrapped
	 * @return El valor del atributo wrapped
	 */
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	/**
	 * Establece el valor del atributo wrapped
	 * @param wrapped con el valor del atributo wrapped a establecer
	 */
	public void setWrapped(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}
}
package co.gov.supernotariado.bachue.correspondencia.faces;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Clase de tipo ExceptionHandlerFactory de JSF para inicializar CustomExceptionHandler.
 *
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

    /**
     * Factoría del Manejador de excepción
     */
    private ExceptionHandlerFactory parent;

    /**
     * Constructor
     * @param parent Factoría del Manejador de excepción
     */
    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    /**
     * Obtiene el manejador de la excepción
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        return new CustomExceptionHandler(parent.getExceptionHandler());
    }

}
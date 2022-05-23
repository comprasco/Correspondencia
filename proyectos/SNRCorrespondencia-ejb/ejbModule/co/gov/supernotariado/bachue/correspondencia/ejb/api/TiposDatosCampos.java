package co.gov.supernotariado.bachue.correspondencia.ejb.api;

/**
 * Enumeración de los tipos de dato que se adminten en los datos adicionales de Parámetros
 */
public enum TiposDatosCampos {
    /**
     * Entrada tipo texto 
     */
    TEXT, 
    /**
     * Entrada tipo texto numérico
     */
    TEXTO_NUMERICO,
    /**
     * Entrada tipo correo electrónico
     */
    EMAIL, 
    /**
     * Entrada tipo fecha
     */
    DATE; 
}

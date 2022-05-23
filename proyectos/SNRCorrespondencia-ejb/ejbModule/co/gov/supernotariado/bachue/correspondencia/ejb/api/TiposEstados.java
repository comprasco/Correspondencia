package co.gov.supernotariado.bachue.correspondencia.ejb.api;

/**
 * Enumeración con los Tipos de Estados de los pasos de los procesos
 */
public enum TiposEstados {
	/**
	 * Paso en progreso o ejecución
	 */
	ENPROGRESO, 
	/**
	 * Paso ya ejecutado
	 */
	EJECUTADO,
    /**
     * Paso demorado o atrasado después de su tiempo límite
     */
    DEMORADO;

}

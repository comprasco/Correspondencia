package co.gov.supernotariado.bachue.correspondencia.ejb.api;

/**
 * Enumeración de tipos de parámetros que se pueden generar
 *
 */
public enum TiposParametros {
	/**
	 * Tipo de procesos
	 */
	TIPO_PROCESO,
	/**
	 * Listas de parámetros predefinidas para asociar a las entradas del formulario
	 */
	LISTA_PREDEFINIDA,
	/**
	 * Plantillas word para descargar
	 */
	PLANTILLA_DOCUMENTO,
	/**
	 * Secuencia global
	 */
	SECUENCIA_GLOBAL,
	/**
	 * Parámetros del sistema
	 */
	SISTEMA;
}

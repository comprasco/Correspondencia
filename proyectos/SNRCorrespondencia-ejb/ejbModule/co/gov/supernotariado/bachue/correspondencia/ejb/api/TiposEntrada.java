package co.gov.supernotariado.bachue.correspondencia.ejb.api;

/**
 * Enumeración de los tipos de Entrada para los formularios
 */
public enum TiposEntrada {
    /**
     * Entrada tipo texto 
     */
    TEXT, 
    /**
     * Entrada tipo texto numérico
     */
    TEXTO_NUMERICO,
    /**
     * Entrada tipo area de texto
     */
    TEXTAREA,
    /**
     * Entrada tipo correo electrónico
     */
    EMAIL, 
    /**
     * Entrada tipo fecha
     */
    DATE, 
    /**
     * Entrada tipo selector de lista
     */
    SELECT,
    /**
     * Entrada tipo radio
     */
    RADIO,
    /**
     * Entrada de lista de checkbox
     */
    CHECKBOX,
    /**
     * Entrada tipo archivo 
     */
    FILE,
    /**
     * Entrada tipo secuencia autonumérica
     */
    SEQUENCE,
    /**
     * Entrada de integración con capture
     */
    DIGITALIZACION_CAPTURE,
    /**
     * Entrada para generar salidas de reporte
     */
    SALIDA_REPORTE,
    /**
     * Entrada tipo selector de lista pero atada a listas de parámetros
     */
    LISTA_PREDEFINIDA,
    /**
     * Entrada tipo radio atado a listas de parámetros
     */
    RADIO_LISTA_PREDEFINIDA,
    /**
     * Tipo archivo plano para enviar a transportadora 472
     */
    GENERAR_ARCHIVO_472,
    /**
     * Tipo archivo plano para cargar desde la transportadora 472
     */
    CARGAR_ARCHIVO_472,
    /**
     * Entrada tipo archivo, toma el último archivo encontrado y lo vuelve a generar con aspose y lo carga al repositorio final 
     */
    ARCHIVO_AUTOMATICO,
    /**
     * Entrada para visualizar el último archivo cargado en pasos anteriores 
     */
    VISOR_ARCHIVO,
    /**
     * Entrada para visualizar una lista con todos los documentos cargados en pasos anteriores 
     */
    VISOR_ARCHIVOS_GENERAL,
	/**
	 * Plantillas word para descargar
	 */
	PLANTILLA_DOCUMENTO,
	/**
	 * Secuencia global definida como parámetro
	 */
	SECUENCIA_GLOBAL;

}

package co.gov.supernotariado.bachue.correspondencia.ejb.api;

/**
 * Constantes que maneja la aplicación
 *
 */
public class ConstantesCorrespondencia {
	
	/**
	 * Constructor privado para que esta clase no se pueda instanciar.
	 */
	private ConstantesCorrespondencia() {
		
	}
	
	/**
	 * Versión actual para la aplicación
	 */
	public static final String VERSION_CORRESPONDENCIA = "SNRCORRESPONDENCIA_00921";

	/**
	 * Nombre Rol Super administrador
	 */
	public static final String ROL_SUPERADMIN = "rol_superadmin";
	/**
	 * Nombre Rol Administrador
	 */
	public static final String ROL_ADMIN = "rol_admin";
	/**
	 * Nombre Rol Usuario - Ejecución y Consulta
	 */
	public static final String ROL_USUARIO = "rol_usuario";
	/**
	 * Nombre Rol Usuario Solo Ejecución
	 */
	public static final String ROL_USUARIO_EJECUCION = "rol_usuario_ejecucion";

	/**
	 * Entrada especial para integraciones fecha proceso
	 */
	public static final String ENTRADA_ESPECIAL_FECHA_PROCESO = "FECHA_PROCESO";

	/**
	 * Entrada especial para integraciones secuencia proceso
	 */
	public static final String ENTRADA_ESPECIAL_SECUENCIA_PROCESO = "SECUENCIA_PROCESO";

	/**
	 * Nombre clave de las entradas para los consecutivos EE
	 */
	public static final String NOMBRE_ENTRADA_CONSECUTIVOEE = "consecutivoee";
	/**
	 * Nombre clave de las entradas para marcar el envio devuelto
	 */
	public static final String NOMBRE_ENTRADA_ENVIODEVUELTO = "enviodevuelto";
	/**
	 * Nombre clave de las entradas para marcar la fecha de envio a 472
	 */
	public static final String NOMBRE_ENTRADA_FECHAENVIO472 = "fechaenvio472";
	/**
	 * Nombre clave de las entradas para marcar la fecha guía a 472
	 */
	public static final String NOMBRE_ENTRADA_FECHAGUIA472 = "fechaguia472";
	/**
	 * Nombre clave de las entradas para marcar el número de guía 472
	 */
	public static final String NOMBRE_ENTRADA_NUMEROGUIA = "Guía";
	/**
	 * Nombre clave de las entradas para marcar el número de guía 472
	 */
	public static final String NOMBRE_ENTRADA_CAUSARECHAZO = "Causa Rechazo";
	/**
	 * Nombre clave de las entradas para marcar si se debe reenviar el radicado
	 */
	public static final String NOMBRE_ENTRADA_REENVIAR = "reenviar";

	/**
	 * Nombre clave del campo para un parámetro tipo lista que indica la ciudad
	 */
	public static final String NOMBRE_CAMPO_PARAMETRO_LISTA_CIUDAD = "Ciudad";
	/**
	 * Nombre clave del campo para un parámetro tipo lista que indica la dirección
	 */
	public static final String NOMBRE_CAMPO_PARAMETRO_LISTA_DIRECCION = "Dirección";
	/**
	 * Nombre clave del campo para un parámetro tipo lista que indica el teléfono
	 */
	public static final String NOMBRE_CAMPO_PARAMETRO_LISTA_TELEFONO = "Teléfono";

	/**
	 * Nombre clave del proceso de correspondencia recibida
	 */
	public static final String NOMBRE_PROCESO_CORRESPONDENCIA_RECIBIDA = "CORRESPONDENCIA_RECIBIDA";
	/**
	 * Nombre clave del proceso de generación ee
	 */
	public static final String NOMBRE_PROCESO_GENERACION_EE = "GENERACION_EE";
	/**
	 * Nombre clave de la etapa de Envío Generado
	 */
	public static final String NOMBRE_ETAPA_ENVIO_GENERADO = "ENVÍO GENERADO";
	
	
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos URL
	 */
	public static final String PARAMETRO_INTEGRACION_CATALOGOS_URL = "INTEGRACION_CATALOGOS_URL";

	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Módulo
	 */
	public static final String PARAMETRO_INTEGRACION_CATALOGOS_MODULO = "INTEGRACION_CATALOGOS_MODULO";

	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Nombre Catálogo
	 */
	public static final String PARAMETRO_INTEGRACION_CATALOGOS_CATALOGO = "INTEGRACION_CATALOGOS_CATALOGO";

	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Orip por Usuario
	 */
	public static final String PARAMETRO_INTEGRACION_CATALOGOS_ORIPPORUSUARIO = "INTEGRACION_CATALOGOS_ORIPPORUSUARIO";

	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Orip por Usuario
	 */
	public static final String PARAMETRO_NOMBRE_ROL_ADMINISTRADOR = "NOMBRE_ROL_ADMINISTRADOR";


	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Envio Documentos
	 */
	public static final String PARAMETRO_CATALOGO_JOB_ENVIO_GESTOR_DOCUMENTAL_ENDPOINT = "CATALOGO_JOB_ENVIO_GESTOR_DOCUMENTAL_ENDPOINT";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Consulta Documentos
	 */
	public static final String PARAMETRO_CATALOGO_CONSULTA_DOCUMENTOS_OWCC_ENDPOINT = "CATALOGO_CONSULTA_DOCUMENTOS_OWCC_ENDPOINT";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Digitalización Capture
	 */
	public static final String PARAMETRO_CATALOGO_URL_DIGITALIZACION_CAPTURE_CORRESPONDENCIA = "CATALOGO_URL_DIGITALIZACION_CAPTURE_CORRESPONDENCIA";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Notificador
	 */
	public static final String PARAMETRO_CATALOGO_NOTIFICADOR_CORRESPONDENCIA_ENDPOINT = "CATALOGO_NOTIFICADOR_CORRESPONDENCIA_ENDPOINT";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Bioclient Imprimir PDF
	 */
	public static final String PARAMETRO_CATALOGO_INTEGRACION_BIOCLIENT_IMPRIMIRPDF = "CATALOGO_INTEGRACION_BIOCLIENT_IMPRIMIRPDF";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Bioclient Verificar
	 */
	public static final String PARAMETRO_CATALOGO_INTEGRACION_BIOCLIENT_VERIFY = "CATALOGO_INTEGRACION_BIOCLIENT_VERIFY";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Gestión Usuarios 
	 */
	public static final String PARAMETRO_CATALOGO_INTEGRACION_CAWEB_URL = "CATALOGO_INTEGRACION_CAWEB_URL";
	/**
	 * Nombre para el parámetro de sistema Integración Catálogos Tipo Catálogo Bioclient verificar segundo factor
	 */
	public static final String PARAMETRO_CATALOGO_PAD_DE_FIRMAS_ENDPOINT = "CATALOGO_PAD_DE_FIRMAS_ENDPOINT";
	/**
	 * Nombre para el parámetro de sistema para identificar el componente asociado a correspondencia
	 */
	public static final String PARAMETRO_CAWEB_COMPONENETE = "CAWEB_COMPONENTE";
	/**
	 * Nombre para el parámetro de sistema para la URL de logout
	 */
	public static final String PARAMETRO_CAWEB_LOGOUT_URL = "CAWEB_LOGOUT_URL";
	/**
	 * Nombre para el parámetro de sistema Integración owcc profile
	 */
	public static final String PARAMETRO_OWCC_PROFILE = "OWCC_PROFILE";
	/**
	 * Nombre para el parámetro de sistema Integración owcc radicado
	 */
	public static final String PARAMETRO_OWCC_PARAM_RADICADO = "OWCC_PARAM_RADICADO";
	/**
	 * Nombre para el parámetro de sistema Integración owcc destinatario
	 */
	public static final String PARAMETRO_OWCC_PARAM_DESTINATARIO = "OWCC_PARAM_DESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema Integración owcc codigo orip
	 */
	public static final String PARAMETRO_OWCC_PARAM_CODIGO_ORIP = "OWCC_PARAM_CODIGO_ORIP";
	/**
	 * Nombre para el parámetro de sistema Integración owcc doc title
	 */
	public static final String PARAMETRO_OWCC_PARAM_DOC_TITLE = "OWCC_PARAM_DOC_TITLE";
	/**
	 * Nombre para el parámetro de sistema Integración owcc doc type
	 */
	public static final String PARAMETRO_OWCC_PARAM_DOC_TYPE = "OWCC_PARAM_DOC_TYPE";
	/**
	 * Nombre para el parámetro de sistema Integración owcc doc type valor por defecto
	 */
	public static final String PARAMETRO_OWCC_PARAM_DOC_TYPE_VALUE = "OWCC_PARAM_DOC_TYPE_VALUE";
	/**
	 * Nombre para el parámetro de sistema Integración owcc doc id
	 */
	public static final String PARAMETRO_OWCC_PARAM_DOC_ID = "OWCC_PARAM_DOC_ID";
	/**
	 * Valor para el parámetro de sistema Integración owcc valor para repositorio final
	 */
	public static final String PARAMETRO_OWCC_PARAM_REPOSITORIO_FINAL_VALUE = "OWCC_PARAM_REPOSITORIO_FINAL_VALUE";
	/**
	 * Valor para el parámetro de sistema Integración owcc valor para repositorio temporal
	 */
	public static final String PARAMETRO_OWCC_PARAM_REPOSITORIO_TEMPORAL_VALUE = "OWCC_PARAM_REPOSITORIO_TEMPORAL_VALUE";
	/**
	 * Valor para el parámetro de sistema integración correos usuario
	 */
	public static final String PARAMETRO_CORREO_USUARIO = "CORREO_USUARIO";
	/**
	 * Valor para el parámetro de sistema integración correos clave
	 */
	public static final String PARAMETRO_CORREO_CLAVE = "CORREO_CLAVE";
	/**
	 * Valor para el parámetro de sistema integración correos estructura
	 */
	public static final String PARAMETRO_CORREO_ESTRUCTURA = "CORREO_ESTRUCTURA";

	/**
	 * Valor para el parámetro de sistema integración correos recordatorio paso asunto
	 */
	public static final String PARAMETRO_CORREO_RECORDATORIO_PASO_ASUNTO = "CORREO_RECORDATORIO_PASO_ASUNTO";
	/**
	 * Valor para el parámetro de sistema integración correos recordatorio paso texto
	 */
	public static final String PARAMETRO_CORREO_RECORDATORIO_PASO_TEXTO = "CORREO_RECORDATORIO_PASO_TEXTO";
	/**
	 * Valor para el parámetro de sistema integración correos proceso finalizado asunto
	 */
	public static final String PARAMETRO_CORREO_FINAL_PROCESO_ASUNTO = "CORREO_FINAL_PROCESO_ASUNTO";
	/**
	 * Valor para el parámetro de sistema integración correos proceso finalizado asunto
	 */
	public static final String PARAMETRO_CORREO_FINAL_PROCESO_TEXTO = "CORREO_FINAL_PROCESO_TEXTO";
	/**
	 * Valor para el parámetro de sistema integración correos paso atrasado finalizado asunto
	 */
	public static final String PARAMETRO_CORREO_PASO_ATRASADO_ASUNTO = "CORREO_PASO_ATRASADO_ASUNTO";
	/**
	 * Valor para el parámetro de sistema integración correos paso atrasado finalizado texto
	 */
	public static final String PARAMETRO_CORREO_PASO_ATRASADO_TEXTO = "CORREO_PASO_ATRASADO_TEXTO";
	/**
	 * Valor para el parámetro de sistema integración correos paso actual asunto
	 */
	public static final String PARAMETRO_CORREO_PASO_ACTUAL_ASUNTO = "CORREO_PASO_ACTUAL_ASUNTO";
	/**
	 * Valor para el parámetro de sistema integración correos paso actual texto
	 */
	public static final String PARAMETRO_CORREO_PASO_ACTUAL_TEXTO = "CORREO_PASO_ACTUAL_TEXTO";
	/**
	 * Valor para el parámetro de sistema integración correos paso próximo asunto
	 */
	public static final String PARAMETRO_CORREO_PASO_PROXIMO_ASUNTO = "CORREO_PASO_PROXIMO_ASUNTO";
	/**
	 * Valor para el parámetro de sistema integración correos paso próximo texto
	 */
	public static final String PARAMETRO_CORREO_PASO_PROXIMO_TEXTO = "CORREO_PASO_PROXIMO_TEXTO";

	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo orip
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_ORIP = "GENERACIONEE_CAMPO_ORIP";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo clasificación
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_CLASIFICACION = "GENERACIONEE_CAMPO_CLASIFICACION";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo canal
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_CANAL = "GENERACIONEE_CAMPO_CANAL";
	/**
	 * Valor para el parámetro de sistema proceso generación ee campo canal físico
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_CANAL_FISICO = "GENERACIONEE_CAMPO_CANAL_FISICO";
	/**
	 * Valor para el parámetro de sistema proceso generación ee campo canal electrónico
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_CANAL_ELECTRONICO = "GENERACIONEE_CAMPO_CANAL_ELECTRONICO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo nir
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_NIR = "GENERACIONEE_CAMPO_NIR";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo turno
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_TURNO = "GENERACIONEE_CAMPO_TURNO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo número folios
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_NUMEROFOLIOS = "GENERACIONEE_CAMPO_NUMEROFOLIOS";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo radicación manual
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_RADICACIONMANUAL = "GENERACIONEE_CAMPO_RADICACIONMANUAL";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo nombre documento
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_NOMBREDOCUMENTO = "GENERACIONEE_CAMPO_NOMBREDOCUMENTO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo tipo documento destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_TIPODOCUMENTODESTINATARIO = "GENERACIONEE_CAMPO_TIPODOCUMENTODESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo número documento destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_NUMERODOCUMENTODESTINATARIO = "GENERACIONEE_CAMPO_NUMERODOCUMENTODESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo primer nombre destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_PRIMERNOMBREDESTINATARIO = "GENERACIONEE_CAMPO_PRIMERNOMBREDESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo segundo nombre destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_SEGUNDONOMBREDESTINATARIO = "GENERACIONEE_CAMPO_SEGUNDONOMBREDESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo primer apellido destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_PRIMERAPELLIDODESTINATARIO = "GENERACIONEE_CAMPO_PRIMERAPELLIDODESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo segundo apellido destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_SEGUNDOAPELLIDODESTINATARIO = "GENERACIONEE_CAMPO_SEGUNDOAPELLIDODESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo ciudad destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_CIUDADDESTINATARIO = "GENERACIONEE_CAMPO_CIUDADDESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo dirección destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_DIRECCIONDESTINATARIO = "GENERACIONEE_CAMPO_DIRECCIONDESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo teléfono destinatario
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_TELEFONODESTINATARIO = "GENERACIONEE_CAMPO_TELEFONODESTINATARIO";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo llave
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_LLAVE = "GENERACIONEE_CAMPO_LLAVE";
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo valor
	 */
	public static final String PARAMETRO_GENERACIONEE_CAMPO_VALOR = "GENERACIONEE_CAMPO_VALOR";

}

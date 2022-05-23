package co.gov.supernotariado.bachue.correspondencia.ejb.integraciones;

/**
 * Atributos para la consulta del catálogo
 */
public class ParametrosSistemaTO {
	/**
	 * URL para consultar el catálogo
	 */
	private String endpoint;
	/**
	 * Módulo a consultar en el catálogo
	 */
	private String modulo;
	/**
	 * Nombre de catálogo a consultar en el catálogo
	 */
	private String catalogo;
	/**
	 * Nombre de catálogo a consultar en el catálogo las orip por usuario
	 */
	private String catalogoOrip;
	/**
	 * Nombre de parámetro para consultar el catálogo de envío documentos
	 */
	private String paramEnvioDocumentos;
	/**
	 * Nombre de parámetro para consultar el catálogo de consulta documentos
	 */
	private String paramConsultaDocumentos;
	/**
	 * Nombre de parámetro para consultar el catálogo de digitalización
	 */
	private String paramDigitalizacion;
	/**
	 * Nombre de parámetro para consultar el catálogo de notificador
	 */
	private String paramNotificador;
	/**
	 * Nombre de parámetro para consultar el catálogo de bioclient
	 */
	private String paramBioclient;
	/**
	 * Nombre de parámetro para consultar el catálogo de bioclient verificar
	 */
	private String paramBioclientVerificar;
	/**
	 * Nombre de parámetro para consultar el catálogo de gestión usuarios
	 */
	private String paramGestionUsuarios;
	/**
	 * Nombre de parámetro para consultar el catálogo de bioclient segunda clave
	 */
	private String paramBioclientSegunda;
	/**
	 * Nombre de parámetro para ca web (gestión usuarios) componente
	 */
	private String paramCawebComponente;
	/**
	 * Nombre de parámetro para ca web url de logout
	 */
	private String paramCawebLogoutUrl;
	/**
	 * Nombre de parámetro para owcc profile
	 */
	private String paramOwccProfile;
	/**
	 * Nombre de parámetro para owcc radicado
	 */
	private String paramOwccParamRadicado;
	/**
	 * Nombre de parámetro para owcc destinatario
	 */
	private String paramOwccParamDestinatario;
	/**
	 * Nombre de parámetro para owcc codigo orip
	 */
	private String paramOwccParamCodigoOrip;
	/**
	 * Nombre de parámetro para owcc doc title
	 */
	private String paramOwccParamDocTitle;
	/**
	 * Nombre de parámetro para owcc doc type
	 */
	private String paramOwccParamDocType;
	/**
	 * Nombre de parámetro para owcc doc type valor por defecto
	 */
	private String paramOwccParamDocTypeValue;
	/**
	 * Nombre de parámetro para owcc doc id
	 */
	private String paramOwccParamDocId;
	/**
	 * Nombre de parámetro para owcc repositorio final
	 */
	private String paramOwccParamRepositorioValueFinal;
	/**
	 * Nombre de parámetro para owcc repositorio temporal
	 */
	private String paramOwccParamRepositorioValueTemporal;

	/**
	 * Nombre de parámetro para correos usuario
	 */
	private String paramCorreosUsuario;
	/**
	 * Nombre de parámetro para correos clave
	 */
	private String paramCorreosClave;
	/**
	 * Nombre de parámetro para correos estructura
	 */
	private String paramCorreosEstructura;
	/**
	 * Nombre de parámetro para correos recordatorio paso asunto
	 */
	private String paramCorreosRecordatorioPasoAsunto;
	/**
	 * Nombre de parámetro para correos recordatorio paso texto
	 */
	private String paramCorreosRecordatorioPasoTexto;
	/**
	 * Nombre de parámetro para correos proceso finalizado asunto
	 */
	private String paramCorreosProcesoFinalizadoAsunto;
	/**
	 * Nombre de parámetro para correos proceso finalizado texto
	 */
	private String paramCorreosProcesoFinalizadoTexto;
	/**
	 * Nombre de parámetro para correos paso atrasado asunto
	 */
	private String paramCorreosPasoAtrasadoAsunto;
	/**
	 * Nombre de parámetro para correos paso atrasado texto
	 */
	private String paramCorreosPasoAtrasadoTexto;
	/**
	 * Nombre de parámetro para correos paso actual asunto
	 */
	private String paramCorreosPasoActualAsunto;
	/**
	 * Nombre de parámetro para correos paso actual texto
	 */
	private String paramCorreosPasoActualTexto;
	/**
	 * Nombre de parámetro para correos paso próximo asunto
	 */
	private String paramCorreosPasoProximoAsunto;
	/**
	 * Nombre de parámetro para correos paso próximo texto
	 */
	private String paramCorreosPasoProximoTexto;

	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo orip
	 */
	private String paramGeneracioneeOrip;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo clasificación
	 */
	private String paramGeneracioneeClasificacion;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo canal
	 */
	private String paramGeneracioneeCanal;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo canal físico
	 */
	private String paramGeneracioneeCanalFisico;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo canal electrónico
	 */
	private String paramGeneracioneeCanalElectronico;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo nir
	 */
	private String paramGeneracioneeNir;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo turno
	 */
	private String paramGeneracioneeTurno;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo número folios
	 */
	private String paramGeneracioneeNumeroFolios;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo radicación manual
	 */
	private String paramGeneracioneeRadicacionManual;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo nombre documento
	 */
	private String paramGeneracioneeNombreDocumento;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo tipo documento destinatario
	 */
	private String paramGeneracioneeTipoDocumentoDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo número documento destinatario
	 */
	private String paramGeneracioneeNumeroDocumentoDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo primer nombre destinatario
	 */
	private String paramGeneracioneePrimerNombreDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo segundo nombre destinatario
	 */
	private String paramGeneracioneeSegundoNombreDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo primer apellido destinatario
	 */
	private String paramGeneracioneePrimerApellidoDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo segundo apellido destinatario
	 */
	private String paramGeneracioneeSegundoApellidoDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo ciudad destinatario
	 */
	private String paramGeneracioneeCiudadDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo dirección destinatario
	 */
	private String paramGeneracioneeDireccionDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo teléfono destinatario
	 */
	private String paramGeneracioneeTelefonoDestinatario;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo llave
	 */
	private String paramGeneracioneeLlave;
	/**
	 * Nombre para el parámetro de sistema proceso generación ee campo valor
	 */
	private String paramGeneracioneeValor;

	
	/**
	 * Obtiene el valor del atributo endpoint
	 * @return El valor del atributo endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}
	/**
	 * Establece el valor del atributo endpoint
	 * @param endpoint con el valor del atributo endpoint a establecer
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	/**
	 * Obtiene el valor del atributo modulo
	 * @return El valor del atributo modulo
	 */
	public String getModulo() {
		return modulo;
	}
	/**
	 * Establece el valor del atributo modulo
	 * @param modulo con el valor del atributo modulo a establecer
	 */
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	/**
	 * Obtiene el valor del atributo catalogo
	 * @return El valor del atributo catalogo
	 */
	public String getCatalogo() {
		return catalogo;
	}
	/**
	 * Establece el valor del atributo catalogo
	 * @param catalogo con el valor del atributo catalogo a establecer
	 */
	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}
	/**
	 * Obtiene el valor del atributo catalogoOrip
	 * @return El valor del atributo catalogoOrip
	 */
	public String getCatalogoOrip() {
		return catalogoOrip;
	}
	/**
	 * Establece el valor del atributo catalogoOrip
	 * @param catalogoOrip con el valor del atributo catalogoOrip a establecer
	 */
	public void setCatalogoOrip(String catalogoOrip) {
		this.catalogoOrip = catalogoOrip;
	}
	/**
	 * Obtiene el valor del atributo paramEnvioDocumentos
	 * @return El valor del atributo paramEnvioDocumentos
	 */
	public String getParamEnvioDocumentos() {
		return paramEnvioDocumentos;
	}
	/**
	 * Establece el valor del atributo paramEnvioDocumentos
	 * @param paramEnvioDocumentos con el valor del atributo paramEnvioDocumentos a establecer
	 */
	public void setParamEnvioDocumentos(String paramEnvioDocumentos) {
		this.paramEnvioDocumentos = paramEnvioDocumentos;
	}
	/**
	 * Obtiene el valor del atributo paramConsultaDocumentos
	 * @return El valor del atributo paramConsultaDocumentos
	 */
	public String getParamConsultaDocumentos() {
		return paramConsultaDocumentos;
	}
	/**
	 * Establece el valor del atributo paramConsultaDocumentos
	 * @param paramConsultaDocumentos con el valor del atributo paramConsultaDocumentos a establecer
	 */
	public void setParamConsultaDocumentos(String paramConsultaDocumentos) {
		this.paramConsultaDocumentos = paramConsultaDocumentos;
	}
	/**
	 * Obtiene el valor del atributo paramDigitalizacion
	 * @return El valor del atributo paramDigitalizacion
	 */
	public String getParamDigitalizacion() {
		return paramDigitalizacion;
	}
	/**
	 * Establece el valor del atributo paramDigitalizacion
	 * @param paramDigitalizacion con el valor del atributo paramDigitalizacion a establecer
	 */
	public void setParamDigitalizacion(String paramDigitalizacion) {
		this.paramDigitalizacion = paramDigitalizacion;
	}
	/**
	 * Obtiene el valor del atributo paramNotificador
	 * @return El valor del atributo paramNotificador
	 */
	public String getParamNotificador() {
		return paramNotificador;
	}
	/**
	 * Establece el valor del atributo paramNotificador
	 * @param paramNotificador con el valor del atributo paramNotificador a establecer
	 */
	public void setParamNotificador(String paramNotificador) {
		this.paramNotificador = paramNotificador;
	}
	/**
	 * Obtiene el valor del atributo paramBioclient
	 * @return El valor del atributo paramBioclient
	 */
	public String getParamBioclient() {
		return paramBioclient;
	}
	/**
	 * Establece el valor del atributo paramBioclient
	 * @param paramBioclient con el valor del atributo paramBioclient a establecer
	 */
	public void setParamBioclient(String paramBioclient) {
		this.paramBioclient = paramBioclient;
	}
	/**
	 * Obtiene el valor del atributo paramBioclientVerificar
	 * @return El valor del atributo paramBioclientVerificar
	 */
	public String getParamBioclientVerificar() {
		return paramBioclientVerificar;
	}
	/**
	 * Establece el valor del atributo paramBioclientVerificar
	 * @param paramBioclientVerificar con el valor del atributo paramBioclientVerificar a establecer
	 */
	public void setParamBioclientVerificar(String paramBioclientVerificar) {
		this.paramBioclientVerificar = paramBioclientVerificar;
	}
	/**
	 * Obtiene el valor del atributo paramGestionUsuarios
	 * @return El valor del atributo paramGestionUsuarios
	 */
	public String getParamGestionUsuarios() {
		return paramGestionUsuarios;
	}
	/**
	 * Establece el valor del atributo paramGestionUsuarios
	 * @param paramGestionUsuarios con el valor del atributo paramGestionUsuarios a establecer
	 */
	public void setParamGestionUsuarios(String paramGestionUsuarios) {
		this.paramGestionUsuarios = paramGestionUsuarios;
	}
	/**
	 * Obtiene el valor del atributo paramBioclientSegunda
	 * @return El valor del atributo paramBioclientSegunda
	 */
	public String getParamBioclientSegunda() {
		return paramBioclientSegunda;
	}
	/**
	 * Establece el valor del atributo paramBioclientSegunda
	 * @param paramBioclientSegunda con el valor del atributo paramBioclientSegunda a establecer
	 */
	public void setParamBioclientSegunda(String paramBioclientSegunda) {
		this.paramBioclientSegunda = paramBioclientSegunda;
	}
	/**
	 * Obtiene el valor del atributo paramOwccProfile
	 * @return El valor del atributo paramOwccProfile
	 */
	public String getParamOwccProfile() {
		return paramOwccProfile;
	}
	/**
	 * Establece el valor del atributo paramOwccProfile
	 * @param paramOwccProfile con el valor del atributo paramOwccProfile a establecer
	 */
	public void setParamOwccProfile(String paramOwccProfile) {
		this.paramOwccProfile = paramOwccProfile;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamRadicado
	 * @return El valor del atributo paramOwccParamRadicado
	 */
	public String getParamOwccParamRadicado() {
		return paramOwccParamRadicado;
	}
	/**
	 * Establece el valor del atributo paramOwccParamRadicado
	 * @param paramOwccParamRadicado con el valor del atributo paramOwccParamRadicado a establecer
	 */
	public void setParamOwccParamRadicado(String paramOwccParamRadicado) {
		this.paramOwccParamRadicado = paramOwccParamRadicado;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamCodigoOrip
	 * @return El valor del atributo paramOwccParamCodigoOrip
	 */
	public String getParamOwccParamCodigoOrip() {
		return paramOwccParamCodigoOrip;
	}
	/**
	 * Establece el valor del atributo paramOwccParamCodigoOrip
	 * @param paramOwccParamCodigoOrip con el valor del atributo paramOwccParamCodigoOrip a establecer
	 */
	public void setParamOwccParamCodigoOrip(String paramOwccParamCodigoOrip) {
		this.paramOwccParamCodigoOrip = paramOwccParamCodigoOrip;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamDocType
	 * @return El valor del atributo paramOwccParamDocType
	 */
	public String getParamOwccParamDocType() {
		return paramOwccParamDocType;
	}
	/**
	 * Establece el valor del atributo paramOwccParamDocType
	 * @param paramOwccParamDocType con el valor del atributo paramOwccParamDocType a establecer
	 */
	public void setParamOwccParamDocType(String paramOwccParamDocType) {
		this.paramOwccParamDocType = paramOwccParamDocType;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamDocTypeValue
	 * @return El valor del atributo paramOwccParamDocTypeValue
	 */
	public String getParamOwccParamDocTypeValue() {
		return paramOwccParamDocTypeValue;
	}
	/**
	 * Establece el valor del atributo paramOwccParamDocTypeValue
	 * @param paramOwccParamDocTypeValue con el valor del atributo paramOwccParamDocTypeValue a establecer
	 */
	public void setParamOwccParamDocTypeValue(String paramOwccParamDocTypeValue) {
		this.paramOwccParamDocTypeValue = paramOwccParamDocTypeValue;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamDocId
	 * @return El valor del atributo paramOwccParamDocId
	 */
	public String getParamOwccParamDocId() {
		return paramOwccParamDocId;
	}
	/**
	 * Establece el valor del atributo paramOwccParamDocId
	 * @param paramOwccParamDocId con el valor del atributo paramOwccParamDocId a establecer
	 */
	public void setParamOwccParamDocId(String paramOwccParamDocId) {
		this.paramOwccParamDocId = paramOwccParamDocId;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamRepositorioValueFinal
	 * @return El valor del atributo paramOwccParamRepositorioValueFinal
	 */
	public String getParamOwccParamRepositorioValueFinal() {
		return paramOwccParamRepositorioValueFinal;
	}
	/**
	 * Establece el valor del atributo paramOwccParamRepositorioValueFinal
	 * @param paramOwccParamRepositorioValueFinal con el valor del atributo paramOwccParamRepositorioValueFinal a establecer
	 */
	public void setParamOwccParamRepositorioValueFinal(String paramOwccParamRepositorioValueFinal) {
		this.paramOwccParamRepositorioValueFinal = paramOwccParamRepositorioValueFinal;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamDocTitle
	 * @return El valor del atributo paramOwccParamDocTitle
	 */
	public String getParamOwccParamDocTitle() {
		return paramOwccParamDocTitle;
	}
	/**
	 * Establece el valor del atributo paramOwccParamDocTitle
	 * @param paramOwccParamDocTitle con el valor del atributo paramOwccParamDocTitle a establecer
	 */
	public void setParamOwccParamDocTitle(String paramOwccParamDocTitle) {
		this.paramOwccParamDocTitle = paramOwccParamDocTitle;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamRepositorioValueTemporal
	 * @return El valor del atributo paramOwccParamRepositorioValueTemporal
	 */
	public String getParamOwccParamRepositorioValueTemporal() {
		return paramOwccParamRepositorioValueTemporal;
	}
	/**
	 * Establece el valor del atributo paramOwccParamRepositorioValueTemporal
	 * @param paramOwccParamRepositorioValueTemporal con el valor del atributo paramOwccParamRepositorioValueTemporal a establecer
	 */
	public void setParamOwccParamRepositorioValueTemporal(String paramOwccParamRepositorioValueTemporal) {
		this.paramOwccParamRepositorioValueTemporal = paramOwccParamRepositorioValueTemporal;
	}
	/**
	 * Obtiene el valor del atributo paramCawebComponente
	 * @return El valor del atributo paramCawebComponente
	 */
	public String getParamCawebComponente() {
		return paramCawebComponente;
	}
	/**
	 * Establece el valor del atributo paramCawebComponente
	 * @param paramCawebComponente con el valor del atributo paramCawebComponente a establecer
	 */
	public void setParamCawebComponente(String paramCawebComponente) {
		this.paramCawebComponente = paramCawebComponente;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosUsuario
	 * @return El valor del atributo paramCorreosUsuario
	 */
	public String getParamCorreosUsuario() {
		return paramCorreosUsuario;
	}
	/**
	 * Establece el valor del atributo paramCorreosUsuario
	 * @param paramCorreosUsuario con el valor del atributo paramCorreosUsuario a establecer
	 */
	public void setParamCorreosUsuario(String paramCorreosUsuario) {
		this.paramCorreosUsuario = paramCorreosUsuario;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosClave
	 * @return El valor del atributo paramCorreosClave
	 */
	public String getParamCorreosClave() {
		return paramCorreosClave;
	}
	/**
	 * Establece el valor del atributo paramCorreosClave
	 * @param paramCorreosClave con el valor del atributo paramCorreosClave a establecer
	 */
	public void setParamCorreosClave(String paramCorreosClave) {
		this.paramCorreosClave = paramCorreosClave;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosEstructura
	 * @return El valor del atributo paramCorreosEstructura
	 */
	public String getParamCorreosEstructura() {
		return paramCorreosEstructura;
	}
	/**
	 * Establece el valor del atributo paramCorreosEstructura
	 * @param paramCorreosEstructura con el valor del atributo paramCorreosEstructura a establecer
	 */
	public void setParamCorreosEstructura(String paramCorreosEstructura) {
		this.paramCorreosEstructura = paramCorreosEstructura;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosRecordatorioPasoAsunto
	 * @return El valor del atributo paramCorreosRecordatorioPasoAsunto
	 */
	public String getParamCorreosRecordatorioPasoAsunto() {
		return paramCorreosRecordatorioPasoAsunto;
	}
	/**
	 * Establece el valor del atributo paramCorreosRecordatorioPasoAsunto
	 * @param paramCorreosRecordatorioPasoAsunto con el valor del atributo paramCorreosRecordatorioPasoAsunto a establecer
	 */
	public void setParamCorreosRecordatorioPasoAsunto(String paramCorreosRecordatorioPasoAsunto) {
		this.paramCorreosRecordatorioPasoAsunto = paramCorreosRecordatorioPasoAsunto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosRecordatorioPasoTexto
	 * @return El valor del atributo paramCorreosRecordatorioPasoTexto
	 */
	public String getParamCorreosRecordatorioPasoTexto() {
		return paramCorreosRecordatorioPasoTexto;
	}
	/**
	 * Establece el valor del atributo paramCorreosRecordatorioPasoTexto
	 * @param paramCorreosRecordatorioPasoTexto con el valor del atributo paramCorreosRecordatorioPasoTexto a establecer
	 */
	public void setParamCorreosRecordatorioPasoTexto(String paramCorreosRecordatorioPasoTexto) {
		this.paramCorreosRecordatorioPasoTexto = paramCorreosRecordatorioPasoTexto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosProcesoFinalizadoAsunto
	 * @return El valor del atributo paramCorreosProcesoFinalizadoAsunto
	 */
	public String getParamCorreosProcesoFinalizadoAsunto() {
		return paramCorreosProcesoFinalizadoAsunto;
	}
	/**
	 * Establece el valor del atributo paramCorreosProcesoFinalizadoAsunto
	 * @param paramCorreosProcesoFinalizadoAsunto con el valor del atributo paramCorreosProcesoFinalizadoAsunto a establecer
	 */
	public void setParamCorreosProcesoFinalizadoAsunto(String paramCorreosProcesoFinalizadoAsunto) {
		this.paramCorreosProcesoFinalizadoAsunto = paramCorreosProcesoFinalizadoAsunto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosProcesoFinalizadoTexto
	 * @return El valor del atributo paramCorreosProcesoFinalizadoTexto
	 */
	public String getParamCorreosProcesoFinalizadoTexto() {
		return paramCorreosProcesoFinalizadoTexto;
	}
	/**
	 * Establece el valor del atributo paramCorreosProcesoFinalizadoTexto
	 * @param paramCorreosProcesoFinalizadoTexto con el valor del atributo paramCorreosProcesoFinalizadoTexto a establecer
	 */
	public void setParamCorreosProcesoFinalizadoTexto(String paramCorreosProcesoFinalizadoTexto) {
		this.paramCorreosProcesoFinalizadoTexto = paramCorreosProcesoFinalizadoTexto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosPasoAtrasadoAsunto
	 * @return El valor del atributo paramCorreosPasoAtrasadoAsunto
	 */
	public String getParamCorreosPasoAtrasadoAsunto() {
		return paramCorreosPasoAtrasadoAsunto;
	}
	/**
	 * Establece el valor del atributo paramCorreosPasoAtrasadoAsunto
	 * @param paramCorreosPasoAtrasadoAsunto con el valor del atributo paramCorreosPasoAtrasadoAsunto a establecer
	 */
	public void setParamCorreosPasoAtrasadoAsunto(String paramCorreosPasoAtrasadoAsunto) {
		this.paramCorreosPasoAtrasadoAsunto = paramCorreosPasoAtrasadoAsunto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosPasoAtrasadoTexto
	 * @return El valor del atributo paramCorreosPasoAtrasadoTexto
	 */
	public String getParamCorreosPasoAtrasadoTexto() {
		return paramCorreosPasoAtrasadoTexto;
	}
	/**
	 * Establece el valor del atributo paramCorreosPasoAtrasadoTexto
	 * @param paramCorreosPasoAtrasadoTexto con el valor del atributo paramCorreosPasoAtrasadoTexto a establecer
	 */
	public void setParamCorreosPasoAtrasadoTexto(String paramCorreosPasoAtrasadoTexto) {
		this.paramCorreosPasoAtrasadoTexto = paramCorreosPasoAtrasadoTexto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosPasoActualAsunto
	 * @return El valor del atributo paramCorreosPasoActualAsunto
	 */
	public String getParamCorreosPasoActualAsunto() {
		return paramCorreosPasoActualAsunto;
	}
	/**
	 * Establece el valor del atributo paramCorreosPasoActualAsunto
	 * @param paramCorreosPasoActualAsunto con el valor del atributo paramCorreosPasoActualAsunto a establecer
	 */
	public void setParamCorreosPasoActualAsunto(String paramCorreosPasoActualAsunto) {
		this.paramCorreosPasoActualAsunto = paramCorreosPasoActualAsunto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosPasoActualTexto
	 * @return El valor del atributo paramCorreosPasoActualTexto
	 */
	public String getParamCorreosPasoActualTexto() {
		return paramCorreosPasoActualTexto;
	}
	/**
	 * Establece el valor del atributo paramCorreosPasoActualTexto
	 * @param paramCorreosPasoActualTexto con el valor del atributo paramCorreosPasoActualTexto a establecer
	 */
	public void setParamCorreosPasoActualTexto(String paramCorreosPasoActualTexto) {
		this.paramCorreosPasoActualTexto = paramCorreosPasoActualTexto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosPasoProximoAsunto
	 * @return El valor del atributo paramCorreosPasoProximoAsunto
	 */
	public String getParamCorreosPasoProximoAsunto() {
		return paramCorreosPasoProximoAsunto;
	}
	/**
	 * Establece el valor del atributo paramCorreosPasoProximoAsunto
	 * @param paramCorreosPasoProximoAsunto con el valor del atributo paramCorreosPasoProximoAsunto a establecer
	 */
	public void setParamCorreosPasoProximoAsunto(String paramCorreosPasoProximoAsunto) {
		this.paramCorreosPasoProximoAsunto = paramCorreosPasoProximoAsunto;
	}
	/**
	 * Obtiene el valor del atributo paramCorreosPasoProximoTexto
	 * @return El valor del atributo paramCorreosPasoProximoTexto
	 */
	public String getParamCorreosPasoProximoTexto() {
		return paramCorreosPasoProximoTexto;
	}
	/**
	 * Establece el valor del atributo paramCorreosPasoProximoTexto
	 * @param paramCorreosPasoProximoTexto con el valor del atributo paramCorreosPasoProximoTexto a establecer
	 */
	public void setParamCorreosPasoProximoTexto(String paramCorreosPasoProximoTexto) {
		this.paramCorreosPasoProximoTexto = paramCorreosPasoProximoTexto;
	}
	/**
	 * Obtiene el valor del atributo paramCawebLogoutUrl
	 * @return El valor del atributo paramCawebLogoutUrl
	 */
	public String getParamCawebLogoutUrl() {
		return paramCawebLogoutUrl;
	}
	/**
	 * Establece el valor del atributo paramCawebLogoutUrl
	 * @param paramCawebLogoutUrl con el valor del atributo paramCawebLogoutUrl a establecer
	 */
	public void setParamCawebLogoutUrl(String paramCawebLogoutUrl) {
		this.paramCawebLogoutUrl = paramCawebLogoutUrl;
	}
	/**
	 * Obtiene el valor del atributo paramOwccParamDestinatario
	 * @return El valor del atributo paramOwccParamDestinatario
	 */
	public String getParamOwccParamDestinatario() {
		return paramOwccParamDestinatario;
	}
	/**
	 * Establece el valor del atributo paramOwccParamDestinatario
	 * @param paramOwccParamDestinatario con el valor del atributo paramOwccParamDestinatario a establecer
	 */
	public void setParamOwccParamDestinatario(String paramOwccParamDestinatario) {
		this.paramOwccParamDestinatario = paramOwccParamDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeOrip
	 * @return El valor del atributo paramGeneracioneeOrip
	 */
	public String getParamGeneracioneeOrip() {
		return paramGeneracioneeOrip;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeOrip
	 * @param paramGeneracioneeOrip con el valor del atributo paramGeneracioneeOrip a establecer
	 */
	public void setParamGeneracioneeOrip(String paramGeneracioneeOrip) {
		this.paramGeneracioneeOrip = paramGeneracioneeOrip;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeClasificacion
	 * @return El valor del atributo paramGeneracioneeClasificacion
	 */
	public String getParamGeneracioneeClasificacion() {
		return paramGeneracioneeClasificacion;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeClasificacion
	 * @param paramGeneracioneeClasificacion con el valor del atributo paramGeneracioneeClasificacion a establecer
	 */
	public void setParamGeneracioneeClasificacion(String paramGeneracioneeClasificacion) {
		this.paramGeneracioneeClasificacion = paramGeneracioneeClasificacion;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeCanal
	 * @return El valor del atributo paramGeneracioneeCanal
	 */
	public String getParamGeneracioneeCanal() {
		return paramGeneracioneeCanal;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeCanal
	 * @param paramGeneracioneeCanal con el valor del atributo paramGeneracioneeCanal a establecer
	 */
	public void setParamGeneracioneeCanal(String paramGeneracioneeCanal) {
		this.paramGeneracioneeCanal = paramGeneracioneeCanal;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeCanalFisico
	 * @return El valor del atributo paramGeneracioneeCanalFisico
	 */
	public String getParamGeneracioneeCanalFisico() {
		return paramGeneracioneeCanalFisico;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeCanalFisico
	 * @param paramGeneracioneeCanalFisico con el valor del atributo paramGeneracioneeCanalFisico a establecer
	 */
	public void setParamGeneracioneeCanalFisico(String paramGeneracioneeCanalFisico) {
		this.paramGeneracioneeCanalFisico = paramGeneracioneeCanalFisico;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeCanalElectronico
	 * @return El valor del atributo paramGeneracioneeCanalElectronico
	 */
	public String getParamGeneracioneeCanalElectronico() {
		return paramGeneracioneeCanalElectronico;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeCanalElectronico
	 * @param paramGeneracioneeCanalElectronico con el valor del atributo paramGeneracioneeCanalElectronico a establecer
	 */
	public void setParamGeneracioneeCanalElectronico(String paramGeneracioneeCanalElectronico) {
		this.paramGeneracioneeCanalElectronico = paramGeneracioneeCanalElectronico;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeNir
	 * @return El valor del atributo paramGeneracioneeNir
	 */
	public String getParamGeneracioneeNir() {
		return paramGeneracioneeNir;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeNir
	 * @param paramGeneracioneeNir con el valor del atributo paramGeneracioneeNir a establecer
	 */
	public void setParamGeneracioneeNir(String paramGeneracioneeNir) {
		this.paramGeneracioneeNir = paramGeneracioneeNir;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeTurno
	 * @return El valor del atributo paramGeneracioneeTurno
	 */
	public String getParamGeneracioneeTurno() {
		return paramGeneracioneeTurno;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeTurno
	 * @param paramGeneracioneeTurno con el valor del atributo paramGeneracioneeTurno a establecer
	 */
	public void setParamGeneracioneeTurno(String paramGeneracioneeTurno) {
		this.paramGeneracioneeTurno = paramGeneracioneeTurno;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeNumeroFolios
	 * @return El valor del atributo paramGeneracioneeNumeroFolios
	 */
	public String getParamGeneracioneeNumeroFolios() {
		return paramGeneracioneeNumeroFolios;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeNumeroFolios
	 * @param paramGeneracioneeNumeroFolios con el valor del atributo paramGeneracioneeNumeroFolios a establecer
	 */
	public void setParamGeneracioneeNumeroFolios(String paramGeneracioneeNumeroFolios) {
		this.paramGeneracioneeNumeroFolios = paramGeneracioneeNumeroFolios;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeRadicacionManual
	 * @return El valor del atributo paramGeneracioneeRadicacionManual
	 */
	public String getParamGeneracioneeRadicacionManual() {
		return paramGeneracioneeRadicacionManual;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeRadicacionManual
	 * @param paramGeneracioneeRadicacionManual con el valor del atributo paramGeneracioneeRadicacionManual a establecer
	 */
	public void setParamGeneracioneeRadicacionManual(String paramGeneracioneeRadicacionManual) {
		this.paramGeneracioneeRadicacionManual = paramGeneracioneeRadicacionManual;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeNombreDocumento
	 * @return El valor del atributo paramGeneracioneeNombreDocumento
	 */
	public String getParamGeneracioneeNombreDocumento() {
		return paramGeneracioneeNombreDocumento;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeNombreDocumento
	 * @param paramGeneracioneeNombreDocumento con el valor del atributo paramGeneracioneeNombreDocumento a establecer
	 */
	public void setParamGeneracioneeNombreDocumento(String paramGeneracioneeNombreDocumento) {
		this.paramGeneracioneeNombreDocumento = paramGeneracioneeNombreDocumento;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeTipoDocumentoDestinatario
	 * @return El valor del atributo paramGeneracioneeTipoDocumentoDestinatario
	 */
	public String getParamGeneracioneeTipoDocumentoDestinatario() {
		return paramGeneracioneeTipoDocumentoDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeTipoDocumentoDestinatario
	 * @param paramGeneracioneeTipoDocumentoDestinatario con el valor del atributo paramGeneracioneeTipoDocumentoDestinatario a establecer
	 */
	public void setParamGeneracioneeTipoDocumentoDestinatario(String paramGeneracioneeTipoDocumentoDestinatario) {
		this.paramGeneracioneeTipoDocumentoDestinatario = paramGeneracioneeTipoDocumentoDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeNumeroDocumentoDestinatario
	 * @return El valor del atributo paramGeneracioneeNumeroDocumentoDestinatario
	 */
	public String getParamGeneracioneeNumeroDocumentoDestinatario() {
		return paramGeneracioneeNumeroDocumentoDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeNumeroDocumentoDestinatario
	 * @param paramGeneracioneeNumeroDocumentoDestinatario con el valor del atributo paramGeneracioneeNumeroDocumentoDestinatario a establecer
	 */
	public void setParamGeneracioneeNumeroDocumentoDestinatario(String paramGeneracioneeNumeroDocumentoDestinatario) {
		this.paramGeneracioneeNumeroDocumentoDestinatario = paramGeneracioneeNumeroDocumentoDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneePrimerNombreDestinatario
	 * @return El valor del atributo paramGeneracioneePrimerNombreDestinatario
	 */
	public String getParamGeneracioneePrimerNombreDestinatario() {
		return paramGeneracioneePrimerNombreDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneePrimerNombreDestinatario
	 * @param paramGeneracioneePrimerNombreDestinatario con el valor del atributo paramGeneracioneePrimerNombreDestinatario a establecer
	 */
	public void setParamGeneracioneePrimerNombreDestinatario(String paramGeneracioneePrimerNombreDestinatario) {
		this.paramGeneracioneePrimerNombreDestinatario = paramGeneracioneePrimerNombreDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeSegundoNombreDestinatario
	 * @return El valor del atributo paramGeneracioneeSegundoNombreDestinatario
	 */
	public String getParamGeneracioneeSegundoNombreDestinatario() {
		return paramGeneracioneeSegundoNombreDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeSegundoNombreDestinatario
	 * @param paramGeneracioneeSegundoNombreDestinatario con el valor del atributo paramGeneracioneeSegundoNombreDestinatario a establecer
	 */
	public void setParamGeneracioneeSegundoNombreDestinatario(String paramGeneracioneeSegundoNombreDestinatario) {
		this.paramGeneracioneeSegundoNombreDestinatario = paramGeneracioneeSegundoNombreDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneePrimerApellidoDestinatario
	 * @return El valor del atributo paramGeneracioneePrimerApellidoDestinatario
	 */
	public String getParamGeneracioneePrimerApellidoDestinatario() {
		return paramGeneracioneePrimerApellidoDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneePrimerApellidoDestinatario
	 * @param paramGeneracioneePrimerApellidoDestinatario con el valor del atributo paramGeneracioneePrimerApellidoDestinatario a establecer
	 */
	public void setParamGeneracioneePrimerApellidoDestinatario(String paramGeneracioneePrimerApellidoDestinatario) {
		this.paramGeneracioneePrimerApellidoDestinatario = paramGeneracioneePrimerApellidoDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeSegundoApellidoDestinatario
	 * @return El valor del atributo paramGeneracioneeSegundoApellidoDestinatario
	 */
	public String getParamGeneracioneeSegundoApellidoDestinatario() {
		return paramGeneracioneeSegundoApellidoDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeSegundoApellidoDestinatario
	 * @param paramGeneracioneeSegundoApellidoDestinatario con el valor del atributo paramGeneracioneeSegundoApellidoDestinatario a establecer
	 */
	public void setParamGeneracioneeSegundoApellidoDestinatario(String paramGeneracioneeSegundoApellidoDestinatario) {
		this.paramGeneracioneeSegundoApellidoDestinatario = paramGeneracioneeSegundoApellidoDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeLlave
	 * @return El valor del atributo paramGeneracioneeLlave
	 */
	public String getParamGeneracioneeLlave() {
		return paramGeneracioneeLlave;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeLlave
	 * @param paramGeneracioneeLlave con el valor del atributo paramGeneracioneeLlave a establecer
	 */
	public void setParamGeneracioneeLlave(String paramGeneracioneeLlave) {
		this.paramGeneracioneeLlave = paramGeneracioneeLlave;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeValor
	 * @return El valor del atributo paramGeneracioneeValor
	 */
	public String getParamGeneracioneeValor() {
		return paramGeneracioneeValor;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeValor
	 * @param paramGeneracioneeValor con el valor del atributo paramGeneracioneeValor a establecer
	 */
	public void setParamGeneracioneeValor(String paramGeneracioneeValor) {
		this.paramGeneracioneeValor = paramGeneracioneeValor;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeCiudadDestinatario
	 * @return El valor del atributo paramGeneracioneeCiudadDestinatario
	 */
	public String getParamGeneracioneeCiudadDestinatario() {
		return paramGeneracioneeCiudadDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeCiudadDestinatario
	 * @param paramGeneracioneeCiudadDestinatario con el valor del atributo paramGeneracioneeCiudadDestinatario a establecer
	 */
	public void setParamGeneracioneeCiudadDestinatario(String paramGeneracioneeCiudadDestinatario) {
		this.paramGeneracioneeCiudadDestinatario = paramGeneracioneeCiudadDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeDireccionDestinatario
	 * @return El valor del atributo paramGeneracioneeDireccionDestinatario
	 */
	public String getParamGeneracioneeDireccionDestinatario() {
		return paramGeneracioneeDireccionDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeDireccionDestinatario
	 * @param paramGeneracioneeDireccionDestinatario con el valor del atributo paramGeneracioneeDireccionDestinatario a establecer
	 */
	public void setParamGeneracioneeDireccionDestinatario(String paramGeneracioneeDireccionDestinatario) {
		this.paramGeneracioneeDireccionDestinatario = paramGeneracioneeDireccionDestinatario;
	}
	/**
	 * Obtiene el valor del atributo paramGeneracioneeTelefonoDestinatario
	 * @return El valor del atributo paramGeneracioneeTelefonoDestinatario
	 */
	public String getParamGeneracioneeTelefonoDestinatario() {
		return paramGeneracioneeTelefonoDestinatario;
	}
	/**
	 * Establece el valor del atributo paramGeneracioneeTelefonoDestinatario
	 * @param paramGeneracioneeTelefonoDestinatario con el valor del atributo paramGeneracioneeTelefonoDestinatario a establecer
	 */
	public void setParamGeneracioneeTelefonoDestinatario(String paramGeneracioneeTelefonoDestinatario) {
		this.paramGeneracioneeTelefonoDestinatario = paramGeneracioneeTelefonoDestinatario;
	}
	

}

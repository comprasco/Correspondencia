package co.gov.supernotariado.bachue.correspondencia.ejb.integraciones;

import java.io.Serializable;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.to.OficinaTO;
import https.www_supernotariado_gov_co.schemas.bachue.cb.consultacatalogos.consultarcatalogos.v1.TipoEntradaConsultarCatalogos;
import https.www_supernotariado_gov_co.schemas.bachue.cb.consultacatalogos.consultarcatalogos.v1.TipoSalidaConsultarCatalogos;
import https.www_supernotariado_gov_co.schemas.bachue.cb.consultacatalogos.consultarcatalogos.v1.TipoSalidaConsultarCatalogos.Catalogos;
import https.www_supernotariado_gov_co.schemas.bachue.cb.consultacatalogos.consultarcatalogos.v1.TiposCatalogos;
import https.www_supernotariado_gov_co.services.bachue.cb.consultacatalogos.v1.SDICBConsultaCatalogos;
import https.www_supernotariado_gov_co.services.bachue.cb.consultacatalogos.v1.SDICBConsultaCatalogosService;

/**
 * Maneja la integración de catalogos de bachue.  Patrón Singleton para no llamar siempre al servicio de catalogos sino solo la primera vez.
 */
public class IntegracionCatalogos implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * URL para envio de documentos
	 */
	private String catalogoOWCCEnvioDocumentoURL;
	/**
	 * URL para busqueda de documentos
	 */
	private String catalogoOWCCBusquedaDocumentoURL;
	/**
	 * URL para digitalización en capture
	 */
	private String catalogoDigitalizacionCaptureURL;
	/**
	 * URL para gestión de usuariose
	 */
	private String catalogoGestionUsuariosURL;
	/**
	 * URL para verificación bioclient
	 */
	private String catalogoVerificacionDispositivos;
	/**
	 * URL para mandar a imprimir un documento directamente ej bachue://pdf@ID@DOCNAME@USUARIO@REFERENCIA@COPIAS/
	 */
	private String catalogoBioclientImprimir;
	/**
	 * URL para mandar a verificar segundo factor autenticación ej bachue://verify@SESION@IDUSUARIO
	 */
	private String catalogoBioclientVerificar;
	/**
	 * URL para notificar correspondencia envios
	 */
	private String catalogoNotificadorCorrespondencia;

	/**
	 * Atributos para la consulta del catálogo
	 */
	private transient ParametrosSistemaTO parametrosTO;
	/**
	 * 
	 * Constructor
	 */
	public IntegracionCatalogos(ParametrosSistemaTO parametrosTO) {
		this.parametrosTO = parametrosTO;
		consultarCatalogos();
	}

	/**
	 * Consulta las urls de los servicios en el catalogo
	 * 
	 * @return Respuesta del servicio de catalogos
	 */
	public TipoSalidaConsultarCatalogos consultarCatalogos() {
		TipoSalidaConsultarCatalogos response = null;
		Logger logger = Logger.getLogger(IntegracionCatalogos.class);
		try {
			SDICBConsultaCatalogosService service = new SDICBConsultaCatalogosService();
			SDICBConsultaCatalogos port = service.getSDICBConsultaCatalogos();

			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, parametrosTO.getEndpoint());
			
			logger.info("IntegracionCatalogos.consultarCatalogos:" + parametrosTO.getModulo() + " " + parametrosTO.getCatalogo());

			TipoEntradaConsultarCatalogos entrada = new TipoEntradaConsultarCatalogos();
			entrada.setModulo(parametrosTO.getModulo());
			entrada.setCatalogo(parametrosTO.getCatalogo());

			response = port.consultarCatalogos(entrada);

			if (response != null) {
				logger.info("IntegracionCatalogos.consultarCatalogos-response: ok " + response.getDescripcionMensaje());
				Catalogos responseCatalogo = response.getCatalogos();

				if(responseCatalogo!=null) {
					for (TiposCatalogos tipoCatalogo : responseCatalogo.getCatalogo()) {
						if (tipoCatalogo.getCodigo() != null && tipoCatalogo.getNombre() != null) {
							if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamEnvioDocumentos())) {
								catalogoOWCCEnvioDocumentoURL = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamConsultaDocumentos())) {
								catalogoOWCCBusquedaDocumentoURL = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamDigitalizacion())) {
								catalogoDigitalizacionCaptureURL = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamNotificador())) {
								catalogoNotificadorCorrespondencia = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamBioclient())) {
								catalogoBioclientImprimir = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamBioclientVerificar())) {
								catalogoBioclientVerificar = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamGestionUsuarios())) {
								catalogoGestionUsuariosURL = tipoCatalogo.getNombre().getValue();
							} else if (tipoCatalogo.getCodigo().equals(parametrosTO.getParamBioclientSegunda())) {
								catalogoVerificacionDispositivos = tipoCatalogo.getNombre().getValue();
							}
						}
					}
				}
			} else {
				logger.error("IntegracionCatalogos.consultarCatalogos-response es null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return response;
	}

	/**
	 * Consulta la orip de un usuario
	 * @param loginUsuario Nombre de usuario a verificar
	 * @return Respuesta del servicio de catalogos
	 */
	public OficinaTO obtenerOripPorUsuario(String loginUsuario) {
		OficinaTO oficina = null;
		TipoSalidaConsultarCatalogos response = null;
		Logger logger = Logger.getLogger(IntegracionCatalogos.class);
		try {
			SDICBConsultaCatalogosService service = new SDICBConsultaCatalogosService();
			SDICBConsultaCatalogos port = service.getSDICBConsultaCatalogos();

			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, parametrosTO.getEndpoint());
			
			logger.info("IntegracionCatalogos.obtenerOripPorUsuario:" + parametrosTO.getModulo() + " - " + parametrosTO.getCatalogoOrip()+" - usuario:"+loginUsuario);

			TipoEntradaConsultarCatalogos entrada = new TipoEntradaConsultarCatalogos();
			entrada.setModulo(parametrosTO.getModulo());
			entrada.setCatalogo(parametrosTO.getCatalogoOrip());
			entrada.setParametro(new JAXBElement<>(new QName(String.class.getSimpleName()), String.class, loginUsuario));

			response = port.consultarCatalogos(entrada);

			if (response != null) {
				logger.info("IntegracionCatalogos.obtenerOripPorUsuario-response: ok " + response.getDescripcionMensaje());
				Catalogos responseCatalogo = response.getCatalogos();
				if(responseCatalogo!=null) {
					oficina = new OficinaTO();
					for (TiposCatalogos tipoCatalogo : responseCatalogo.getCatalogo()) {
						oficina.setCirculo(tipoCatalogo.getCodigo());
						oficina.setNombre(tipoCatalogo.getNombre().getValue());
					}
				}
			} else {
				logger.error("IntegracionCatalogos.obtenerOripPorUsuario-response es null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return oficina;
	}

	
	/**
	 * Obtiene el valor del atributo catalogoOWCCEnvioDocumentoURL
	 * 
	 * @return El valor del atributo catalogoOWCCEnvioDocumentoURL
	 */
	public String getCatalogoOWCCEnvioDocumentoURL() {
		return catalogoOWCCEnvioDocumentoURL;
	}

	/**
	 * Establece el valor del atributo catalogoOWCCEnvioDocumentoURL
	 * 
	 * @param catalogoOWCCEnvioDocumentoURL con el valor del atributo
	 *                                      catalogoOWCCEnvioDocumentoURL a
	 *                                      establecer
	 */
	public void setCatalogoOWCCEnvioDocumentoURL(String catalogoOWCCEnvioDocumentoURL) {
		this.catalogoOWCCEnvioDocumentoURL = catalogoOWCCEnvioDocumentoURL;
	}

	/**
	 * Obtiene el valor del atributo catalogoOWCCBusquedaDocumentoURL
	 * 
	 * @return El valor del atributo catalogoOWCCBusquedaDocumentoURL
	 */
	public String getCatalogoOWCCBusquedaDocumentoURL() {
		return catalogoOWCCBusquedaDocumentoURL;
	}

	/**
	 * Establece el valor del atributo catalogoOWCCBusquedaDocumentoURL
	 * 
	 * @param catalogoOWCCBusquedaDocumentoURL con el valor del atributo
	 *                                         catalogoOWCCBusquedaDocumentoURL a
	 *                                         establecer
	 */
	public void setCatalogoOWCCBusquedaDocumentoURL(String catalogoOWCCBusquedaDocumentoURL) {
		this.catalogoOWCCBusquedaDocumentoURL = catalogoOWCCBusquedaDocumentoURL;
	}

	/**
	 * Obtiene el valor del atributo catalogoDigitalizacionCaptureURL
	 * 
	 * @return El valor del atributo catalogoDigitalizacionCaptureURL
	 */
	public String getCatalogoDigitalizacionCaptureURL() {
		return catalogoDigitalizacionCaptureURL;
	}

	/**
	 * Establece el valor del atributo catalogoDigitalizacionCaptureURL
	 * 
	 * @param catalogoDigitalizacionCaptureURL con el valor del atributo
	 *                                         catalogoDigitalizacionCaptureURL a
	 *                                         establecer
	 */
	public void setCatalogoDigitalizacionCaptureURL(String catalogoDigitalizacionCaptureURL) {
		this.catalogoDigitalizacionCaptureURL = catalogoDigitalizacionCaptureURL;
	}

	/**
	 * Obtiene el valor del atributo catalogoBioclientImprimir
	 * 
	 * @return El valor del atributo catalogoBioclientImprimir
	 */
	public String getCatalogoBioclientImprimir() {
		return catalogoBioclientImprimir;
	}

	/**
	 * Establece el valor del atributo catalogoBioclientImprimir
	 * 
	 * @param catalogoBioclientImprimir con el valor del atributo catalogoBioclientImprimir a
	 *                             establecer
	 */
	public void setCatalogoBioclientImprimir(String catalogoBioclientImprimir) {
		this.catalogoBioclientImprimir = catalogoBioclientImprimir;
	}

	/**
	 * Obtiene el valor del atributo catalogoGestionUsuariosURL
	 * @return El valor del atributo catalogoGestionUsuariosURL
	 */
	public String getCatalogoGestionUsuariosURL() {
		return catalogoGestionUsuariosURL;
	}

	/**
	 * Establece el valor del atributo catalogoGestionUsuariosURL
	 * @param catalogoGestionUsuariosURL con el valor del atributo catalogoGestionUsuariosURL a establecer
	 */
	public void setCatalogoGestionUsuariosURL(String catalogoGestionUsuariosURL) {
		this.catalogoGestionUsuariosURL = catalogoGestionUsuariosURL;
	}

	/**
	 * Obtiene el valor del atributo catalogoNotificadorCorrespondencia
	 * @return El valor del atributo catalogoNotificadorCorrespondencia
	 */
	public String getCatalogoNotificadorCorrespondencia() {
		return catalogoNotificadorCorrespondencia;
	}

	/**
	 * Establece el valor del atributo catalogoNotificadorCorrespondencia
	 * @param catalogoNotificadorCorrespondencia con el valor del atributo catalogoNotificadorCorrespondencia a establecer
	 */
	public void setCatalogoNotificadorCorrespondencia(String catalogoNotificadorCorrespondencia) {
		this.catalogoNotificadorCorrespondencia = catalogoNotificadorCorrespondencia;
	}

	/**
	 * Obtiene el valor del atributo catalogoBioclientVerificar
	 * @return El valor del atributo catalogoBioclientVerificar
	 */
	public String getCatalogoBioclientVerificar() {
		return catalogoBioclientVerificar;
	}

	/**
	 * Establece el valor del atributo catalogoBioclientVerificar
	 * @param catalogoBioclientVerificar con el valor del atributo catalogoBioclientVerificar a establecer
	 */
	public void setCatalogoBioclientVerificar(String catalogoBioclientVerificar) {
		this.catalogoBioclientVerificar = catalogoBioclientVerificar;
	}

	/**
	 * Obtiene el valor del atributo catalogoVerificacionDispositivos
	 * @return El valor del atributo catalogoVerificacionDispositivos
	 */
	public String getCatalogoVerificacionDispositivos() {
		return catalogoVerificacionDispositivos;
	}

	/**
	 * Establece el valor del atributo catalogoVerificacionDispositivos
	 * @param catalogoVerificacionDispositivos con el valor del atributo catalogoVerificacionDispositivos a establecer
	 */
	public void setCatalogoVerificacionDispositivos(String catalogoVerificacionDispositivos) {
		this.catalogoVerificacionDispositivos = catalogoVerificacionDispositivos;
	}

	/**
	 * Obtiene el valor del atributo parametrosTO
	 * @return El valor del atributo parametrosTO
	 */
	public ParametrosSistemaTO getParametrosTO() {
		return parametrosTO;
	}

	/**
	 * Establece el valor del atributo parametrosTO
	 * @param parametrosTO con el valor del atributo parametrosTO a establecer
	 */
	public void setParametrosTO(ParametrosSistemaTO catalogoTO) {
		this.parametrosTO = catalogoTO;
	}

}

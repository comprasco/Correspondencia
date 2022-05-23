package co.gov.supernotariado.bachue.correspondencia.ejb.integraciones;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import https.www_supernotariado_gov_co.schemas.bachue.cn.notificadorcorrespondencia.notificarcorrespondencia.v1.TipoEntradaNotificarCorrespondencia;
import https.www_supernotariado_gov_co.schemas.bachue.cn.notificadorcorrespondencia.notificarcorrespondencia.v1.TipoSalidaNotificarCorrespondencia;
import https.www_supernotariado_gov_co.services.bachue.cn.notificadorcorrespondencia.v1.SUTCNNotificadorCorrespondencia;
import https.www_supernotariado_gov_co.services.bachue.cn.notificadorcorrespondencia.v1.SUTCNNotificadorCorrespondenciaQSService;

/**
 * Maneja la integración con el servicio web de notificación correspondencia
 */
public class IntegracionNotificadorCorrespondencia {

	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(IntegracionNotificadorCorrespondencia.class);

	/**
	 * Integración para obtener URLs de servicios web
	 */
	private IntegracionCatalogos integracionCatalogos;
	
	/**
	 * Constructor
	 */
	public IntegracionNotificadorCorrespondencia(IntegracionCatalogos integracionCatalogos) {
		this.integracionCatalogos = integracionCatalogos;
	}
	
	
	/**
	 * Envía un acuse de recibido para la correspondencia
	 * @param identificador Identificador radicado
	 * @param guia Número de guía
	 * @param fechaEnvio Fecha de envío de la correspondencia
	 * @return Respuesta del servicio
	 */
	public TipoSalidaNotificarCorrespondencia notificarCorrespondencia(String identificador, String guia, String fechaEnvio) {
		TipoSalidaNotificarCorrespondencia response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoNotificadorCorrespondencia();
			
			SUTCNNotificadorCorrespondenciaQSService service = new SUTCNNotificadorCorrespondenciaQSService();
			SUTCNNotificadorCorrespondencia port = service.getSUTCNNotificadorCorrespondenciaQSPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionNotificadorCorrespondencia.notificarCorrespondencia-identificador:"+identificador+" - guia:"+guia+" - fechaEnvio:"+fechaEnvio);

            GregorianCalendar c = new GregorianCalendar();
            c.setTime(Calendar.getInstance().getTime());
            XMLGregorianCalendar fechaAcuse = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            
            GregorianCalendar c2 = new GregorianCalendar();
            c2.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaEnvio));
            XMLGregorianCalendar fechaEnvioCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c2);
            
            TipoEntradaNotificarCorrespondencia entrada = new TipoEntradaNotificarCorrespondencia();
            entrada.setFechaAcuse(fechaAcuse);
            entrada.setFechaEnvio(fechaEnvioCalendar);
            
    		JAXBElement<String> elementGuia = new JAXBElement<>(new QName(String.class.getSimpleName()), String.class, guia);
            entrada.setGuia(elementGuia);

            entrada.setIdentificador(identificador);

			response = port.notificarCorrespondencia(entrada);

			if(response!=null) {
				logger.info("IntegracionNotificadorCorrespondencia.notificarCorrespondencia-response: ok "+response.getCodigoMensajeAndDescripcionMensaje());
			} else {
				logger.error("IntegracionNotificadorCorrespondencia.notificarCorrespondencia-response es null");
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return response;
	}

}	


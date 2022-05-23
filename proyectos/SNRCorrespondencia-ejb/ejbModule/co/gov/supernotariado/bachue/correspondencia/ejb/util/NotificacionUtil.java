package co.gov.supernotariado.bachue.correspondencia.ejb.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.AdjuntoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.MensajeCorreoTO;

/**
 * Clase para trabajar con mensajes y notificaciones de correo
 */
public class NotificacionUtil {
	
	/**
	 * Integración para obtener URLs de servicios web
	 */
	private IntegracionCatalogos integracionCatalogos;

	/**
	 * Constructor privado ya que los métodos son estáticos
	 */
	public NotificacionUtil(IntegracionCatalogos integracionCatalogos) {
		this.integracionCatalogos = integracionCatalogos;
	}

	/**
	 * Formato estándar para formatear las fechas
	 */
	public static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm";
	

	/**
	 * Envia un mensaje SMS - No implementado
	 * @param content Contenido del mensaje
	 * @param telefonos Lista de telefonos a enviar el mensaje
	 */
	public void enviarSMS(String content, List<String> telefonos){
		Logger logger = Logger.getLogger(NotificacionUtil.class);
		try{
			if(content.length()>160) {
				content = content.substring(0, 159);
			}
			logger.error("SMS No configurado: "+content+" "+telefonos.size());
		} catch(Exception e){
			logger.error("Error enviando SMS", e);
		}
	}

	
	/**
	 * Envia un correo
	 * @param asunto Asunto del correo
	 * @param contenido Contenido del correo
	 * @param direcciones Lista de direcciones de correo
	 * @param adjuntos Lista de adjuntos a enviar
	 */
	public void enviarCorreo(String asunto, String contenido, List<String> direcciones, List<AdjuntoTO> adjuntos){
		Logger logger = Logger.getLogger(NotificacionUtil.class);
		try{
			String usuario = integracionCatalogos.getParametrosTO().getParamCorreosUsuario();
			String clave = integracionCatalogos.getParametrosTO().getParamCorreosClave();
			
			String estructura = integracionCatalogos.getParametrosTO().getParamCorreosEstructura();
			estructura = estructura.replace("#CORREO_TEXTO", contenido);

			MensajeCorreoTO message = new MensajeCorreoTO(usuario, asunto, estructura, false);
			message.setDestinatario(direcciones);
			message.setAdjuntos(adjuntos);
			GestorCorreo gestorCorreo = new GestorCorreo(usuario, clave);
			gestorCorreo.enviarCorreo(message);
		} catch(Exception e){
			logger.error("Error enviando correo", e);
		}
	}
	
	
	/**
	 * Envia un SMS de recordatorio de paso al usuario responsable del paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarSMSRecordatorioPaso(PasoEjecutado pasoEjecutado){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[2];
			params[0] = pasoEjecutado.getPaso().getNombre();
			if(pasoEjecutado.getFechaFin()!=null) {
				params[1] = sdf.format(pasoEjecutado.getFechaFin());
			} else {
				params[1] = "--";
			}
			
			String texto = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosRecordatorioPasoTexto(), params);
			List<String> telefonos = new ArrayList<>();
			if(pasoEjecutado.getUsuarioAsignado().getCelular()!=null && !pasoEjecutado.getUsuarioAsignado().getCelular().isEmpty()) {
				telefonos.add(pasoEjecutado.getUsuarioAsignado().getCelular());
			}
			enviarSMS(texto, telefonos);
		}
	}

	
	/**
	 * Envia un correo de recordatorio de paso al usuario responsable del paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarCorreoRecordatorioPaso(PasoEjecutado pasoEjecutado){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[2];
			params[0] = pasoEjecutado.getPaso().getNombre();
			if(pasoEjecutado.getFechaFin()!=null) {
				params[1] = sdf.format(pasoEjecutado.getFechaFin());
			} else {
				params[1] = "--";
			}
			
			String html = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosRecordatorioPasoTexto(), params);
			List<String> correos = new ArrayList<>();
			correos.add(pasoEjecutado.getUsuarioAsignado().getCorreoElectronico());

			enviarCorreo(MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosRecordatorioPasoAsunto(), params[0]), html, correos, null);
		}
	}
	

	/**
	 * Envia un correo de finalización de proceso al usuario que inició el proceso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 * @param primerPaso Primer Paso ejecutado del proceso para enviarle información al usuario
	 */
	public void enviarCorreoFinalProceso(PasoEjecutado pasoEjecutado, PasoEjecutado primerPaso){
		if (pasoEjecutado != null && primerPaso.getUsuarioEjecutado() != null) {

			Object[] params = new Object[1];
			params[0] = pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso()+"-"+pasoEjecutado.getProcesoEjecutado().getProceso().getNombre();

			String html = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosProcesoFinalizadoTexto(), params);
			List<String> correos = new ArrayList<>();
			correos.add(primerPaso.getUsuarioAsignado().getCorreoElectronico());

			enviarCorreo(MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosProcesoFinalizadoAsunto(), params[0]), html, correos, null);
		}
	}

	
	/**
	 * Envia un SMS de finalización de proceso al usuario que inició el proceso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 * @param primerPaso Primer Paso ejecutado del proceso para enviarle información al usuario
	 */
	public void enviarSMSFinalProceso(PasoEjecutado pasoEjecutado, PasoEjecutado primerPaso){
		if (pasoEjecutado != null && primerPaso.getUsuarioEjecutado() != null) {

			Object[] params = new Object[1];
			params[0] = pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso()+"-"+pasoEjecutado.getProcesoEjecutado().getProceso().getNombre();

			String texto = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosProcesoFinalizadoTexto(), params);

			List<String> telefonos = new ArrayList<>();
			if(primerPaso.getUsuarioAsignado().getCelular()!=null && !primerPaso.getUsuarioAsignado().getCelular().isEmpty()) {
				telefonos.add(primerPaso.getUsuarioAsignado().getCelular());
			}
			enviarSMS(texto, telefonos);
		}
	}

	
	/**
	 * Envia un SMS de vencimiento de paso al usuario responsable de el paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarSMSPasoAtrasado(PasoEjecutado pasoEjecutado){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[2];
			params[0] = pasoEjecutado.getPaso().getNombre();
			if(pasoEjecutado.getFechaFin()!=null) {
				params[1] = sdf.format(pasoEjecutado.getFechaFin());
			} else {
				params[1] = "--";
			}
			
			String texto = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoAtrasadoTexto(), params);

			List<String> telefonos = new ArrayList<>();
			if(pasoEjecutado.getUsuarioAsignado().getCelular()!=null && !pasoEjecutado.getUsuarioAsignado().getCelular().isEmpty()) {
				telefonos.add(pasoEjecutado.getUsuarioAsignado().getCelular());
			}
			enviarSMS(texto, telefonos);
		}
	}


	/**
	 * Envia un correo de vencimiento de paso al usuario responsable del paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarCorreoPasoAtrasado(PasoEjecutado pasoEjecutado){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[2];
			params[0] = pasoEjecutado.getPaso().getNombre();
			if(pasoEjecutado.getFechaFin()!=null) {
				params[1] = sdf.format(pasoEjecutado.getFechaFin());
			} else {
				params[1] = "--";
			}

			String html = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoAtrasadoTexto(), params);
			List<String> correos = new ArrayList<>();
			correos.add(pasoEjecutado.getUsuarioAsignado().getCorreoElectronico());

			enviarCorreo(MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoAtrasadoAsunto(), params[0]), html, correos, null);
		}
	}

	
	/**
	 * Envia un SMS al usuario de el paso actual con notificación de ejecución de paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarSMSPasoActual(PasoEjecutado pasoEjecutado){
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioEjecutado() != null) {
			Object[] params = new Object[2];
			params[0] = pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso();
			params[1] = pasoEjecutado.getPaso().getNombre();

			String texto = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoActualTexto(), params);

			List<String> telefonos = new ArrayList<>();
			if(pasoEjecutado.getUsuarioAsignado().getCelular()!=null && !pasoEjecutado.getUsuarioAsignado().getCelular().isEmpty()) {
				telefonos.add(pasoEjecutado.getUsuarioAsignado().getCelular());
			}
			enviarSMS(texto, telefonos);
		}
	}

	
	/**
	 * Envia un correo al usuario de el paso actual con notificación de ejecución de paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarCorreoPasoActual(PasoEjecutado pasoEjecutado){
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[6];
			params[0] = pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso();
			params[1] = pasoEjecutado.getPaso().getNombre();

			String html = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoActualTexto(), params);

			List<String> correos = new ArrayList<>();
			correos.add(pasoEjecutado.getUsuarioAsignado().getCorreoElectronico());

			enviarCorreo(MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoActualAsunto(), params[1]), html, correos, null);
		}
	}

	
	/**
	 * Envia un SMS al usuario de la siguiente paso para alertarlo
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarSMSProximoPaso(PasoEjecutado pasoEjecutado){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[3];
			params[0] = pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso();
			params[1] = pasoEjecutado.getPaso().getNombre();
			if(pasoEjecutado.getFechaFin()!=null) {
				params[2] = sdf.format(pasoEjecutado.getFechaFin());
			} else {
				params[2] = "--";
			}

			String texto = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoProximoTexto(), params);

			List<String> telefonos = new ArrayList<>();
			if(pasoEjecutado.getUsuarioAsignado().getCelular()!=null && !pasoEjecutado.getUsuarioAsignado().getCelular().isEmpty()) {
				telefonos.add(pasoEjecutado.getUsuarioAsignado().getCelular());
			}
			enviarSMS(texto, telefonos);
		}
	}

	/**
	 * Envia un correo al usuario de la siguiente paso para alertarlo
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarCorreoProximoPaso(PasoEjecutado pasoEjecutado){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA);
		if (pasoEjecutado != null && pasoEjecutado.getUsuarioAsignado() != null) {
			Object[] params = new Object[3];
			params[0] = pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso();
			params[1] = pasoEjecutado.getPaso().getNombre();
			if(pasoEjecutado.getFechaFin()!=null) {
				params[2] = sdf.format(pasoEjecutado.getFechaFin());
			} else {
				params[2] = "--";
			}

			String html = MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoProximoTexto(), params);

			List<String> correos = new ArrayList<>();
			correos.add(pasoEjecutado.getUsuarioAsignado().getCorreoElectronico());

			enviarCorreo(MessageFormat.format(integracionCatalogos.getParametrosTO().getParamCorreosPasoProximoTexto(), params[1]), html, correos, null);
		}
	}

	
	/**
	 * Envia un correo a los usuarios de un grupo con la asignacion de un paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarSMSProximoPasoGrupo(PasoEjecutado pasoEjecutado){
		enviarSMSProximoPaso(pasoEjecutado);
	}

	
	/**
	 * Envia un correo a los usuarios de un grupo con la asignacion de un paso
	 * @param pasoEjecutado Paso Ejecutado con la información del Paso
	 */
	public void enviarCorreoProximoPasoGrupo(PasoEjecutado pasoEjecutado){
		enviarCorreoProximoPaso(pasoEjecutado);
	}


	/**
	 * Obtiene el valor del atributo integracionCatalogos
	 * @return El valor del atributo integracionCatalogos
	 */
	public IntegracionCatalogos getIntegracionCatalogos() {
		return integracionCatalogos;
	}


	/**
	 * Establece el valor del atributo integracionCatalogos
	 * @param integracionCatalogos con el valor del atributo integracionCatalogos a establecer
	 */
	public void setIntegracionCatalogos(IntegracionCatalogos integracionCatalogos) {
		this.integracionCatalogos = integracionCatalogos;
	}

}

package co.gov.supernotariado.bachue.correspondencia.ws.notificardigitalizacion;

import java.util.List;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.ConstantesCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionUsuarios;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ProcesosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import https.www_supernotariado_gov_co.schemas.bachue.cr.controldigitalizacion.notificardigitalizacion.v1.TipoEntradaNotificarDigitalizacion;
import https.www_supernotariado_gov_co.schemas.bachue.cr.controldigitalizacion.notificardigitalizacion.v1.TipoSalidaNotificarDigitalizacion;

/**
 * Servicio web que permite avanzar la etapa de un proceso de Digitalización a Aprobación
 */
public class ControlDigitalizacion {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(ControlDigitalizacion.class);

	/**
	 * Código de mensaje para éxito
	 */
	private static final String CODIGO_EXITO = "200";

	/**
	 * Código de mensaje para error
	 */
	private static final String CODIGO_ERROR = "409";

	/** Manejador de lógica de negocio de procesos */
	@EJB(name = "ProcesoBusiness")
	private ProcesosStatelessLocal procesoControl;
	
	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;

    /**
     * Método principal del servicio, permite avanzar la etapa de un proceso de Digitalización a Aprobación
     * @param entradaNotificarDigitalizacion Parámetros de entrada del servicio web
     * @param wsContext Contexto de ejecución del servicio web para obtener la IP del cliente 
     * @return Respuesta del servicio web con el un código de salida y un mensaje
     */
    public TipoSalidaNotificarDigitalizacion notificarDigitalizacion(TipoEntradaNotificarDigitalizacion entradaNotificarDigitalizacion, WebServiceContext wsContext) {
		UsuarioTO usuario = new UsuarioTO();
		usuario.setId("1");
		usuario.setNombre("admin");
		
    	MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        
        String direccionIp = req.getRemoteAddr();

        TipoSalidaNotificarDigitalizacion response = new TipoSalidaNotificarDigitalizacion();

		boolean ok = validaRequest(entradaNotificarDigitalizacion, response);
		if(ok) {
			try {
				Proceso proceso = procesoControl.obtenerProcesoActivoPorNombreClave(ConstantesCorrespondencia.NOMBRE_PROCESO_CORRESPONDENCIA_RECIBIDA);
				if(proceso!=null) {
					ProcesoEjecutado procesoEjecutado = procesoControl.obtenerProcesoEjecutadoPorSecuenciaIdentificador(entradaNotificarDigitalizacion.getIdentificadorTramite(), proceso.getIdentificador());
					if(procesoEjecutado==null) {
						agregarCodigoMensaje(response, CODIGO_ERROR, "Proceso con radicado "+entradaNotificarDigitalizacion.getIdentificadorTramite()+" no encontrado");
					} else {
						logger.debug("Nombre proceso encontrado: "+procesoEjecutado.getProceso().getNombre());
						if(procesoEjecutado.isActivo()) {
							procesoControl.inicializarJsonPasoData(procesoEjecutado.getPasoActual());
							
							if(procesoEjecutado.getPasoActual().getJsonPasoData().isPermitirWS()) {
								List<PasoEjecutado> pasos = procesoControl.obtenerPasosEjecutadosPorProceso(procesoEjecutado.getId());
								if(!pasos.isEmpty()) {
									PasoEjecutado data = pasos.get(0);
									
									IntegracionCatalogos integracionCatalogos = parametrosControl.obtenerIntegracionCatalogos();
									IntegracionUsuarios integracionUsuarios = new IntegracionUsuarios(integracionCatalogos);
									PasoEjecutado nuevoPaso = procesoControl.ejecutarPasoProceso(integracionUsuarios, data, procesoEjecutado.getProceso().getId(), null, usuario, direccionIp, 1, procesoEjecutado.getIdOripEjecucion());
	
									if(nuevoPaso!=null) {
										agregarCodigoMensaje(response, CODIGO_EXITO, "Ejecución correcta. Proceso avanzó a la etapa: "+nuevoPaso.getPaso().getNombre());
									} else {
										agregarCodigoMensaje(response, CODIGO_EXITO, "Ejecución correcta. Proceso finalizó todas sus etapas o tiene otras etapas pendientes en paralelo");
									}
								} else {
									agregarCodigoMensaje(response, CODIGO_ERROR, "No se encontraron etapas activas para el proceso");
								}
							} else {
								agregarCodigoMensaje(response, CODIGO_ERROR, "El Paso actual ("+procesoEjecutado.getPasoActual().getNombre()+") no permite ejecución por WS");
							}
						} else {
							agregarCodigoMensaje(response, CODIGO_ERROR, "Proceso no se encuentra activo");
						}
					}
				} else {
					agregarCodigoMensaje(response, CODIGO_ERROR, "Proceso "+ConstantesCorrespondencia.NOMBRE_PROCESO_CORRESPONDENCIA_RECIBIDA+" no encontrado");
				}
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
				agregarCodigoMensaje(response, CODIGO_ERROR, "Error desconocido. "+e.getMessage());
			}
		}
		return response;
	}
    
    /**
     * Agrega mensaje de response
     * @param response Tipo de Salida Response
     * @param codigo Código de salida
     * @param mensaje Mensaje de salida
     */
    private void agregarCodigoMensaje(TipoSalidaNotificarDigitalizacion response, String codigo, String mensaje) {
    	try {
	    	response.setCodigoMensaje(codigo);
	    	response.setDescripcionMensaje(mensaje);
    	}catch(Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    }

	
	/**
	 * Valida los datos del request
	 * @param request Request del Servicio
	 * @param response Tipo de Salida Response del servicio
	 * @return true si la validación fue correcta
	 */
	private boolean validaRequest(TipoEntradaNotificarDigitalizacion request, TipoSalidaNotificarDigitalizacion response) {
		boolean ok = true;
		if(request==null) {
			agregarCodigoMensaje(response, CODIGO_ERROR, "Debe enviar parámetros por GET");
			ok = false;
		} else {
			if(request.getIdentificadorTramite()==null || request.getIdentificadorTramite().isEmpty() || request.getIdentificadorTramite().trim().equals("?")) {
				agregarCodigoMensaje(response, CODIGO_ERROR, "Parámetro IdentificadorTramite no encontrado");
				ok = false;
			} else if(request.getEstado()==null || request.getEstado().isEmpty() || request.getEstado().trim().equals("?")) {
				agregarCodigoMensaje(response, CODIGO_ERROR, "Parámetro Estado no encontrado");
				ok = false;
			} else if(request.getSistemaOrigen()==null || request.getSistemaOrigen().isEmpty() || request.getSistemaOrigen().trim().equals("?")) {
				agregarCodigoMensaje(response, CODIGO_ERROR, "Parámetro SistemaOrigen no encontrado");
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Obtiene el valor del atributo procesoControl
	 * @return El valor del atributo procesoControl
	 */
	public ProcesosStatelessLocal getProcesoControl() {
		return procesoControl;
	}

	/**
	 * Establece el valor del atributo procesoControl
	 * @param procesoControl con el valor del atributo procesoControl a establecer
	 */
	public void setProcesoControl(ProcesosStatelessLocal procesoControl) {
		this.procesoControl = procesoControl;
	}

	/**
	 * Obtiene el valor del atributo parametrosControl
	 * @return El valor del atributo parametrosControl
	 */
	public ParametrosStatelessLocal getParametrosControl() {
		return parametrosControl;
	}

	/**
	 * Establece el valor del atributo parametrosControl
	 * @param parametrosControl con el valor del atributo parametrosControl a establecer
	 */
	public void setParametrosControl(ParametrosStatelessLocal parametrosControl) {
		this.parametrosControl = parametrosControl;
	}

}

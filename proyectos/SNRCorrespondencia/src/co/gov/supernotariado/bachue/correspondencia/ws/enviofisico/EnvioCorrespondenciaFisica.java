package co.gov.supernotariado.bachue.correspondencia.ws.enviofisico;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
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
import https.www_supernotariado_gov_co.schemas.bachue.cr.enviocorrespondenciafisica.enviarcorrespondenciafisica.v1.TipoEntradaEnviarCorrespondenciaFisica;
import https.www_supernotariado_gov_co.schemas.bachue.cr.enviocorrespondenciafisica.enviarcorrespondenciafisica.v1.TipoSalidaEnviarCorrespondenciaFisica;

/**
 * Servicio web que realiza el cambio de etapa de un proceso para avanzarlo a la etapa de Envío
 */
public class EnvioCorrespondenciaFisica {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(EnvioCorrespondenciaFisica.class);
	
	/**
	 * Código de mensaje para éxito
	 */
	private static final int CODIGO_EXITO = 200;

	/**
	 * Código de mensaje para error
	 */
	private static final int CODIGO_ERROR = 409;

	/** Manejador de lógica de negocio de procesos */
	@EJB(name = "ProcesoBusiness")
	private ProcesosStatelessLocal procesoControl;

	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;


    /**
     * Método principal del servicio, realiza el cambio de etapa de un proceso para avanzarlo a la etapa de Envío
     * @param entradaEnviarCorrespondenciaFisica Parámetros de entrada del servicio web
     * @param wsContext Contexto de ejecución del servicio web para obtener la IP del cliente 
     * @return Respuesta del servicio web con el un código de salida y un mensaje
     */
    public TipoSalidaEnviarCorrespondenciaFisica enviarCorrespondenciaFisica(TipoEntradaEnviarCorrespondenciaFisica entradaEnviarCorrespondenciaFisica, WebServiceContext wsContext) {
    	TipoSalidaEnviarCorrespondenciaFisica response = new TipoSalidaEnviarCorrespondenciaFisica();

		UsuarioTO usuario = new UsuarioTO();
		usuario.setId("1");
		usuario.setNombre("admin");

    	MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        
        String direccionIp = req.getRemoteAddr();

		boolean ok = validaRequest(entradaEnviarCorrespondenciaFisica, response);
		if(ok) {
			try {
				Proceso proceso = procesoControl.obtenerProcesoActivoPorNombreClave(ConstantesCorrespondencia.NOMBRE_PROCESO_GENERACION_EE);
				if(proceso!=null) {
					ProcesoEjecutado procesoEjecutado = procesoControl.obtenerProcesoEjecutadoPorSecuenciaSalidaIdentificador(entradaEnviarCorrespondenciaFisica.getIdentificador(), proceso.getIdentificador());
					if(procesoEjecutado==null) {
						agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "Proceso con consecutivo EE "+entradaEnviarCorrespondenciaFisica.getIdentificador()+" no encontrado");
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
										agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_EXITO), "Ejecución correcta. Proceso avanzó a la etapa: "+nuevoPaso.getPaso().getNombre());
									} else {
										agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_EXITO), "Ejecución correcta. Proceso finalizó todas sus etapas o tiene otras etapas pendientes en paralelo");
									}
								} else {
									agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "No se encontraron etapas activas para el proceso");
								}
							} else {
								agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "El Paso actual ("+procesoEjecutado.getPasoActual().getNombre()+") no permite ejecución por WS");
							}
						} else {
							agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "Proceso no se encuentra activo");
						}
					}
				} else {
					agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "Proceso "+ConstantesCorrespondencia.NOMBRE_PROCESO_GENERACION_EE+" no encontrado");
				}
			} catch(Exception e) {
	    		StringWriter sw = new StringWriter();
	    		e.printStackTrace(new PrintWriter(sw));
	    		String exceptionAsString = sw.toString();

				agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "Error: "+exceptionAsString);
	    		logger.error(e.getMessage(), e);
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
    private void agregarCodigoMensaje(TipoSalidaEnviarCorrespondenciaFisica response, BigInteger codigo, String mensaje) {
    	try {
	    	response.getCodigoMensajeAndDescripcionMensaje().clear();
	    	response.getCodigoMensajeAndDescripcionMensaje().add(codigo);
	    	response.getCodigoMensajeAndDescripcionMensaje().add(mensaje);
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
	private boolean validaRequest(TipoEntradaEnviarCorrespondenciaFisica request, TipoSalidaEnviarCorrespondenciaFisica response) {
		boolean ok = true;
		if(request==null) {
			agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "Debe enviar parámetros por GET");
			ok = false;
		} else {
			if(request.getIdentificador()==null || request.getIdentificador().isEmpty() || request.getIdentificador().trim().equals("?")) {
				agregarCodigoMensaje(response, BigInteger.valueOf(CODIGO_ERROR), "Parámetro Identificador no encontrado");
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

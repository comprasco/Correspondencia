package co.gov.supernotariado.bachue.correspondencia.ws.generacionee;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.ConstantesCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionUsuarios;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ProcesosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.SecuenciasSingletonLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import https.www_supernotariado_gov_co.schemas.bachue.cr.generacionidscorrespondencia.obtenereecorrespondencia.v1.ObjectFactory;
import https.www_supernotariado_gov_co.schemas.bachue.cr.generacionidscorrespondencia.obtenereecorrespondencia.v1.TipoDestinatario;
import https.www_supernotariado_gov_co.schemas.bachue.cr.generacionidscorrespondencia.obtenereecorrespondencia.v1.TipoEntradaObtenerEECorrespondencia;
import https.www_supernotariado_gov_co.schemas.bachue.cr.generacionidscorrespondencia.obtenereecorrespondencia.v1.TipoSalidaObtenerEECorrespondencia;
import https.www_supernotariado_gov_co.schemas.bachue.cr.generacionidscorrespondencia.obtenereecorrespondencia.v1.TipoVariable;

/**
 * Servicio web que realiza la ejecución de un proceso y obtiene un consecutivo de salida EE
 */
@Named
@RequestScoped
public class GeneracionEECorrespondencia {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(GeneracionEECorrespondencia.class);

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

	/** Manejador de lógica de negocio de secuencias */
	@EJB(name = "SecuenciasBusiness")
	private SecuenciasSingletonLocal secuenciasControl;

	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;


    /**
     * Método principal del servicio, inicializa el proceso con los parámetros de entrada recibidos
     * @param entradaObtenerEE Parámetros de entrada del servicio web
     * @param wsContext Contexto de ejecución del servicio web para obtener la IP del cliente 
     * @return Respuesta del servicio web con el un código de salida, un mensaje y un consecutivo EE
     */
    public TipoSalidaObtenerEECorrespondencia obtenerEECorrespondencia(TipoEntradaObtenerEECorrespondencia entradaObtenerEE, WebServiceContext wsContext) {
    	TipoSalidaObtenerEECorrespondencia response = new TipoSalidaObtenerEECorrespondencia();

		UsuarioTO usuario = new UsuarioTO();
		usuario.setId("1");
		usuario.setNombre("admin");
		
		String oripEjecucion = "";
		String consecutivoEE = "";

		IntegracionCatalogos integracionCatalogos = parametrosControl.obtenerIntegracionCatalogos();
		
		List<String> llavesEspeciales = new ArrayList<>();
		llavesEspeciales.add(integracionCatalogos.getParametrosTO().getParamGeneracioneeCiudadDestinatario());
		llavesEspeciales.add(integracionCatalogos.getParametrosTO().getParamGeneracioneeDireccionDestinatario());
		llavesEspeciales.add(integracionCatalogos.getParametrosTO().getParamGeneracioneeTelefonoDestinatario());
		
    	try {
	    	MessageContext mc = wsContext.getMessageContext();
	        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
	        
	        String direccionIp = req.getRemoteAddr();
	        logger.info("Client IP = " + direccionIp); 
	
			boolean ok = validaRequest(integracionCatalogos, entradaObtenerEE, response);
			if(ok) {
				Proceso proceso = procesoControl.obtenerProcesoActivoPorNombreClave(ConstantesCorrespondencia.NOMBRE_PROCESO_GENERACION_EE);
				if(proceso!=null) {
		        	logger.info("ObtenerEECorrespondencia: Proceso encontrado:"+ proceso.getNombre());
		        	List<Paso> pasos = procesoControl.buscarPasosActivosProceso(proceso.getId());
		        	if(!pasos.isEmpty()) {
		        		Paso paso = pasos.get(0);
			        	logger.info("ObtenerEECorrespondencia: Paso encontrado:"+ paso.getNombre());
			        	
			        	List<ValorEntrada> valorEntradas = buscarEntradas(paso.getId());
			        	List<ValorEntrada> valorEntradasFinal = new ArrayList<>();
			        	for(ValorEntrada valorEntrada:valorEntradas) {
			        		if(valorEntrada.getEntrada().getNombre()!=null) {
			        			if(valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.SECUENCIA_GLOBAL.name())) {
			        				// se fija un valor temporal para agregarlo a la lista de entradas final 
			        				valorEntrada.setValor("tmp");
								} else if (valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())) {
									valorEntrada.setValor("tmp");
			        			} else {
				        			if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeOrip())) {
				        				oripEjecucion = entradaObtenerEE.getOrip();
				        				valorEntrada.setValor(oripEjecucion);
				        			} else if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeClasificacion())) {
				        				if(entradaObtenerEE.getClasificacion()!=null) {
				        					// Revisa si es lista de opciones para asignar valor de id de la opción
				        					List<NodoTO> opciones = procesoControl.getListaOpcionesEntrada(valorEntrada.getEntrada(), null);
				        					if(opciones!=null && !opciones.isEmpty()) {
				        						for(NodoTO opcion:opciones) {
				        							if(opcion.getText().equals(entradaObtenerEE.getClasificacion())) {
				        								valorEntrada.setValor(opcion.getId());
				        								valorEntrada.setValorEntradaTexto(opcion.getText());
				        							}
				        						}
				        					} 
				        					
				        					if(valorEntrada.getValor()==null || valorEntrada.getValor().isEmpty()) {
				        						valorEntrada.setValor(entradaObtenerEE.getClasificacion());
				        					}
				        					
				        				}
				        			} else if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeCanal())) {
				        				if(entradaObtenerEE.getCanal()!=null) {
				        					
				        					// Revisa si es lista de opciones para asignar valor de id de la opción
				        					List<NodoTO> opciones = procesoControl.getListaOpcionesEntrada(valorEntrada.getEntrada(), null);
				        					if(opciones!=null && !opciones.isEmpty()) {
				        						for(NodoTO opcion:opciones) {
				        							if(opcion.getClave()!=null && opcion.getClave().equals(entradaObtenerEE.getCanal())) {
				        								valorEntrada.setValor(opcion.getId());
				        								valorEntrada.setValorEntradaTexto(opcion.getText());
				        							}
				        						}
				        					}

				        					if(valorEntrada.getValor()==null || valorEntrada.getValor().isEmpty()) {
				        						valorEntrada.setValor(entradaObtenerEE.getCanal());
				        					}
				        				}
				        			} else if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeNir())) {
				        				valorEntrada.setValor(entradaObtenerEE.getNir());
				        			} else if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeTurno())) {
				        				valorEntrada.setValor(entradaObtenerEE.getTurno());
				        			} else if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeNumeroFolios())) {
				        				valorEntrada.setValor(entradaObtenerEE.getNumeroFolios());
				        			} else if(valorEntrada.getEntrada().getNombre().equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeRadicacionManual())) {
				        				// Deja el valor de la entrada en 2 (equivale a NO en la lista predefinida de la entrada SI/NO)
				        				valorEntrada.setValor("2");
				        			}
			        			}
			        			
			        			if(valorEntrada.getValor()!=null && !valorEntrada.getValor().isEmpty()) {
			        				valorEntradasFinal.add(valorEntrada);
			        			}
			        		}
			        	}
	
	        			if(entradaObtenerEE.getDocumentos()!=null) {
	        				for(String documento:entradaObtenerEE.getDocumentos().getNombreDocumento()) {
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeNombreDocumento(), documento);
	        				}
	        			}
	
	        			
	        			if(entradaObtenerEE.getDestinatarios()!=null) {
	        				for(TipoDestinatario destinatario:entradaObtenerEE.getDestinatarios().getDestinatario()) {
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeTipoDocumentoDestinatario(), destinatario.getTipoDocDestinatario());
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeNumeroDocumentoDestinatario(), destinatario.getNumeroDocDestinatario());
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneePrimerNombreDestinatario(), destinatario.getPrimerNombreDestinatario());
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeSegundoNombreDestinatario(), destinatario.getSegundoNombreDestinatario());
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneePrimerApellidoDestinatario(), destinatario.getPrimerApellidoDestinatario());
	        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeSegundoApellidoDestinatario(), destinatario.getSegundoApellidoDestinatario());
	        					
			        			if(destinatario.getVariables()!=null) {
			        				for(TipoVariable variable:destinatario.getVariables().getVariable()) {
			        					// Verifica campos especiales envio 
			        					if(llavesEspeciales.contains(variable.getLlave())) {
				        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, variable.getLlave(), variable.getValor());
			        					} else {
			        						// Si no, los registra normal como llave valor
				        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeLlave(), variable.getLlave());
				        					addValorEntradaFinal(valorEntradasFinal, valorEntradas, integracionCatalogos.getParametrosTO().getParamGeneracioneeValor(), variable.getValor());
			        					}
			        				}
			        			}
	        				}
	        			}
			        	
			        	PasoEjecutado pasoEjecutado = procesoControl.inicializaPasoEjecutado(paso, usuario.getId(), direccionIp, oripEjecucion);
			        	
						IntegracionUsuarios integracionUsuarios = new IntegracionUsuarios(integracionCatalogos);
						PasoEjecutado nuevoPaso = procesoControl.ejecutarPasoProceso(integracionUsuarios, pasoEjecutado, proceso.getId(), valorEntradasFinal, usuario, direccionIp, 1, oripEjecucion);

						if(pasoEjecutado.getProcesoEjecutado()!=null && pasoEjecutado.getProcesoEjecutado().getSecuenciaProcesoSalida()!=null) {
							consecutivoEE = pasoEjecutado.getProcesoEjecutado().getSecuenciaProcesoSalida();
						}
						
						if(nuevoPaso!=null) {
							agregarCodigoMensaje(response, consecutivoEE, BigInteger.valueOf(CODIGO_EXITO), "Ejecución correcta. Proceso avanzó a la etapa: "+nuevoPaso.getPaso().getNombre());
						} else {
							agregarCodigoMensaje(response, consecutivoEE, BigInteger.valueOf(CODIGO_EXITO), "Ejecución correcta. Proceso finalizó todas sus etapas");
						}
		        	}
		        } else {
					agregarCodigoMensaje(response, null, BigInteger.valueOf(CODIGO_ERROR), "Proceso "+ConstantesCorrespondencia.NOMBRE_PROCESO_GENERACION_EE+" no encontrado");
		        }
			}
    	} catch(Exception e) {
    		StringWriter sw = new StringWriter();
    		e.printStackTrace(new PrintWriter(sw));
    		String exceptionAsString = sw.toString();
    		
			agregarCodigoMensaje(response, null, BigInteger.valueOf(CODIGO_ERROR), "Error: "+ exceptionAsString);
    		logger.error(e.getMessage(), e);
    	}
        
        return response;
    }
    
    
	/**
	 * Valida los datos del request
     * @param integracionCatalogos Datos de integración y parámetros del sistema
	 * @param request Request del Servicio
	 * @param response Tipo de Salida Response del servicio
	 * @return true si la validación fue correcta
	 */
	private boolean validaRequest(IntegracionCatalogos integracionCatalogos, TipoEntradaObtenerEECorrespondencia request, TipoSalidaObtenerEECorrespondencia response) {
		boolean ok = true;
		if(request==null) {
			agregarCodigoMensaje(response, null, BigInteger.valueOf(CODIGO_ERROR), "Debe enviar parámetros por GET");
			ok = false;
		} else {
			if(!validarParametro(integracionCatalogos, request.getCanal(), integracionCatalogos.getParametrosTO().getParamGeneracioneeCanal(), response, true) 
					|| !validarParametro(integracionCatalogos, request.getClasificacion(), integracionCatalogos.getParametrosTO().getParamGeneracioneeClasificacion(), response, true)
					|| !validarParametro(integracionCatalogos, request.getOrip(), integracionCatalogos.getParametrosTO().getParamGeneracioneeOrip(), response, true)
					|| !validarParametro(integracionCatalogos, request.getNir(), integracionCatalogos.getParametrosTO().getParamGeneracioneeNir(), response, false)
					|| !validarParametro(integracionCatalogos, request.getTurno(), integracionCatalogos.getParametrosTO().getParamGeneracioneeTurno(), response, false)
					|| !validarParametro(integracionCatalogos, request.getNumeroFolios(), integracionCatalogos.getParametrosTO().getParamGeneracioneeNumeroFolios(), response, false)) {
				ok = false;
			} else if(request.getDocumentos()!=null && request.getDocumentos().getNombreDocumento()!=null && !request.getDocumentos().getNombreDocumento().isEmpty()) {
				for(String documento:request.getDocumentos().getNombreDocumento()) {
					if(!validarParametro(integracionCatalogos, documento, integracionCatalogos.getParametrosTO().getParamGeneracioneeNombreDocumento(), response, true)) {
						ok = false;
						break;
					}
				}
			}
			
			if(ok) {
				if(request.getDestinatarios()==null || request.getDestinatarios().getDestinatario()==null || request.getDestinatarios().getDestinatario().isEmpty()) {
					agregarCodigoMensaje(response, null, BigInteger.valueOf(CODIGO_ERROR), "Parámetro Destinatarios es Requerido");
					ok = false;
				} else {
					for(TipoDestinatario destinatario:request.getDestinatarios().getDestinatario()) {
						if(!validarParametro(integracionCatalogos, destinatario.getTipoDocDestinatario(), integracionCatalogos.getParametrosTO().getParamGeneracioneeTipoDocumentoDestinatario(), response, true)
							|| !validarParametro(integracionCatalogos, destinatario.getNumeroDocDestinatario(), integracionCatalogos.getParametrosTO().getParamGeneracioneeNumeroDocumentoDestinatario(), response, true)
							|| !validarParametro(integracionCatalogos, destinatario.getPrimerNombreDestinatario(), integracionCatalogos.getParametrosTO().getParamGeneracioneePrimerNombreDestinatario(), response, true)
							|| !validarParametro(integracionCatalogos, destinatario.getSegundoNombreDestinatario(), integracionCatalogos.getParametrosTO().getParamGeneracioneeSegundoNombreDestinatario(), response, false)
							|| !validarParametro(integracionCatalogos, destinatario.getPrimerApellidoDestinatario(), integracionCatalogos.getParametrosTO().getParamGeneracioneePrimerApellidoDestinatario(), response, true)
							|| !validarParametro(integracionCatalogos, destinatario.getSegundoApellidoDestinatario(), integracionCatalogos.getParametrosTO().getParamGeneracioneeSegundoApellidoDestinatario(), response, false)) {
							ok = false;
						}
						
						if(ok && destinatario.getVariables()!=null && destinatario.getVariables().getVariable()!=null && !destinatario.getVariables().getVariable().isEmpty()) {
							for(TipoVariable variable:destinatario.getVariables().getVariable()) {
								if(!validarParametro(integracionCatalogos, variable.getLlave(), integracionCatalogos.getParametrosTO().getParamGeneracioneeLlave(), response, true)
									|| !validarParametro(integracionCatalogos, variable.getValor(), integracionCatalogos.getParametrosTO().getParamGeneracioneeValor(), response, true)) {
									ok = false;
								}
							}
						}
	
						if(!ok) {
							break;
						}
					}
				}
			}
		}
		return ok;
	}
	
	
    /**
     * Agrega mensaje de response
     * @param response Tipo de Salida Response
     * @param identificador Código de respuesta EE de salida
     * @param codigo Código de salida
     * @param mensaje Mensaje de salida
     */
	private void agregarCodigoMensaje(TipoSalidaObtenerEECorrespondencia response, String identificador, BigInteger codigo, String mensaje) {
    	try {
    		if(identificador==null) {
    			identificador = "";
    		}
    		if(codigo==null) {
    			codigo = BigInteger.ZERO;
    		}
    		if(mensaje==null) {
    			mensaje = "";
    		}
    		JAXBElement<String> elementIdentificador = new ObjectFactory().createTipoSalidaObtenerEECorrespondenciaIdentificador(identificador);
    		JAXBElement<BigInteger> elementCodigo = new ObjectFactory().createTipoSalidaObtenerEECorrespondenciaCodigoMensaje(codigo);
    		JAXBElement<String> elementMensaje = new ObjectFactory().createTipoSalidaObtenerEECorrespondenciaDescripcionMensaje(mensaje);
	    	response.getIdentificadorAndCodigoMensajeAndDescripcionMensaje().clear();
	    	response.getIdentificadorAndCodigoMensajeAndDescripcionMensaje().add(elementIdentificador);
	    	response.getIdentificadorAndCodigoMensajeAndDescripcionMensaje().add(elementCodigo);
	    	response.getIdentificadorAndCodigoMensajeAndDescripcionMensaje().add(elementMensaje);
        	logger.info("Identificador:"+identificador+", Codigo:"+codigo+", Mensaje:"+mensaje);
    	}catch(Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    }

    
    /**
     * Valida un parametro para que no venga vacío
     * @param integracionCatalogos Datos de integración y parámetros del sistema
     * @param parametro Parametro a evaluar
     * @param nombreParametro Nombre del parámetro
     * @param response Tipo de Salida Response
     * @param obligatorio Indica si es obligatoria la validación
     * @return true si el parámetro no tiene errores de validación@ 
     */
    private boolean validarParametro(IntegracionCatalogos integracionCatalogos, Object parametro, String nombreParametro, TipoSalidaObtenerEECorrespondencia response, boolean obligatorio) {
		boolean ok = true;
		if(obligatorio) {
			if(parametro==null || parametro.toString().isEmpty() || parametro.toString().trim().equals("?")) {
				ok = false;
			}
		} else {
			if(parametro!=null && !parametro.toString().isEmpty() && parametro.toString().trim().equals("?")) {
				ok = false;
			}
		}
		if(nombreParametro.equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeCanal()) && (parametro==null || (!parametro.equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeCanalFisico()) && !parametro.equals(integracionCatalogos.getParametrosTO().getParamGeneracioneeCanalElectronico())))) {
			agregarCodigoMensaje(response, null, BigInteger.valueOf(CODIGO_ERROR), "Parámetro "+nombreParametro+" inválido. Se espera: "+integracionCatalogos.getParametrosTO().getParamGeneracioneeCanalFisico()+" o "+integracionCatalogos.getParametrosTO().getParamGeneracioneeCanalElectronico());
			return false;
		}
		
		if(!ok) {
			agregarCodigoMensaje(response, null, BigInteger.valueOf(CODIGO_ERROR), "Parámetro "+nombreParametro+" inválido");
		}
		return ok;
	}

    
    
    /**
     * Agrega una entrada a una lista de entradas definitiva para insertar
     * @param valorEntradasFinal Lista con los valores de entradas final a la cual se insertan el nuevo campo
     * @param valorEntradas Lista con los valores de entradas temporal
     * @param campo Campo a evaluar para agregar
     * @param valor Valor de la entrada a agregar
     */
    private void addValorEntradaFinal(List<ValorEntrada> valorEntradasFinal, List<ValorEntrada> valorEntradas, String campo, String valor) {
    	for(ValorEntrada valorEntrada:valorEntradas) {
    		if(valorEntrada.getEntrada().getNombre()!=null && valorEntrada.getEntrada().getNombre().equals(campo)) {
				ValorEntrada nve = copiarValorEntrada(valorEntrada);
				nve.setValor(valor);
				valorEntradasFinal.add(nve);
				break;
    		}
		}
    }

	/**
	 * Copia los valores de una entrada definida para poder crear nuevas entradas dinamicamente
	 * @param valorEntrada Valor de Entrada a copiar
	 * @return Nuevo Valor de Entrada inicializada
	 */
	private ValorEntrada copiarValorEntrada(ValorEntrada valorEntrada) {
		ValorEntrada nve = new ValorEntrada();
		nve.setArchivoCargado(valorEntrada.getArchivoCargado());
		nve.setDocumentos(valorEntrada.getDocumentos());
		nve.setEntrada(valorEntrada.getEntrada());
		procesoControl.inicializarJsonEntradaData(nve.getEntrada());
		return nve; 
	}
    
	
	/**
	 * Busca las entradas de un paso
	 * 
	 * @param pasoId Paso asociado
	 * @return Lista con los valores de entradas inicializadas
	 */
	private List<ValorEntrada> buscarEntradas(long pasoId) {
		List<ValorEntrada> listaEntradas = new ArrayList<>();
		List<Entrada> meta = procesoControl.buscarEntradasActivasPaso(pasoId);
		for (Entrada entradaentrada : meta) {
			ValorEntrada value = new ValorEntrada();
			value.setEntrada(entradaentrada);
			listaEntradas.add(value);
		}
		return listaEntradas;
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
	 * Obtiene el valor del atributo secuenciasControl
	 * @return El valor del atributo secuenciasControl
	 */
	public SecuenciasSingletonLocal getSecuenciasControl() {
		return secuenciasControl;
	}


	/**
	 * Establece el valor del atributo secuenciasControl
	 * @param secuenciasControl con el valor del atributo secuenciasControl a establecer
	 */
	public void setSecuenciasControl(SecuenciasSingletonLocal secuenciasControl) {
		this.secuenciasControl = secuenciasControl;
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

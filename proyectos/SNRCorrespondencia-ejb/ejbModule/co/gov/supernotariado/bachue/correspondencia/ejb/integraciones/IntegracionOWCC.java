package co.gov.supernotariado.bachue.correspondencia.ejb.integraciones;

import java.util.List;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.consultar.v1.TipoEntradaConsultar;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.consultar.v1.TipoEntradaConsultar.Parametros;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.consultar.v1.TipoParametro;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.consultar.v1.TipoSalidaConsultar;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.obtenerarchivo.v1.TipoEntradaObtenerArchivo;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.obtenerarchivo.v1.TipoSalidaObtenerArchivo;
import https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoEntradaEnviarDocumento;
import https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoSalidaEnviarDocumento;
import https.www_supernotariado_gov_co.services.bachue.co.busquedadocumentos.v1.SUTCOBusquedaDocumentos;
import https.www_supernotariado_gov_co.services.bachue.co.busquedadocumentos.v1.SUTCOBusquedaDocumentos_Service;
import https.www_supernotariado_gov_co.services.bachue.co.enviodocumentos.v1.SUTCOEnvioDocumentos;
import https.www_supernotariado_gov_co.services.bachue.co.enviodocumentos.v1.SUTCOEnvioDocumentos_Service;

/**
 * Maneja la integración de documentos con OWCC
 */
public class IntegracionOWCC {

	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(IntegracionOWCC.class);

	/**
	 * Integración para obtener URLs de servicios web
	 */
	private IntegracionCatalogos integracionCatalogos;
	
	/**
	 * Constructor
	 */
	public IntegracionOWCC(IntegracionCatalogos integracionCatalogos) {
		this.integracionCatalogos = integracionCatalogos;
	}
	
	
	/**
	 * Consulta los documentos disponibles en OWCC
	 * @param radicado consecutivo de radicación
	 * @param repositorio TEMPORAL o FINAL
	 * @return Respuesta con la consulta del documento
	 */
	public TipoSalidaConsultar busquedaDocumentosPorRadicado(String radicado, String repositorio) {
		TipoSalidaConsultar response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoOWCCBusquedaDocumentoURL();
			
			SUTCOBusquedaDocumentos_Service service = new SUTCOBusquedaDocumentos_Service();
			SUTCOBusquedaDocumentos port = service.getSUTCOBusquedaDocumentosPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionOWCC.busquedaDocumentos:"+integracionCatalogos.getParametrosTO().getParamOwccParamRadicado()+":"+radicado);

            TipoEntradaConsultar entrada = new TipoEntradaConsultar();
            entrada.setSistemaOrigen(integracionCatalogos.getParametrosTO().getParamOwccProfile());
            entrada.setRepositorio(repositorio);
            
            Parametros parametros = new Parametros();
            TipoParametro parametro = new TipoParametro();
            parametro.setNombre(integracionCatalogos.getParametrosTO().getParamOwccParamRadicado());
            parametro.setValor(radicado);
            parametros.getParametro().add(parametro);

            entrada.setParametros(parametros);
			response = port.consultar(entrada);

			if(response!=null) {
				logger.info("IntegracionOWCC.busquedaDocumentos-response: ok "+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionOWCC.busquedaDocumentos-response es null");
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return response;
	}

	
	
	/**
	 * Consulta los documentos disponibles en OWCC por un Identificador
	 * Obtiene los metadatos y la url de visor del documento, mas no los bytes del documento
	 * @param idDocumento Identificador del documento en OWCC
	 * @param repositorio FINAL o TEMPORAL
	 * @return Respuesta con la consulta del documento
	 */
	public TipoSalidaConsultar busquedaDocumentosPorIdDocumento(String idDocumento, String repositorio) {
		TipoSalidaConsultar response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoOWCCBusquedaDocumentoURL();
			
			SUTCOBusquedaDocumentos_Service service = new SUTCOBusquedaDocumentos_Service();
			SUTCOBusquedaDocumentos port = service.getSUTCOBusquedaDocumentosPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionOWCC.busquedaDocumentos-"+integracionCatalogos.getParametrosTO().getParamOwccParamDocId()+":"+idDocumento);

            TipoEntradaConsultar entrada = new TipoEntradaConsultar();
            entrada.setSistemaOrigen(integracionCatalogos.getParametrosTO().getParamOwccProfile());
            
            Parametros parametros = new Parametros();
            TipoParametro parametro = new TipoParametro();
            parametro.setNombre(integracionCatalogos.getParametrosTO().getParamOwccParamDocId());
            parametro.setValor(idDocumento);
            parametros.getParametro().add(parametro);
            
            entrada.setRepositorio(repositorio);

            entrada.setParametros(parametros);
			response = port.consultar(entrada);

			if(response!=null) {
				logger.info("IntegracionOWCC.busquedaDocumentos-response: ok "+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionOWCC.busquedaDocumentos-response es null");
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return response;
	}

	
	/**
	 * Obtiene los bytes de un archivo en OWCC
	 * @param dId Identificador del documento en OWCC
	 * @return Respuesta del servicio con el archivo obtenido
	 */
	public TipoSalidaObtenerArchivo obtenerArchivo(String dId) {
		TipoSalidaObtenerArchivo response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoOWCCBusquedaDocumentoURL();
			
			SUTCOBusquedaDocumentos_Service service = new SUTCOBusquedaDocumentos_Service();
			SUTCOBusquedaDocumentos port = service.getSUTCOBusquedaDocumentosPort();

			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionOWCC.obtenerArchivo:"+ dId);

            TipoEntradaObtenerArchivo entrada = new TipoEntradaObtenerArchivo();
            entrada.setDID(dId);
            entrada.setDDocName("");
			response = port.obtenerArchivo(entrada);
			if(response!=null) {
				logger.info("IntegracionOWCC.obtenerArchivo-response: ok "+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionOWCC.obtenerArchivo-response es null");
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}
		return response;
	}

	
	/**
	 * Envía un documento a OWCC
	 * @param titulo Titulo documento
	 * @param tipo Tipo documento
	 * @param nombreArchivo Nombre del archivo
	 * @param archivo bytes
	 * @param codigoOrip Código de la Orip que ejecuta
	 * @param pasoEjecutado Paso Ejecutado asociado
	 * @param entradas Lista de entradas con metadatos asociados, deben enviarse tanto las de pasos anteriores como del paso actual
	 * 
	 */
	public TipoSalidaEnviarDocumento enviarDocumento(String titulo, String tipo, String nombreArchivo, byte[] archivo, String codigoOrip, PasoEjecutado pasoEjecutado, List<ValorEntrada> entradas) {
		TipoSalidaEnviarDocumento response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoOWCCEnvioDocumentoURL();

			SUTCOEnvioDocumentos_Service service = new SUTCOEnvioDocumentos_Service();
			SUTCOEnvioDocumentos port = service.getSUTCOEnvioDocumentosPort();

			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionOWCC.enviarDocumento:"+ titulo+" - "+tipo+" - "+" - "+nombreArchivo);

    		https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoEntradaEnviarDocumento.Parametros parametros = new https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoEntradaEnviarDocumento.Parametros();

    		if(pasoEjecutado!=null && pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso()!=null && !pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso().isEmpty()) {
            	IntegracionOWCC.estableceValorParametro(parametros, integracionCatalogos.getParametrosTO().getParamOwccParamRadicado(), pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso());
    		}
       		if(codigoOrip!=null && !codigoOrip.isEmpty()) {
            	IntegracionOWCC.estableceValorParametro(parametros, integracionCatalogos.getParametrosTO().getParamOwccParamCodigoOrip(), codigoOrip);
            }
           	IntegracionOWCC.estableceValorParametro(parametros, integracionCatalogos.getParametrosTO().getParamOwccParamDocType(), integracionCatalogos.getParametrosTO().getParamOwccParamDocTypeValue());
            
       		if(entradas!=null) {
	    		for(ValorEntrada entrada:entradas) {
	    			if(entrada.getEntrada().isEnviarCheckin() && entrada.getValor()!=null && !entrada.getValor().isEmpty()) {
	    				String valorEntrada = entrada.getValor();
	    				if(entrada.getValorEntradaTexto()!=null && !entrada.getValorEntradaTexto().isEmpty()) {
	    					valorEntrada = entrada.getValorEntradaTexto();
	    				}
	    				IntegracionOWCC.estableceValorParametro(parametros, entrada.getEntrada().getNombre(), valorEntrada);
	    			}
	    		}
       		}
    		
    		estableceParametroNombre(parametros, nombreArchivo);
    		
            TipoEntradaEnviarDocumento entrada = new TipoEntradaEnviarDocumento();
            entrada.setArchivo(archivo);
            entrada.setNombreArchivo(nombreArchivo);
            entrada.setSistemaOrigen(integracionCatalogos.getParametrosTO().getParamOwccProfile());
            entrada.setParametros(parametros);
            
            if(entrada.getRepositorio()==null || entrada.getRepositorio().isEmpty()){
            	entrada.setRepositorio(integracionCatalogos.getParametrosTO().getParamOwccParamRepositorioValueFinal());
            }

			response = port.enviarDocumento(entrada);
			if(response!=null) {
				logger.info("IntegracionOWCC.enviarDocumento-response docName:"+response.getDocName());
				logger.info("IntegracionOWCC.enviarDocumento-response dID:"+response.getDID());
				logger.info("IntegracionOWCC.enviarDocumento-response mensaje:"+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionOWCC.enviarDocumento-response es null");
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}
		return response;
	}
	
	
	/**
	 * Crea un nuevo parámetro o actualiza el valor de uno ya existente
	 * @param parametros Parámetros a fijar en el archivo
	 * @param nombre Nombre del parámetro
	 * @param valor Valor del parámetro 
	 */
	public static void estableceValorParametro(https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoEntradaEnviarDocumento.Parametros parametros, String nombre, String valor) {
		https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoParametro parametro = null;
		for(https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoParametro param:parametros.getParametro()) {
			if(param.getNombre().equals(nombre)) {
				parametro = param;
				break;
			}
		}
		
		if(parametro==null) {
			parametro = new https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoParametro();
			parametro.setNombre(nombre);
			parametros.getParametro().add(parametro);
		}
		parametro.setValor(valor);
	}
	
	
	/**
	 * Agrega parametro dDocTitle al documento a cargar
	 * @param parametros Parametros con los metadatos a fijar
	 * @param nombreArchivo Nombre del archivo
	 */
	public void estableceParametroNombre(https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoEntradaEnviarDocumento.Parametros parametros, String nombreArchivo) {
		try {
    		if(nombreArchivo!=null && !nombreArchivo.isEmpty()) {
    			String nombre = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
            	IntegracionOWCC.estableceValorParametro(parametros, integracionCatalogos.getParametrosTO().getParamOwccParamDocTitle(), nombre);
    		}
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
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

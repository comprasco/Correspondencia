package co.gov.supernotariado.bachue.correspondencia.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ProcesosStatelessLocal;

/**
 * Permite descargar archivos 
 */
@WebServlet("/Descargar")
public class DescargarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(DescargarServlet.class);

	/** Manejador de lógica de negocio de procesos */
	@EJB(name = "ProcesoBusiness")
	private ProcesosStatelessLocal procesoControl;

	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;

	/**
	 * Operación get del servlet
	 * @param request Request HTTP
	 * @param response Response HTTP
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.descargar(request, response);
	}

	/**
	 * Operación post del servlet
	 * @param request Request HTTP
	 * @param response Response HTTP
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.descargar(request, response);
	}

	/**
	 * Realiza la descarga del archivo solicitado según los parámetros recibidos en el request
	 * @param request Request HTTP
	 * @param response Response HTTP
	 */
	@SuppressWarnings("unchecked")
	public void descargar(HttpServletRequest request, HttpServletResponse response) {
		try {
			String contentDisposition = "Content-Disposition";
			OutputStream os = response.getOutputStream();
	        
			HttpSession session = request.getSession(); 
	        IntegracionCatalogos integracionCatalogos = (IntegracionCatalogos) session.getAttribute("integracionCatalogos");
	        if(integracionCatalogos==null) {
				integracionCatalogos = parametrosControl.obtenerIntegracionCatalogos();
	        	session.setAttribute("integracionCatalogos", integracionCatalogos);
	        }

			String tipo = request.getParameter("t");

			String valor = request.getParameter("v");

			if(tipo.equals("1")) {
				ByteArrayOutputStream baos = procesoControl.generarSalidaReporte(Long.valueOf(valor));
		        response.setHeader(contentDisposition, String.format("filename=\"%s\"", "reporte_pdf"+Calendar.getInstance().getTimeInMillis()+".pdf"));
		        response.setContentType("application/pdf");  
				os.write(baos.toByteArray());
				os.flush();
				os.close();
			} else if(tipo.equals("3") && session.getAttribute("valorEntrada")!=null) {
	        	ValorEntrada valorEntrada = (ValorEntrada) session.getAttribute("valorEntrada");
	        	List<ValorEntrada> entradas = (List<ValorEntrada>) (Object) session.getAttribute("entradasPlantilla");
		        response.setHeader(contentDisposition, String.format("attachment;filename=\"%s\"",  valorEntrada.getEntrada().getNombreEntrada()+".doc"));
		        os.write(procesoControl.generarPlantilla(integracionCatalogos, entradas, Long.parseLong(valorEntrada.getValor())));
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}
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

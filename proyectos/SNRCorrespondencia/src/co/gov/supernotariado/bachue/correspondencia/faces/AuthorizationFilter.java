package co.gov.supernotariado.bachue.correspondencia.faces;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.bean.GestorSesionBean;

/**
 * Clase de tipo Filtro JSF que permite validar la sesión del usuario
 */
@WebFilter("/views/*")
public class AuthorizationFilter implements Filter {
	private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

    /**
     * Realiza el filtro por donde pasan todas las peticiones y revisa si la sesión está activa
     * @param req Request del servlet
     * @param res Response del servlet
     * @param chain Cadena de filtro
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {    
    	Logger logger = Logger.getLogger(AuthorizationFilter.class);

    	HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String errorURL = request.getContextPath() + "/views/400.xhtml";
        String loginURL = request.getContextPath() + "/views/login.jsf";

        String usuario = "";
        if(request.getUserPrincipal()!=null) {
        	usuario = request.getRemoteUser();
        	logger.info("request.getRemoteUser(): "+usuario);
        }

        boolean errorRequest = request.getRequestURI().equals(errorURL);
        boolean loginRequest = request.getRequestURI().equals(loginURL);
        boolean loggedIn = (session != null) && (session.getAttribute("usuario") != null);
        boolean resourceRequest = request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
        boolean ajaxRequest = "partial/ajax".equals(request.getHeader("Faces-Request"));
        if(!errorRequest) {
	        if (!loggedIn && usuario!=null && !usuario.isEmpty()) {
	        	// SSO
	            FacesContext context = FacesUtil.getFacesContext(request, response);
	            GestorSesionBean bean = (GestorSesionBean) context.getApplication().evaluateExpressionGet(context, "#{" + "gestorSesionBean" + "}", Object.class);
	
	    		if(bean!=null){
	    			bean.setMostrarCamposLogin(false);
	    			bean.setUsuario(usuario);
	    			bean.login();
	    			if(bean.getUsuarioActual()!=null) {
	    				response.sendRedirect(loginURL); // So, just perform standard synchronous redirect.
	    			} else {
	    				response.sendRedirect(errorURL); 
	    			}
	    		}
	        } else if (loggedIn || loginRequest || resourceRequest) {
	            if (!resourceRequest) { // Prevent browser from caching restricted resources. See also https://stackoverflow.com/q/4194207/157882
	                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	                response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	                response.setDateHeader("Expires", 0); // Proxies.
	            }
	
	            chain.doFilter(request, response); // So, just continue request.
	
	        } else if (ajaxRequest) {
	            response.setContentType("text/xml");
	            response.setCharacterEncoding("UTF-8");
	            response.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So, return special XML response instructing JSF ajax to send a redirect.
	
	        } else {
	        	response.sendRedirect(loginURL); // So, just perform standard synchronous redirect.
	        }
        }
    }
    
    
    /**
     *	Evento de destrucción del filtro
     */
    public void destroy() {
    	// Must implement
    }


	/**
	 * Evento de inicialización del filtro
	 * @param filterConfig Configuración del filtro
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
    	// Must implement
	}

}

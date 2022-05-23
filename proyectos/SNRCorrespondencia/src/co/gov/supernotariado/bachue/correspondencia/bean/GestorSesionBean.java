package co.gov.supernotariado.bachue.correspondencia.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.ConstantesCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.RolesSistema;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionUsuarios;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import co.gov.supernotariado.bachue.correspondencia.to.MenuTO;

/**
 * Managed Bean que permite controlar la sesión del usuario en la aplicación
 */
@ManagedBean
@SessionScoped
public class GestorSesionBean extends GenericBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;

	/**
	 * Integracion catálogos
	 */
	private transient IntegracionCatalogos integracionCatalogos;

	/**
	 * Integracion usuarios
	 */
	private transient IntegracionUsuarios integracionUsuarios;

	/**
	 * Properties con mensajes de etiquetas y mensajes de la aplicación
	 */
	private transient ResourceBundle mensajes;

	/** Logger de impresión de mensajes en los logs del servidor */
	private final transient Logger logger = Logger.getLogger(GestorSesionBean.class);

	/**
	 * Camino de migas que se muestra al navegar por la aplicación
	 */
	private transient List<String> breadcrumb = new ArrayList<>();
	
	/**
	 * Lista de opciones de menú disponible para el usuario
	 */
	private transient List<MenuTO> menu = new ArrayList<>();
	
	/**
	 * Menú actual seleccionado en la aplicación
	 */
	private transient MenuTO menuActual = null;
	
	/**
	 * Listado de usuarios registrados
	 */
	private transient List<UsuarioTO> usuarios = new ArrayList<>();
	
	/**
	 * Listado de grupos de usuarios
	 */
	private transient List<GrupoTO> grupos = new ArrayList<>();

	/**
	 * Objeto de usuario actual autenticado en la aplicación
	 */
	private transient UsuarioTO usuarioActual;
	
	/**
	 * Página actual que renderiza la aplicación
	 */
	private String paginaActual;
	
	/**
	 * Nombre de usuario login
	 */
	private String usuario;
	
	/**
	 * Clave que ingresa el usuario
	 */
	private String clave;
	
	/**
	 * Versión actual de la aplicación
	 */
	private String versionActual;
	
	/**
	 * Dirección IP desde donde se conecta el usuario
	 */
	private String direccionIp = "";

	/**
	 * Página principal para administración de procesos
	 */
	private String paginaIndexProcesos = "indexProcesos.xhtml";
	
	/**
	 * Página principal para administración de parámetros
	 */
	private String paginaIndexParametros = "indexParametros.xhtml";
	
	/**
	 * Página principal para administración de grupos y roles
	 */
	private String paginaIndexRolesGrupos = "indexRolesGrupos.xhtml";
	
	/**
	 * Localización actual de la aplicación
	 */
	private Locale locale; 
	
	/**
	 * Indica si se muestran los cuadros de usuario y contraseña (se ocultan en CA) 
	 */
	private boolean mostrarCamposLogin = true;
	
	
	/**
	 * Constructor
	 */
	public GestorSesionBean() {
		// Obtiene direccion IP
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		direccionIp = request.getHeader("X-FORWARDED-FOR");
		if (direccionIp == null) {
			direccionIp = request.getRemoteAddr();
		}
	}
	
	/**
	 * Inicializador de atributos
	 */
	@PostConstruct
	public void init() {
		locale = new Locale("es");
		if(FacesContext.getCurrentInstance()!=null && FacesContext.getCurrentInstance().getViewRoot()!=null) {
			FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
		}
		mensajes = ResourceBundle.getBundle("messages", locale);
		
		versionActual = ConstantesCorrespondencia.VERSION_CORRESPONDENCIA;
	}

	
	/**
	 * Realiza el login del usuario
	 * @return Cadena con la redirección de página
	 */
	public String login() {
		String outcome = "";
		try {
			// Inicializa variables
			integracionCatalogos = parametrosControl.obtenerIntegracionCatalogos();
			integracionUsuarios = new IntegracionUsuarios(integracionCatalogos);
			usuarios = integracionUsuarios.getUsuarios();
			grupos = integracionUsuarios.getGrupos();
			
			
			usuarioActual = null;
			
			if(usuario!=null) {
				// CA
				List<GrupoTO> listaGrupos = integracionUsuarios.obtenerRolesUsuario(usuario);
				if(!listaGrupos.isEmpty()) {
					usuarioActual = integracionUsuarios.generarUsuario(usuario, usuario, usuario, 0, null, true);
					usuarioActual.setOficina(integracionCatalogos.obtenerOripPorUsuario(usuario));

					long idRol = parametrosControl.obtenerRolInternoUsuario(listaGrupos);
					if(idRol > 0) {
						usuarioActual.setRol((int)idRol);
						for(GrupoTO grupo:listaGrupos) {
							usuarioActual.getGrupos().add(grupo);
						}
					} else {
						logger.error("Login Usuario:" +usuario+" no encontrado en ningún Rol Interno. ");
					}
				}
			
				// Simulación de usuarios quemados
				if(usuarioActual==null) {
					for(UsuarioTO user:usuarios) {
						if(this.usuario.equals(user.getNombreUsuario())) {
							usuarioActual = user;
							break;
						}
					}
				}
				// --
				
				if(usuarioActual!=null) {
					verificarSegundoFactor();
				} else {
					String message = mensajes.getString("etiqueta_usuarionoencontrado");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
				}
			}

		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			String message = mensajes.getString("etiqueta_errorsistemalogin");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
		return outcome;
	}
	
	
	
	/**
	 * Verifica el segundo factor de autenticación
	 */
	public void verificarSegundoFactor() {
		
		String urlBioClient = integracionCatalogos.getCatalogoBioclientVerificar();
		if(urlBioClient==null || urlBioClient.isEmpty()) {
			integracionCatalogos.consultarCatalogos();
		}
		if(urlBioClient==null || urlBioClient.isEmpty()) {
			String message = mensajes.getString("etiqueta_errorsistemalogin")+". Error Servicio Catálogos.";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		} else {
			try {
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				session.setAttribute("usuario", usuarioActual);
				session.setAttribute("catalogo", integracionCatalogos);

				Object[] args = new Object[2];
				
				String sessionId = FacesContext.getCurrentInstance().getExternalContext().getSessionId(true);
				if(sessionId.length()>20) {
					sessionId = sessionId.substring(0, 20);
				}
						
				args[0] = sessionId;
				args[1] = usuarioActual.getId();
				
				urlBioClient = MessageFormat.format(urlBioClient, args);
				PrimeFaces.current().executeScript("PF('dialogoCargando').show();connect('"+urlBioClient+"');");
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	
	/**
	 * Metodo vacío para llamar en caso de login normal (sin CA) por javascript
	 */
	public void dummy() {
		// Metodo vacío para llamar en caso de login normal (sin CA) por javascript
	}
	
	
	
	/**
	 * Inicializa variables de la aplicación luego de superar segundo factor
	 * @return Cadena con la redirección de página
	 */
	public String inicializaVariables() {
		String outcome = "";
		try {
			if(usuarioActual!=null) {
				boolean resultado = true;
				if(usuarioActual.isValidarSegundoFactor()) {
					String sessionId = FacesContext.getCurrentInstance().getExternalContext().getSessionId(true);
					if(sessionId.length()>20) {
						sessionId = sessionId.substring(0, 20);
					}
					resultado = integracionUsuarios.verificacionSegundoFactor(sessionId);
				}
				
				if(resultado) {
					armarMenu();
					outcome = irAOpcion(1);
					StringBuilder stringGrupos = new StringBuilder();
					for(GrupoTO grupo:usuarioActual.getGrupos()) {
						stringGrupos.append(grupo.getNombre()).append(",");
					}
					logger.info("Login Usuario:" +usuarioActual.getNombreUsuario()+" Grupos:"+stringGrupos.toString()+" ORIP:"+usuarioActual.getOficina().getCirculo());
				} else {
					String message = mensajes.getString("etiqueta_usuarionosuperasegundofactor");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
				}
			} else {
				String message = mensajes.getString("etiqueta_usuarionoencontrado");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			String message = mensajes.getString("etiqueta_errorsistemalogin");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
		}
		return outcome;
	}


	
	/**
	 * Establece el menú de la aplicación según los permisos del usuario
	 */
	public void armarMenu() {
		String indexEjecucion = "indexEjecucion.xhtml";
		if(usuarioActual!=null && menu.isEmpty()) {
			menu.add(new MenuTO(1, mensajes.getString("etiqueta_MENU_INICIO2"), mensajes.getString("etiqueta_MENU_INICIO"), "index.xhtml", "inicio", false, 0));
			if(usuarioActual.getRol()==RolesSistema.SUPERADMIN.getId()) {
				menu.add(new MenuTO(2, mensajes.getString("etiqueta_MENU_PARAMETROS2"), mensajes.getString("etiqueta_MENU_PARAMETROS"), paginaIndexParametros, "listaParametros", false, 0));
				menu.add(new MenuTO(21, mensajes.getString("etiqueta_MENU_AGREGARPARAMETRO2"), mensajes.getString("etiqueta_MENU_AGREGARPARAMETRO"), paginaIndexParametros, "detalleParametro", true, 2));
				menu.add(new MenuTO(22, mensajes.getString("etiqueta_MENU_MODIFICARPARAMETRO2"), mensajes.getString("etiqueta_MENU_MODIFICARPARAMETRO"), paginaIndexParametros, "detalleParametro", true, 2));
				
				menu.add(new MenuTO(6, mensajes.getString("etiqueta_MENU_ROLESGRUPOS2"), mensajes.getString("etiqueta_MENU_ROLESGRUPOS"), paginaIndexRolesGrupos, "listaRolesGrupos", false, 0));
				menu.add(new MenuTO(61, mensajes.getString("etiqueta_MENU_ADMINROLESGRUPOS2"), mensajes.getString("etiqueta_MENU_ADMINROLESGRUPOS"), paginaIndexRolesGrupos, "detalleRolesGrupos", true, 2));

				menu.add(new MenuTO(3, mensajes.getString("etiqueta_MENU_ADMINPROCESOS2"), mensajes.getString("etiqueta_MENU_ADMINPROCESOS"), paginaIndexProcesos, "listaProcesos", false, 0));
				menu.add(new MenuTO(31, mensajes.getString("etiqueta_MENU_AGREGARPROCESO2"), mensajes.getString("etiqueta_MENU_AGREGARPROCESO"), paginaIndexProcesos, "detalleProceso", true, 3));
				menu.add(new MenuTO(32, mensajes.getString("etiqueta_MENU_MODIFICARPROCESO2"), mensajes.getString("etiqueta_MENU_MODIFICARPROCESO"), paginaIndexProcesos, "detalleProceso", true, 3));
			}
			
			if(usuarioActual.getRol()==RolesSistema.SUPERADMIN.getId() || usuarioActual.getRol()==RolesSistema.EJECUCION.getId() || 
					usuarioActual.getRol()==RolesSistema.EJECUCION_CONSULTA.getId() || usuarioActual.getRol()==RolesSistema.INICIAR_PROCESO.getId()) {
				menu.add(new MenuTO(4, mensajes.getString("etiqueta_MENU_GESTIONPROCESO2"), mensajes.getString("etiqueta_MENU_GESTIONPROCESO"), indexEjecucion, "listaEjecucion", false, 0));
				menu.add(new MenuTO(41, mensajes.getString("etiqueta_MENU_GESTIONPROCESOEJECUTAR2"), mensajes.getString("etiqueta_MENU_GESTIONPROCESOEJECUTAR"), indexEjecucion, "detalleEjecucion", true, 4));
			}
			if(usuarioActual.getRol()==RolesSistema.SUPERADMIN.getId() || usuarioActual.getRol()==RolesSistema.EJECUCION_CONSULTA.getId() || usuarioActual.getRol()==RolesSistema.CONSULTA.getId()) {
				menu.add(new MenuTO(5, mensajes.getString("etiqueta_MENU_CONSULTAPROCESO2"), mensajes.getString("etiqueta_MENU_CONSULTAPROCESO"), indexEjecucion, "listaEjecucion", false, 0));
				menu.add(new MenuTO(51, mensajes.getString("etiqueta_MENU_GESTIONPROCESOEJECUTAR2"), mensajes.getString("etiqueta_MENU_GESTIONPROCESOEJECUTAR"), indexEjecucion, "detalleEjecucion", true, 5));
			}
		}
	}
	
	
	/**
	 * Permite navegar a una opción del menú
	 * @param opcion Opción del menú hacia la cuál dirigir
	 * @return Cadena con la redirección de página
	 */
	public String irAOpcion(long opcion) {
		String action = "";
		for(MenuTO m:menu) {
			if(m.getId()==opcion) {
				if(m.getIdPadre()==0) {
					breadcrumb.clear();
				}
				
				boolean encontrado = false;
				for(String me:breadcrumb) {
					if(me.equals(m.getNombreAux())) {
						encontrado = true;
						break;
					}
				}
				
				if(!encontrado) {
					breadcrumb.add(m.getNombreAux());
				}
				action = m.getPaginaIndice();
				paginaActual = m.getPaginaInicial();
				menuActual = m;
				break;
			}
		}
		return action;
	}
	
	
	/**
	 * Cierra la sesión actual
	 */
	public void cerrarSesion() {
        try {
	        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	        HttpServletRequest request = (HttpServletRequest) context.getRequest();
	        HttpSession session = request.getSession(false);
	        session.invalidate();
	        request.logout();
	        weblogic.servlet.security.ServletAuthentication.logout(request);
	        context.redirect(integracionCatalogos.getParametrosTO().getParamCawebLogoutUrl());
        } catch (Exception ex) {
        	logger.error(ex);
        }
    }
	
	
	/**
	 * Llamado automáticamente para mantener la sesión activa
	 */
	public void mantenerSesion() {
	    FacesContext context = FacesContext.getCurrentInstance();
	    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	    request.getSession();
	}
	

	/**
	 * Obtiene una lista del menú
	 * @return Lista con el Menú a mostrar
	 */
	public List<MenuTO> getMenuList(){
		List<MenuTO> data = new ArrayList<>();
		for(MenuTO m:menu) {
			if(!m.isOcultar()) {
				data.add(m);
			}
		}
		return data;
	}
	
	
	/**
	 * Obtiene la información de un usuario a partir de su id
	 * @param id Identificador del usuario a buscar
	 * @return Objeto con la información de usuario encontrado
	 */
	public UsuarioTO obtenerUsuario(String id) {
		return integracionUsuarios.obtenerUsuario(id);
	}
	
	
	/**
	 * Obtiene la información de un grupo a partir de su id
	 * @param idGrupo Identificador del grupo a buscar
	 * @param idOrip Identificador de la orip que ejecuta actualmente.  Si no se requiere buscar los usuarios del grupo, puede enviarse null
	 * @return Objeto con la información de grupo encontrado
	 */
	public GrupoTO obtenerGrupo(String idGrupo, String idOrip) {
		return integracionUsuarios.obtenerGrupo(idGrupo, idOrip);
	}
	
	
	/**
	 * Cambia el idioma de sistema entre Ingles y Español
	 */
	public void cambiarIdioma() {
		if(locale.toString().equals("es")) {
			locale = new Locale("en");
		} else {
			locale = new Locale("es");
		}
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
		mensajes = ResourceBundle.getBundle("messages", locale);
        menu = new ArrayList<>();
		armarMenu();
	}

	/**
	 * Obtiene el valor del atributo mensajes
	 * @return El valor del atributo mensajes
	 */
	public ResourceBundle getMensajes() {
		return mensajes;
	}

	/**
	 * Establece el valor del atributo mensajes
	 * @param mensajes con el valor del atributo mensajes a establecer
	 */
	public void setMensajes(ResourceBundle mensajes) {
		this.mensajes = mensajes;
	}

	/**
	 * Obtiene el valor del atributo breadcrumb
	 * @return El valor del atributo breadcrumb
	 */
	public List<String> getBreadcrumb() {
		return breadcrumb;
	}

	/**
	 * Establece el valor del atributo breadcrumb
	 * @param breadcrumb con el valor del atributo breadcrumb a establecer
	 */
	public void setBreadcrumb(List<String> breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	/**
	 * Obtiene el valor del atributo menu
	 * @return El valor del atributo menu
	 */
	public List<MenuTO> getMenu() {
		return menu;
	}

	/**
	 * Establece el valor del atributo menu
	 * @param menu con el valor del atributo menu a establecer
	 */
	public void setMenu(List<MenuTO> menu) {
		this.menu = menu;
	}

	/**
	 * Obtiene el valor del atributo menuActual
	 * @return El valor del atributo menuActual
	 */
	public MenuTO getMenuActual() {
		return menuActual;
	}

	/**
	 * Establece el valor del atributo menuActual
	 * @param menuActual con el valor del atributo menuActual a establecer
	 */
	public void setMenuActual(MenuTO menuActual) {
		this.menuActual = menuActual;
	}

	/**
	 * Obtiene el valor del atributo usuarios
	 * @return El valor del atributo usuarios
	 */
	public List<UsuarioTO> getUsuarios() {
		return usuarios;
	}

	/**
	 * Establece el valor del atributo usuarios
	 * @param usuarios con el valor del atributo usuarios a establecer
	 */
	public void setUsuarios(List<UsuarioTO> usuarios) {
		this.usuarios = usuarios;
	}

	/**
	 * Obtiene el valor del atributo grupos
	 * @return El valor del atributo grupos
	 */
	public List<GrupoTO> getGrupos() {
		return grupos;
	}

	/**
	 * Establece el valor del atributo grupos
	 * @param grupos con el valor del atributo grupos a establecer
	 */
	public void setGrupos(List<GrupoTO> grupos) {
		this.grupos = grupos;
	}

	/**
	 * Obtiene el valor del atributo usuarioActual
	 * @return El valor del atributo usuarioActual
	 */
	public UsuarioTO getUsuarioActual() {
		return usuarioActual;
	}

	/**
	 * Establece el valor del atributo usuarioActual
	 * @param usuarioActual con el valor del atributo usuarioActual a establecer
	 */
	public void setUsuarioActual(UsuarioTO usuarioActual) {
		this.usuarioActual = usuarioActual;
	}

	/**
	 * Obtiene el valor del atributo paginaActual
	 * @return El valor del atributo paginaActual
	 */
	public String getPaginaActual() {
		return paginaActual;
	}

	/**
	 * Establece el valor del atributo paginaActual
	 * @param paginaActual con el valor del atributo paginaActual a establecer
	 */
	public void setPaginaActual(String paginaActual) {
		this.paginaActual = paginaActual;
	}

	/**
	 * Obtiene el valor del atributo usuario
	 * @return El valor del atributo usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * Establece el valor del atributo usuario
	 * @param usuario con el valor del atributo usuario a establecer
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * Obtiene el valor del atributo clave
	 * @return El valor del atributo clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * Establece el valor del atributo clave
	 * @param clave con el valor del atributo clave a establecer
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}

	/**
	 * Obtiene el valor del atributo versionActual
	 * @return El valor del atributo versionActual
	 */
	public String getVersionActual() {
		return versionActual;
	}

	/**
	 * Establece el valor del atributo versionActual
	 * @param versionActual con el valor del atributo versionActual a establecer
	 */
	public void setVersionActual(String versionActual) {
		this.versionActual = versionActual;
	}

	/**
	 * Obtiene el valor del atributo direccionIp
	 * @return El valor del atributo direccionIp
	 */
	public String getDireccionIp() {
		return direccionIp;
	}

	/**
	 * Establece el valor del atributo direccionIp
	 * @param direccionIp con el valor del atributo direccionIp a establecer
	 */
	public void setDireccionIp(String direccionIp) {
		this.direccionIp = direccionIp;
	}

	/**
	 * Obtiene el valor del atributo paginaIndexProcesos
	 * @return El valor del atributo paginaIndexProcesos
	 */
	public String getPaginaIndexProcesos() {
		return paginaIndexProcesos;
	}

	/**
	 * Establece el valor del atributo paginaIndexProcesos
	 * @param paginaIndexProcesos con el valor del atributo paginaIndexProcesos a establecer
	 */
	public void setPaginaIndexProcesos(String paginaIndexProcesos) {
		this.paginaIndexProcesos = paginaIndexProcesos;
	}

	/**
	 * Obtiene el valor del atributo paginaIndexParametros
	 * @return El valor del atributo paginaIndexParametros
	 */
	public String getPaginaIndexParametros() {
		return paginaIndexParametros;
	}

	/**
	 * Establece el valor del atributo paginaIndexParametros
	 * @param paginaIndexParametros con el valor del atributo paginaIndexParametros a establecer
	 */
	public void setPaginaIndexParametros(String paginaIndexParametros) {
		this.paginaIndexParametros = paginaIndexParametros;
	}

	/**
	 * Obtiene el valor del atributo paginaIndexRolesGrupos
	 * @return El valor del atributo paginaIndexRolesGrupos
	 */
	public String getPaginaIndexRolesGrupos() {
		return paginaIndexRolesGrupos;
	}

	/**
	 * Establece el valor del atributo paginaIndexRolesGrupos
	 * @param paginaIndexRolesGrupos con el valor del atributo paginaIndexRolesGrupos a establecer
	 */
	public void setPaginaIndexRolesGrupos(String paginaIndexRolesGrupos) {
		this.paginaIndexRolesGrupos = paginaIndexRolesGrupos;
	}

	/**
	 * Obtiene el valor del atributo locale
	 * @return El valor del atributo locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Establece el valor del atributo locale
	 * @param locale con el valor del atributo locale a establecer
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Obtiene el valor del atributo logger
	 * @return El valor del atributo logger
	 */
	public Logger getLogger() {
		return logger;
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

	/**
	 * Obtiene el valor del atributo integracionUsuarios
	 * @return El valor del atributo integracionUsuarios
	 */
	public IntegracionUsuarios getIntegracionUsuarios() {
		return integracionUsuarios;
	}

	/**
	 * Establece el valor del atributo integracionUsuarios
	 * @param integracionUsuarios con el valor del atributo integracionUsuarios a establecer
	 */
	public void setIntegracionUsuarios(IntegracionUsuarios integracionUsuarios) {
		this.integracionUsuarios = integracionUsuarios;
	}

	/**
	 * Obtiene el valor del atributo mostrarCamposLogin
	 * @return El valor del atributo mostrarCamposLogin
	 */
	public boolean isMostrarCamposLogin() {
		return mostrarCamposLogin;
	}

	/**
	 * Establece el valor del atributo mostrarCamposLogin
	 * @param mostrarCamposLogin con el valor del atributo mostrarCamposLogin a establecer
	 */
	public void setMostrarCamposLogin(boolean mostrarCamposLogin) {
		this.mostrarCamposLogin = mostrarCamposLogin;
	}
	
}

package co.gov.supernotariado.bachue.correspondencia.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.RolesSistema;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RolGrupo;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.RolTO;

/**
 * Managed Bean que permite controlar la asociación entre roles y grupos de la aplicación
 */
@ManagedBean
@ViewScoped
public class RolesGruposBean extends GenericBean {
	/** Logger de impresión de mensajes en los logs del servidor */
	private final Logger logger = Logger.getLogger(RolesGruposBean.class);
	
	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;
	
	/**
	 * Bean de sesión mara manejar la sesión
	 */
	@ManagedProperty("#{gestorSesionBean}")
    private GestorSesionBean gestorSesionBean;

	/**
	 * Lista principal de roles
	 */
	private LazyDataModel<RolTO> list;

	/**
	 * Mensajes de la aplicación
	 */
	private ResourceBundle mensajes = ResourceBundle.getBundle("messages");

	/**
	 * Rol seleccionado
	 */
	private RolTO rolSeleccionado;
	
	/**
	 * Lista de grupos a seleccionar
	 */
	private DualListModel<GrupoTO> grupos;
	
	/**
     * Inicializa variables del bean
     */
    @PostConstruct
    public void init() {
		mensajes = ResourceBundle.getBundle("messages", gestorSesionBean.getLocale());

    	buscar();
    }
    
    /**
     * Buscador para la lista de parámetros
     */
    public void buscar(){
    	gestorSesionBean.irAOpcion(6);
    	rolSeleccionado = null;
    	
    	this.list = new LazyDataModel<RolTO>(){
            private static final long serialVersionUID = 1L;
            
            @Override
            public List<RolTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            	List<RolTO> lista = new ArrayList<>();
            	for(RolesSistema rolesSistema:RolesSistema.values()){
            		RolTO rol = new RolTO();
            		rol.setId(rolesSistema.getId());
            		rol.setNombre(mensajes.getString("etiqueta_ROL_"+rolesSistema.getId()));
            		lista.add(rol);
            	}
    			list.setRowCount(RolesSistema.values().length);
            	return lista;
            }
        };

        
	}
    
    
    /**
     * Modifica los grupos de un Rol
     * @param rol Rol a modificar
     */
    public void modificar(RolTO rol) {
    	if(rol!=null) {
    		rolSeleccionado = rol;
    		List<GrupoTO> gruposAntes = new ArrayList<>(gestorSesionBean.getGrupos());
    		List<GrupoTO> gruposSeleccionados = new ArrayList<>();
    		List<RolGrupo> gruposGuardados = parametrosControl.obtenerGruposPorRol(rol.getId());
    		for(RolGrupo grupo:gruposGuardados) {
    			GrupoTO grupo1 = gestorSesionBean.obtenerGrupo(String.valueOf(grupo.getGrupoId()), null);
    			if(grupo1!=null) {
	    			gruposSeleccionados.add(grupo1);
	    			Iterator<GrupoTO> iter = gruposAntes.iterator();
	    			while(iter.hasNext()) {
		    			GrupoTO grupoAntes = iter.next();
						if(grupo.getGrupoId()==Long.parseLong(grupoAntes.getId())) {
			    			iter.remove();
						}
	    			}
    			}
    		}
    		
    		grupos = new DualListModel<>(gruposAntes, gruposSeleccionados);
    		gestorSesionBean.irAOpcion(61);
    	}
    }
    
    
    /**
     * Guarda los cambios realizados en los grupos de un Rol
     */
    public void guardar() {
    	try {
			List<RolGrupo> gruposGuardados = parametrosControl.obtenerGruposPorRol(rolSeleccionado.getId());
	
			// Elimina los que ya no están
			for(RolGrupo grupoGuardado:gruposGuardados) {
				boolean encontrado = false;
				for(GrupoTO grupo:grupos.getTarget()) {
					if(grupoGuardado.getGrupoId()==Long.parseLong(grupo.getId())) {
						encontrado = true;
						break;
					}
				}
				if(!encontrado) {
					parametrosControl.eliminarRolGrupo(grupoGuardado);
				}
			}
			
			// Guarda los nuevos
			for(GrupoTO grupo:grupos.getTarget()) {
				boolean encontrado = false;
				for(RolGrupo grupoGuardado:gruposGuardados) {
					if(grupoGuardado.getGrupoId()==Long.parseLong(grupo.getId())) {
						encontrado = true;
						break;
					}
				}
				if(!encontrado) {
					parametrosControl.guardarRolGrupo(rolSeleccionado.getId(), Long.parseLong(grupo.getId()), gestorSesionBean.getUsuarioActual().getId(), gestorSesionBean.getDireccionIp());
				}
			}
			String message = mensajes.getString("mensaje_guardarrolesgrupos");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    	} catch(Exception e){
			logger.error("Error guardar", e);
			String message = mensajes.getString("error")+": "+e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    	}
    }
    
    
	/**
	 * Obtiene el Time zone del servidor por defecto
	 * @return Time zone del servidor por defecto
	 */
	public TimeZone getTimeZone(){
		return TimeZone.getDefault();
	}

	/**
	 * Obtiene el valor del atributo gestorSesionBean
	 * @return El valor del atributo gestorSesionBean
	 */
	public GestorSesionBean getGestorSesionBean() {
		return gestorSesionBean;
	}

	/**
	 * Establece el valor del atributo gestorSesionBean
	 * @param gestorSesionBean con el valor del atributo gestorSesionBean a establecer
	 */
	public void setGestorSesionBean(GestorSesionBean gestorSesionBean) {
		this.gestorSesionBean = gestorSesionBean;
	}

	/**
	 * Obtiene el valor del atributo list
	 * @return El valor del atributo list
	 */
	public LazyDataModel<RolTO> getList() {
		return list;
	}

	/**
	 * Establece el valor del atributo list
	 * @param list con el valor del atributo list a establecer
	 */
	public void setList(LazyDataModel<RolTO> list) {
		this.list = list;
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
	 * Obtiene el valor del atributo rolSeleccionado
	 * @return El valor del atributo rolSeleccionado
	 */
	public RolTO getRolSeleccionado() {
		return rolSeleccionado;
	}

	/**
	 * Establece el valor del atributo rolSeleccionado
	 * @param rolSeleccionado con el valor del atributo rolSeleccionado a establecer
	 */
	public void setRolSeleccionado(RolTO rolSeleccionado) {
		this.rolSeleccionado = rolSeleccionado;
	}

	/**
	 * Obtiene el valor del atributo logger
	 * @return El valor del atributo logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Obtiene el valor del atributo grupos
	 * @return El valor del atributo grupos
	 */
	public DualListModel<GrupoTO> getGrupos() {
		return grupos;
	}

	/**
	 * Establece el valor del atributo grupos
	 * @param grupos con el valor del atributo grupos a establecer
	 */
	public void setGrupos(DualListModel<GrupoTO> grupos) {
		this.grupos = grupos;
	}

}

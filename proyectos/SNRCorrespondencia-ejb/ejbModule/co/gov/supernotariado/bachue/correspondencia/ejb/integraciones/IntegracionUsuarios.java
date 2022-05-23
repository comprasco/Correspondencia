package co.gov.supernotariado.bachue.correspondencia.ejb.integraciones;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.corebachue.servicios.ws.BiometriaController;
import co.gov.supernotariado.bachue.corebachue.servicios.ws.BiometriaWS;
import co.gov.supernotariado.bachue.corebachue.servicios.ws.SesionDTO;
import co.gov.supernotariado.bachue.corebachue.servicios.ws.SesionEntradaDTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.OficinaTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolescomponente.v1.RolTypeORC;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolescomponente.v1.TipoEntradaObtenerRolesComponente;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolescomponente.v1.TipoSalidaObtenerRolesComponente;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolescomponente.v1.TipoSalidaObtenerRolesComponente.Roles;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolesusuario.v1.RolTypeORU;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolesusuario.v1.TipoEntradaObtenerRolesUsuario;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolesusuario.v1.TipoSalidaObtenerRolesUsuario;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerusuariosrolorip.v1.TipoEntradaObtenerUsuariosRolOrip;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerusuariosrolorip.v1.TipoSalidaObtenerUsuariosRolOrip;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerusuariosrolorip.v1.TipoSalidaObtenerUsuariosRolOrip.Usuarios;
import https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerusuariosrolorip.v1.TipoUsuario;
import https.www_supernotariado_gov_co.services.bachue.cb.gestionusuarios.v1.SUTCBGestionUsuarios;
import https.www_supernotariado_gov_co.services.bachue.cb.gestionusuarios.v1.SUTCBGestionUsuarios_Service;

/**
 * Integración con servicios de autenticación y autorización de Usuarios
 *
 */
public class IntegracionUsuarios {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(IntegracionUsuarios.class);

	/**
	 * Integración para obtener URLs de servicios web
	 */
	private IntegracionCatalogos integracionCatalogos;

	/**
	 * Lista de usuarios del sistema
	 */
	private List<UsuarioTO> usuarios = new ArrayList<>();
	
	/**
	 * Lista de grupos del sistema
	 */
	private List<GrupoTO> grupos = new ArrayList<>();

	/**
	 * Inicializa listas
	 */
	public IntegracionUsuarios(IntegracionCatalogos integracionCatalogos) {
		this.integracionCatalogos = integracionCatalogos;
		obtenerRolesCA();
	}
	
	
	/**
	 * Verifica la sesión activa del usuario en el servidor después de superar el segundo factor de autenticación
	 * @param sessionId Identificador de la sesión del usuario (tomado de JSF)
	 * @return boolean true si la sesión está activa y false si está inactivo
	 */
	public boolean verificacionSegundoFactor(String sessionId) {
		boolean resultado = false;
		SesionDTO response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoVerificacionDispositivos();
			
			BiometriaController service = new BiometriaController();
			BiometriaWS port = service.getBiometriaWSPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionUsuarios.verificacionSegundoFactor-SessionId:"+ sessionId);

            SesionEntradaDTO entrada = new SesionEntradaDTO();
            entrada.setSesion(sessionId);
            
			response = port.consultarSesion(entrada);

			if(response!=null) {
				logger.info("IntegracionUsuarios.verificacionSegundoFactor-response: ok: "+response.getCodigo()+" resultado "+response.isResultado());
				if(response.isResultado()!=null) {
					resultado = response.isResultado();
				}
			} else {
				logger.error("IntegracionUsuarios.verificacionSegundoFactor-response es null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return resultado;
	}
	
	

	
	/**
	 * Obtiene los roles asociados al componente de correspondencia desde el servicio web de gestión de usuarios
	 * @return Response del servicio
	 */
	public TipoSalidaObtenerRolesComponente obtenerRolesComponente() {
		TipoSalidaObtenerRolesComponente response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoGestionUsuariosURL();
			
			SUTCBGestionUsuarios_Service service = new SUTCBGestionUsuarios_Service();
			SUTCBGestionUsuarios port = service.getSUTCBGestionUsuariosPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionUsuarios.obtenerRolesComponente-Componente:"+ integracionCatalogos.getParametrosTO().getParamCawebComponente());

            TipoEntradaObtenerRolesComponente entrada = new TipoEntradaObtenerRolesComponente();
            entrada.setComponente(integracionCatalogos.getParametrosTO().getParamCawebComponente());
            
			response = port.obtenerRolesComponente(entrada);

			if(response!=null) {
				logger.info("IntegracionUsuarios.obtenerRolesComponente-response: ok "+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionUsuarios.obtenerRolesComponente-response es null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return response;
	}
	
	

	
	
	/**
	 * Obtiene los roles asociados al usuario desde el servicio web de gestión de usuarios
	 * @param loginUsuario Nombre de usuario a validar
	 * @return Lista de Grupos / Roles del usuario
	 */
	public List<GrupoTO> obtenerRolesUsuario(String loginUsuario) {
		List<GrupoTO> listaGrupos = new ArrayList<>();
		TipoSalidaObtenerRolesUsuario response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoGestionUsuariosURL();
			
			SUTCBGestionUsuarios_Service service = new SUTCBGestionUsuarios_Service();
			SUTCBGestionUsuarios port = service.getSUTCBGestionUsuariosPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionUsuarios.obtenerRolesUsuario-Componente:"+ integracionCatalogos.getParametrosTO().getParamCawebComponente()+" - loginUsuario:"+loginUsuario);

            TipoEntradaObtenerRolesUsuario entrada = new TipoEntradaObtenerRolesUsuario();
            entrada.setLoginUsuario(loginUsuario);
            entrada.setComponente(integracionCatalogos.getParametrosTO().getParamCawebComponente());
            
			response = port.obtenerRolesUsuario(entrada);

			if(response!=null) {
				https.www_supernotariado_gov_co.schemas.bachue.cb.gestionusuarios.obtenerrolesusuario.v1.TipoSalidaObtenerRolesUsuario.Roles roles = response.getRoles();
				if(roles!=null) {
					for(RolTypeORU item:roles.getRol()) {
						if(item.getCodigoRol()!=null && !item.getCodigoRol().isEmpty()) {
							listaGrupos.add(generarGrupo(item.getCodigoRol(), item.getNombreRol()));
						}
					}
				}
				logger.info("IntegracionUsuarios.obtenerRolesUsuario-response: ok "+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionUsuarios.obtenerRolesUsuario-response es null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return listaGrupos;
	}
	
	
	
	/**
	 * Obtiene los roles asociados al usuario desde el servicio web de gestión de usuarios
	 * @param idOrip Identificador de la orip
	 * param @idRol Identificador del rol a buscar 
	 * @return Response del servicio
	 */
	public List<UsuarioTO> obtenerUsuariosRolOrip(String idOrip, String idRol) {
		OficinaTO oficina = new OficinaTO();
		oficina.setCirculo(idOrip);

		List<UsuarioTO> listaUsuarios = new ArrayList<>();
		TipoSalidaObtenerUsuariosRolOrip response = null;
		try {
			String endpoint = integracionCatalogos.getCatalogoGestionUsuariosURL();
			
			SUTCBGestionUsuarios_Service service = new SUTCBGestionUsuarios_Service();
			SUTCBGestionUsuarios port = service.getSUTCBGestionUsuariosPort();
	        
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            logger.info("IntegracionUsuarios.obtenerUsuariosRolOrip-idOrip:"+ idOrip+" - idRol:"+idRol);

            TipoEntradaObtenerUsuariosRolOrip entrada = new TipoEntradaObtenerUsuariosRolOrip();
            entrada.setCodigoCirculo(idOrip);
            entrada.setCodigoRol(idRol);
            
			response = port.obtenerUsuariosRolOrip(entrada);

			if(response!=null && response.getUsuarios()!=null) {
				Usuarios objetoUsuario = response.getUsuarios();
				if(objetoUsuario!=null && objetoUsuario.getUsuario()!=null) {
					for(TipoUsuario tipoUsuario:objetoUsuario.getUsuario()) {
						String nombre = agregarNombreUsuario(tipoUsuario.getPrimerNombre());
						nombre += agregarNombreUsuario(tipoUsuario.getSegundoNombre());
						nombre += agregarNombreUsuario(tipoUsuario.getPrimerApellido());
						nombre += agregarNombreUsuario(tipoUsuario.getSegundoApellido());
						
						if(!nombre.isEmpty()) {
							UsuarioTO usuario = generarUsuario(tipoUsuario.getIdUsuario(), nombre, tipoUsuario.getIdUsuario(), Integer.valueOf(idRol), oficina, true);
							listaUsuarios.add(usuario);
							
						}
					}
				}
				logger.info("IntegracionUsuarios.obtenerRolesUsuario-response: ok "+response.getDescripcionMensaje());
			} else {
				logger.error("IntegracionUsuarios.obtenerRolesUsuario-response es null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return listaUsuarios;
	}
	
	/**
	 * Agrega un trozo del nombre de usuario
	 * @param nombre Nombre del usuario (primero, segundo, apellido)
	 * @return String vacío si el nombre es null o el nombre ingresado si viene lleno
	 */
	private String agregarNombreUsuario(String nombre) {
		String data = "";
		if(nombre!=null && !nombre.isEmpty()) {
			data = nombre + " ";
		}
		return data;
	}

	
	/**
	 * Obtiene la oficina a la cual está asociado el usuario
	 * @param idUsuario Identificador del usuario a buscar
	 * @return Oficina asociada al usuario
	 */
	public OficinaTO obtenerOficinaUsuario(String idUsuario) {
		OficinaTO oficina = null;
		for(UsuarioTO usuario:usuarios) {
			if(usuario.getId().equals(idUsuario)) {
				oficina = usuario.getOficina();
			}
		}
		return oficina;
	}
	
	
	/**
	 * Obtiene la información de un usuario a partir de su id
	 * @param id Identificador del usuario a buscar
	 * @return Usuario encontrado
	 */
	public UsuarioTO obtenerUsuario(String id) {
		UsuarioTO usu = null;
		for(UsuarioTO user:usuarios) {
			if(user.getId().equals(id)) {
				usu = user;
				break;
			}
		}
		
		// Usuarios reales, id es igual al nombre de usuario.  No hay info de nombre real de usuario
		if(usu==null) {
			usu = generarUsuario(id, id, id, 0, null, true);
			// registra el usuario en cache de usuarios
			usuarios.add(usu);
		}

		if(usu.getGrupos()==null || usu.getGrupos().isEmpty()) {
			usu.setGrupos(obtenerRolesUsuario(usu.getId()));
		}
		return usu;
	}
	
	/**
	 * Obtiene la información de un grupo a partir de su id
	 * @param idGrupo Identificador del grupo a buscar
	 * @param idOrip Identificador de la orip que ejecuta actualmente.  Si no se requiere buscar los usuarios del grupo, puede enviarse null
	 * @return Objeto con la información de grupo encontrado
	 */
	public GrupoTO obtenerGrupo(String idGrupo, String idOrip) {
		// Acá ya están los grupos reales, los grupos reales no tienen asignados sus usuarios
		GrupoTO grupo = null;
		for(GrupoTO g:grupos) {
			if(g.getId().equals(idGrupo)) {
				grupo = g;
				if((grupo.getUsuarios()==null || grupo.getUsuarios().isEmpty()) && idOrip!=null && !idOrip.isEmpty()) {
					grupo.setUsuarios(obtenerUsuariosRolOrip(idOrip, idGrupo));
				}
				break;
			}
		}
		return grupo;
	}



	/**
	 * Genera la información de un usuario
	 * @param id Identificador del usuario
	 * @param nombre Nombre del usuario
	 * @param nombreUsuario Nombre (alias) del usuario
	 * @param rol Identificador del rol del usuario
	 * @param oficina Oficina asociada al usuario
	 * @return Usuario generado
	 */
	public UsuarioTO generarUsuario(String id, String nombre, String nombreUsuario, int rol, OficinaTO oficina, boolean validarSegundoFactor) {
		UsuarioTO usu = new UsuarioTO();
		usu.setId(id);
		usu.setNombre(nombre);
		usu.setNombreUsuario(nombreUsuario);
		usu.setRol(rol);
		usu.setOficina(oficina);
		usu.setValidarSegundoFactor(validarSegundoFactor);
		return usu;
	}
	
	
	/**
	 * Genera la información de un grupo
	 * @param id Identificador del grupo
	 * @param nombre Nombre del grupo
	 * @return Grupo generado
	 */
	public GrupoTO generarGrupo(String id, String nombre) {
		GrupoTO grupo = new GrupoTO();
		grupo.setId(id);
		grupo.setNombre(nombre);
		return grupo;
	}

	
	/**
	 * Obtiene los roles desde el servicio web de CA
	 */
	public void obtenerRolesCA() {
		TipoSalidaObtenerRolesComponente response = obtenerRolesComponente();
		if(response!=null) {
			Roles roles = response.getRoles();
			if(roles!=null) {
				for(RolTypeORC item:roles.getRol()) {
					grupos.add(generarGrupo(item.getCodigoRol(), item.getNombreRol()));
				}
			}
		}
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

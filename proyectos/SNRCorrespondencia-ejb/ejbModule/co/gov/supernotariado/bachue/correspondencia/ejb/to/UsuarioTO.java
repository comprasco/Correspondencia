package co.gov.supernotariado.bachue.correspondencia.ejb.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar datos de los usuarios de la SNR
 *
 */
public class UsuarioTO implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador del usuario
	 */
	private String id;
	
	/**
	 * Celular del usuario
	 */
	private String celular;
	
	/**
	 * Nombre de usuario en el sistema
	 */
	private String nombreUsuario;

	/**
	 * Nombre de la persona
	 */
	private String nombre;

	/**
	 * Correo electrónico
	 */
	private String correoElectronico;
	
	/**
	 * Rol del usuario según RolesSistema.java
	 */
	private int rol;
	
	/**
	 * Indica si validar segundo factor de autenticación
	 */
	private boolean validarSegundoFactor = true;

	/**
	 * Oficina a la que pertenece el usuario
	 */
	private OficinaTO oficina;
	
	/**
	 * Grupos a los que pertenece el usuario
	 */
	private List<GrupoTO> grupos = new ArrayList<>();

	/**
	 * Obtiene el valor del atributo id
	 * @return El valor del atributo id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Establece el valor del atributo id
	 * @param id con el valor del atributo id a establecer
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Obtiene el valor del atributo celular
	 * @return El valor del atributo celular
	 */
	public String getCelular() {
		return celular;
	}

	/**
	 * Establece el valor del atributo celular
	 * @param celular con el valor del atributo celular a establecer
	 */
	public void setCelular(String celular) {
		this.celular = celular;
	}

	/**
	 * Obtiene el valor del atributo nombreUsuario
	 * @return El valor del atributo nombreUsuario
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	/**
	 * Establece el valor del atributo nombreUsuario
	 * @param nombreUsuario con el valor del atributo nombreUsuario a establecer
	 */
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	/**
	 * Obtiene el valor del atributo nombre
	 * @return El valor del atributo nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el valor del atributo nombre
	 * @param nombre con el valor del atributo nombre a establecer
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el valor del atributo correoElectronico
	 * @return El valor del atributo correoElectronico
	 */
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	/**
	 * Establece el valor del atributo correoElectronico
	 * @param correoElectronico con el valor del atributo correoElectronico a establecer
	 */
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	/**
	 * Obtiene el valor del atributo rol
	 * @return El valor del atributo rol
	 */
	public int getRol() {
		return rol;
	}

	/**
	 * Establece el valor del atributo rol
	 * @param rol con el valor del atributo rol a establecer
	 */
	public void setRol(int rol) {
		this.rol = rol;
	}

	/**
	 * Obtiene el valor del atributo oficina
	 * @return El valor del atributo oficina
	 */
	public OficinaTO getOficina() {
		return oficina;
	}

	/**
	 * Establece el valor del atributo oficina
	 * @param oficina con el valor del atributo oficina a establecer
	 */
	public void setOficina(OficinaTO oficina) {
		this.oficina = oficina;
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
	 * Obtiene el valor del atributo validarSegundoFactor
	 * @return El valor del atributo validarSegundoFactor
	 */
	public boolean isValidarSegundoFactor() {
		return validarSegundoFactor;
	}

	/**
	 * Establece el valor del atributo validarSegundoFactor
	 * @param validarSegundoFactor con el valor del atributo validarSegundoFactor a establecer
	 */
	public void setValidarSegundoFactor(boolean validarSegundoFactor) {
		this.validarSegundoFactor = validarSegundoFactor;
	}


}

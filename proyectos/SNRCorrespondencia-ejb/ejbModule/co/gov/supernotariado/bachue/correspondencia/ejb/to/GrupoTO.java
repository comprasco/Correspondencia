package co.gov.supernotariado.bachue.correspondencia.ejb.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja la información de los grupos de la aplicación
 *
 */
public class GrupoTO implements Serializable{
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador del grupo
	 */
	private String id;
	
	/**
	 * Nombre del grupo
	 */
	private String nombre;
	
	/**
	 * Listado de usuarios del grupo
	 */
	private List<UsuarioTO> usuarios = new ArrayList<>();

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

	
}

package co.gov.supernotariado.bachue.correspondencia.ejb.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RolGrupo;

/**
 * Clase que maneja la información de los roles de la aplicación
 *
 */
public class RolTO implements Serializable{
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador del rol
	 */
	private long id;
	
	/**
	 * Nombre del rol
	 */
	private String nombre;
	
	/**
	 * Listado de grupos asociados al rol
	 */
	private List<RolGrupo> grupos = new ArrayList<>();

	/**
	 * Obtiene el valor del atributo id
	 * @return El valor del atributo id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el valor del atributo id
	 * @param id con el valor del atributo id a establecer
	 */
	public void setId(long id) {
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
	 * Obtiene el valor del atributo grupos
	 * @return El valor del atributo grupos
	 */
	public List<RolGrupo> getGrupos() {
		return grupos;
	}

	/**
	 * Establece el valor del atributo grupos
	 * @param grupos con el valor del atributo grupos a establecer
	 */
	public void setGrupos(List<RolGrupo> grupos) {
		this.grupos = grupos;
	}

	
}

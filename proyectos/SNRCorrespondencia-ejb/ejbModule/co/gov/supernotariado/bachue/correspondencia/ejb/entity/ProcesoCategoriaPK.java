package co.gov.supernotariado.bachue.correspondencia.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clase para manejar la llave primaria de la tabla SDB_PNG_CORRESPONDENCIA_PROCESO_CATEGORIA
 *
 */
@Embeddable
public class ProcesoCategoriaPK implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identificador del proceso asociado
	 */
	@Column(name="ID_PROCESO")
	private long procesoId;

	/**
	 * Identificador de la categoría asociada
	 */
	@Column(name="ID_CATEGORIA")
	private long categoriaId;

	/**
	 * Obtiene el valor del atributo procesoId
	 * @return El valor del atributo procesoId
	 */
	public long getProcesoId() {
		return procesoId;
	}

	/**
	 * Establece el valor del atributo procesoId
	 * @param procesoId con el valor del atributo procesoId a establecer
	 */
	public void setProcesoId(long procesoId) {
		this.procesoId = procesoId;
	}

	/**
	 * Obtiene el valor del atributo categoriaId
	 * @return El valor del atributo categoriaId
	 */
	public long getCategoriaId() {
		return categoriaId;
	}

	/**
	 * Establece el valor del atributo categoriaId
	 * @param categoriaId con el valor del atributo categoriaId a establecer
	 */
	public void setCategoriaId(long categoriaId) {
		this.categoriaId = categoriaId;
	}

}

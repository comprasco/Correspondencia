package co.gov.supernotariado.bachue.correspondencia.ejb.json;

import java.io.Serializable;

/**
 * Clase usada para guardar datos adicionales del proceso.
 * Será guardado en un campo tipo JSON
 */
public class JsonProcesoData implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Atributo para guardar información temporalmente en la configuración del proceso */
	private transient String temporal;

	/**
	 * Obtiene el valor del atributo temporal
	 * @return El valor del atributo temporal
	 */
	public String getTemporal() {
		return temporal;
	}

	/**
	 * Establece el valor del atributo temporal
	 * @param temporal con el valor del atributo temporal a establecer
	 */
	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}

}

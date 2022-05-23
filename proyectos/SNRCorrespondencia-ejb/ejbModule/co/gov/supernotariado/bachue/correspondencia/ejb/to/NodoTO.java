package co.gov.supernotariado.bachue.correspondencia.ejb.to;

import java.io.Serializable;

/**
 * Clase para manejar datos de listas paramétricas
 *
 */
public class NodoTO implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador del nodo
	 */
	private String id;
	
	/**
	 * Texto del nodo
	 */
	private String text;

	/**
	 * Valor clave del nodo
	 */
	private String clave;

	/**
	 * Indica si el elemento está seleccionado
	 */
	private boolean selected = false;

	/** Auxiliar para marcar campos tipo clave valor */
	private boolean keyvalueentrada = false;
	
	/** Auxiliar para marcar el campo como valor null para validarlo como requerido en los campos tipo lista */
	private boolean markedNull = false;

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
	 * Obtiene el valor del atributo text
	 * @return El valor del atributo text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Establece el valor del atributo text
	 * @param text con el valor del atributo text a establecer
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Obtiene el valor del atributo selected
	 * @return El valor del atributo selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Establece el valor del atributo selected
	 * @param selected con el valor del atributo selected a establecer
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Obtiene el valor del atributo keyvalueentrada
	 * @return El valor del atributo keyvalueentrada
	 */
	public boolean isKeyvalueentrada() {
		return keyvalueentrada;
	}

	/**
	 * Establece el valor del atributo keyvalueentrada
	 * @param keyvalueentrada con el valor del atributo keyvalueentrada a establecer
	 */
	public void setKeyvalueentrada(boolean keyvalueentrada) {
		this.keyvalueentrada = keyvalueentrada;
	}

	/**
	 * Obtiene el valor del atributo markedNull
	 * @return El valor del atributo markedNull
	 */
	public boolean isMarkedNull() {
		return markedNull;
	}

	/**
	 * Establece el valor del atributo markedNull
	 * @param markedNull con el valor del atributo markedNull a establecer
	 */
	public void setMarkedNull(boolean markedNull) {
		this.markedNull = markedNull;
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


}

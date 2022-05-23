package co.gov.supernotariado.bachue.correspondencia.to;

import java.io.Serializable;

/**
 * Clase Transfer Object para manejar los datos a mostrar en los componentes de autocompletar
 */
public class SugerenciaTO implements Serializable {
	/**
	 * Identificador serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Valor de la sugerencia
	 */
	private String value;
	/**
	 * Label a mostrar para la sugerencia
	 */
	private String label;

	/**
	 * Constructor
	 * @param value Valor de la sugerencia
	 * @param label Label a mostrar para la sugerencia
	 */
	public SugerenciaTO(String value, String label) {
		this.value = value;
		this.label = label;
	}

	/**
	 * Obtiene el valor del atributo value
	 * @return El valor del atributo value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Establece el valor del atributo value
	 * @param value con el valor del atributo value a establecer
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor del atributo label
	 * @return El valor del atributo label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Establece el valor del atributo label
	 * @param label con el valor del atributo label a establecer
	 */
	public void setLabel(String label) {
		this.label = label;
	}


}

package co.gov.supernotariado.bachue.correspondencia.ejb.json;

import java.io.Serializable;

/**
 * Guarda en formato json los datos adicionales de los tipos de parametros tipo opción (listas predefinidas)
 */
public class JsonDatosOpcionesParametro implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Valor autonumerico identificador del dato adicional */
	private int id;

	/** Nombre que se le da al dato de la opción */
	private String nombreDato;
	
	/** Tipo de dato (alfanumérico, numérico, fecha). Según TiposDatosCampos.java */
	private String tipoDato;

	/** Longitud de dato en campos abiertos */
	private String longitud;

	/** Indica si está activo o se eliminó este dato */
	private boolean activo;

	/** Valor temporal que se le puede dar a este dato */ 
	private transient String valor;

	/**
	 * Obtiene el valor del atributo id
	 * @return El valor del atributo id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Establece el valor del atributo id
	 * @param id con el valor del atributo id a establecer
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Obtiene el valor del atributo nombreDato
	 * @return El valor del atributo nombreDato
	 */
	public String getNombreDato() {
		return nombreDato;
	}

	/**
	 * Establece el valor del atributo nombreDato
	 * @param nombreDato con el valor del atributo nombreDato a establecer
	 */
	public void setNombreDato(String nombreDato) {
		this.nombreDato = nombreDato;
	}

	/**
	 * Obtiene el valor del atributo tipoDato
	 * @return El valor del atributo tipoDato
	 */
	public String getTipoDato() {
		return tipoDato;
	}

	/**
	 * Establece el valor del atributo tipoDato
	 * @param tipoDato con el valor del atributo tipoDato a establecer
	 */
	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}

	/**
	 * Obtiene el valor del atributo activo
	 * @return El valor del atributo activo
	 */
	public boolean isActivo() {
		return activo;
	}

	/**
	 * Establece el valor del atributo activo
	 * @param activo con el valor del atributo activo a establecer
	 */
	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/**
	 * Obtiene el valor del atributo valor
	 * @return El valor del atributo valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * Establece el valor del atributo valor
	 * @param valor con el valor del atributo valor a establecer
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * Obtiene el valor del atributo longitud
	 * @return El valor del atributo longitud
	 */
	public String getLongitud() {
		return longitud;
	}

	/**
	 * Establece el valor del atributo longitud
	 * @param longitud con el valor del atributo longitud a establecer
	 */
	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

}

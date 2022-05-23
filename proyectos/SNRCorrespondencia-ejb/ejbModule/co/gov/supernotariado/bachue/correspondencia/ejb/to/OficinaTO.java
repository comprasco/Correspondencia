package co.gov.supernotariado.bachue.correspondencia.ejb.to;

import java.io.Serializable;

/**
 * Clase para manejar datos de las oficinas de SNR - ORIP
 *
 */
public class OficinaTO implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Código del circulo de la oficina
	 */
	private String circulo;
	
	/**
	 * Nombre de la oficina
	 */
	private String nombre;

	/**
	 * Tipo de oficina
	 */
	private String tipoOficina;

	/**
	 * Regional de la oficina
	 */
	private String regional;

	/**
	 * Obtiene el valor del atributo circulo
	 * @return El valor del atributo circulo
	 */
	public String getCirculo() {
		return circulo;
	}

	/**
	 * Establece el valor del atributo circulo
	 * @param circulo con el valor del atributo circulo a establecer
	 */
	public void setCirculo(String circulo) {
		this.circulo = circulo;
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
	 * Obtiene el valor del atributo tipoOficina
	 * @return El valor del atributo tipoOficina
	 */
	public String getTipoOficina() {
		return tipoOficina;
	}

	/**
	 * Establece el valor del atributo tipoOficina
	 * @param tipoOficina con el valor del atributo tipoOficina a establecer
	 */
	public void setTipoOficina(String tipoOficina) {
		this.tipoOficina = tipoOficina;
	}

	/**
	 * Obtiene el valor del atributo regional
	 * @return El valor del atributo regional
	 */
	public String getRegional() {
		return regional;
	}

	/**
	 * Establece el valor del atributo regional
	 * @param regional con el valor del atributo regional a establecer
	 */
	public void setRegional(String regional) {
		this.regional = regional;
	}

	
}

package co.gov.supernotariado.bachue.correspondencia.ejb.json;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Guarda valores json para las entradas tipo opción múltiple
 */
public class JsonOpcion implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/**
	 * Valor autonumerico identificador de la opción
	 */
	private int id;
	
	/**
	 * Nombre de la opción
	 */
	private String nombre;

	/**
	 * Valor clave de la opción, no es requerido
	 */
	private String clave;

	/**
	 * Indica si está activo o se eliminó esta opción
	 */
	private boolean activo;

	/**
	 * Indica si el elemento debe ser manejado como null para que sea validado como requerido si se selecciona esta opción
	 */
	private boolean valorNulo;
	
	/**
	 * En el caso de combos anidados guarda el valor de padre
	 */
	private String opcionPadreId;
	
	/**
	 * Datos adicionales de los parametros
	 */
	private Map<Integer, String> datosAdicionales; 
	
	/**
	 * Datos adicionales de los parametros temporal
	 */
	private transient List<JsonDatosOpcionesParametro> datosAdicionalesTmp; 

	/**
	 * Temporal indica si es requerida la opción
	 */
	private transient boolean requerido = false;

	/**
	 * Temporal indica si está seleccionada la opción
	 */
	private transient boolean seleccionado = false;

	/**
	 * Temporal indica si la opción es nueva
	 */
	private transient boolean nuevo = false;

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
	 * Obtiene el valor del atributo valorNulo
	 * @return El valor del atributo valorNulo
	 */
	public boolean isValorNulo() {
		return valorNulo;
	}

	/**
	 * Establece el valor del atributo valorNulo
	 * @param valorNulo con el valor del atributo valorNulo a establecer
	 */
	public void setValorNulo(boolean valorNulo) {
		this.valorNulo = valorNulo;
	}

	/**
	 * Obtiene el valor del atributo opcionPadreId
	 * @return El valor del atributo opcionPadreId
	 */
	public String getOpcionPadreId() {
		return opcionPadreId;
	}

	/**
	 * Establece el valor del atributo opcionPadreId
	 * @param opcionPadreId con el valor del atributo opcionPadreId a establecer
	 */
	public void setOpcionPadreId(String opcionPadreId) {
		this.opcionPadreId = opcionPadreId;
	}

	/**
	 * Obtiene el valor del atributo datosAdicionales
	 * @return El valor del atributo datosAdicionales
	 */
	public Map<Integer, String> getDatosAdicionales() {
		return datosAdicionales;
	}

	/**
	 * Establece el valor del atributo datosAdicionales
	 * @param datosAdicionales con el valor del atributo datosAdicionales a establecer
	 */
	public void setDatosAdicionales(Map<Integer, String> datosAdicionales) {
		this.datosAdicionales = datosAdicionales;
	}

	/**
	 * Obtiene el valor del atributo datosAdicionalesTmp
	 * @return El valor del atributo datosAdicionalesTmp
	 */
	public List<JsonDatosOpcionesParametro> getDatosAdicionalesTmp() {
		return datosAdicionalesTmp;
	}

	/**
	 * Establece el valor del atributo datosAdicionalesTmp
	 * @param datosAdicionalesTmp con el valor del atributo datosAdicionalesTmp a establecer
	 */
	public void setDatosAdicionalesTmp(List<JsonDatosOpcionesParametro> datosAdicionalesTmp) {
		this.datosAdicionalesTmp = datosAdicionalesTmp;
	}

	/**
	 * Obtiene el valor del atributo requerido
	 * @return El valor del atributo requerido
	 */
	public boolean isRequerido() {
		return requerido;
	}

	/**
	 * Establece el valor del atributo requerido
	 * @param requerido con el valor del atributo requerido a establecer
	 */
	public void setRequerido(boolean requerido) {
		this.requerido = requerido;
	}

	/**
	 * Obtiene el valor del atributo seleccionado
	 * @return El valor del atributo seleccionado
	 */
	public boolean isSeleccionado() {
		return seleccionado;
	}

	/**
	 * Establece el valor del atributo seleccionado
	 * @param seleccionado con el valor del atributo seleccionado a establecer
	 */
	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	/**
	 * Obtiene el valor del atributo nuevo
	 * @return El valor del atributo nuevo
	 */
	public boolean isNuevo() {
		return nuevo;
	}

	/**
	 * Establece el valor del atributo nuevo
	 * @param nuevo con el valor del atributo nuevo a establecer
	 */
	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}



}

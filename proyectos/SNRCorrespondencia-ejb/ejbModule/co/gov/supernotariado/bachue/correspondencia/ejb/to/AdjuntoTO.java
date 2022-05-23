package co.gov.supernotariado.bachue.correspondencia.ejb.to;

/**
 * Clase para definir un adjunto de una notificacion
 *
 */
public class AdjuntoTO {
    /**
     * bytes para adjuntar
     */
    private byte[] archivo;

    /**
     * Nombre del archivo a adjuntar
     */
    private String nombre;

    /**
     * Constructor
     */
    public AdjuntoTO() {
    }

    /**
     * Constructor con parámetros
     * @param archivo Bytes del archivo
     * @param nombre Nombre del archivo
     */
    public AdjuntoTO(byte[] archivo, String nombre) {
    	this.archivo = archivo;
    	this.nombre = nombre;
    }

	/**
	 * Obtiene el valor del atributo archivo
	 * @return El valor del atributo archivo
	 */
	public byte[] getArchivo() {
		return archivo;
	}

	/**
	 * Establece el valor del atributo archivo
	 * @param archivo con el valor del atributo archivo a establecer
	 */
	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
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

}

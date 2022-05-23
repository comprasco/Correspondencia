package co.gov.supernotariado.bachue.correspondencia.to;

/**
 * Clase Transfer Object para manejar los datos del menú a mostrar en la pantalla
 */
public class MenuTO {
	/**
	 * ID de la opción del Menú
	 */
	private long id;
	/**
	 * Nombre de la opción
	 */
	private String nombre;
	/**
	 * Nombre auxiliar o secundario
	 */
	private String nombreAux;
	/**
	 * Página indide de la opción (index.xhtml)
	 */
	private String paginaIndice;
	/**
	 * Página inicial de la opción
	 */
	private String paginaInicial;
	/**
	 * Indica si está oculta la opción
	 */
	private boolean ocultar;
	/**
	 * Identificador de la opción padre de esta opción
	 */
	private long idPadre;
	
	/**
	 * Constructor
	 * @param id ID de la opción del Menú
	 * @param nombre Nombre de la opción
	 * @param nombreAux Nombre auxiliar o secundario
	 * @param paginaIndice Página indide de la opción
	 * @param paginaInicial Página inicial de la opción
	 * @param ocultar Indica si está oculta la opción
	 * @param idPadre Identificador de la opción padre de esta opción
	 */
	public MenuTO(long id, String nombre, String nombreAux, String paginaIndice, String paginaInicial, boolean ocultar, long idPadre) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.nombreAux = nombreAux;
		this.paginaIndice = paginaIndice;
		this.paginaInicial = paginaInicial;
		this.ocultar = ocultar;
		this.idPadre = idPadre;
	}

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
	 * Obtiene el valor del atributo nombreAux
	 * @return El valor del atributo nombreAux
	 */
	public String getNombreAux() {
		return nombreAux;
	}

	/**
	 * Establece el valor del atributo nombreAux
	 * @param nombreAux con el valor del atributo nombreAux a establecer
	 */
	public void setNombreAux(String nombreAux) {
		this.nombreAux = nombreAux;
	}

	/**
	 * Obtiene el valor del atributo paginaIndice
	 * @return El valor del atributo paginaIndice
	 */
	public String getPaginaIndice() {
		return paginaIndice;
	}

	/**
	 * Establece el valor del atributo paginaIndice
	 * @param paginaIndice con el valor del atributo paginaIndice a establecer
	 */
	public void setPaginaIndice(String paginaIndice) {
		this.paginaIndice = paginaIndice;
	}

	/**
	 * Obtiene el valor del atributo paginaInicial
	 * @return El valor del atributo paginaInicial
	 */
	public String getPaginaInicial() {
		return paginaInicial;
	}

	/**
	 * Establece el valor del atributo paginaInicial
	 * @param paginaInicial con el valor del atributo paginaInicial a establecer
	 */
	public void setPaginaInicial(String paginaInicial) {
		this.paginaInicial = paginaInicial;
	}

	/**
	 * Obtiene el valor del atributo ocultar
	 * @return El valor del atributo ocultar
	 */
	public boolean isOcultar() {
		return ocultar;
	}

	/**
	 * Establece el valor del atributo ocultar
	 * @param ocultar con el valor del atributo ocultar a establecer
	 */
	public void setOcultar(boolean ocultar) {
		this.ocultar = ocultar;
	}

	/**
	 * Obtiene el valor del atributo idPadre
	 * @return El valor del atributo idPadre
	 */
	public long getIdPadre() {
		return idPadre;
	}

	/**
	 * Establece el valor del atributo idPadre
	 * @param idPadre con el valor del atributo idPadre a establecer
	 */
	public void setIdPadre(long idPadre) {
		this.idPadre = idPadre;
	}

}

package co.gov.supernotariado.bachue.correspondencia.ejb.api;

/**
 * Enumeración de los roles del sistema
 */
public enum RolesSistema {
	
	/** Super administrador puede cambiar cualquier parámetro y dato del sistema  (Grupo Administradores) */
	SUPERADMIN (1,"Super Administrador"),
	/** Administrador puede ver procesos de otros usuarios y ejecutarlos (no usado en correspondencia) */
	ADMIN (2,"Administrador"),
	/** Puede ejecutar procesos y consultar procesos (Grupo Coordinadores Distribución) */
	EJECUCION_CONSULTA (3, "Ejecución y Consulta"),
	/** Puede ejecutar procesos (Grupos Digitalizadores, Jefes de Área, Funcionarios, Envíos) */
	EJECUCION (4, "Ejecución"),
	/** Puede solamente iniciar procesos (Grupo Radicación) */
	INICIAR_PROCESO (5, "Solo Iniciar Proceso"),
	/** Puede solamente consultar (Grupo Consultas) */
	CONSULTA (6, "Consulta");
	
	/** Id del rol */
	private int id;
	/** Nombre del rol */
	private String nombre;
	
	/**
	 * Constructor
	 * @param id Identificador del rol
	 * @param nombre Nombre del rol
	 */
	private RolesSistema(int id, String nombre) {
		 this.id = id;
		 this.nombre =nombre;
	}

	/**
	 * Obtiene el nombre del rol por id
	 * @param id Identificador del rol
	 * @return Cadena con el nombre del rol
	 */
	public static String getNombreRolSistemaById(int id) {
		String data = "";
		if (id == RolesSistema.SUPERADMIN.getId()) {
			data = RolesSistema.SUPERADMIN.getNombre();
		} else if(id == RolesSistema.ADMIN.getId()) {
			data = RolesSistema.ADMIN.getNombre();
		} else if(id == RolesSistema.EJECUCION_CONSULTA.getId()) {
			data = RolesSistema.EJECUCION_CONSULTA.getNombre();
		} else if(id == RolesSistema.EJECUCION.getId()) {
			data = RolesSistema.EJECUCION.getNombre();
		} else if(id == RolesSistema.CONSULTA.getId()) {
			data = RolesSistema.CONSULTA.getNombre();
		}
		return data;
	}

	/**
	 * Obtiene el valor del atributo id
	 * @return El valor del atributo id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Obtiene el valor del atributo nombre
	 * @return El valor del atributo nombre
	 */
	public String getNombre() {
		return nombre;
	}
	
}

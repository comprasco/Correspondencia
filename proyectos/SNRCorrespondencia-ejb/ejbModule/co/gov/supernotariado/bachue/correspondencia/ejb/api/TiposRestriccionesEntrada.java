package co.gov.supernotariado.bachue.correspondencia.ejb.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeración de los tipos de restricciones que manejan algunas entradas de formulario
 */
public enum TiposRestriccionesEntrada {
    /**
     * Mayor a un valor dado 
     */
    MAYOR, 
    /**
     * Menor que un valor dado
     */
    MENOR,
    /**
     * Mayor o igual a un valor dado
     */
    MAYOR_O_IGUAL, 
    /**
     * Menor o igual que un valor dado
     */
    MENOR_O_IGUAL;
	
	/**
	 * Obtiene un listado de los tipos de datos que tienen validación de longitud
	 * @return Lista de tipos de datos
	 */
	public static List<String> obtenerTodos(){
		List<String> lista = new ArrayList<>();
		TiposRestriccionesEntrada[] tipos = TiposRestriccionesEntrada.values();
		for(TiposRestriccionesEntrada tipo:tipos) {
			lista.add(tipo.name());
		}
		return lista;
	}

}

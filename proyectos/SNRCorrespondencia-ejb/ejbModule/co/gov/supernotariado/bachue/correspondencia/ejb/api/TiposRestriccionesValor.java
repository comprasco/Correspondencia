package co.gov.supernotariado.bachue.correspondencia.ejb.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeración de los tipos de dato que se adminten en los datos adicionales de Parámetros
 */
public enum TiposRestriccionesValor {
    /**
     * Fecha actual 
     */
    SYSDATE;
	
	/**
	 * Obtiene un listado de los tipos de datos que tienen validación de longitud
	 * @return Lista con los tipos de datos
	 */
	public static List<String> obtenerValoresRestricciones(String tipoEntrada){
		List<String> lista = new ArrayList<>();
		if(tipoEntrada.equals(TiposEntrada.DATE.name())) {
			lista.add(SYSDATE.name());
		}
		return lista;
	}

}

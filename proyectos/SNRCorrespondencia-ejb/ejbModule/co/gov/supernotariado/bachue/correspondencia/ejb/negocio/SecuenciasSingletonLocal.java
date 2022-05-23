package co.gov.supernotariado.bachue.correspondencia.ejb.negocio;

import javax.ejb.Local;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;

/**
 * Singleton para trabajar el manejo de secuencias
 */
@Local
public interface SecuenciasSingletonLocal {

	/**
	 * Arma un string con la secuencia global del parametro
	 * @param idParametro Identificador del parámetro a consultar
	 * @return Cadena con la secuencia global encontrada
	 */
	String obtenerValorSecuenciaGlobal(long idParametro, boolean aumentarValor);

	/**
	 * Genera un campo de secuencia para un proceso
	 * @param procesoEjecutado Proceso ejecutado a evaluar
	 */
	void generarSiguienteSecuenciaProceso(ProcesoEjecutado procesoEjecutado);

	/**
	 * Obtiene la siguiente secuencia de una entrada de este tipo
	 * @param entradaId Identificador de la entrada a validar
	 * @param aumentarValor Si debe aumentar (persistir) el valor encontrado
	 * @return Cadena con el valor de la secuencia obtenida
	 */
	String obtenerEntradaValorSecuencia(long entradaId, boolean aumentarValor);

}

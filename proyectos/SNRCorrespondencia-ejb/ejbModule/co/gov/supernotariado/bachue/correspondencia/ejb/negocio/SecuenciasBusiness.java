package co.gov.supernotariado.bachue.correspondencia.ejb.negocio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoSecuencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaGlobal;
import co.gov.supernotariado.bachue.correspondencia.ejb.session.CorrespondenciaStatelessLocal;

/**
 * Maneja la lógica de negocio para las secuencias personalizadas del sistema
 */
@Singleton(name="SecuenciasBusiness", mappedName="ejb/SecuenciasBusiness")
@Local(SecuenciasSingletonLocal.class)
public class SecuenciasBusiness implements SecuenciasSingletonLocal {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(SecuenciasBusiness.class);

	/** Manejador de persistencia */
	@EJB(name = "CorrespondenciaStateless")
	private CorrespondenciaStatelessLocal persistencia;


	/**
	 * Arma un string con la secuencia global del parametro
	 * @param idParametro Identificador del parámetro a consultar
	 * @return Cadena con la secuencia global encontrada
	 */
	@SuppressWarnings("unchecked")
	public String obtenerValorSecuenciaGlobal(long idParametro, boolean aumentarValor) {
		StringBuilder secuencia = new StringBuilder();
		try {
			if(idParametro>0) {
				List<SecuenciaGlobal> lista = (List<SecuenciaGlobal>) (Object) this.persistencia.buscarPorNamedQuery(SecuenciaGlobal.SECUENCIAGLOBAL_POR_PARAMETRO_ORDEN_ID_ASC, idParametro);
				for(SecuenciaGlobal sec:lista) {
					if (sec.isActivo()) {
						secuencia.append(obtenerValorSecuencia(sec, aumentarValor));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error generando secuencia", e);
		}
		return secuencia.toString();
	}

	
	
	/**
	 * Aumente el valor de la secuencia autonumerica
	 * 
	 * @param secuenciaGlobal Secuencia Global a afectar
	 * @param aumentarValor Indica si aumenta el valor o solamente recupera el último valor encontrado
	 * @return Cadena con el valor de secuencia encontrado
	 */
	private String obtenerValorSecuencia(SecuenciaGlobal secuenciaGlobal, boolean aumentarValor) {
		String secuencia = "";
		try {
			Calendar actual = Calendar.getInstance();
			secuencia = secuenciaGlobal.getValorSecuencia();
			if(secuenciaGlobal.isAutonumerico()) {
				if(secuenciaGlobal.getReiniciarCada()==1) {
					Calendar ultimo = Calendar.getInstance();
					
					if(secuenciaGlobal.getFechaUltimoReinicio() == null) {
						secuenciaGlobal.setFechaUltimoReinicio(ultimo.getTime());
					} else {
						ultimo.setTime(secuenciaGlobal.getFechaUltimoReinicio());
					}

					if(ultimo.get(Calendar.YEAR) < actual.get(Calendar.YEAR)) {
						secuencia = "1";
						secuenciaGlobal.setFechaUltimoReinicio(Calendar.getInstance().getTime());
					}
				}
				
				if(aumentarValor) {
					int id = Integer.parseInt(secuencia);
					secuenciaGlobal.setValorSecuencia(String.valueOf(++id));
					persistencia.actualizar(secuenciaGlobal);
				}
			}

			if(secuenciaGlobal.getTamanoSecuencia() > secuencia.length()) {
				secuencia = StringUtils.leftPad(secuencia, secuenciaGlobal.getTamanoSecuencia(), "0");
			}
			
			if(secuenciaGlobal.isTipoFecha()) {
				if(secuencia.contains("YYYY")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					secuencia = secuencia.replace("YYYY", sdf.format(actual.getTime()));
				} else if(secuencia.contains("MM")) {
					SimpleDateFormat sdf = new SimpleDateFormat("MM");
					secuencia = secuencia.replace("MM", sdf.format(actual.getTime()));
				} else if(secuencia.contains("DD")) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd");
					secuencia = secuencia.replace("DD", sdf.format(actual.getTime()));
				}	
			}
		} catch(Exception e) {
			logger.error("Secuencia no es numerica: "+secuenciaGlobal.getValorSecuencia()+" - id:"+secuenciaGlobal.getId(), e);
		}
		return secuencia;
	}
	
	
	/**
	 * Genera un campo de secuencia para un proceso
	 * @param procesoEjecutado Proceso ejecutado a evaluar
	 */
	@SuppressWarnings("unchecked")
	public void generarSiguienteSecuenciaProceso(ProcesoEjecutado procesoEjecutado) {
		try {
			List<ProcesoSecuencia> seqs = (List<ProcesoSecuencia>) (Object) persistencia.buscarPorNamedQuery(ProcesoSecuencia.PROCESOSECUENCIA_POR_PROCESO, procesoEjecutado.getProceso().getId());
			if(seqs!=null) {
				StringBuilder secuencia = new StringBuilder();
				for(ProcesoSecuencia fs:seqs) {
					if(fs.isActivo()) {
						secuencia.append(obtenerValorSecuenciaProceso(fs));
					}
				}
				procesoEjecutado.setSecuenciaProceso(secuencia.toString());
			}
		} catch(Exception e) {
			logger.error("Error generando secuencia proceso", e);
		}
	}

	
	
	/**
	 * Obtiene y aumenta el valor de la secuencia de una entrada
	 * @param ps Objeto Proceso Secuencia con la información de la secuencia
	 * @return Cadena con la secuencia obtenida
	 */
	private String obtenerValorSecuenciaProceso(ProcesoSecuencia ps) {
		String secuencia = "";
		try {
			Calendar fechaActual = Calendar.getInstance();
			secuencia = ps.getValorSecuencia();
			if(ps.isAutoNumerico()) {
				if(ps.getReiniciarCada()==1) {
					Calendar ultimo = Calendar.getInstance();
					
					if(ps.getFechaUltimoReinicio() == null) {
						ps.setFechaUltimoReinicio(ultimo.getTime());
					} else {
						ultimo.setTime(ps.getFechaUltimoReinicio());
					}

					if(ultimo.get(Calendar.YEAR) < fechaActual.get(Calendar.YEAR)) {
						secuencia = "1";
						ps.setFechaUltimoReinicio(Calendar.getInstance().getTime());
					}
				}
				
				int id = Integer.parseInt(secuencia);
				ps.setValorSecuencia(String.valueOf(++id));
				persistencia.actualizar(ps);
			}

			if(ps.getTamanoSecuencia() > secuencia.length()) {
				secuencia = StringUtils.leftPad(secuencia, ps.getTamanoSecuencia(), "0");
			}
			
			if(ps.isTipoFecha()) {
				if(secuencia.contains("YYYY")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					secuencia = secuencia.replace("YYYY", sdf.format(fechaActual.getTime()));
				} else if(secuencia.contains("MM")) {
					SimpleDateFormat sdf = new SimpleDateFormat("MM");
					secuencia = secuencia.replace("MM", sdf.format(fechaActual.getTime()));
				} else if(secuencia.contains("DD")) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd");
					secuencia = secuencia.replace("DD", sdf.format(fechaActual.getTime()));
				}	
			}
		} catch(Exception e) {
			logger.error("Secuencia no es numerica: "+ps.getValorSecuencia()+" - id:"+ps.getId(), e);
		}
		return secuencia;
	}
	
	
	
	/**
	 * Obtiene la siguiente secuencia de una entrada de este tipo
	 * @param entradaId Identificador de la entrada a validar
	 * @param aumentarValor Si debe aumentar (persistir) el valor encontrado
	 * @return Cadena con el valor de la secuencia obtenida
	 */
	@SuppressWarnings("unchecked")
	public String obtenerEntradaValorSecuencia(long entradaId, boolean aumentarValor) {
		StringBuilder secuencia = new StringBuilder();
		try {
			List<SecuenciaEntrada> seqs = (List<SecuenciaEntrada>) (Object) this.persistencia.buscarPorNamedQuery(SecuenciaEntrada.SECUENCIAENTRADA_POR_ENTRADA, entradaId);
			if(seqs!=null) {
				for(SecuenciaEntrada fs:seqs) {
					if(fs.isActivo()) {
						secuencia.append(obtenerValorSecuenciaEntrada(fs, aumentarValor));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error generando secuencia", e);
		}
		return secuencia.toString();
	}
	
	
	
	/**
	 * Aumente el valor de la secuencia autonumerica de una entrada
	 * 
	 * @param secuenciaEntrada Secuencia de entrada a afectar
	 * @param aumentarValor Indica si guardar el valor obtenido o solamente mostrarlo sin afectar la secuencia
	 * @return Cadena con la secuencia obtenida
	 */
	private String obtenerValorSecuenciaEntrada(SecuenciaEntrada secuenciaEntrada, boolean aumentarValor) {
		String secuencia = "";
		try {
			Calendar actual = Calendar.getInstance();
			secuencia = secuenciaEntrada.getValorSecuencia();
			if(secuenciaEntrada.isAutonumerico()) {
				if(secuenciaEntrada.getReiniciarCada()==1) {
					Calendar ultimo = Calendar.getInstance();
					
					if(secuenciaEntrada.getFechaUltimoReinicio() == null) {
						secuenciaEntrada.setFechaUltimoReinicio(ultimo.getTime());
					} else {
						ultimo.setTime(secuenciaEntrada.getFechaUltimoReinicio());
					}

					if(ultimo.get(Calendar.YEAR) < actual.get(Calendar.YEAR)) {
						secuencia = "1";
						secuenciaEntrada.setFechaUltimoReinicio(Calendar.getInstance().getTime());
					}
				}
				
				if(aumentarValor) {
					int id = Integer.parseInt(secuencia);
					secuenciaEntrada.setValorSecuencia(String.valueOf(++id));
					persistencia.actualizar(secuenciaEntrada);
				}
			}

			if(secuenciaEntrada.getTamanoSecuencia() > secuencia.length()) {
				secuencia = StringUtils.leftPad(secuencia, secuenciaEntrada.getTamanoSecuencia(), "0");
			}
			
			if(secuenciaEntrada.isTipoFecha()) {
				if(secuencia.contains("YYYY")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					secuencia = secuencia.replace("YYYY", sdf.format(actual.getTime()));
				} else if(secuencia.contains("MM")) {
					SimpleDateFormat sdf = new SimpleDateFormat("MM");
					secuencia = secuencia.replace("MM", sdf.format(actual.getTime()));
				} else if(secuencia.contains("DD")) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd");
					secuencia = secuencia.replace("DD", sdf.format(actual.getTime()));
				}	
			}
		} catch(Exception e) {
			logger.error("Secuencia no es numerica: "+secuenciaEntrada.getValorSecuencia()+" - id:"+secuenciaEntrada.getId(), e);
		}
		return secuencia;
	}

}

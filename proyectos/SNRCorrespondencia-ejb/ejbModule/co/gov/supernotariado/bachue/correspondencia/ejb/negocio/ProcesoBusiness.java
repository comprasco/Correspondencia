package co.gov.supernotariado.bachue.correspondencia.ejb.negocio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.aspose.words.Document;
import com.aspose.words.FindReplaceDirection;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.SaveFormat;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.ConstantesCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEstados;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposParametros;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposPeriodosPasos;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Archivo;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.CondicionRegla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.EntidadGenerica;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoria;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoSecuencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Regla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RestriccionEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionNotificadorCorrespondencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionOWCC;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionUsuarios;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonPasoData;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonProcesoData;
import co.gov.supernotariado.bachue.correspondencia.ejb.session.CorrespondenciaStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.NodoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.JsonUtil;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.NotificacionUtil;
import https.www_supernotariado_gov_co.schemas.bachue.co.busquedadocumentos.obtenerarchivo.v1.TipoSalidaObtenerArchivo;
import https.www_supernotariado_gov_co.schemas.bachue.co.enviodocumentos.enviardocumento.v1.TipoSalidaEnviarDocumento;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 * Maneja varios puntos de la ejecución de un proceso
 */
@Stateless(name="ProcesoBusiness", mappedName="ejb/ProcesoBusiness")
@Local(ProcesosStatelessLocal.class)
public class ProcesoBusiness implements ProcesosStatelessLocal{

	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(ProcesoBusiness.class);

	/**
	 * Propiedad usada para corregir java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11GraphicsEnvironment 
	 */
	public static final String HEADLESS_PROPERTY = "java.awt.headless";

	
	/**
	 * Tipos de entradas que requieren validacion de reglas
	 */
	protected static final String[] TIPOS_ENTRADAS_REGLAS = {TiposEntrada.RADIO.name(), TiposEntrada.SELECT.name(), TiposEntrada.CHECKBOX.name(), TiposEntrada.LISTA_PREDEFINIDA.name(), TiposEntrada.RADIO_LISTA_PREDEFINIDA.name()};
	/**
	 * Tipos de entradas que requieren campo de restricción de longitud
	 */
	protected static final String[] TIPOS_ENTRADAS_LONGITUD = {TiposEntrada.TEXT.name(), TiposEntrada.TEXTO_NUMERICO.name(), TiposEntrada.EMAIL.name(), TiposEntrada.TEXTAREA.name()};
	/**
	 * Tipos de entradas que permiten restricciones
	 */
	protected static final String[] TIPOS_ENTRADAS_RESTRICCIONES = {TiposEntrada.DATE.name()};

	/** Manejador de lógica de negocio de secuencias */
	@EJB(name = "SecuenciasBusiness")
	private SecuenciasSingletonLocal secuenciasControl;

	
	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;

	
	/** Manejador de persistencia */
	@EJB(name = "CorrespondenciaStateless")
	private CorrespondenciaStatelessLocal persistencia;
	
	/**
	 * Actualiza una entidad en la base de datos
	 * @param entidad Entidad a buscar
	 * @return Entidad consultada
	 */
	public EntidadGenerica buscarEntidadPorId(EntidadGenerica entidad, Object id) {
		return persistencia.buscarPorId(entidad, id);
	}

	
	/**
	 * Actualiza una entidad en la base de datos
	 * @param entidad Entidad a afectar
	 */
	public void actualizarEntidad(EntidadGenerica entidad) {
		persistencia.actualizar(entidad);
	}
	
	/**
	 * Obtiene un listado de los tipos de entradas que se admiten para reglas
	 * @return Listado de Cadenas con los tipos de entradas
	 */
	public List<String> getTiposEntradasReglas(){
		try {
			return Arrays.asList(TIPOS_ENTRADAS_REGLAS);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return new ArrayList<>();
	}
	
	/**
	 * Obtiene un listado de los tipos de entradas que tienen validación de longitud
	 * @return Lista de Cadenas con los tipos de entradas
	 */
	public List<String> getTiposEntradasLongitud(){
		try {
			return Arrays.asList(TIPOS_ENTRADAS_LONGITUD);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene un listado de los tipos de datos que tienen validación de longitud
	 * @return Lista de Cadenas con los tipos de entradas
	 */
	public List<String> getTiposEntradasRestricciones(){
		try {
			return Arrays.asList(TIPOS_ENTRADAS_RESTRICCIONES);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return new ArrayList<>();
	}
	
	
	/**
	 * Obtiene un proceso activo por su identificador
	 * @param identificador Identificador del proceso
	 * @return Proceso obtenido
	 */
	@SuppressWarnings("unchecked")
	public Proceso obtenerProcesoActivoPorIdentificador(long identificador){
		Proceso proceso = null;
		try {
			List<Proceso> result = (List<Proceso>)(Object) persistencia.buscarPorNamedQuery(Proceso.PROCESO_ACTIVO_POR_IDENTIFICADOR_ORDERDESC, identificador);
			if(!result.isEmpty()) {
				proceso = result.get(0);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return proceso;
	}

	
	/**
	 * Obtiene un proceso activo por su nombre clave
	 * @param nombreClave Cadena con el nombre clave a buscar
	 * @return Proceso obtenido
	 */
	@SuppressWarnings("unchecked")
	public Proceso obtenerProcesoActivoPorNombreClave(String nombreClave){
		Proceso proceso = null;
		try {
			List<Proceso> result = (List<Proceso>)(Object) persistencia.buscarPorNamedQuery(Proceso.PROCESO_ACTIVO_POR_NOMBRECLAVE_ORDERDESC, nombreClave);
			if(!result.isEmpty()) {
				proceso = result.get(0);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return proceso;
	}

	
	/**
	 * Obtiene el paso actual de un proceso ejecutado
	 * @param idProcesoEjecutado Identificador del proceso
	 * @return Lista de Pasos encontrados
	 */
	@SuppressWarnings("unchecked")
	public List<PasoEjecutado> obtenerPasosEjecutadosPorProceso(long idProcesoEjecutado){
		List<PasoEjecutado> result = new ArrayList<>();
		try {
			result = (List<PasoEjecutado>)(Object) persistencia.buscarPorNamedQuery(PasoEjecutado.PASOEJECUTADO_ACTUAL_POR_PROCESO, idProcesoEjecutado);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	
	/**
	 * Obtiene un listado de procesos ejecutados por radicado e identificador del proceso
	 * @param radicado Radicado a buscar
	 * @param identificadorProceso Identificador del proceso a buscar
	 * @return Proceso ejecutado encontrado
	 */
	@SuppressWarnings("unchecked")
	public ProcesoEjecutado obtenerProcesoEjecutadoPorSecuenciaIdentificador(String radicado, long identificadorProceso){
		ProcesoEjecutado result = null;
		try {
			List<ProcesoEjecutado> procesos = (List<ProcesoEjecutado>)(Object) persistencia.buscarPorNamedQuery(ProcesoEjecutado.PROCESOEJECUTADO_POR_SECUENCIA_IDENTIFICADORPROCESO, radicado, identificadorProceso);
			if(!procesos.isEmpty()) {
				result = procesos.get(0);
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * Obtiene un listado de procesos ejecutados por radicado e identificador del proceso
	 * @param secuenciaSalidaProceso Secuencia de salida (consecutivo ee) a buscar 
	 * @param identificadorProceso Identificador de proceso a buscar
	 * @return Proceso Ejecutado obtenido
	 */
	@SuppressWarnings("unchecked")
	public ProcesoEjecutado obtenerProcesoEjecutadoPorSecuenciaSalidaIdentificador(String secuenciaSalidaProceso, long identificadorProceso){
		ProcesoEjecutado result = null;
		try {
			List<ProcesoEjecutado> procesos = (List<ProcesoEjecutado>)(Object) persistencia.buscarPorNamedQuery(ProcesoEjecutado.PROCESOEJECUTADO_POR_SECUENCIASALIDA_IDENTIFICADORPROCESO, secuenciaSalidaProceso, identificadorProceso);
			if(!procesos.isEmpty()) {
				result = procesos.get(0);
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	
	
	/**
	 * Revisa las reglas de las entradas para fijar el paso siguiente
	 * Se revisan las entradas en orden inverso, las siguientes reglas van sobreescribiendo las anteriores
	 * Esto por que también se tienen en cuenta entradas de pasos anteriores
	 * Devuelve el paso siguiente del proceso, comenzando por el paso 0 de la lista proximosPasos
	 * Sobreescribe la propiedad permitirparalelo de la configuración del paso, con la que tenga la regla
	 * @param pasoEjecutado Paso ejecutado sobre el cual validar
	 * @param entradasAnteriores Lista de entradas anteriores para evaluar su valor en las reglas a evaluar
	 * @param proximosPasos Listado de proximos pasos para seleccionar el siguiente paso basado en las reglas
	 * @return Paso con el siguiente en el proceso
	 */
	@SuppressWarnings("unchecked")
	public Paso validarReglas(PasoEjecutado pasoEjecutado, List<ValorEntrada> entradasAnteriores, List<Paso> proximosPasos){
		logger.info("Evaluando Reglas PasoID:"+pasoEjecutado.getPaso().getId());
		Paso proximoPaso = null;
		if(proximosPasos!=null && !proximosPasos.isEmpty()) {
			proximoPaso = proximosPasos.get(0);
		}

		// Compara las entradas en orden inverso, primero las últimas entradas
		List<ValorEntrada> entradascopy = new ArrayList<>(entradasAnteriores);
		Collections.reverse(entradascopy);
		
		boolean reglaOk = false;

		List<Regla> reglas = (List<Regla>)(Object)persistencia.buscarPorNamedQuery(Regla.REGLA_ACTIVAS_POR_PASO, pasoEjecutado.getPaso().getId());
		for(Regla regla:reglas) {
			List<CondicionRegla> condiciones = (List<CondicionRegla>)(Object)persistencia.buscarPorNamedQuery(CondicionRegla.CONDICIONREGLA_POR_REGLA, regla.getId());
			int totalCondiciones = condiciones.size();
			int totalEvaluadas = 0;
			boolean condicionOk = true;
			for(CondicionRegla condicion:condiciones) {
				
				// Evalua todas las entradas en orden inverso
				for(ValorEntrada valorEntrada:entradascopy){
					if(condicion.getEntrada().getId() == valorEntrada.getEntrada().getId()){
						logger.info("Condicional ReglaID:"+regla.getId()+" - Entrada:"+condicion.getEntrada().getNombreEntrada()+" - CondicionEntrada:"+condicion.getContenido()+" - ValorEntrada:"+valorEntrada.getValor()+" - Ir a PasoID:"+regla.getSiguientePasoId()+ " - FinalizaProceso:"+regla.isFinalizaProceso());
						if(valorEntrada.getValor()!=null && valorEntrada.getValor().equals(condicion.getContenido())) {
							totalEvaluadas++;
						} else {
							condicionOk = false;
						}
						// Siempre rompe acá pues ya evaluó el valor de la condición con la última entrada encontrada
						break;
					}
				}
				
				// Si no cumple alguna de las condiciones aborta y sigue con la siguiente regla
				if(!condicionOk) {
					break;
				}
				
				// Si cumple todas las condiciones fija el siguiente paso según la regla
				if(totalEvaluadas == totalCondiciones) {
					if(regla.isFinalizaProceso()) {
						proximoPaso = new Paso();
						proximoPaso.setId(-1l);
						inicializarJsonPasoData(proximoPaso);
					} else {
						if(regla.getSiguientePasoId()!=null && regla.getSiguientePasoId()>0) {
							proximoPaso = buscarPasoPorId(regla.getSiguientePasoId());
							proximoPaso.setSaltoPaso(true);
						} else {
							proximoPaso = proximosPasos.get(0);
							proximoPaso.setSaltoPaso(false);
						}
						pasoEjecutado.getPaso().getJsonPasoData().setPermitirParalelo(regla.isPermiteParalelo());
						proximoPaso.setCampoObservacionesRequerido(regla.isCampoObservacionesRequerido());
					}
					reglaOk = true;
				}
			}

			// Si se validó la regla aborta las siguiente reglas
			if(reglaOk) {
				break;
			}
		}
		return proximoPaso;
	}
	
	
	/**
	 * Permite obtener un List a partir de la lista de opciones de un entrada guardado en un campo tipo json
	 * Utiliza el objeto NodeTO a manera de objeto de key:value.
	 * Solo tiene en cuenta las opciones activas  
	 * @param entrada Entrada a validar
	 * @param entradaPadreId Indica el valor del campo padre en el caso de campos anidados 
	 * @return Lista de Opciones de una entrada
	 */
	public List<NodoTO> getListaOpcionesEntrada(Entrada entrada, String entradaPadreId){
		List<NodoTO> result = new ArrayList<>();
		inicializarJsonEntradaData(entrada);
		try {
			if(entrada.getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || entrada.getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name())) {
				Parametro parametro = parametrosControl.buscarParametroPorId(entrada.getParametroOpcionesId());
				if(parametro.getOpcionesJson()!=null){
					// No tiene en cuenta entrada padre cuando es este tipo de dato
					result = obtenerOpciones(parametro.getOpcionesJson(), null);
				}
			} else {
				if(entrada.getOpcionesJson()!=null){
					result = obtenerOpciones(entrada.getOpcionesJson(), entradaPadreId);
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		entrada.setListaOpciones(result);
		return result;
	}
	
	
	/**
	 * Obtiene opciones en forma de listado de nodos desde una lista de opciones de objeto JsonOpcion
	 * @param opciones Listado de Opciones original 
	 * @param entradaPadreId Entrada padre en caso de campos anidados
	 * @return Lista de nodos con las opciones
	 */
	private List<NodoTO> obtenerOpciones(List<JsonOpcion> opciones, String entradaPadreId){
		List<NodoTO> result = new ArrayList<>();
		for(JsonOpcion opcion:opciones) {
			// Solo agrega la opción si no trae parentid o si el parentid de la opcion es igual al parametro
			if(opcion.isActivo() && (entradaPadreId==null || entradaPadreId.isEmpty() || (opcion.getOpcionPadreId()!=null && opcion.getOpcionPadreId().equals(entradaPadreId)))) {
				NodoTO p = new NodoTO();
				p.setId(String.valueOf(opcion.getId()));
				p.setClave(opcion.getClave());
				p.setText(opcion.getNombre());
				p.setMarkedNull(opcion.isValorNulo());
				result.add(p);
			}
		}
		return result; 
	}
	
	
	/**
	 * Permite obtener un List a partir de la lista de opciones de un entrada guardado en un campo tipo json
	 * Utiliza el objeto NodeTO a manera de objeto de key:value.
	 * Tiene en cuenta tanto activos como inactivos, esto para recuperar valores que se guardaron anteriormente a la inactivación del dato  
	 * @param entrada Entrada sobre la que obtener las opciones
	 * @param entradaPadreId Indica el valor del campo padre en el caso de campos anidados 
	 * @return Lista de nodos con las opciones
	 */
	public List<NodoTO> getListaOpcionesEntradaTodos(Entrada entrada, String entradaPadreId){
		List<NodoTO> result = new ArrayList<>();

		try {
			if(entrada.getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || entrada.getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name())) {
				result = new ArrayList<>();
				Parametro parametro = parametrosControl.buscarParametroPorId(entrada.getParametroOpcionesId());
				if(parametro.getOpcionesJson()!=null){
					result = obtenerOpciones(parametro.getOpcionesJson(), entradaPadreId);
				}
			} else {
				if(entrada.getOpcionesJson()!=null){
					result = obtenerOpciones(entrada.getOpcionesJson(), entradaPadreId);
				}
			}
		} catch(Exception e) {
			logger.error("Error getListaOpcionesEntradaTodos", e);
		}
		entrada.setListaOpciones(result);
		return result;
	}

	
	/**
	 * Calcula el tiempo limite de el paso segun las horas laborales
	 * @param tiempoPaso Tiempo configurado para el paso
	 * @param hl Horas laborales
	 * @param tipoPeriodo Tipo de periodo configurado para el paso
	 * @return Calendario con la fecha de fin del paso
	 */
	public Calendar calcularTiempoFinPaso(int tiempoPaso, String[] hl, String tipoPeriodo) {
		Calendar end = Calendar.getInstance();
		Calendar aux = Calendar.getInstance();
		int time = tiempoPaso;
		if(tipoPeriodo.equals(TiposPeriodosPasos.HORAS.name())){
			time = time * 60;
		} else if(tipoPeriodo.equals(TiposPeriodosPasos.DIAS.name())){
			time = time * 60 * 24;
		} else if(tipoPeriodo.equals(TiposPeriodosPasos.SEMANAS.name())){
			time = time * 60 * 24 * 7;
		} else if(tipoPeriodo.equals(TiposPeriodosPasos.MESES.name())){
			Calendar today = Calendar.getInstance();
			Calendar endMonths = Calendar.getInstance();
			endMonths.add(Calendar.MONTH, time);
			long differenceInMilis = endMonths.getTimeInMillis() - today.getTimeInMillis();
			time = new BigDecimal(differenceInMilis / 1000 / 60).intValue();
		}
		if(hl == null) {
			end.add(Calendar.MINUTE, time);
		}
		else {
			calculaHorasLaborales(hl, time, end, aux);
		}
		return end;
	}
	
	
	/** Usado para calcular el tiempo entre horas laborales para finalizar una tarea
	 * @param hl Horas laborales 
	 * @param time Tiempo periodo calculado
	 * @param end Calendario para calcular 
	 * @param aux Calendario auxiliar para calcular
	 */
	public void calculaHorasLaborales(String[] hl, int time, Calendar end, Calendar aux) {
		// Si la hora actual esta dentro del rango de horas laborales
		int firstdayhours = 0;
		int workinghours = (Integer.parseInt(hl[1]) - Integer.parseInt(hl[0])) * 60;
		if(end.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(hl[0]) && end.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(hl[1])) {
			aux.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hl[1]));
			aux.set(Calendar.MINUTE, 0);
			aux.set(Calendar.SECOND, 0);
			// Calcula cuantas horas quedan para finalizar la jornada laboral
			firstdayhours = new BigDecimal((aux.getTimeInMillis() - end.getTimeInMillis()) / 1000 / 60).intValue();
			if(firstdayhours < 0) {
				firstdayhours = 0;
			}
		}
		// Si la duración de el paso es menor que lo que queda del dia laboral, solo se agrega el tiempo
		if(time - firstdayhours < 0) {
			end.add(Calendar.MINUTE, time);
		} else {
			// Resta los minutos del primer dia
			time -= firstdayhours;
			// Obtiene el numero de dias a sumar
			BigDecimal timef = new BigDecimal(time).divide(new BigDecimal(workinghours), 2, RoundingMode.HALF_EVEN);
			BigInteger decimal = timef.remainder(BigDecimal.ONE).movePointRight(timef.scale()).abs().toBigInteger();
			
			// Si quedan minutos por sumar, o si la hora actual tiene minutos pasada la hora, se suma un dia mas
			int minutes = 0;
			if(decimal.intValue() > 0 || end.get(Calendar.MINUTE) != 0) {
				minutes = time - (timef.intValue() * workinghours);
				if(minutes > 0) {
					timef = timef.add(BigDecimal.ONE);
				}
			}
	
			end.add(Calendar.DAY_OF_YEAR, timef.intValue());
			
			// Deja la hora en la inicial de la hora laboral para sumar los minutos restantes
			if(minutes > 0) {
				end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hl[0]));
				end.set(Calendar.MINUTE, 0);
				end.set(Calendar.SECOND, 0);
				end.add(Calendar.MINUTE, minutes);
			} else {
				end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hl[1]));
				end.set(Calendar.MINUTE, 0);
				end.set(Calendar.SECOND, 0);
			}
		}
	}

	
	
	/**
	 * Verifica todos los campos anidados contra los valores de los campos padre
	 * Recupera también la lista de opciones para todos los campos tipo selección
	 * No se puede buscar solamente por el valor del campo padre seleccionado ya que se debe buscar por campos anidados de mas de 1 nivel
	 * @param entradas campos a validar
	 */
	public void validarEntradasAnidadas(List<ValorEntrada> entradas) {
		for(ValorEntrada parent:entradas){
			// Si no trae informacion del entrada padre seleccionado o si corresponde con el que va en el loop
				for(ValorEntrada value:entradas){
					// No se compara contra si mismo
					if(value.getEntrada().getId()!=parent.getEntrada().getId()) {
						if(value.getEntrada().isEntradaAnidada()) {

							// Si coincide con el id del entrada padre
							if(value.getEntrada().getEntradaAnidadaId()==parent.getEntrada().getId()) {
								value.setMostrar(false);
								
								if(value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.SELECT.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())){
									// Si el entrada no esta guardado trae opciones activas, si ya está guardado las trae todas
									if(value.getId()==0) {
										getListaOpcionesEntrada(value.getEntrada(), parent.getValor());
									} else {
										getListaOpcionesEntradaTodos(value.getEntrada(), parent.getValor());
									}
									
									// Solo select - radio no colocar valor predefinido
									if((value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.SELECT.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name()))
											&& !value.getEntrada().getListaOpciones().isEmpty() && value.getId()==0){
										// Si el entrada no está guardado se coloca el primer valor encontrado
										if(value.getValor()==null || value.getValor().isEmpty()) {
											if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.SELECT.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name())) {
												value.setValor(value.getEntrada().getListaOpciones().get(0).getId());
											}
										} else {
											// Verifica si el valor actual esta dentro de la lista de opciones (si cambia un combo no deberia encontrar el valor)
											// Si se cambiara siempre el valor de entradavalue, se resetea y no cambia el valor en el formulario del usuario
											boolean found = false;
											for(NodoTO node:value.getEntrada().getListaOpciones()) {
												if(node.getId().equals(value.getValor())) {
													found = true;
													break;
												}
											}
											if(!found) {
												value.setValor(value.getEntrada().getListaOpciones().get(0).getId());
											}
										}
									}
								}
	
								
								// Debe coincidir el valor actual del dato padre
								for(JsonOpcion option:value.getEntrada().getOpcionesJson()) {
									if(parent.isMostrar()) {
										// Si es checkbox hace split con los valores seleccionados 
										if(parent.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())){
											if(parent.getValor()!=null) {
												String[] values = parent.getValor().split(",");
												for(String optionvalue:values) {
													if(option.getOpcionPadreId()!=null && option.getOpcionPadreId().equals(optionvalue)) {
														value.setMostrar(true);
														break;
													}
												}
											}
										} else {
											if(option.getOpcionPadreId()!=null && option.getOpcionPadreId().equals(parent.getValor())) {
												value.setMostrar(true);
												break;
											}
										}
									}
								}
							}
						} else {
							// Si no es campo anidado busca las opciones normales
							// Si el entrada no esta guardado trae opciones activas, si ya está guardado las trae todas
							if(value.getId()==0) {
								getListaOpcionesEntrada(value.getEntrada(), null);
							} else {
								getListaOpcionesEntradaTodos(value.getEntrada(), null);
							}
						}
					}
			}
		}
	}


	/**
	 * Identifica entradas anidadas para validaciones en formulario
	 * Identifica si tiene reglas asociadas
	 * Usado al cargar el formulario para ser llenado, y sabe si debe validar en cada cambio de valor de campo las reglas y entradas anidadas.  No en consultas de detalle posteriores.
	 * @param entradas Lista de entradas con las cuales validar
	 */
	@SuppressWarnings("unchecked")
	public void validarReglasYEntradasAnidadas(List<ValorEntrada> entradas) {
		for(ValorEntrada parent:entradas){
			
			// Valida si la entrada tiene reglas asociadas
			List<CondicionRegla> reglas = (List<CondicionRegla>)(Object) persistencia.buscarPorNamedQuery(CondicionRegla.CONDICIONREGLA_POR_ENTRADA_PASO, parent.getEntrada().getId(), parent.getEntrada().getPaso().getId());
			if(!reglas.isEmpty()) {
				parent.setVerificarReglas(true);
			}
			
			// Valida si tiene entrada anidadas asociados 
			for(ValorEntrada value:entradas){
				// No se compara contra si mismo
				if(value.getEntrada().getId()!=parent.getEntrada().getId() && value.getEntrada().isEntradaAnidada() && value.getEntrada().getEntradaAnidadaId()==parent.getEntrada().getId()) {
					// Si coincide con el id del entrada padre
					parent.setVerificarAnidados(true);
					break;
				}
			}
		}
	}
	

	
	/**
	 * Guarda la transaccion para avanzar una Etapa
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param pasoEjecutado Paso Ejecutado a guardar
	 * @param usuarioActual Usuario actual
	 * @param siguientePaso Siguiente paso dentro del proceso
	 * @param categoriasSeleccionadas Categorias asociadas al paso
	 * @param usuariosAsignados Listado de usuarios asignados
	 * @param gruposAsignados Listado de grupos asignados
	 * @param opcion 1-avanzar, 2-devolver
	 * @return Paso ejecutado guardado
	 */
	public PasoEjecutado guardarTransaccionPasoEjecutado(IntegracionUsuarios integracionUsuarios, PasoEjecutado pasoEjecutado, UsuarioTO usuarioActual, Paso siguientePaso, Long categoriasSeleccionadas, String[] usuariosAsignados, String[] gruposAsignados, List<ValorEntrada> entradas, int opcion) {
		String oripEjecucion = usuarioActual.getOficina().getCirculo();
		String direccionIp = "";
		if(pasoEjecutado.getId()==0) {
			direccionIp = pasoEjecutado.getIpCreacion();
		} else {
			direccionIp = pasoEjecutado.getIpModificacion();
		}
		
		pasoEjecutado = guardarPasoEjecutado(integracionUsuarios, pasoEjecutado, usuarioActual, siguientePaso, categoriasSeleccionadas, direccionIp, oripEjecucion);
		
		pasoEjecutado = guardarAsignacionesSiguientePaso(pasoEjecutado, usuariosAsignados, gruposAsignados);

		guardarValorEntradas(integracionUsuarios.getIntegracionCatalogos(), entradas, pasoEjecutado, usuarioActual.getId(), direccionIp);

		if (siguientePaso != null) {
			generarNuevoPaso(integracionUsuarios, usuariosAsignados, gruposAsignados, siguientePaso, pasoEjecutado, opcion, direccionIp);
		}
		
		return pasoEjecutado;
	}

	
	/**
	 * Guarda un nuevo Paso Ejecutado o actualiza uno existente
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param pasoEjecutado Paso Ejecutado a guardar
	 * @param usuarioActual Usuario actual
	 * @param siguientePaso Siguiente paso dentro del proceso
	 * @param categoriasSeleccionadas Categorias asociadas al paso
	 * @param ipAddress Dirección IP del usuario actual
	 * @param oripEjecucion ORIP de ejecución de proceso
	 * @return Paso ejecutado guardado
	 */
	public PasoEjecutado guardarPasoEjecutado(IntegracionUsuarios integracionUsuarios, PasoEjecutado pasoEjecutado, UsuarioTO usuarioActual, Paso siguientePaso, Long categoriasSeleccionadas, String ipAddress, String oripEjecucion) {
		pasoEjecutado.setFechaEjecucion(Calendar.getInstance().getTime()); 
		pasoEjecutado.setActivo(false);
		pasoEjecutado.setEstado(TiposEstados.EJECUTADO.name());
		
		if(pasoEjecutado.getUsuarioAsignadoId()==null || pasoEjecutado.getUsuarioAsignadoId().isEmpty()){
			pasoEjecutado.setUsuarioAsignadoId(usuarioActual.getId());
		}
		if(usuarioActual.getOficina()==null) {
			usuarioActual.setOficina(integracionUsuarios.obtenerOficinaUsuario(usuarioActual.getId()));
		}

		if(pasoEjecutado.getId()>0){
			// No se fija orip aca, se mantiene la original
			pasoEjecutado.setIdUsuarioModificacion(usuarioActual.getId());
			pasoEjecutado.setFechaModificacion(Calendar.getInstance().getTime());
			pasoEjecutado.setIpModificacion(ipAddress);
			if(siguientePaso==null){
				pasoEjecutado.setUltimoPaso(true);
			}
			pasoEjecutado.setFechaSiguienteRecordatorio(null);
			persistencia.actualizar(pasoEjecutado);

			boolean actualizarPasoActual = true;
			if(siguientePaso!=null) {
				// Determina si actualizar el paso actual del proceso debido a actividades paralelas, se mantiene la de mayor orden
				List<PasoEjecutado> pasosActuales = obtenerPasosEjecutadosPorProceso(pasoEjecutado.getProcesoEjecutado().getId());
				if(pasosActuales.size()>1) {
					for(PasoEjecutado paso:pasosActuales) {
						if(siguientePaso.getOrdenPaso()<paso.getPaso().getOrdenPaso()) {
							actualizarPasoActual = false;
							break;
						}
					}
				}
			} else {
				pasoEjecutado.getProcesoEjecutado().setActivo(false);
			} 
			if(actualizarPasoActual && siguientePaso!=null){
				pasoEjecutado.getProcesoEjecutado().setPasoActual(siguientePaso);
			}
			persistencia.actualizar(pasoEjecutado.getProcesoEjecutado());
			
			List<PasoEjecutado> pasosAnteriores = buscarPasosEjecutadosAnteriores(pasoEjecutado.getProcesoEjecutado().getId());

			// Enviar notificacion de finalización de proceso
			if(siguientePaso==null && !pasosAnteriores.isEmpty()) {
				if(pasoEjecutado.getProcesoEjecutado().getProceso().getNotificacionFinalizacion()==1 || pasoEjecutado.getProcesoEjecutado().getProceso().getNotificacionFinalizacion()==3) {
					new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarCorreoFinalProceso(pasoEjecutado, pasosAnteriores.get(0));
				}
				if(pasoEjecutado.getProcesoEjecutado().getProceso().getNotificacionFinalizacion()==2 || pasoEjecutado.getProcesoEjecutado().getProceso().getNotificacionFinalizacion()==3) {
					new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarSMSFinalProceso(pasoEjecutado, pasosAnteriores.get(0));
				}
			}

		} else{
			ProcesoEjecutado procesoEjecutado = new ProcesoEjecutado();
			procesoEjecutado.setProceso(pasoEjecutado.getPaso().getProceso());
			procesoEjecutado.setFechaCreacion(pasoEjecutado.getFechaCreacion());
			procesoEjecutado.setIdUsuarioCreacion(usuarioActual.getId());
			procesoEjecutado.setIdOripEjecucion(oripEjecucion);
			procesoEjecutado.setIpCreacion(ipAddress);
			if(siguientePaso==null){
				pasoEjecutado.setUltimoPaso(true);
				procesoEjecutado.setPasoActual(pasoEjecutado.getPaso());
				procesoEjecutado.setActivo(false);
			} else{
				procesoEjecutado.setPasoActual(siguientePaso);
				procesoEjecutado.setActivo(true);
			}
			if(categoriasSeleccionadas!=null && categoriasSeleccionadas>0){
				procesoEjecutado.setCategoria(new Parametro(categoriasSeleccionadas));
			}
			
			secuenciasControl.generarSiguienteSecuenciaProceso(procesoEjecutado);
			
			persistencia.persistir(procesoEjecutado);

			pasoEjecutado.setProcesoEjecutado(procesoEjecutado);

			pasoEjecutado.setFechaFin(Calendar.getInstance().getTime());
			
			pasoEjecutado.setIdOripEjecucion(oripEjecucion);
			persistencia.persistir(pasoEjecutado);
		}
		
		// Inicializa nuevamente los datos json de paso
		inicializarJsonPasoData(pasoEjecutado.getPaso());
		return pasoEjecutado;
	}
	

	/**
	 * Guarda información sobre las asignaciones del paso
	 * @param pasoEjecutado Paso ejecutado a validar
	 * @param usuariosAsignados Listado de usuarios asignados
	 * @param gruposAsignados Listado de grupos asignados
	 * @return Paso Ejecutado con los usuarios o grupos asignados
	 */
	public PasoEjecutado guardarAsignacionesSiguientePaso(PasoEjecutado pasoEjecutado, String[] usuariosAsignados, String[] gruposAsignados) {
		try {
			if(usuariosAsignados!=null) {
				StringBuilder usu = new StringBuilder();
				for(String id:usuariosAsignados) {
					if(id!=null) {
						usu.append(id+",");
					}
				}
				if(usu.toString().endsWith(",")) {
					usu = new StringBuilder(usu.substring(0, usu.length()-1));
				}
				pasoEjecutado.setUsuariosAsignados(usu.toString());
			} else if(gruposAsignados!=null) {
				StringBuilder grupo = new StringBuilder();
				for(String id:gruposAsignados) {
					if(id!=null) {
						grupo.append(id+",");
					}
				}
				if(grupo.toString().endsWith(",")) {
					grupo = new StringBuilder(grupo.substring(0, grupo.length()-1));
				}
				pasoEjecutado.setGruposAsignados(grupo.toString());
			}
			persistencia.actualizar(pasoEjecutado);
			
			// Inicializa nuevamente los datos json de paso
			inicializarJsonPasoData(pasoEjecutado.getPaso());
		} catch(Exception e) {
			logger.error("Error guardarAsignacionesPasoEjecutado", e);
		}
		return pasoEjecutado;
	}
	
	
	/**
	 * Inicializa un Paso Ejecutado desde ceros.  No lo guarda
	 * @param paso Paso a asociar al paso ejecutado
	 * @param idUsuario Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual Dirección IP del usuario actual
	 * @param oripEjecucion ORIP de ejecución del proceso
	 * @return Paso Ejecutado inicializado
	 */
	public PasoEjecutado inicializaPasoEjecutado(Paso paso, String idUsuario, String direccionIp, String oripEjecucion){
		PasoEjecutado nuevoPasoEjecutado = new PasoEjecutado();
		nuevoPasoEjecutado.setFechaCreacion(Calendar.getInstance().getTime());
		nuevoPasoEjecutado.setIdUsuarioCreacion(idUsuario);
		nuevoPasoEjecutado.setIpCreacion(direccionIp);
		nuevoPasoEjecutado.setActivo(true);
		nuevoPasoEjecutado.setUltimoPaso(false);
		nuevoPasoEjecutado.setPaso(paso);
		if(paso.getUsuarioAsignadoId()!=null && !paso.getUsuarioAsignadoId().isEmpty()){
			nuevoPasoEjecutado.setUsuarioAsignadoId(paso.getUsuarioAsignadoId());
		} else if(paso.getGrupoAsignadoId()!=null && !paso.getGrupoAsignadoId().isEmpty()){
			nuevoPasoEjecutado.setUsuarioAsignadoId(idUsuario);
			nuevoPasoEjecutado.setGrupoAsignadoId(paso.getGrupoAsignadoId());
		}
		nuevoPasoEjecutado.setEstado(TiposEstados.ENPROGRESO.name());
		nuevoPasoEjecutado.setIdOripEjecucion(oripEjecucion);
		
		return nuevoPasoEjecutado;
	}
	
	
	

	/**
	 * Inicializa un paso ejecutado con la información básica en estado pendiente y lo persiste.  Este paso lo completará otro usuario más adelante
	 * @param proximoPaso Proximo paso a asignar
	 * @param pasoEjecutadoInicial Paso ejecutado base para inicializar el actual
	 * @param opcion 1-avanzar, 2-devolver
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Paso ejecutado inicializado
	 */
	public PasoEjecutado inicializarNuevoPaso(Paso proximoPaso, PasoEjecutado pasoEjecutadoInicial, int opcion, String direccionIp){
		PasoEjecutado pasoEjecutado = inicializaPasoEjecutado(proximoPaso, pasoEjecutadoInicial.getIdUsuarioCreacion(), direccionIp, pasoEjecutadoInicial.getIdOripEjecucion());
		pasoEjecutado.setProcesoEjecutado(pasoEjecutadoInicial.getProcesoEjecutado());
		pasoEjecutado.setUsuarioAsignadoId(proximoPaso.getUsuarioAsignadoId());
		pasoEjecutado.setGrupoAsignadoId(proximoPaso.getGrupoAsignadoId());
		if(opcion==1){
			pasoEjecutado.setPasoAnterior(pasoEjecutadoInicial);
		} else if(opcion==2){
			List<PasoEjecutado> pasosAnteriores = buscarPasosEjecutadosAnteriores(pasoEjecutadoInicial.getProcesoEjecutado().getId());
			ListIterator<PasoEjecutado> li = pasosAnteriores.listIterator(pasosAnteriores.size());
			while(li.hasPrevious()) {
				PasoEjecutado ti = li.previous();
				if(ti.getPaso().getId()==proximoPaso.getId()){
					pasoEjecutado.setPasoAnterior(ti.getPasoAnterior());
					break;
				}
			}
			if((pasoEjecutado.getUsuarioAsignadoId()==null || pasoEjecutado.getUsuarioAsignadoId().isEmpty()) && (pasoEjecutado.getGrupoAsignadoId()==null || pasoEjecutado.getGrupoAsignadoId().isEmpty())) {
				pasoEjecutado.setUsuarioAsignadoId(pasoEjecutado.getIdUsuarioCreacion());
			}
		}

		// Tiempo limite paso
		int time = pasoEjecutado.getPaso().getDuracion().intValue();
		if(time > 0) {
			String[] wh = null;
			if(pasoEjecutado.getPaso().getHorarioLaboral()!=null && !pasoEjecutado.getPaso().getHorarioLaboral().isEmpty()) {
				wh = pasoEjecutado.getPaso().getHorarioLaboral().split("-");
			}

			Calendar end = calcularTiempoFinPaso(time, wh, pasoEjecutado.getPaso().getDuracionPeriodo());
			pasoEjecutado.setFechaFin(end.getTime());
		}

		// Verifica recordatorios
		if(pasoEjecutado.getPaso().getEnviarRecordatorio() > 0 && pasoEjecutado.getPaso().getFrecuenciaRecordatorio()!=null && pasoEjecutado.getPaso().getFrecuenciaRecordatorio()>0){
			Calendar end = Calendar.getInstance();
			if(pasoEjecutado.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.MESES.name())){
				end.add(Calendar.MONTH, pasoEjecutado.getPaso().getFrecuenciaRecordatorio().intValue());
			} else if(pasoEjecutado.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.SEMANAS.name())){
				end.add(Calendar.WEEK_OF_YEAR, pasoEjecutado.getPaso().getFrecuenciaRecordatorio().intValue());
			} else if(pasoEjecutado.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.DIAS.name())){
				end.add(Calendar.DAY_OF_YEAR, pasoEjecutado.getPaso().getFrecuenciaRecordatorio().intValue());
			} else if(pasoEjecutado.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.HORAS.name())){
				end.add(Calendar.HOUR_OF_DAY, pasoEjecutado.getPaso().getFrecuenciaRecordatorio().intValue());
			} else if(pasoEjecutado.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.MINUTOS.name())){
				end.add(Calendar.MINUTE, pasoEjecutado.getPaso().getFrecuenciaRecordatorio().intValue());
			}
			pasoEjecutado.setFechaSiguienteRecordatorio(end.getTime());
		}
		
		// Si no crea el paso normal
		persistencia.persistir(pasoEjecutado);
		
		// Refresca objeto para traer todos sus datos
		pasoEjecutado = (PasoEjecutado) persistencia.buscarPorId(new PasoEjecutado(), pasoEjecutado.getId());
		inicializarJsonPasoData(pasoEjecutado.getPaso());
		
		return pasoEjecutado;
	}
	
	
	/**
	 * Permite generar un nuevo paso para un proceso 
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param usuariosAsignados Listado de usuarios asignados al nuevo paso
	 * @param gruposAsignados Listaod de grupos asignados al nuevo paso
	 * @param siguientePaso Paso que se asignará al paso ejecutado
	 * @param pasoEjecutadoActual Paso ejecutado actual dentro del proceso
	 * @param opcion 1-avanzar, 2-devolver
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Paso ejecutado inicializado
	 */
	public PasoEjecutado generarNuevoPaso(IntegracionUsuarios integracionUsuarios, String[] usuariosAsignados, String[] gruposAsignados, Paso siguientePaso, PasoEjecutado pasoEjecutadoActual, int opcion, String direccionIp) {
		PasoEjecutado nuevoPaso = null; 

		// Valida si es una ejecución paralela y tiene etapas pendientes no genera el nuevo paso.  Se creará al finalizar la última etapa pendiente.
		// Solamente tiene en cuenta si esta encendida la casilla de unificar pasos proceso
		boolean paralelo = false;
		if(siguientePaso.getJsonPasoData().isUnificarPasosProceso()) {
			List<PasoEjecutado> pasosActuales = obtenerPasosEjecutadosPorProceso(pasoEjecutadoActual.getProcesoEjecutado().getId());
			if(!pasosActuales.isEmpty()) {
				paralelo = true;
			}
		}

		if(!paralelo) {
			
			// si se permite paralelo se crea una etapa siguiente por cada usuario o grupo, si no, se crea separado por comas la asignación y cualquiera de estos podría ejecutarla
			if(pasoEjecutadoActual.getPaso().getJsonPasoData().isPermitirParalelo()) {
				if(usuariosAsignados!=null) {
					for(String id:usuariosAsignados) {
						siguientePaso.setUsuarioAsignadoId(id);
						siguientePaso.setGrupoAsignadoId(null);
						nuevoPaso = inicializarNuevoPaso(siguientePaso, pasoEjecutadoActual, opcion, direccionIp);
						// Enviar notificaciones
						if(nuevoPaso.getPaso().getEnviarEmail()>0){
							nuevoPaso.setUsuarioAsignado(integracionUsuarios.obtenerUsuario(id));
							if(nuevoPaso.getPaso().getEnviarEmail()==1 || nuevoPaso.getPaso().getEnviarEmail()==3) {
								new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarCorreoProximoPaso(nuevoPaso);
							}
							if(nuevoPaso.getPaso().getEnviarEmail()==2 || nuevoPaso.getPaso().getEnviarEmail()==3) {
								new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarSMSProximoPaso(nuevoPaso);
							}
						}
					}
				} else if(gruposAsignados!=null) {
					for(String idGrupo:gruposAsignados) {
						siguientePaso.setUsuarioAsignadoId(null);
						siguientePaso.setGrupoAsignadoId(idGrupo);
						nuevoPaso = inicializarNuevoPaso(siguientePaso, pasoEjecutadoActual, opcion, direccionIp);
						// Enviar notificaciones
						if(nuevoPaso.getPaso().getEnviarEmail()>0){
							GrupoTO grupo = integracionUsuarios.obtenerGrupo(idGrupo, pasoEjecutadoActual.getIdOripEjecucion());
							if(grupo.getUsuarios()!=null){
								for(UsuarioTO user:grupo.getUsuarios()){
									nuevoPaso.setUsuarioAsignado(user);
									if(nuevoPaso.getPaso().getEnviarEmail()==1 || nuevoPaso.getPaso().getEnviarEmail()==3) {
										new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarCorreoProximoPasoGrupo(nuevoPaso);
									}
									if(nuevoPaso.getPaso().getEnviarEmail()==2 || nuevoPaso.getPaso().getEnviarEmail()==3) {
										new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarSMSProximoPasoGrupo(nuevoPaso);
									}
								}
							}
						}
					}
				}
			} else {
				if(usuariosAsignados!=null) {
					siguientePaso.setUsuarioAsignadoId("");
					for(String id:usuariosAsignados) {
						if(id!=null) {
							siguientePaso.setUsuarioAsignadoId(siguientePaso.getUsuarioAsignadoId()+id+",");
						}
					}
					// Quita la coma al final
					if(siguientePaso.getUsuarioAsignadoId()!=null && !siguientePaso.getUsuarioAsignadoId().isEmpty()) {
						siguientePaso.setUsuarioAsignadoId(siguientePaso.getUsuarioAsignadoId().substring(0, siguientePaso.getUsuarioAsignadoId().length()-1));
					}
						
					siguientePaso.setGrupoAsignadoId(null);
					nuevoPaso = inicializarNuevoPaso(siguientePaso, pasoEjecutadoActual, opcion, direccionIp);
					// Enviar notificaciones
					if(nuevoPaso.getPaso().getEnviarEmail()>0){
						for(String id:usuariosAsignados) {
							nuevoPaso.setUsuarioAsignado(integracionUsuarios.obtenerUsuario(id));
							if(nuevoPaso.getPaso().getEnviarEmail()==1 || nuevoPaso.getPaso().getEnviarEmail()==3) {
								new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarCorreoProximoPaso(nuevoPaso);
							}
							if(nuevoPaso.getPaso().getEnviarEmail()==2 || nuevoPaso.getPaso().getEnviarEmail()==3) {
								new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarSMSProximoPaso(nuevoPaso);
							}
						}
					}
				} else if(gruposAsignados!=null) {
					siguientePaso.setGrupoAsignadoId("");
					for(String id:gruposAsignados) {
						if(id!=null) {
							siguientePaso.setGrupoAsignadoId(siguientePaso.getGrupoAsignadoId()+id+",");
						}
					}
					// Quita la coma al final
					if(siguientePaso.getGrupoAsignadoId()!=null && !siguientePaso.getGrupoAsignadoId().isEmpty()) {
						siguientePaso.setGrupoAsignadoId(siguientePaso.getGrupoAsignadoId().substring(0, siguientePaso.getGrupoAsignadoId().length()-1));
					}

					siguientePaso.setUsuarioAsignadoId(null);
					nuevoPaso = inicializarNuevoPaso(siguientePaso, pasoEjecutadoActual, opcion, direccionIp);
					// Enviar notificaciones
					if(nuevoPaso.getPaso().getEnviarEmail()>0){
						for(String idGrupo:gruposAsignados) {
							GrupoTO grupo = integracionUsuarios.obtenerGrupo(idGrupo, pasoEjecutadoActual.getIdOripEjecucion());
							if(grupo.getUsuarios()!=null){
								for(UsuarioTO user:grupo.getUsuarios()){
									nuevoPaso.setUsuarioAsignado(user);
									if(nuevoPaso.getPaso().getEnviarEmail()==1 || nuevoPaso.getPaso().getEnviarEmail()==3) {
										new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarCorreoProximoPasoGrupo(nuevoPaso);
									}
									if(nuevoPaso.getPaso().getEnviarEmail()==2 || nuevoPaso.getPaso().getEnviarEmail()==3) {
										new NotificacionUtil(integracionUsuarios.getIntegracionCatalogos()).enviarSMSProximoPasoGrupo(nuevoPaso);
									}
								}
							}
						}
					}
				}
			}
		}
		return nuevoPaso;
	}
	
	
	
	/**
	 * Ejecuta un paso del proceso.  Guarda las entradas asociadas
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param pasoEjecutadoActual PasoEjecutado a ejecutar
	 * @param idProceso Identificador del proceso asociado
	 * @param entradasActuales Listado con las entradas a guardar
	 * @param usuarioActual Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @param opcion 1-avanzar, 2-devolver
	 * @param oripEjecucion ORIP de ejecución del proceso
	 * @return Paso Ejecutado que fue guardado
	 */
	public PasoEjecutado ejecutarPasoProceso(IntegracionUsuarios integracionUsuarios, PasoEjecutado pasoEjecutadoActual, long idProceso, List<ValorEntrada> entradasActuales, UsuarioTO usuarioActual, String direccionIp, int opcion, String oripEjecucion) {
		inicializarJsonPasoData(pasoEjecutadoActual.getPaso());

		String[] usuariosAsignados = null;

		PasoEjecutado nuevoPaso = null;

		Paso siguientePaso = null;
		if (opcion == 1) {
			// Obtiene el siguientepaso
			int order = pasoEjecutadoActual.getPaso().getOrdenPaso().intValue();
			List<Paso> proximosPasos = buscarPasosActivosProceso(idProceso);

			Iterator<Paso> iter2 = proximosPasos.iterator();
			while (iter2.hasNext()) {
				Paso paso = iter2.next();
				if (paso.getOrdenPaso().intValue() <= order) {
					iter2.remove();
				}
			}

			siguientePaso = establecerSiguientePaso(entradasActuales, pasoEjecutadoActual, proximosPasos);
			if(siguientePaso!=null && siguientePaso.getId()==-1l) {
				siguientePaso = null;
			}

		} else {
			// devolver paso
			siguientePaso = pasoEjecutadoActual.getPasoAnterior().getPaso();
			inicializarJsonPasoData(siguientePaso);

			pasoEjecutadoActual.setPasoDevuelto(true);
			// Fija el usuario anterior como usuario asignado
			usuariosAsignados = new String[1];
			usuariosAsignados[0] = pasoEjecutadoActual.getPasoAnterior().getUsuarioAsignadoId();
		}

		
		// Guarda paso ejecutado actual
		PasoEjecutado pasoEjecutado = guardarPasoEjecutado(integracionUsuarios, pasoEjecutadoActual, usuarioActual, siguientePaso, 0l, direccionIp, oripEjecucion);
		
		guardarValorEntradas(integracionUsuarios.getIntegracionCatalogos(), entradasActuales, pasoEjecutado, usuarioActual.getId(), direccionIp);
		
		if(siguientePaso!=null) {
			String[] gruposAsignados = null;

			List<String[]> resultado = estableceUsuariosProximoPaso(integracionUsuarios, siguientePaso, pasoEjecutado, usuarioActual, 1);
			if(resultado!=null && !resultado.isEmpty()) {
				usuariosAsignados = resultado.get(0);
				gruposAsignados = resultado.get(1);
			}
			
			guardarAsignacionesSiguientePaso(pasoEjecutado, usuariosAsignados, gruposAsignados);

			nuevoPaso = generarNuevoPaso(integracionUsuarios, usuariosAsignados, gruposAsignados, siguientePaso, pasoEjecutado, 1, direccionIp);
		}
		return nuevoPaso;
	}

	
	
	
	/**
	 * Establece los usuarios para el proximo paso
	 * @param integracionUsuarios Información de los usuarios y grupos de usuarios
	 * @param paso Paso sobre el cual evaluar los usuarios
	 * @param pasoEjecutadoAnterior Paso anterior
	 * @param usuarioActual Usuario actual
	 * @param nivelAcceso Indica el nivel de acceso que tiene el usuario actual
	 * @return Lista de identificadores de usuario
	 */
	public List<String[]> estableceUsuariosProximoPaso(IntegracionUsuarios integracionUsuarios, Paso paso, PasoEjecutado pasoEjecutadoAnterior, UsuarioTO usuarioActual, int nivelAcceso) {
		String[] usuariosAsignados = null;
		String[] gruposAsignados = null;
		
		if (paso != null) {
			if (paso.getUsuarioAsignadoId() != null && !paso.getUsuarioAsignadoId().isEmpty()) {
				usuariosAsignados = new String[1];
				usuariosAsignados[0] = paso.getUsuarioAsignadoId();
			}

			if (paso.getGrupoAsignadoId() != null && !paso.getGrupoAsignadoId().isEmpty()) {
				gruposAsignados = new String[1];
				gruposAsignados[0] = paso.getGrupoAsignadoId();
			}

			if (paso.getJsonPasoData() != null) {
				JsonPasoData jsonData = paso.getJsonPasoData();
				
				jsonData.setGruposSeleccionadosMostrar(new ArrayList<GrupoTO>());
				jsonData.setUsuariosSeleccionadosMostrar(new ArrayList<UsuarioTO>());
				if (jsonData.getAsignarUsuarioEspecial() > 0) {
					logger.debug("paso:" + paso.getNombre() + " asignarUsuarioEspecial PasoID:" + jsonData.getAsignarUsuarioEspecial());

					// Primero compara la tarea actual y luego empieza con las anteriores
					PasoEjecutado anterior = pasoEjecutadoAnterior;
					while (anterior != null) {
						if (anterior.getPaso().getId() == jsonData.getAsignarUsuarioEspecial()) {
							logger.debug("ultimopaso:" + anterior.getPaso().getNombre() + " usuarioejecucion:" + anterior.getIdUsuarioCreacion());
							paso.setUsuarioAsignadoId(anterior.getIdUsuarioCreacion());
							usuariosAsignados = new String[1];
							usuariosAsignados[0] = paso.getUsuarioAsignadoId();
							jsonData.getUsuariosSeleccionadosMostrar().add(integracionUsuarios.obtenerUsuario(paso.getUsuarioAsignadoId()));
							paso.setGrupoAsignadoId(null);
							anterior = null;
						} else {
							anterior = anterior.getPasoAnterior();
						}
					}

					if (paso.getUsuarioAsignadoId() == null || paso.getUsuarioAsignadoId().isEmpty()) {
						paso.setUsuarioAsignadoId(usuarioActual.getId());
						usuariosAsignados = new String[1];
						usuariosAsignados[0] = paso.getUsuarioAsignadoId();
						jsonData.getUsuariosSeleccionadosMostrar().add(integracionUsuarios.obtenerUsuario(paso.getUsuarioAsignadoId()));
						paso.setGrupoAsignadoId(null);
					}

				} else {
					if (jsonData.getGruposSeleccionados() != null) {
						
						// Inicializa gruposasignados con el tamaño de la seleccion si no se va a mostrar el combo de grupos
						if(jsonData.isOcultarSeleccionGrupos()) {
							gruposAsignados = new String[jsonData.getGruposSeleccionados().size()];
						}

						int i = 0;
						for (Object objetoGrupo : jsonData.getGruposSeleccionados()) {
							GrupoTO grupo = integracionUsuarios.obtenerGrupo(String.valueOf(objetoGrupo), null);
							if(grupo!=null) {
								if (!jsonData.isLimitarGruposSegunUsuarioActual() || nivelAcceso==1) {
									jsonData.getGruposSeleccionadosMostrar().add(grupo);
									
									// si el combo de grupos está oculto se preseleccionan los grupos seleccionados para la etapa
									if(jsonData.isOcultarSeleccionGrupos()) {
										gruposAsignados[i++] = grupo.getId();
									}
								} else {
									logger.debug("Limitar grupos:" + grupo.getNombre());
									UsuarioTO usuario = usuarioActual;
									if (pasoEjecutadoAnterior != null && pasoEjecutadoAnterior.getUsuarioAsignadoId() != null && !pasoEjecutadoAnterior.getUsuarioAsignadoId().isEmpty()) {
										usuario = integracionUsuarios.obtenerUsuario(pasoEjecutadoAnterior.getUsuarioAsignadoId());
									}
									// Valida que el grupo a insertar esté dentro de los grupos del usuario
									for (GrupoTO g : usuario.getGrupos()) {
										logger.debug("Grupo usuario:" + g.getNombre());
										if (g.getId().equals(grupo.getId())) {
											logger.debug("GrupoID:" + g.getId());
											jsonData.getGruposSeleccionadosMostrar().add(grupo);
											break;
										}
									}
								}
							}
						}
					}

					if (jsonData.getUsuariosSeleccionados() != null && !jsonData.getUsuariosSeleccionados().isEmpty()) {
						for (Object id : jsonData.getUsuariosSeleccionados()) {
							UsuarioTO usuario = integracionUsuarios.obtenerUsuario(String.valueOf(id));
							jsonData.getUsuariosSeleccionadosMostrar().add(usuario);
						}
					}

					if (jsonData.isMostrarUsuariosGrupos() && jsonData.getUsuariosSeleccionadosMostrar().isEmpty()) {
						if (!jsonData.isLimitarGruposSegunUsuarioActual() || nivelAcceso==1) {
							for (Object objetoGrupo : jsonData.getGruposSeleccionados()) {
								GrupoTO grupo = integracionUsuarios.obtenerGrupo(String.valueOf(objetoGrupo), usuarioActual.getOficina().getCirculo());
								// Puede no encontrar un grupo, si se asignó en algún tiempo pero después se eliminó el grupo
								if(grupo!=null && grupo.getUsuarios()!=null && !grupo.getUsuarios().isEmpty() && grupo.getUsuarios().get(0).getOficina().getCirculo().equals(usuarioActual.getOficina().getCirculo())) {
									jsonData.getUsuariosSeleccionadosMostrar().addAll(grupo.getUsuarios());
								}
							}
						} else {
							for (GrupoTO grupo : jsonData.getGruposSeleccionadosMostrar()) {
								GrupoTO grupo2 = integracionUsuarios.obtenerGrupo(String.valueOf(grupo.getId()), usuarioActual.getOficina().getCirculo());
								
								if(grupo!=null && grupo.getUsuarios()!=null && !grupo2.getUsuarios().isEmpty() && grupo2.getUsuarios().get(0).getOficina().getCirculo().equals(usuarioActual.getOficina().getCirculo())) {
									jsonData.getUsuariosSeleccionadosMostrar().addAll(grupo2.getUsuarios());
								}
							}
						}

					}
				}
				
				
				// ocultar usuario actual
				if(jsonData.isOcultarUsuarioActualSiguientePaso() && !jsonData.getUsuariosSeleccionadosMostrar().isEmpty()) {
					Iterator<UsuarioTO> iter = jsonData.getUsuariosSeleccionadosMostrar().iterator();
					while(iter.hasNext()) {
						UsuarioTO user = iter.next();
						if(user.getId().equals(usuarioActual.getId())){
							iter.remove();
						}
					}
				}

			}
		}
		List<String[]> resultado = new ArrayList<>();
		resultado.add(usuariosAsignados);
		resultado.add(gruposAsignados);
		return resultado;
	}

	
	
	/**
	 * Establece el siguiente paso del proceso a partir de la evaluacion de las entradas
	 * @param entradasActuales Lista con entradas para validar
	 * @param pasoEjecutadoActual Paso Ejecutado actual
	 * @param proximosPasos Listado de los próximos pasos del proceso para determinar cual es el siguiente
	 * @return Próximo Paso
	 */
	public Paso establecerSiguientePaso(List<ValorEntrada> entradasActuales, PasoEjecutado pasoEjecutadoActual, List<Paso> proximosPasos) {
		
		// Lista temporal con todos los valores de entradas de pasos pasadas + la actual
		List<ValorEntrada> list = new ArrayList<>();
		long idPasoAnterior = 0;
		
		try {
			if(pasoEjecutadoActual.getPasoAnterior()!=null && pasoEjecutadoActual.getPasoAnterior().getId()>0) {
				idPasoAnterior = pasoEjecutadoActual.getPasoAnterior().getId();
				while(idPasoAnterior>0) {
					PasoEjecutado pasoAnterior = (PasoEjecutado) buscarEntidadPorId(new PasoEjecutado(), idPasoAnterior);
					if(pasoAnterior!=null) {
						list.addAll(buscarValoresEntradas(idPasoAnterior));
						idPasoAnterior = 0; 
						if(pasoAnterior.getPasoAnterior()!=null) {
							idPasoAnterior = pasoAnterior.getPasoAnterior().getId(); 
						}
					}
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	
		
		List<ValorEntrada> allEntradas = new ArrayList<>();
		allEntradas.addAll(list);
	
		// Fija el valor de los checks
		if(entradasActuales != null) {
			for (ValorEntrada value : entradasActuales) {
				// Fija valor para los checkbox para poder evaluar su regla y sus campos
				// anidados
				if (value.isMostrar() && value.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())) {
					value.setValor("");
					if (value.getListaEntradas() != null && !value.getListaEntradas().isEmpty()) {
						for (String objeto : value.getListaEntradas()) {
							value.setValor(value.getValor() + objeto + ",");
						}
						value.setValor(value.getValor().substring(0, value.getValor().lastIndexOf(',')));
					}
				}
			}
			allEntradas.addAll(entradasActuales);
		}

		// Se eejcuta antes de de armar la lista solo con los mostrados ya que aca deben
		// evaluarse todos los entradas
		// Revisa cuales cammpos mostrar y cuales ocultar según los campos anidados
		validarEntradasAnidadas(allEntradas);

		if(entradasActuales != null) {
			// Agrega a una lista temporal los entradas que se van a evaluar en las reglas
			for (ValorEntrada value : entradasActuales) {
				// Solo agrega los campos mostrados actualmente
				if (value.isMostrar()) {
					list.add(value);
				}
			}
		}

		// Revisa las reglas para evaluar la siguiente paso
		return validarReglas(pasoEjecutadoActual, list, proximosPasos);
	}
	
	
	
	/**
	 * Establece los valores de las entradas ya guardadas de un paso
 	 * @param pasoEjecutadoId Identificador del paso ejecutado
	 * @return Lista de las entradas obtenidas
	 */
	public List<ValorEntrada> buscarValoresEntradas(long pasoEjecutadoId) {
		List<ValorEntrada> listaEntradas = persistencia.buscarValorEntradaPorPasoEjecutado(pasoEjecutadoId);
		for (ValorEntrada value : listaEntradas) {
			inicializarJsonEntradaData(value.getEntrada());
			
			getListaOpcionesEntradaTodos(value.getEntrada(), null);

			if (value.getValor() != null && !value.getValor().isEmpty()) {
				if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.FILE.name())) {
					String[] ids = value.getValor().split(",");
					List<Archivo> documentos = new ArrayList<>();
					for (String id : ids) {
						Archivo documento = (Archivo) persistencia.buscarPorId(new Archivo(), Long.valueOf(id));
						if (documento != null) {
							documentos.add(documento);
						}
					}
					value.setDocumentos(documentos);
				} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())) {
					String[] datas = value.getValor().split(",");
					for (String objeto : datas) {
						value.getListaEntradas().add(objeto);
					}
				}
			}

		}
		return listaEntradas;
	}

	
	/**
	 * Elimina un proceso sin ningun otro procedimiento adicional
	 * @param proceso Proceso a eliminar
	 */
	public void borrarProcesoSimple(Proceso proceso) {
		persistencia.eliminar(proceso);
	}
	
	
	/**
	 * Borra el proceso y todas sus versiones si no tiene ejecuciones
	 * @param procesoActual Proceso sobre el cual efectuar la operación
	 * @return número de procesos ejecutados encontrados.  0 indica que se eliminó correctamente. mayor a 1 indica que hay procesos ejecutados y no se puede borrar el proceso
	 */
	@SuppressWarnings("unchecked")
	public int borrarProceso(Proceso procesoActual) {
		int numeroProcesos = 0;
		int version = procesoActual.getNumeroMayorVersion();
		List<Proceso> procesos = (List<Proceso>)(Object) persistencia.buscarPorNamedQuery(Proceso.PROCESOS_POR_IDENTIFICADOR_ORDERDESC, procesoActual.getIdentificador());
		for(Proceso proceso:procesos) {
			List<ProcesoEjecutado> procesosEjecutados = (List<ProcesoEjecutado>)(Object)persistencia.buscarPorNamedQuery(ProcesoEjecutado.PROCESOEJECUTADO_POR_PROCESO, proceso.getId());
			if(procesosEjecutados.isEmpty()) {
				persistencia.eliminar(proceso);
				if(proceso.getNumeroMayorVersion() < version) {
					version = proceso.getNumeroMayorVersion();
				}
			} else {
				// aborta en el numero menor que logró encontrar sin procesos ejecutados
				numeroProcesos = procesosEjecutados.size();
				proceso.setNumeroMayorVersion(version);
				persistencia.actualizar(proceso);
				break;
			}
		}
		return numeroProcesos;
	}

	
	/**
	 * Elimina versiones anteriores de los procesos para eliminar datos basura
	 * Solo las elimina si no tienen procesos ejecutados 
	 * @param procesoActual Proceso sobre el cual efectuar la operación
	 * @return número de procesos ejecutados encontrados.  0 indica que se eliminó correctamente. Mayor a 1 indica que hay procesos ejecutados y no se puede borrar el proceso
	 */
	@SuppressWarnings("unchecked")
	public int eliminarVersionActual(Proceso procesoActual) {
		int numeroProcesos = 0;

		List<ProcesoEjecutado> procesosEjecutados = (List<ProcesoEjecutado>)(Object)persistencia.buscarPorNamedQuery(ProcesoEjecutado.PROCESOEJECUTADO_POR_PROCESO, procesoActual.getId());
		if(procesosEjecutados.isEmpty()) {
			persistencia.eliminar(procesoActual);

			List<Proceso> procesos = (List<Proceso>)(Object) persistencia.buscarPorNamedQuery(Proceso.PROCESOS_POR_IDENTIFICADOR_ORDERDESC, procesoActual.getIdentificador());
			if(!procesos.isEmpty()) {
				Proceso p = procesos.get(0);
				p.setArchivado(false);
				p.setActivo(true);
				actualizarEntidad(p);
			}
		} else {
			// no se puede eliminar si tiene procesos ejecutados
			numeroProcesos = procesosEjecutados.size();
		}
		return numeroProcesos;
	}
	
	
	/**
	 * Elimina versiones anteriores de los procesos para eliminar datos basura
	 * Solo las elimina si no tienen procesos ejecutados 
	 * @param data Proceso sobre el cual efectuar la operación
	 * @return Mensaje de error si no se logró eliminar la versión
	 */
	@SuppressWarnings("unchecked")
	public String eliminaVersionesAnterioresProceso(Proceso data) {
		String mensaje = "";
		int versionOriginal = data.getNumeroMayorVersion();
		int version = data.getNumeroMayorVersion();
		List<Proceso> procesos = (List<Proceso>)(Object) persistencia.buscarPorNamedQuery(Proceso.PROCESOS_POR_IDENTIFICADOR_ORDERDESC, data.getIdentificador());
		for(Proceso proceso:procesos) {
			if(proceso.isArchivado()) {
				List<ProcesoEjecutado> procesosEjecutados = (List<ProcesoEjecutado>)(Object) persistencia.buscarPorNamedQuery(ProcesoEjecutado.PROCESOEJECUTADO_POR_PROCESO, proceso.getId());
				logger.debug("El proceso "+proceso.getId()+" con versión "+proceso.getNumeroMayorVersion()+" tiene "+procesosEjecutados.size()+" procesos ejecutados.");
				if(procesosEjecutados.isEmpty()) {
					persistencia.eliminar(proceso);
					if(proceso.getNumeroMayorVersion() < version) {
						version = proceso.getNumeroMayorVersion();
					}
				} else {
					mensaje = "El proceso "+proceso.getId()+" con versión "+proceso.getNumeroMayorVersion()+" tiene "+procesosEjecutados.size()+" procesos ejecutados. No es posible eliminar esta versión.";
					logger.error(mensaje);
					// aborta en el numero menor que logró encontrar sin procesos ejecutados
					break;
				}
			}
		}
		try {
			data.setNumeroMayorVersion(version);
			persistencia.actualizar(data);
		} catch(Exception e) {
			data.setNumeroMayorVersion(versionOriginal);
		}
		return mensaje;
	}

	
	/**
	 * Busca un listado de procesos.  Usado en la lista de administración de procesos
	 * @param activo Indica si buscar registros activos
	 * @param filtro Cadena con filtro general a consultar
	 * @param ordenadoPor Orden a aplicar en la consulta
	 * @param limite Límite de registros a aplicar en la consulta
	 * @param buscarDesde Inicio de registros en la búsqueda (paginación)
	 * @return Listado con los procesos encontrados
	 */
	public List<Proceso> buscarProcesosPor(Boolean activo, String filtro, String ordenadoPor, int limite, int buscarDesde) {
		return persistencia.buscarProcesosPor(activo, filtro, ordenadoPor, limite, buscarDesde);
	}

	
	/**
	 * Cuenta los registros de un listado de procesos.  Usado en la lista de administración de procesos
	 * @param activo Indica si buscar registros activos
	 * @param filtro Cadena con filtro general a consultar
	 * @return Número de registros encontrados
	 */
	public int contarProcesosPor(Boolean activo, String filtro) {
		return persistencia.contarProcesosPor(activo, filtro);
	}

	
	/**
	 * Genera un nuevo proceso copiado de uno actual
	 *
	 * @param procesoId Proceso a generar
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @param archivarActual Indica si el proceso actual se archiva o continua activo
	 * @return Proceso generado
	 */
	@SuppressWarnings("unchecked")
	public Proceso generarNuevoProceso(long procesoId, String usuarioActualId, String direccionIp, boolean archivarActual) {
		Proceso proceso = buscarProcesoPorId(procesoId);
		if(proceso!=null) {

			long idProcesoAnterior = proceso.getId();
			persistencia.detach(proceso);

			if(proceso.getJsonProcesoData()!=null) {
				proceso.setDatosAdicionales(JsonUtil.transformarAJson(proceso.getJsonProcesoData()));
			}
			
			proceso.setId(0);
			proceso.setFechaModificacion(Calendar.getInstance().getTime());
			proceso.setIdUsuarioModificacion(usuarioActualId);
			proceso.setIpModificacion(direccionIp);
			proceso.setArchivado(false);
			proceso.setActivo(true);

			if(!archivarActual) {
				proceso.setIdentificador(persistencia.obtenerSecuenciaProceso());
				proceso.setNumeroMayorVersion(1);
			} else {
				proceso.setNumeroMayorVersion(proceso.getNumeroMayorVersion()+1);
			}
			
			persistencia.persistir(proceso);
			
			List<ProcesoCategoria> categorias = persistencia.buscarCategoriasPorProceso(idProcesoAnterior);
			
			for(ProcesoCategoria categoriasProceso:categorias) {
				ProcesoCategoria t = categoriasProceso;
				persistencia.detach(t);

				t.getId().setProcesoId(proceso.getId());
				t.setFechaModificacion(Calendar.getInstance().getTime());
				t.setIdUsuarioModificacion(usuarioActualId);
				t.setIpModificacion(direccionIp);
				persistencia.persistir(t);
			}
			
			List<ProcesoSecuencia> secuenciaProceso = (List<ProcesoSecuencia>) (Object) persistencia.buscarPorNamedQuery(ProcesoSecuencia.PROCESOSECUENCIA_POR_PROCESO, idProcesoAnterior);

			for(ProcesoSecuencia procesoSecuencia:secuenciaProceso) {
				ProcesoSecuencia t = procesoSecuencia;
				persistencia.detach(t);
				
				t.setId(0);
				t.setProceso(proceso);
				persistencia.persistir(t);
			}

			// Guarda relacion del id paso actual, con cual id quedo en el nueva paso
			Map<Long, Long> pasosmap = new HashMap<>();
			// Guarda relacion del id entrada actual, con cual id quedo en la nueva entrada
			Map<Long, Long> entradamap = new HashMap<>();

			List<Paso> pasos = buscarPasosActivosProceso(idProcesoAnterior);
			for(Paso objeto:pasos) {
				long idPasoAnterior = objeto.getId();
				Paso t = objeto;
				persistencia.detach(t);

				List<Entrada> listaEntradas = buscarEntradasActivasPaso(idPasoAnterior);
				t.setId(0);
				t.setFechaCreacion(Calendar.getInstance().getTime());
				t.setIdUsuarioCreacion(usuarioActualId);
				t.setIpCreacion(direccionIp);
				t.setFechaModificacion(null);
				t.setIdUsuarioModificacion(null);
				t.setIpModificacion(null);
				t.setProceso(proceso);
				
				if(t.getJsonPasoData()!=null) {
					t.setDatosAdicionales(JsonUtil.transformarAJson(t.getJsonPasoData()));
				}

				persistencia.persistir(t);
				logger.debug("idpaso:"+idPasoAnterior+" nuevoidpaso:"+t.getId());
				pasosmap.put(idPasoAnterior, t.getId());

				for(Entrada objetoEntrada:listaEntradas) {
					long identrada = objetoEntrada.getId();
					Entrada ent = objetoEntrada;
					persistencia.detach(ent);

					List<SecuenciaEntrada> listasecuencia = null;
					if(ent.getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())) {
						listasecuencia = (List<SecuenciaEntrada>) (Object) persistencia.buscarPorNamedQuery(SecuenciaEntrada.SECUENCIAENTRADA_POR_ENTRADA, ent.getId());
					}
					
					ent.setId(0);
					ent.setPaso(t);
					ent.setFechaCreacion(Calendar.getInstance().getTime());
					ent.setIdUsuarioCreacion(usuarioActualId);
					ent.setIpCreacion(direccionIp);
					ent.setFechaModificacion(null);
					ent.setIdUsuarioModificacion(null);
					ent.setIpModificacion(null);
					if(ent.isEntradaAnidada()) {
						ent.setEntradaAnidadaId(entradamap.get(ent.getEntradaAnidadaId()));
					}
					if(ent.getIdEntradaCopiar()!=null && ent.getIdEntradaCopiar()>0) {
						ent.setIdEntradaCopiar(entradamap.get(ent.getIdEntradaCopiar()));
					}
					persistencia.persistir(ent);
					entradamap.put(identrada, ent.getId());
					
					if(ent.getTipoEntrada().equals(TiposEntrada.SEQUENCE.name()) && !listasecuencia.isEmpty()) {
						for(SecuenciaEntrada mts:listasecuencia) {
							SecuenciaEntrada m = mts;
							persistencia.detach(m);

							m.setId(0);
							m.setEntrada(ent);
							m.setFechaModificacion(Calendar.getInstance().getTime());
							m.setIdUsuarioModificacion(usuarioActualId);
							m.setIpModificacion(direccionIp);
							persistencia.persistir(m);
						}
					}
					
					// Restricciones entradas
					List<RestriccionEntrada> restricciones = buscarRestriccionesEntrada(identrada);
					for(RestriccionEntrada restriccion:restricciones) {
						persistencia.detach(restriccion);

						restriccion.setId(0);
						restriccion.setEntrada(ent);
						restriccion.setFechaModificacion(Calendar.getInstance().getTime());
						restriccion.setIdUsuarioModificacion(usuarioActualId);
						restriccion.setIpModificacion(direccionIp);
						persistencia.persistir(restriccion);
					}
				}
				
			}
			
			generarNuevoProcesoAsignacionUsuarios(idProcesoAnterior, pasosmap);

			// Pasos antiguos
			logger.debug("idproceso:"+idProcesoAnterior);
			List<Paso> listaPasos = buscarPasosActivosProceso(idProcesoAnterior);
			logger.debug("pasos encontrados:"+listaPasos.size());
			for(Paso objeto:listaPasos) {
				List<Regla> listaReglas = persistencia.buscarReglasActivasPorPaso(objeto.getId());
				logger.debug("pasoid:"+objeto.getId()+" reglas encontradas:"+listaReglas.size());
				if(!listaReglas.isEmpty()) {
					Long nuevoPaso = pasosmap.get(objeto.getId());
					if(nuevoPaso!=null) {
						Paso t = buscarPasoPorId(nuevoPaso);
						for(Regla re:listaReglas) {
							persistencia.detach(re);

							List<CondicionRegla> detalles = persistencia.buscarCondicionReglaPorRegla(re.getId());
							re.setId(0);
							re.setPaso(t);
							Long siguientePaso = pasosmap.get(re.getSiguientePasoId());
							logger.debug("siguientepasoid:"+re.getSiguientePasoId()+" nuevosiguientepasoid:"+siguientePaso);
							if(siguientePaso!=null) {
								re.setSiguientePasoId(siguientePaso);
								re.setFechaCreacion(Calendar.getInstance().getTime());
								re.setIdUsuarioCreacion(usuarioActualId);
								re.setIpCreacion(direccionIp);
								re.setFechaModificacion(null);
								re.setIdUsuarioModificacion(null);
								re.setIpModificacion(null);
								persistencia.persistir(re);
								
								for(CondicionRegla det:detalles) {
									persistencia.detach(det);
									
									det.setId(0);
									det.setRegla(re);
	
									Long nuevaEntrada = entradamap.get(det.getEntrada().getId());
									if(nuevaEntrada!=null) {
										Entrada md = buscarEntradaPorId(nuevaEntrada);
										if(md!=null) {
											det.setEntrada(md);
											det.setFechaCreacion(Calendar.getInstance().getTime());
											det.setIdUsuarioCreacion(usuarioActualId);
											det.setIpCreacion(direccionIp);
											det.setFechaModificacion(null);
											det.setIdUsuarioModificacion(null);
											det.setIpModificacion(null);
											persistencia.persistir(det);
										}
									}
								}
							}
						}
					}
				}
			}

			// Archivo version anterior
			if(archivarActual) {
				Proceso procesoAnterior = buscarProcesoPorId(idProcesoAnterior);
				
				procesoAnterior.setArchivado(true);
				procesoAnterior.setActivo(false);
				procesoAnterior.setFechaModificacion(Calendar.getInstance().getTime());
				procesoAnterior.setIdUsuarioModificacion(usuarioActualId);
				procesoAnterior.setIpModificacion(direccionIp);

				if(procesoAnterior.getJsonProcesoData()!=null) {
					procesoAnterior.setDatosAdicionales(JsonUtil.transformarAJson(procesoAnterior.getJsonProcesoData()));
				}
				persistencia.actualizar(procesoAnterior);
			}
		}
		return proceso;
	}
	
	
	
	/**
	 * Asignacion de usuarios y pasos al generar nueva version
	 * 
	 * @param idProceso Identificador proceso
	 * @param pasosmap Mapa de identificadores pasos nuevos vs antiguos
	 */
	private void generarNuevoProcesoAsignacionUsuarios(long idProceso, Map<Long, Long> pasosmap) {
		List<Paso> listaPasos = buscarPasosActivosProceso(idProceso);
		for(Paso objeto:listaPasos) {
			if(objeto.getJsonPasoData().getAsignarUsuarioEspecial()>0) {
				Long nuevoid = pasosmap.get(objeto.getId());
				Paso nuevopaso = buscarPasoPorId(nuevoid);

				Long nuevoidasignado = pasosmap.get(objeto.getJsonPasoData().getAsignarUsuarioEspecial());
				nuevopaso.getJsonPasoData().setAsignarUsuarioEspecial(nuevoidasignado);
				nuevopaso.setDatosAdicionales(JsonUtil.transformarAJson(nuevopaso.getJsonPasoData()));
				persistencia.actualizar(nuevopaso);
			}
		}
	}

	
    /**
     * Guarda todos los detalles de un proceso
     * @param proceso Proceso a guardar
     * @param categoriasSeleccionadas Categorias asociadas al proceso
     * @param usuarioActualId Identificador del Usuario actual
     * @param direccionIp Dirección IP del usuario actual
     */
    public void guardarProceso(Proceso proceso, Long[] categoriasSeleccionadas, String usuarioActualId, String direccionIp){
		persistencia.guardarProceso(proceso, categoriasSeleccionadas, usuarioActualId, direccionIp);
	}

	
	/**
	 * Elimina una secuencia de una entrada
	 * @param secuenciaEntrada Secuencia a eliminar
	 */
	public void eliminarSecuenciaEntrada(SecuenciaEntrada secuenciaEntrada) {
		if(secuenciaEntrada.getId()!=0) {
			this.persistencia.eliminar(secuenciaEntrada);
		}
	}

	
	/**
	 * Busca las secuencias de una entrada de este tipo 
	 * @param entradaId Identificador de la entrada a consultar
	 * @return Lista de secuencias asociadas
	 */
	@SuppressWarnings("unchecked")
	public List<SecuenciaEntrada> buscarSecuenciaEntrada(long entradaId) {
		List<SecuenciaEntrada> resultado = new ArrayList<>();
		if(entradaId > 0) {
			resultado = (List<SecuenciaEntrada>) (Object) this.persistencia.buscarPorNamedQuery(SecuenciaEntrada.SECUENCIAENTRADA_POR_ENTRADA, entradaId);
		}
		return resultado;
	}
	
	
	/**
	 * Guarda las secuencias asociadas a una entrada
	 * @param secuenciaEntrada Lista con las secuencias de entradas a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarSecuenciasEntrada(List<SecuenciaEntrada> secuenciaEntrada, String usuarioActualId, String direccionIp) {
		persistencia.guardarSecuenciasEntrada(secuenciaEntrada, usuarioActualId, direccionIp);
	}


	/**
	 * Elimina una secuencia de un proceso
	 * @param secuencia Secuencia a eliminar
	 */
	public void eliminarSecuenciaProceso(ProcesoSecuencia secuencia) {
		if(secuencia.getId()!=0) {
			persistencia.eliminar(secuencia);
		}
	}

	/**
	 * Busca las secuencia de proceso asociadas a un proceso
	 * @param procesoId Identificador del proceso a consultar
	 * @return Lista de secuencias obtenidas
	 */
	@SuppressWarnings("unchecked")
	public List<ProcesoSecuencia> buscarSecuenciaProceso(long procesoId) {
		List<ProcesoSecuencia> resultado = new ArrayList<>();
		if(procesoId > 0) {
			resultado = (List<ProcesoSecuencia>) (Object) persistencia.buscarPorNamedQuery(ProcesoSecuencia.PROCESOSECUENCIA_POR_PROCESO, procesoId);
		}
		return resultado;
	}

	
	/**
	 * Guarda las secuencias asociadas a un proceso
	 * @param secuenciaProceso Lista con las secuencias de proceso a guardar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarSecuenciasProceso(List<ProcesoSecuencia> secuenciaProceso, String usuarioActualId, String direccionIp) {
		persistencia.guardarSecuenciasProceso(secuenciaProceso, usuarioActualId, direccionIp);
	}


	/**
	 * Busca los procesos ejecutados de un proceso
	 * @param procesoId Identificador del proceso a consultar
	 * @return Lista de Procesos ejecutados obtenidos
	 */
	@SuppressWarnings("unchecked")
	public List<ProcesoEjecutado> buscarProcesosEjecutadosProceso(long procesoId) {
		List<ProcesoEjecutado> resultado = new ArrayList<>();
		if(procesoId > 0) {
			resultado = (List<ProcesoEjecutado>)(Object) persistencia.buscarPorNamedQuery(ProcesoEjecutado.PROCESOEJECUTADO_ACTIVOS_POR_PROCESO, procesoId);
		}
		return resultado;
	}

	/**
	 * Busca todos los pasos activos por un proceso dado
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los Pasos encontrados
	 */
	public List<Paso> buscarPasosActivosProceso(long procesoId) {
		List<Paso> resultado = new ArrayList<>();
		if(procesoId > 0) {
			resultado = this.persistencia.buscarPasosActivosPorProceso(procesoId);
			for(Paso paso:resultado) {
				inicializarJsonPasoData(paso);
			}
		}
		return resultado;
	}


	/**
	 * Busca las entradas activas de un paso dado
	 * @param pasoId Identificador del paso
	 * @return Listado con las entradas encontradas
	 */
	public List<Entrada> buscarEntradasActivasPaso(long pasoId) {
		List<Entrada> resultado = new ArrayList<>();
		if(pasoId > 0) {
			resultado = this.persistencia.buscarEntradasActivasPorPaso(pasoId);
			for(Entrada entrada:resultado) {
				inicializarJsonEntradaData(entrada);
			}
		}
		return resultado;
	}

	
	/**
	 * Busca las entradas activas de todos los pasos de un proceso
	 * @param procesoId Identificador del proceso al cual 
	 * @return Lista de entradas obtenidas
	 */
	@SuppressWarnings("unchecked")
	public List<Entrada> buscarEntradasActivasProceso(long procesoId) {
		List<Entrada> resultado = new ArrayList<>();
		if(procesoId > 0) {
			resultado = (List<Entrada>)(Object)this.persistencia.buscarPorNamedQuery(Entrada.ENTRADA_ACTIVAS_POR_PROCESO, procesoId);
			for(Entrada entrada:resultado) {
				inicializarJsonEntradaData(entrada);
			}
		}
		return resultado;
	}


	/**
	 * Busca todos los pasos ejecutados de un proceso ejecutado
	 * @param procesoEjecutadoId Identificador del proceso a consultar
	 * @return Lista con los Pasos Ejecutados obtenidos
	 */
	@SuppressWarnings("unchecked")
	public List<PasoEjecutado> buscarPasosEjecutadosAnteriores(long procesoEjecutadoId) {
		List<PasoEjecutado> resultado = new ArrayList<>();
		if(procesoEjecutadoId > 0) {
			resultado = (List<PasoEjecutado>)(Object) persistencia.buscarPorNamedQuery(PasoEjecutado.PASOEJECUTADO_ANTERIORES, procesoEjecutadoId);
		}
		return resultado;
	}

	
	/**
	 * Busca las categorías asociadas a un proceso
	 * @param procesoId Identificador del Proceso a consultar
	 * @return Listado con los procesos categoria encontrados
	 */
	public List<ProcesoCategoria> buscarCategoriasProceso(long procesoId) {
		List<ProcesoCategoria> resultado = new ArrayList<>();
		if(procesoId > 0) {
			resultado = this.persistencia.buscarCategoriasPorProceso(procesoId);
		}
		return resultado;
	}
	
	
	/**
	 * Busca las reglas asociadas a un paso
	 * @param pasoId Identificador del paso a consultar
	 * @return Lista de reglas obtenidas
	 */
	public List<Regla> buscarReglasPaso(long pasoId){
		List<Regla> reglas = this.persistencia.buscarReglasActivasPorPaso(pasoId);
		for(Regla r:reglas){
			if(r.getSiguientePasoId()!=null && r.getSiguientePasoId()>0) {
				r.setSiguientePaso(buscarPasoPorId(r.getSiguientePasoId()));
			}
			r.setDetalles(this.persistencia.buscarCondicionReglaPorRegla(r.getId()));
		}
		return reglas;
	}

	
	/**
	 * Guarda un paso asociado a un proceso
	 * @param paso Paso a guardar
	 * @param proceso Proceso asociado al paso
	 * @param pasosListSize Tamaño de lista de pasos actual para indicar el orden que se guarda para el paso
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
    public void guardarPaso(Paso paso, Proceso proceso, int pasosListSize, String usuarioActualId, String direccionIp){
		persistencia.guardarPaso(paso, proceso, pasosListSize, usuarioActualId, direccionIp);
	}
    
    
	/**
	 * Guarda una entrada asociada a un paso
	 * @param entrada Entrada a guardar
	 * @param paso Paso asociado a la entrada
	 * @param entradas Lista de entradas con metadatos del paso Lista de entradas con metadatos del pasoListSize Tamaño de lista de entradas actual
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
    public void guardarEntrada(Entrada entrada, Paso paso, int entradas, String usuarioActualId, String direccionIp){
		persistencia.guardarEntrada(entrada, paso, entradas, usuarioActualId, direccionIp);
		inicializarJsonEntradaData(entrada);
	}

	/**
	 * Elimina una entrada y todas sus reglas asociadas
	 * @param entrada Entrada a eliminar
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
    public void eliminarEntrada(Entrada entrada, String usuarioActualId, String direccionIp) {
    	persistencia.eliminarEntrada(entrada, usuarioActualId, direccionIp);
    }


	/**
	 * Guarda una regla y sus condiciones
	 * @param regla Regla a guardar
	 * @param paso Paso asociado a la regla
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarRegla(Regla regla, Paso paso, String usuarioActualId, String direccionIp){
		persistencia.guardarRegla(regla, paso, usuarioActualId, direccionIp);
	}
	
	
	/**
	 * Busca las restricciones de una entrada
	 * @param entradaId Identificador de la entrada a consultar
	 * @return Lista de restricciones de la entrada consultada
	 */
	@SuppressWarnings("unchecked")
	public List<RestriccionEntrada> buscarRestriccionesEntrada(long entradaId) {
		List<RestriccionEntrada> resultado = new ArrayList<>();
		if(entradaId > 0) {
			resultado = (List<RestriccionEntrada>)(Object)this.persistencia.buscarPorNamedQuery(RestriccionEntrada.RESTRICCION_POR_ENTRADA, entradaId);
		}
		return resultado;
	}

	/**
	 * Guarda las restricciones asociadas a una entrada
	 * @param restricciones Lista de restricciones a guardar
	 * @param entrada Entrada asociada a las restricciones
	 * @param usuarioActualId Identificador del Usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarRestriccionesEntrada(List<RestriccionEntrada> restricciones, Entrada entrada, String usuarioActualId, String direccionIp){
		persistencia.guardarRestriccionesEntrada(restricciones, entrada, usuarioActualId, direccionIp);
	}

	/**
	 * Elimina una restricción de una entrada
	 * @param restriccionEntrada Restriccion de entrada a eliminar
	 */
	public void eliminarRestriccionEntrada(RestriccionEntrada restriccionEntrada) {
		if(restriccionEntrada.getId()!=0) {
			persistencia.eliminar(restriccionEntrada);
		}
	}

	/**
	 * Obtiene un listado de radicados para componentes autocomplete
	 * @param texto Texto con la consulta del radicado 
	 * @param filtros Mapa con los filtros de la consulta
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param grupos Lista de grupos asignados por los cuales filtrar
	 * @return Lista de cadenas con las sugerencias de radicados
	 */
	public List<String> obtenerSugerenciasRadicados(String texto, Map<String, Object> filtros, int tipoConsulta, List<String> grupos) {
		return persistencia.obtenerSugerenciasRadicados(texto, filtros, tipoConsulta, grupos);
	}

	
	/**
	 * Busca los pasos ejecutados dados unos filtros.  Usado en la consulta principal de la bandeja de entrada de los usuarios
	 * @param filtros Mapa con los filtros a aplicar
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param entradas Lista de entradas con metadatos del paso
	 * @param grupos Lista de grupos asignados sobre los cuales consultar los pasos
	 * @param ordenadoPor Orden a aplicar en la consulta
	 * @param limite Límite de registros a aplicar en la consulta
	 * @param buscarDesde Inicio de registros en la búsqueda (paginación)
	 * @return Listado con los pasos encontrados
	 */
	public List<PasoEjecutado> buscarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entradas, List<String> grupos, String ordenadoPor, int limite, int buscarDesde) {
		return persistencia.buscarPasosEjecutadosPor(filtros, tipoConsulta, entradas, grupos, ordenadoPor, limite, buscarDesde);
	}

	/**
	 * Cuenta los pasos ejecutados dados unos filtros.  Usado en la consulta principal de la bandeja de entrada de los usuarios
	 * @param filtros Mapa con los filtros a aplicar
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param entradas Lista de entradas con metadatos del paso
	 * @param grupos Lista de grupos asignados sobre los cuales consultar los pasos
	 * @return Número de registros encontrados
	 */
	public int contarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entradas, List<String> grupos) {
		return persistencia.contarPasosEjecutadosPor(filtros, tipoConsulta, entradas, grupos);
	}
	
	
    /**
     * Permite guardar un listado de entradas asociadas a un paso ejecutado
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
     * @param entradas Entradas a guardar
     * @param pasoEjecutado Paso ejecutado asociado
     * @param usuarioActualId Identificador del usuario
     * @param direccionIp Direccion IP del usuario
     */
    public void guardarValorEntradas(IntegracionCatalogos integracionCatalogos, List<ValorEntrada> entradas, PasoEjecutado pasoEjecutado, String usuarioActualId, String direccionIp){
    	
    	if(entradas!=null) {
			for (ValorEntrada value : entradas) {
				if (value.getId() == 0) {
	
					if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.PLANTILLA_DOCUMENTO.name()) || (!value.isMostrar() && !value.getEntrada().isEntradaOculta())) {
						value.setValor(null);
					}
	
					
					// Las Secuencias se generan así estén ocultas en el formulario, pero solo si están marcadas como entrada oculta
					if((!value.isMostrar() && value.getEntrada().isEntradaOculta()) || value.isMostrar()) {
						if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.SEQUENCE.name())) {
							value.setValor(secuenciasControl.obtenerEntradaValorSecuencia(value.getEntrada().getId(), true));
						} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.SECUENCIA_GLOBAL.name())) {
							value.setValor(secuenciasControl.obtenerValorSecuenciaGlobal(value.getEntrada().getParametroOpcionesId(), true));
						}
					}
	
					if (value.isMostrar()) {
						if(value.getEntrada().getTipoEntrada().equals(TiposEntrada.ARCHIVO_AUTOMATICO.name())) {
							// Consulta el último documento cargado
							Archivo documento = null;
							List<PasoEjecutado> pasosAnteriores = buscarPasosEjecutadosAnteriores(pasoEjecutado.getProcesoEjecutado().getId());
	
							for(PasoEjecutado pasoE:pasosAnteriores) {
								for(ValorEntrada valorEntrada:pasoE.getEntradas()) {
									if(valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.FILE.name())){
										String[] ids = valorEntrada.getValor().split(",");
										for (String id : ids) {
											documento = (Archivo) buscarEntidadPorId(new Archivo(), Long.valueOf(id));
										}
									}
								}
							}
							if (documento != null) {
								byte[] archivoFinal = generarDocumento(integracionCatalogos, entradas, documento);
								guardarArchivo(integracionCatalogos, pasoEjecutado, entradas, archivoFinal, documento.getNombre(), documento.getTipoArchivo(), usuarioActualId, direccionIp);
							}
						} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.SALIDA_REPORTE.name())) {
							// se carga sticker a owcc
							try {
								ByteArrayOutputStream baos = generarPDFSalidaReporte(value);
								String nombre = value.getEntrada().getNombre()+"_"+pasoEjecutado.getProcesoEjecutado().getSecuenciaProceso()+".pdf";
	
								String ids = guardarArchivo(integracionCatalogos, pasoEjecutado, entradas, baos.toByteArray(), nombre, "pdf", usuarioActualId, direccionIp);
								value.setValor(ids);
								if(value.getValor().isEmpty()) {
									value.setValor("0");
								}
								value.setValorEntradaTexto(nombre);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.FILE.name())) {
							if(value.getArchivoCargado()!=null && value.getArchivoCargado().getSize()>0) {
								byte[] archivoBytes = value.getArchivoCargado().getContents();
								String archivoNombre = value.getArchivoCargado().getFileName();
								String archivoTipo = value.getArchivoCargado().getContentType();
	
								String ids = guardarArchivo(integracionCatalogos, pasoEjecutado, entradas, archivoBytes, archivoNombre, archivoTipo, usuarioActualId, direccionIp);
								if (!ids.isEmpty()) {
									value.setValor(ids);
								}
							}
						} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())) {
							value.setValor("");
							value.setValorEntradaTexto("");
							if (value.getListaEntradas() != null && !value.getListaEntradas().isEmpty()) {
								for (String objeto : value.getListaEntradas()) {
									value.setValor(value.getValor() + objeto + ",");
	
									// Por cada item seleccionado guarda el texto correspondiente
									for (NodoTO node : value.getEntrada().getListaOpciones()) {
										if (node.getId().equals(objeto)) {
											value.setValorEntradaTexto(value.getValorEntradaTexto() + node.getText() + ",");
										}
									}
	
								}
								value.setValor(value.getValor().substring(0, value.getValor().lastIndexOf(',')));
								value.setValorEntradaTexto(value.getValorEntradaTexto().substring(0, value.getValorEntradaTexto().lastIndexOf(',')));
							}
						} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.DATE.name())) {
							if (value.getValorEntradaFecha() != null) {
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								value.setValor(sdf.format(value.getValorEntradaFecha()));
							}
						} else if (value.getEntrada().getTipoEntrada().equals(TiposEntrada.SELECT.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO.name())
								|| value.getEntrada().getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name())|| value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name())) {
							// Guarda el texto asociado al valor del item seleccionado
							for (NodoTO node : value.getEntrada().getListaOpciones()) {
								if (node.getId().equals(value.getValor())) {
									// Si la opción está marcada como null no se guarda
									if(node.isMarkedNull()) {
										value.setValor(null);
									} else {
										value.setValorEntradaTexto(node.getText());
									}
									break;
								}
							}
						}
	
					}
	
					// Si despues de las validaciones el campo tiene valor, se guarda en la base de
					// datos
					if (value.getValor() != null && !value.getValor().isEmpty()) {
						// guarda de todas maneras el texto de la entrada en el otro valor para después consultar mas fácil
						if(value.getValorEntradaTexto()==null || value.getValorEntradaTexto().isEmpty()) {
							value.setValorEntradaTexto(value.getValor());
						}
						
						guardarValorEntrada(value, pasoEjecutado, usuarioActualId, direccionIp);
						
						// guarda consecutivo ee si lo trae dentro del proceso ejecutado
						if(value.getEntrada().getNombre()!=null && value.getEntrada().getNombre().equals(ConstantesCorrespondencia.NOMBRE_ENTRADA_CONSECUTIVOEE) && 
								pasoEjecutado.getProcesoEjecutado()!=null && (pasoEjecutado.getProcesoEjecutado().getSecuenciaProcesoSalida()==null || pasoEjecutado.getProcesoEjecutado().getSecuenciaProcesoSalida().isEmpty())){
							pasoEjecutado.getProcesoEjecutado().setSecuenciaProcesoSalida(value.getValor());
								actualizarEntidad(pasoEjecutado.getProcesoEjecutado());
						}
	
					}
				}
			}
    	}
    }

	
	/**
	 * Guarda un valor de entrada en la base de datos
	 * @param valorEntrada Valor de la entrada
	 * @param pasoEjecutado Paso Ejecutado asociado a la entrada
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 */
	public void guardarValorEntrada(ValorEntrada valorEntrada, PasoEjecutado pasoEjecutado, String usuarioActualId, String direccionIp) {
		if(valorEntrada!=null) {
			valorEntrada.setPasoEjecutado(pasoEjecutado);
			valorEntrada.setFechaCreacion(Calendar.getInstance().getTime());
			valorEntrada.setIdUsuarioCreacion(usuarioActualId);
			valorEntrada.setIpCreacion(direccionIp);
			persistencia.persistir(valorEntrada);
		}
	}
	
	
	/** 
	 * Valida si un proceso puede ejecutarse si tiene activa la restricción de tiempo
	 * @param proceso Proceso a validar
	 * @return true si se permite ejecutar el paso
	 */
	public boolean validarRestriccionTiempoEjecutado(Proceso proceso) {
		boolean permitirEjecutarPaso = true;
		List<ProcesoEjecutado> fi = persistencia.validarProcesosEjecutadosPorPeriodo(proceso);
		if (fi != null && !fi.isEmpty()) {
			permitirEjecutarPaso = false;
		}
		return permitirEjecutarPaso;

	}

	/**
	 * Elimina un proceso ejecutado. También elimina por cascade todos sus pasos ejecutados y entradas asociadas
	 * @param procesoEjecutado Proceso Ejecutado a eliminar
	 */
	public void eliminarProcesoEjecutado(ProcesoEjecutado procesoEjecutado) {
		if(procesoEjecutado!=null && procesoEjecutado.getId()!=0) {
			persistencia.eliminar(procesoEjecutado);
		}
	}
	
	
	/**
	 * Reactiva la última etapa de un proceso ejecutado para que pueda seguirse ejecutando 
	 * @param pasoEjecutado Paso Ejecutado a afectar
	 */
	public void reactivarProcesoEjecutado(PasoEjecutado pasoEjecutado) {
		if(pasoEjecutado!=null && pasoEjecutado.getId()!=0) {
			pasoEjecutado.setUltimoPaso(false);
			persistencia.actualizar(pasoEjecutado);
			pasoEjecutado.getProcesoEjecutado().setActivo(true);
			persistencia.actualizar(pasoEjecutado.getProcesoEjecutado());
		}
	}
	
	


	
	/**
	 * Guarda un archivo en base de datos (metadata) y en OWCC (fisico)
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param pasoEjecutado Paso Ejecutado actual
	 * @param entradas Lista de entradas con metadatos del proceso
	 * @param archivoBytes Bytes del archivo a guardar
	 * @param archivoNombre Nombre del archivo
	 * @param archivoTipo Tipo de archivo
	 * @param usuarioActualId Identificador del usuario actual
	 * @param direccionIp Dirección IP del usuario actual
	 * @return Identificador del archivo guardado
	 */
	public String guardarArchivo(IntegracionCatalogos integracionCatalogos, PasoEjecutado pasoEjecutado, List<ValorEntrada> entradas, byte[] archivoBytes, String archivoNombre, String archivoTipo, String usuarioActualId, String direccionIp) {
		String ids = "";
		try {
			if(archivoBytes!=null && archivoNombre!=null && !archivoNombre.isEmpty()) {
	            byte[] archivoPDF = convertirDocumentoAPDF(archivoBytes);
	            archivoNombre = archivoNombre.substring(0, archivoNombre.lastIndexOf('.'))+".pdf";
	            if(archivoPDF!=null) {
	            	
	                // Evalua entradas del proceso para enviar datos parametros
	        		List<PasoEjecutado> pasosAnteriores = null; 
	        		if(pasoEjecutado!=null && pasoEjecutado.getProcesoEjecutado()!=null) {
	        			pasosAnteriores = buscarPasosEjecutadosAnteriores(pasoEjecutado.getProcesoEjecutado().getId());
		        		// Lista temporal con todos los valores de entradas de pasos pasadas + la actual
		        		List<ValorEntrada> listaEntradas = new ArrayList<>();
		        		if (pasosAnteriores != null) {
		        			for (PasoEjecutado ti : pasosAnteriores) {
		        				ti.setEntradas(buscarValoresEntradas(ti.getId()));
		        				listaEntradas.addAll(ti.getEntradas());
		        			}
		        		}
		        		if(entradas!=null && !entradas.isEmpty()) {
		        			listaEntradas.addAll(entradas);
		        		}
		        		
		        		// Guardar archivo en Oracle Web Content
						TipoSalidaEnviarDocumento response = new IntegracionOWCC(integracionCatalogos).enviarDocumento(archivoNombre, "", archivoNombre, archivoPDF, pasoEjecutado.getIdOripEjecucion(), pasoEjecutado, listaEntradas);
						if (response != null && response.getDID() != null && !response.getDID().isEmpty()) {
							// Guardar metadatos en BD
							Archivo archivo = new Archivo();
							archivo.setActivo(true);
							archivo.setFechaCreacion(Calendar.getInstance().getTime());
							archivo.setIdUsuarioCreacion(usuarioActualId);
							archivo.setIpCreacion(direccionIp);
							archivo.setNombre(archivoNombre);
							archivo.setTamano(archivoBytes.length);
							archivo.setTipoArchivo(archivoTipo);
							archivo.setIdentificadorExterno(response.getDID());
							archivo.setPasoEjecutado(pasoEjecutado);
							persistencia.persistir(archivo);
							ids = String.valueOf(archivo.getId());
						}
	        		}
	            }
			}
		} catch (Exception e) {
			logger.error("Error guardando archivo", e);
		}
		return ids;
	}
	
	
	
	/**
	 * Envia la notificacion de correspondencia al servicio web 
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param identificador Identioficador del proceso
	 * @param guia Número de guía de envío
	 * @param fechaEnvio Fecha de envío de la guía
	 */
	public void enviarNotificarCorrespondencia(IntegracionCatalogos integracionCatalogos, String identificador, String guia, String fechaEnvio) {
		try {
			IntegracionNotificadorCorrespondencia integracionNotificadorCorrespondencia = new IntegracionNotificadorCorrespondencia(integracionCatalogos);
			integracionNotificadorCorrespondencia.notificarCorrespondencia(identificador, guia, fechaEnvio);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}


	/**
	 * Genera los bytes de un pdf para una salida de reporte. 
	 * @param valorEntrada Puede estar guardado o no en la base de datos desde que tenga el resto de datos cargados
	 * @return Bytes con el PDF de salida
	 * @throws JRException
	 */
	public ByteArrayOutputStream generarPDFSalidaReporte(ValorEntrada valorEntrada) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("logo_60_small", this.getClass().getResourceAsStream("../reportes/logo_60_small.png"));
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
			if(valorEntrada!=null && valorEntrada.getValor()!=null && !valorEntrada.getValor().isEmpty() && valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.SALIDA_REPORTE.name())) {
				JsonObject json = new JsonObject();
	
				json.addProperty(ConstantesCorrespondencia.ENTRADA_ESPECIAL_FECHA_PROCESO, sdf.format(valorEntrada.getPasoEjecutado().getFechaCreacion()));
				json.addProperty(ConstantesCorrespondencia.ENTRADA_ESPECIAL_SECUENCIA_PROCESO, valorEntrada.getPasoEjecutado().getProcesoEjecutado().getSecuenciaProceso());
	
				// Busca los valores de las entradas
				List<ValorEntrada> entradas = buscarValoresEntradas(valorEntrada.getPasoEjecutado().getId());
				for(ValorEntrada value:entradas){
					if(value.getEntrada().getNombre()!=null && !value.getEntrada().getNombre().isEmpty()) {
						String texto = "";
	
						try {
							if(value.getEntrada().getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name())) {
								Parametro parametro = parametrosControl.buscarParametroPorId(value.getEntrada().getParametroOpcionesId());
								if(parametro.getOpcionesJson()!=null && !parametro.getOpcionesJson().isEmpty()){
									for(JsonOpcion opcion:parametro.getOpcionesJson()) {
										if(opcion.getId() == Integer.valueOf(value.getValor())) {
											texto = opcion.getNombre();
											break;
										}
									}
								}
							} else {
								// Otros tipos
								for(JsonOpcion opcion:value.getEntrada().getOpcionesJson()) {
									if(opcion.getId() == Integer.valueOf(value.getValor())) {
										texto = opcion.getNombre();
										break;
									}
								}
							}
						} catch(Exception e) {
							logger.error(e.getMessage());
						}
	
						if(texto==null || texto.isEmpty()) {
							texto = value.getValor(); 
						}
	
						json.addProperty(value.getEntrada().getNombre(), texto);
					}
	
				}
				
				String rawJsonData = new Gson().toJson(json);
			    
				ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());
			    JsonDataSource ds = new JsonDataSource(jsonDataStream);
				JasperPrint jasperPrint = JasperFillManager.fillReport(this.getClass().getResourceAsStream("../reportes/"+valorEntrada.getEntrada().getNombre()+".jasper"), parametros, ds);
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
				SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
				exporter.setConfiguration(configuration);
				exporter.exportReport();
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return baos;
	}


	/**
	 * Genera el reporte pdf para una salida de reporte.
	 * @param idValorEntrada Debe ser mayor a cero para buscar desde la base de datos el valor de la entrada
	 * @return Bytes con el reporte generado
	 * @throws JRException
	 */
	public ByteArrayOutputStream generarSalidaReporte(long idValorEntrada) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(idValorEntrada>0) {
			ValorEntrada valorEntrada = (ValorEntrada) buscarEntidadPorId(new ValorEntrada(), idValorEntrada);
			
			if(valorEntrada!=null && valorEntrada.getValor()!=null && !valorEntrada.getValor().isEmpty() && valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.SALIDA_REPORTE.name())) {
				baos = generarPDFSalidaReporte(valorEntrada);
			}
		}
		return baos;
	}

	
	/**
	 * Genera la planilla de distribución para el rol Distribución
	 * @param pasos Lista con los pasos ejecutados para traer metadatos
	 * @param orip Descripción de la ORIP
	 * @return Bytes con el archivo generado
	 * @throws JRException
	 */
	public ByteArrayOutputStream generarPlanillaDistribucion(List<PasoEjecutado> pasos, String orip) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, Object> parametros = new HashMap<>();
		
		JsonObject json = new JsonObject();
		json.addProperty("titulo", orip);

		JsonArray pr = new JsonArray();
		
		for(PasoEjecutado paso:pasos) {
			JsonObject detalle = new JsonObject(); 

			detalle.addProperty(ConstantesCorrespondencia.ENTRADA_ESPECIAL_SECUENCIA_PROCESO, paso.getProcesoEjecutado().getSecuenciaProceso());

			List<PasoEjecutado> pasosAnteriores = buscarPasosEjecutadosAnteriores(paso.getProcesoEjecutado().getId());

			// Lista temporal con todos los valores de entradas de pasos pasadas + la actual
			List<ValorEntrada> list = new ArrayList<>();
			if (pasosAnteriores != null) {
				for (PasoEjecutado ti : pasosAnteriores) {
					ti.setEntradas(buscarValoresEntradas(ti.getId()));
					list.addAll(ti.getEntradas());
				}
			}
			
			paso.setEntradas(list);

			for(ValorEntrada value:paso.getEntradas()){
				if(value.getEntrada().getNombre()!=null && !value.getEntrada().getNombre().isEmpty()) {
					inicializarJsonEntradaData(value.getEntrada());
					
					String texto = "";

					try {
						if(value.getEntrada().getTipoEntrada().equals(TiposEntrada.LISTA_PREDEFINIDA.name()) || value.getEntrada().getTipoEntrada().equals(TiposEntrada.RADIO_LISTA_PREDEFINIDA.name())) {
							Parametro parametro = parametrosControl.buscarParametroPorId(value.getEntrada().getParametroOpcionesId());
							if(parametro.getOpcionesJson()!=null && !parametro.getOpcionesJson().isEmpty()){
								for(JsonOpcion opcion:parametro.getOpcionesJson()) {
									if(opcion.getId() == Integer.valueOf(value.getValor())) {
										texto = opcion.getNombre();
										break;
									}
								}
							}
						} else {
							// Otros tipos
							for(JsonOpcion opcion:value.getEntrada().getOpcionesJson()) {
								if(opcion.getId() == Integer.valueOf(value.getValor())) {
									texto = opcion.getNombre();
									break;
								}
							}
						}
					} catch(Exception e) {
						logger.error(e.getMessage());
					}

					if(texto==null || texto.isEmpty()) {
						texto = value.getValor(); 
					}

					detalle.addProperty(value.getEntrada().getNombre(), texto);
				}

			}
			pr.add(detalle);
		}

		json.addProperty("totalRegistros", pr.size());

		json.add("listaRadicados", pr);
		
		String rawJsonData = new Gson().toJson(json);
		logger.info("Info enviada reporte: " + rawJsonData);
	    
		ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());
	    JsonDataSource ds = new JsonDataSource(jsonDataStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(this.getClass().getResourceAsStream("../reportes/planilla_distribucion.jasper"), parametros, ds);
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return baos;
	}

	
	/**
	 * Toma un documento y genera otro documento reemplazando datos de las variables
	 * @param archivoOrigen Archivo a convertir
	 * @return bytes con el archivo en PDF
	 */
	private byte[] convertirDocumentoAPDF(byte[] archivoOrigen) {
		System.setProperty(HEADLESS_PROPERTY, "true"); 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			if(archivoOrigen!=null) {
		        ByteArrayInputStream is = new ByteArrayInputStream(archivoOrigen);
		        Document doc = new Document(is);
	        	doc.save(outputStream, SaveFormat.PDF);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        return outputStream.toByteArray();
	}
	
	/**
	 * Toma un documento y genera otro documento reemplazando datos de las variables
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param entradas Lista de entradas con metadatos
	 * @param archivoOrigen Archivo original
	 * @return bytes con el archivo nuevo
	 */
	public byte[] generarDocumento(IntegracionCatalogos integracionCatalogos, List<ValorEntrada> entradas, Archivo archivoOrigen) {
		System.setProperty(HEADLESS_PROPERTY, "true"); 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			if(archivoOrigen!=null) {
				if(entradas!=null) {
					logger.info(entradas.size());
				}
		        logger.info("Generando documento... "+archivoOrigen.getNombre());
		        
		        TipoSalidaObtenerArchivo response = new IntegracionOWCC(integracionCatalogos).obtenerArchivo(archivoOrigen.getIdentificadorExterno());
		        if(response!=null && response.getArchivo()!=null) {
			        ByteArrayInputStream is = new ByteArrayInputStream(response.getArchivo());
			        Document doc = reemplazarDatos(entradas, is);
			        if(doc!=null) {
			        	doc.save(outputStream, SaveFormat.DOC);
			        }
		        }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        return outputStream.toByteArray();
	}

	
	/**
	 * Toma una plantilla y genera un documento reemplazando datos de las variables
	 * @param integracionCatalogos Información de los catalogos con las URLs de los servicios web
	 * @param entradas Lista de entradas con metadatos
	 * @param plantillaId Identificador de la plantilla
	 * @return bytes del archivo con la plantilla generada
	 */
	public byte[] generarPlantilla(IntegracionCatalogos integracionCatalogos, List<ValorEntrada> entradas, long plantillaId) {
		byte[] data = null;
		System.setProperty(HEADLESS_PROPERTY, "true"); 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			if(entradas!=null) {
				logger.info(entradas.size());
			}
	        Parametro plantilla = parametrosControl.buscarParametroPorId(plantillaId);
	        logger.info("Generando plantilla... "+plantilla.getNombre());
	        
	        // Archivo de pruebas
			InputStream is = null;
			
			// Se toma la plantilla real si existe
	    	if(plantilla.getOpcionesJson()!=null && plantilla.getTipoParametro().equals(TiposParametros.PLANTILLA_DOCUMENTO.name()) && !plantilla.getOpcionesJson().isEmpty()) {
    			String idArchivo = plantilla.getOpcionesJson().get(0).getNombre();
    			Archivo archivoGuardado = parametrosControl.obtenerArchivoPorId(Long.valueOf(idArchivo));
    			if(archivoGuardado!=null) {
    				TipoSalidaObtenerArchivo response = new IntegracionOWCC(integracionCatalogos).obtenerArchivo(archivoGuardado.getIdentificadorExterno());
    				if(response!=null && response.getArchivo()!=null) {
    					is = new ByteArrayInputStream(response.getArchivo());
    				}
    			}
	    	}

	    	if(is!=null) {
		        Document doc = reemplazarDatos(entradas, is);
		        if(doc!=null) {
		        	doc.save(outputStream, SaveFormat.DOC);
		        }
		        data = outputStream.toByteArray();
	    	}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        return data;
	}
	
	
	/**
	 * Reemplaza datos de las variables en el documento
	 * @param entradas Lista de entradas con metadatos
	 * @param inputStream con documento de entrada
	 * @return Objeto Documento de aspose con los datos del documento
	 */
	private Document reemplazarDatos(List<ValorEntrada> entradas, InputStream inputStream) {
		Document doc = null;
		try {
			doc = new Document(inputStream);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if(entradas!=null && !entradas.isEmpty()) {
				doc.getRange().replace("#{"+ConstantesCorrespondencia.ENTRADA_ESPECIAL_FECHA_PROCESO+"}", sdf.format(entradas.get(0).getPasoEjecutado().getProcesoEjecutado().getFechaCreacion()), new FindReplaceOptions(FindReplaceDirection.FORWARD));
				doc.getRange().replace("#{"+ConstantesCorrespondencia.ENTRADA_ESPECIAL_SECUENCIA_PROCESO+"}", entradas.get(0).getPasoEjecutado().getProcesoEjecutado().getSecuenciaProceso(), new FindReplaceOptions(FindReplaceDirection.FORWARD));
			}

			// Busca los valores de las entradas
			for(ValorEntrada entrada:entradas) {
				if(entrada.getEntrada().getNombre()!=null && !entrada.getEntrada().getNombre().isEmpty()) {
					String texto = "";

					if(getTiposEntradasReglas().contains(entrada.getEntrada().getTipoEntrada())) {
						texto = entrada.getValorEntradaTexto();
					}
					if(texto==null || texto.isEmpty()) {
						texto = entrada.getValor(); 
					}

					if(texto!=null) {
						doc.getRange().replace("#{"+entrada.getEntrada().getNombre()+"}", texto, new FindReplaceOptions(FindReplaceDirection.FORWARD));
					}
				}
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return doc;
	}
	
	
	/**
	 * Permite consultar un proceso por su ID
	 * @param procesoId Identificador del proceso
	 */
	public Proceso buscarProcesoPorId(long procesoId) {
		Proceso proceso = (Proceso) persistencia.buscarPorId(new Proceso(), procesoId);
		if(proceso!=null) {
			inicializarJsonProcesoData(proceso);
		}
		return proceso;
	}

	
	/**
	 * Inicializa el valor del campo JSON del proceso
	 * @param proceso Proceso a trabajar
	 */
	public void inicializarJsonProcesoData(Proceso proceso) {
		if(proceso.getJsonProcesoData()==null) {
			proceso.setJsonProcesoData(JsonUtil.parseFromJson(JsonProcesoData.class, proceso.getDatosAdicionales()));
		}
	}

	
	/**
	 * Permite consultar un paso por su ID
	 * @param pasoId Identificador del paso
	 */
	public Paso buscarPasoPorId(long pasoId) {
		Paso paso = (Paso) persistencia.buscarPorId(new Paso(), pasoId);
		if(paso!=null) {
			inicializarJsonPasoData(paso);
		}
		return paso;
	}

	
	/**
	 * Inicializa el valor del campo JSON del paso
	 * @param paso Paso a trabajar
	 */
	public void inicializarJsonPasoData(Paso paso) {
		if(paso.getJsonPasoData()==null) {
			paso.setJsonPasoData(JsonUtil.parseFromJson(JsonPasoData.class, paso.getDatosAdicionales()));
		}
	}

	
	/**
	 * Permite consultar una entrada por su ID
	 * @param entradaId Identificador de la entrada
	 */
	public Entrada buscarEntradaPorId(long entradaId) {
		Entrada entrada = (Entrada) persistencia.buscarPorId(new Entrada(), entradaId);
		if(entrada!=null) {
			inicializarJsonEntradaData(entrada);
		}
		return entrada;
	}

	
	/**
	 * Inicializa el valor del campo JSON de la entrada
	 * @param entrada Entrada a trabajar
	 */
	public void inicializarJsonEntradaData(Entrada entrada) {
		try {
			if(entrada.getOpcionesJson()==null || entrada.getOpcionesJson().isEmpty()) {
			    Type listType = new TypeToken<List<JsonOpcion>>(){}.getType();
			    entrada.setOpcionesJson(new Gson().fromJson(entrada.getOpciones(), listType));
			    if(entrada.getOpcionesJson()==null) {
			    	entrada.setOpcionesJson(new ArrayList<>());
			    }
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public List<PasoEjecutado> buscarPasosAtrasados(Date fechaFin) {
		return persistencia.buscarPasosAtrasados(fechaFin);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public List<PasoEjecutado> buscarRecordatoriosPasos(Date fechaRecordatorio) {
		return persistencia.buscarRecordatoriosPasos(fechaRecordatorio);
	}

	
}

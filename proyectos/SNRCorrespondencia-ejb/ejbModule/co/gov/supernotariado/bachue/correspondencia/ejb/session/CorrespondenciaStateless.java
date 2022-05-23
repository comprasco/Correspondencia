package co.gov.supernotariado.bachue.correspondencia.ejb.session;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.CondicionRegla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.EntidadBase;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.EntidadGenerica;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Parametro;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Paso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Proceso;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoria;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoCategoriaPK;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ProcesoSecuencia;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Regla;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.RestriccionEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.SecuenciaGlobal;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.ValorEntrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.JsonUtil;


/**
 * Clase Stateless para manejar el acceso a persistencia
 */
@Stateless(name="CorrespondenciaStateless", mappedName="ejb/CorrespondenciaStateless")
@Local(CorrespondenciaStatelessLocal.class)
public class CorrespondenciaStateless implements CorrespondenciaStatelessLocal {
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(CorrespondenciaStateless.class);

	/** Cadena común para aplicar en consultas */
	private static final String TIPO_PARAMETRO = "tipoParametro";
	/** Cadena común para aplicar en consultas */
	private static final String NOMBRE_PARAMETRO = "nombreParametro";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_PROCESOID = "procesoId";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_PROCESOEJECUTADOID = "procesoEjecutadoId";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_USUARIOASIGNADO = "usuarioAsignado";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_CATEGORIA = "categoria";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_PASOID = "pasoId";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_MULTIFILTRO = "filtro";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_ACTIVO = "activo";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_ULTIMOPASO = "ultimoPaso";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_PASOSEJECUTADOSID = "pasosEjecutadosId";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_CONSECUTIVOSEE = "consecutivosEE";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_ORIP = "oripEjecucion";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_SECUENCIA = "filtroSecuencia";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_SECUENCIAINI = "filtroSecuenciaIni";
	/** Cadena común para aplicar en consultas */
	public static final String FILTRO_SECUENCIAFIN = "filtroSecuenciaFin";
	/** Cadena común para aplicar en consultas */
	private static final String WHERE1_1 = "WHERE 1 = 1 ";
	/** Cadena común para aplicar en consultas */
	private static final String AND_ACTIVO = "AND t.activo = :activo ";
	/** Cadena común para aplicar en consultas */
	private static final String AND_NOMBRE = "AND (LOWER(t.nombre) LIKE LOWER(:filtro) ";
	/** Hint para aplicar en algunas consultas para refrescar cache */
	private static final String CACHE_STOREMODE = "javax.persistence.cache.storeMode";
	
	/**
	 * Manejador de Persistencia con el cual se accede a la base de datos
	 */
	@PersistenceContext(unitName = "pu-snr-correspondencia")
	protected EntityManager em;

	/**
	 * Constructor
	 */
	public CorrespondenciaStateless() {
		super();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void detach(EntidadGenerica entidadGenerica) {
		em.detach(entidadGenerica);
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void detach(EntidadBase entidad) {
		em.detach(entidad);
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public EntidadGenerica buscarPorId(EntidadGenerica entidadGenerica, Object id) {
		return this.em.find(entidadGenerica.getClass(), id);
	}

	/**
	 * {@inheritDoc} 
	 */
	public long persistir(EntidadGenerica entidadGenerica) {
		em.persist(entidadGenerica);
		return entidadGenerica.getId();
	}

	/**
	 * {@inheritDoc} 
	 */
	public void persistir(EntidadBase entidadBase) {
		em.persist(entidadBase);
		em.flush();
	}

	
	/**
	 * {@inheritDoc} 
	 */
	public void eliminar(EntidadGenerica entidadGenerica) {
		EntidadGenerica t = em.merge(entidadGenerica);
		em.remove(t);
	}

	/**
	 * {@inheritDoc} 
	 */
	public void eliminar(EntidadBase entidadBase) {
		EntidadBase t = em.merge(entidadBase);
		em.remove(t);
	}

	/**
	 * {@inheritDoc} 
	 */
	public void actualizar(EntidadGenerica entidadGenerica) {
		em.merge(entidadGenerica);
	}

	/**
	 * {@inheritDoc} 
	 */
	public void actualizarSQL(String sqlUpdate) {
		em.createNativeQuery(sqlUpdate).executeUpdate();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<EntidadGenerica> findAll(Class type) {
		return (List<EntidadGenerica>) em.createQuery("select t from " + type.getSimpleName() + " t ORDER BY id").getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	public List<EntidadGenerica> buscarPorNamedQuery(String nombreConsulta, Object... args) {
		Query consulta = em.createNamedQuery(nombreConsulta);
		consulta.setHint(CACHE_STOREMODE, CacheStoreMode.REFRESH);
		
		consulta.setHint(QueryHints.REFRESH, HintValues.TRUE);
		
		for (int i = 1; i <= args.length; i++) {
			if (args[i - 1] != null && !args[i - 1].equals("")) {
				consulta.setParameter(i, args[i - 1]);
			}
		}
		return consulta.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> buscarParametrosActivosPorTipo(String tipoParametro) {
		Query query = em.createNamedQuery(Parametro.PARAMETRO_ACTIVOS_POR_TIPO);
		query.setParameter(TIPO_PARAMETRO, tipoParametro);
		return query.getResultList();
	}


	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> buscarParametrosActivosPorTipoNombre(String tipoParametro, String nombre) {
		Query query = em.createNamedQuery(Parametro.PARAMETRO_ACTIVOS_POR_TIPO_NOMBRE);
		query.setParameter(TIPO_PARAMETRO, tipoParametro);
		query.setParameter(NOMBRE_PARAMETRO, nombre);
		return query.getResultList();
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> buscarParametrosPor(Boolean activo, String tipoParametro, String ordenadoPor, int limite, int buscarDesde) {
		StringBuilder sql = new StringBuilder("SELECT p FROM Parametro p ");
		sql.append(WHERE1_1);
		if (activo!=null){
			sql.append("AND p.activo = :activo ");
		}
		if (tipoParametro!=null){
			sql.append("AND p.tipoParametro = :tipoParametro ");
		}
		if (ordenadoPor != null && !ordenadoPor.isEmpty()) {
			sql.append("ORDER BY p." + ordenadoPor);
		}
		Query query = em.createQuery(sql.toString());
		if (buscarDesde > 0) {
			query.setFirstResult(buscarDesde);
		}
		if (limite > 0) {
			query.setMaxResults(limite);
		}
		if (activo!=null){
			query.setParameter(FILTRO_ACTIVO, activo);
		}
		if (tipoParametro!=null){
			query.setParameter(TIPO_PARAMETRO, tipoParametro);
		}
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	public int contarParametrosPor(Boolean activo, String tipoParametro) {
		StringBuilder sql = new StringBuilder("SELECT count(p) FROM Parametro p ");
		sql.append(WHERE1_1);
		if (activo!=null){
			sql.append("AND p.activo = :activo ");
		}
		if (tipoParametro!=null){
			sql.append("AND p.tipoParametro = :tipoParametro ");
		}
		Query query = em.createQuery(sql.toString());
		if (activo!=null){
			query.setParameter(FILTRO_ACTIVO, activo);
		}
		if (tipoParametro!=null){
			query.setParameter(TIPO_PARAMETRO, tipoParametro);
		}
		return ((Long) query.getSingleResult()).intValue();
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Paso> buscarPasosActivosPorProceso(long procesoId) {
		Query query = em.createNamedQuery(Paso.PASO_ACTIVOS_POR_PROCESO_ORDEN);
		query.setParameter(FILTRO_PROCESOID, procesoId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Paso> buscarPasosActivosPorProcesoOrdenDesc(long procesoId) {
		Query query = em.createNamedQuery(Paso.PASO_ACTIVOS_POR_PROCESO_ORDEN_DESC);
		query.setParameter(FILTRO_PROCESOID, procesoId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PasoEjecutado> buscarPasosAtrasados(Date fechaFin) {
		Query query = em.createNamedQuery(PasoEjecutado.PASOEJECUTADO_POR_VENCIDAS);
		query.setParameter("fechaFin", fechaFin);
		return query.getResultList();
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PasoEjecutado> buscarRecordatoriosPasos(Date fechaRecordatorio) {
		Query query = em.createNamedQuery(PasoEjecutado.PASOEJECUTADO_POR_RECORDATORIO);
		query.setParameter("fechaProximoRecordatorio", fechaRecordatorio);
		return query.getResultList();
	}

	
	/**
	 * Arma la consulta para obtener los pasos ejecutados.  Usado en la consulta principal para obtener la bandeja del usuario 
	 * @param filtros Mapa con los filtros a aplicar
	 * @param contar Indica si se debe contar registros o consultarlos
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param entrada Lista de entradas con metadatos del paso
	 * @param grupos Lista de grupos asignados sobre los cuales consultar los pasos
	 * @return Cadena con el SQL de la consulta
	 */
	@SuppressWarnings("unchecked")
	private StringBuilder obtenerConsultaPasosEjecutados(Map<String, Object> filtros, boolean contar, int tipoConsulta, List<ValorEntrada> entrada, List<String> grupos) {
		StringBuilder gruposString = estableceFiltroMultipleIN(grupos, false);
		StringBuilder consulta = new StringBuilder("SELECT count(*) FROM SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti ");
		if(!contar) {
			consulta = new StringBuilder("SELECT ti.* FROM SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti ");
		}
		consulta.append("LEFT JOIN SDB_ACC_CORRESPONDENCIA_PROCESO_EJECUTADO pe ON pe.ID = ti.ID_PROCESO_EJECUTADO ");
		consulta.append("LEFT JOIN SDB_PNG_CORRESPONDENCIA_PASO p ON p.ID = ti.ID_PASO ");
				
		consulta.append(WHERE1_1);

		if(filtros.get(FILTRO_ULTIMOPASO)!=null){
			consulta.append("AND ti.ultimo_Paso = ?1 ");
		}

		if(filtros.get(FILTRO_PROCESOID)!=null) {
			consulta.append("AND pe.ID_PROCESO = ?2 ");
		}
				
		consultaPasosEjecutadosPorActivo(consulta, filtros.get(FILTRO_ACTIVO), "ti");

		consultaPorTipoConsulta(consulta, tipoConsulta, filtros, gruposString);
		
		if(filtros.get(FILTRO_PROCESOEJECUTADOID)!=null) {
			consulta.append("AND ti.ID_PROCESO_EJECUTADO = ?5 ");
		}
		if(filtros.get(FILTRO_CATEGORIA)!=null) {
			consulta.append("AND pe.ID_CATEGORIA = ?6 ");
		}
		if (filtros.get(FILTRO_MULTIFILTRO) != null) {
			consultaPorFiltro(consulta);
		}
		// Entrada
		if(filtros.get(FILTRO_PASOID)!=null && !entrada.isEmpty()){
			consulta = consultaPorEntradasPaso(consulta, entrada, filtros.get(FILTRO_PASOID));
		}
		if(filtros.get(FILTRO_SECUENCIA)!=null) {
			consulta.append("AND LOWER(pe.SECUENCIA_PROCESO) LIKE LOWER(?7) ");
		}
		if(filtros.get(FILTRO_SECUENCIAINI)!=null) {
			consulta.append("AND pe.SECUENCIA_PROCESO >= ?8 ");
		}
		if(filtros.get(FILTRO_SECUENCIAFIN)!=null) {
			consulta.append("AND pe.SECUENCIA_PROCESO <= ?9 ");
		}
		if(filtros.get(FILTRO_ORIP)!=null){
			consulta.append("AND ti.id_orip_ejecucion = ?10 ");
		}
		if(filtros.get(FILTRO_CONSECUTIVOSEE)!=null){
			StringBuilder lista = estableceFiltroMultipleIN((List<String>)(Object)filtros.get(FILTRO_CONSECUTIVOSEE), true);
			if(lista==null) {
				// Iguala a cero para no traer resultados
				lista = new StringBuilder("'0'");
			}
			consulta.append("AND pe.SECUENCIA_PROCESO_SALIDA IN ("+lista.toString()+") ");
		}
		if (filtros.get(FILTRO_PASOSEJECUTADOSID) != null) {
			StringBuilder pasosString = new StringBuilder();
			try {
				List<Long> pasos = (List<Long>)filtros.get(FILTRO_PASOSEJECUTADOSID);
				for(Long paso:pasos) {
					pasosString.append(paso).append(',');
				}
				if(pasosString.toString().isEmpty()) {
					// si no viene filtro, se iguala a 0 para no traer resultados
					pasosString.append("0");
				} else if(pasosString.toString().endsWith(",")){
					pasosString = new StringBuilder(pasosString.substring(0, pasosString.length()-1));
				}
				consulta.append("AND ti.id IN ("+pasosString+") ");
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return consulta;
	}
	
	
	/**
	 * Arma la consulta para filtrar por grupos de usuario en la consulta de pasos ejecutados.  Usado en la consulta principal para obtener la bandeja del usuario 
	 * @param lista Lista de grupos asignados sobre los cuales consultar los pasos
	 * @param separador Indica si se debe incluir un separador de caracteres con cada coma. ej '1','2','3'.  En otro caso queda 1,2,3
	 * @return Cadena con el SQL de la consulta
	 */
	private StringBuilder estableceFiltroMultipleIN (List<String> lista, boolean separador) {
		StringBuilder resultado = null;
		if(lista!=null && !lista.isEmpty()){
			resultado = new StringBuilder();
			for(String grupo:lista) {
				if(separador) {
					resultado.append("'"+grupo+"',");
				} else {
					resultado.append(grupo+",");
				}
			}
			if(resultado.toString().endsWith(",")) {
				resultado = new StringBuilder(resultado.toString().substring(0, resultado.length()-1));
			}
		}
		return resultado;
	}
	
	/**
	 * Arma la consulta para filtrar por el campo activo en la consulta de pasos ejecutados.  Usado en la consulta principal para obtener la bandeja del usuario 
	 * @param consulta Cadena con el SQL de la consulta
	 * @param activo Indica si consultar activos
	 * @param aliasTabla Indica el alias que toma la tabla en la consulta principal
	 * @return Cadena con el SQL de la consulta
	 */
	private StringBuilder consultaPasosEjecutadosPorActivo (StringBuilder consulta, Object activo, String aliasTabla) {
		if (activo==null){
			consulta.append("AND ("+aliasTabla+".activo = 1 OR "+aliasTabla+".ultimo_paso = 1) ");
		} else if(Boolean.TRUE.equals(activo)){
			consulta.append("AND ("+aliasTabla+".activo = 1) ");
		} else{
			consulta.append("AND "+aliasTabla+".activo = 0 AND pe.activo = 0 ");
		} 
		return consulta;
	}
	
	
	/**
	 * Arma la consulta para filtrar por el campo multifiltro en la consulta de pasos ejecutados.  Usado en la consulta principal para obtener la bandeja del usuario 
	 * @param consulta Cadena con el SQL de la consulta
	 */
	private void consultaPorFiltro (StringBuilder consulta) {
		consulta.append("AND ( LOWER(pe.SECUENCIA_PROCESO) LIKE LOWER(?3) ");
		// Trae  vacío cuando categoria es null
		consulta.append("OR LOWER(p.NOMBRE) LIKE LOWER(?3) ");
		consulta.append("OR TI.ID_PROCESO_EJECUTADO IN ( ");
		consulta.append("SELECT DISTINCT(TI2.ID_PROCESO_EJECUTADO) FROM SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti2 LEFT JOIN SDB_ACC_CORRESPONDENCIA_VALOR_ENTRADA ve ON ve.ID_PASO_EJECUTADO = ti2.ID ");
		consulta.append("LEFT JOIN SDB_PNG_CORRESPONDENCIA_ENTRADA e ON ve.ID_ENTRADA = e.ID ");
		consulta.append("WHERE e.INCLUIR_BANDEJA = 1 AND LOWER(ve.VALOR_ENTRADA_TEXTO) LIKE LOWER(?3) ) )");
	}
	
	
	/**
	 * Consulta interna de Pasos Ejecutados según filtros de los valores de las entradas del paso ejecutado.  Usado en la consulta principal para obtener la bandeja del usuario
	 * @param consulta Cadena con el SQL de la consulta
	 * @param entrada Lista de entradas con metadatos
	 * @param pasoId Identificador del Paso a evaluar
	 * @return Cadena con el SQL de la consulta
	 */
	private StringBuilder consultaPorEntradasPaso(StringBuilder consulta, List<ValorEntrada> entrada, Object pasoId) {
		consulta.append("AND ti.ID_PROCESO_EJECUTADO IN (select distinct(ti2.ID_PROCESO_EJECUTADO) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti2 LEFT JOIN SDB_ACC_CORRESPONDENCIA_VALOR_ENTRADA md ON md.ID_PASO_EJECUTADO = ti2.id WHERE ");
		int i = 0;
		for(ValorEntrada valorEntrada:entrada){
			establecerValorEntradaCheckbox(valorEntrada);
			
			if(valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.DATE.name())){
				valorEntrada.setValor("");
				if(valorEntrada.getValorEntradaFecha()!=null){
					valorEntrada.setValor(new SimpleDateFormat("dd/MM/yyyy").format(valorEntrada.getValorEntradaFecha()));
				}
			}
			
			if(valorEntrada.getValor()!=null && !valorEntrada.getValor().isEmpty()){
				if(i++ > 0){
					consulta.append("OR ");
				}
				String data2 = consultaPasosEstableceMultiFiltro(valorEntrada.getValor());
				consulta.append("(md.ID_ENTRADA = "+valorEntrada.getEntrada().getId()+" AND LOWER(md.VALOR_ENTRADA_TEXTO) LIKE LOWER('"+data2+"')) ");
			}
		}
		if(i > 0){
			consulta.append("AND ");
		}
		consulta.append("ti2.ID_PASO = "+pasoId+") ");
		
		return consulta;
	}
	
	
	/**
	 * Establece el campo valorEntrada a partir de una lista de seleccion de checkbox
	 * @param valorEntrada Entrada a afectar
	 */
	private void establecerValorEntradaCheckbox(ValorEntrada valorEntrada) {
		if(valorEntrada.getEntrada().getTipoEntrada().equals(TiposEntrada.CHECKBOX.name())){
			valorEntrada.setValor("");
			if(valorEntrada.getListaEntradas()!=null){
				for(String data:valorEntrada.getListaEntradas()){
					valorEntrada.setValor(valorEntrada.getValor()+data+",");
				}
				valorEntrada.setValor(valorEntrada.getValor().substring(0, valorEntrada.getValor().lastIndexOf(',')));
			}
		}
	}
	
	
	/**
	 * Arma la consulta para filtrar por tipo de consulta, grupos y usuarios en la consulta de pasos ejecutados.  Usado en la consulta principal para obtener la bandeja del usuario 
	 * @param consulta Cadena con el SQL de la consulta
	 * @param tipoConsulta Tipo de Consulta a aplicar según el usuario que consulta
	 * @param filtros Filtros a aplicar a la consulta
	 * @param gruposString Lista de grupos asignados a filtrar
	 * @return Cadena con el SQL de la consulta
	 */
	private StringBuilder consultaPorTipoConsulta (StringBuilder consulta, int tipoConsulta, Map<String, Object> filtros, StringBuilder gruposString) {
		Object usuarioAsignado = filtros.get(FILTRO_USUARIOASIGNADO);
		String usuarioVariable = "'%,'||'"+usuarioAsignado+"'||',%'";
		if (tipoConsulta==0 && usuarioAsignado != null) {
			// Consulta todos los procesos 
			// Procesos en los que el usuario haya sido asignado a alguna etapa 
			consulta.append("AND (ti.ID_PROCESO_EJECUTADO IN (select distinct(ti2.ID_PROCESO_EJECUTADO) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti2 where ','||ti2.ID_USUARIO_ASIGNADO||',' LIKE "+usuarioVariable+") ");

			// Procesos en los que el usuario este asignado a el paso actual
			consulta.append("OR ( ");
			consulta.append("ti.ID IN ( select distinct(ti3.ID) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti3 ");
			consulta.append("WHERE (','||ti3.ID_USUARIO_ASIGNADO||',' LIKE "+usuarioVariable+" ");
			consultaPasosEjecutadosPorActivo(consulta, filtros.get(FILTRO_ACTIVO), "ti3");
			consulta.append(") )");
			
			// grupos
			if(gruposString != null) {
				consulta.append("OR ti.ID IN ( select distinct(ti3.ID) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti3 ");
				// se asumen menos de 100 valores en la lista de grupos asignados para un paso
				consulta.append("JOIN (SELECT level as lev_el FROM dual CONNECT BY level <= 100) b ON b.lev_el <= regexp_count( ti3.id_grupo_asignado, ',' ) + 1 ");
				consulta.append("WHERE (trim(regexp_substr(ti3.id_grupo_asignado, '[^,]+', 1, b.lev_el)) IN ("+gruposString+") ");
				consultaPasosEjecutadosPorActivo(consulta, filtros.get(FILTRO_ACTIVO), "ti3");
				consulta.append(") ) ");
			}
			consulta.append(") ) ");
		} else if (tipoConsulta==1) {
			// Procesos en los que el usuario este asignado a el paso actual
			if(usuarioAsignado!=null || gruposString!=null) {
				consulta.append("AND ( ");

				if(usuarioAsignado != null) { 	
					consulta.append("ti.ID IN ( select distinct(ti3.ID) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti3 ");
					consulta.append("WHERE (','||ti3.ID_USUARIO_ASIGNADO||',' LIKE "+usuarioVariable+" ");
					consultaPasosEjecutadosPorActivo(consulta, filtros.get(FILTRO_ACTIVO), "ti3");
					consulta.append(") )");
				}

				if(gruposString != null) {
					consulta.append("OR ti.ID IN ( select distinct(ti3.ID) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti3 ");
					// se asumen menos de 100 valores en la lista de grupos asignados para un paso
					consulta.append("JOIN (SELECT level as lev_el FROM dual CONNECT BY level <= 100) b ON b.lev_el <= regexp_count( ti3.id_grupo_asignado, ',' ) + 1 ");
					consulta.append("WHERE (trim(regexp_substr(ti3.id_grupo_asignado, '[^,]+', 1, b.lev_el)) IN ("+gruposString+") ");
					consultaPasosEjecutadosPorActivo(consulta, filtros.get(FILTRO_ACTIVO), "ti3");
					consulta.append(") ) ");
				}
				consulta.append(") ");
			}
		} else if (tipoConsulta==2 && usuarioAsignado != null){ 
			// Consulta procesos en los que el usuario haya participado
			consulta.append("AND ti.ID_PROCESO_EJECUTADO IN (select distinct(ti2.ID_PROCESO_EJECUTADO) from SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti2 where ','||ti2.ID_USUARIO_ASIGNADO||',' LIKE "+usuarioVariable+") ");
		}
		return consulta;
	}
	
	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PasoEjecutado> buscarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entrada, List<String> grupos, String ordenadoPor, int limite, int buscarDesde) {
		StringBuilder consulta = this.obtenerConsultaPasosEjecutados(filtros, false, tipoConsulta, entrada, grupos);
		
		if (ordenadoPor != null) {
			consulta.append("ORDER BY "+ ordenadoPor);
		}
		Query query = em.createNativeQuery(consulta.toString(), PasoEjecutado.class);
		query.setHint(CACHE_STOREMODE, CacheStoreMode.REFRESH);
		
		logger.debug(consulta.toString());
		if (buscarDesde > 0) {
			query.setFirstResult(buscarDesde);
		}
		if (limite > 0) {
			query.setMaxResults(limite);
		}
		aplicarParametrosPasosEjecutadosPor(query, filtros);
		return query.getResultList();
	}
	
	
	/**
	 * Establece la sentencia para el multifiltro de consultas de pasos ejecutados
	 * @param filtro Texto de filtro a aplicar
	 * @return Texto de filtro ya listo para agregar a la consulta SQL
	 */
	private String consultaPasosEstableceMultiFiltro(String filtro) {
		StringBuilder resultado = new StringBuilder(filtro.trim());
		if(resultado.toString().startsWith("\"") && resultado.toString().endsWith("\"") || (resultado.toString().startsWith("'") && resultado.toString().endsWith("'"))) {
			resultado = new StringBuilder(resultado.toString().substring(1, resultado.length()-1));
		} else if(resultado.toString().startsWith("\"") && !resultado.toString().endsWith("\"") || (resultado.toString().startsWith("'") && !resultado.toString().endsWith("'"))) {
			resultado = new StringBuilder(resultado.toString().substring(1, resultado.length()));
			resultado = new StringBuilder("%"+resultado.toString());
		} else if(!resultado.toString().startsWith("\"") && resultado.toString().endsWith("\"") || (!resultado.toString().startsWith("'") && resultado.toString().endsWith("'"))) {
			resultado = new StringBuilder(resultado.toString().substring(0, resultado.length()-1));
			resultado = new StringBuilder(resultado.toString()+"%");
		} else {
			resultado = new StringBuilder("%"+resultado.toString()+"%");
		}
		resultado = new StringBuilder(resultado.toString().replace("'", "\\'"));
		return resultado.toString();
	}
	

	/**
	 * Aplica los parámetros a los filtros de la consulta de Pasos Ejecutados Por
	 * @param query Objeto Query con la consulta inicializada
	 * @param filtros Mapa de filtros a aplicar
	 */
	private void aplicarParametrosPasosEjecutadosPor(Query query, Map<String, Object> filtros) {
		if(filtros.get(FILTRO_ULTIMOPASO)!=null){
			query.setParameter(1, filtros.get(FILTRO_ULTIMOPASO));
		}
		if(filtros.get(FILTRO_PROCESOID)!=null) {
			query.setParameter(2, filtros.get(FILTRO_PROCESOID));
		}
		if (filtros.get(FILTRO_MULTIFILTRO) != null) {
			query.setParameter(3, consultaPasosEstableceMultiFiltro((String)filtros.get(FILTRO_MULTIFILTRO)));
		}
		if(filtros.get(FILTRO_PROCESOEJECUTADOID)!=null) {
			query.setParameter(5, filtros.get(FILTRO_PROCESOEJECUTADOID));
		}
		if(filtros.get(FILTRO_CATEGORIA)!=null) {
			query.setParameter(6, filtros.get(FILTRO_CATEGORIA));
		}
		if (filtros.get(FILTRO_SECUENCIA) != null) {
			StringBuilder resultado = new StringBuilder("%"+filtros.get(FILTRO_SECUENCIA)+"%");
			query.setParameter(7, resultado.toString());
		}
		if (filtros.get(FILTRO_SECUENCIAINI) != null) {
			query.setParameter(8, filtros.get(FILTRO_SECUENCIAINI));
		}
		if (filtros.get(FILTRO_SECUENCIAFIN) != null) {
			query.setParameter(9, filtros.get(FILTRO_SECUENCIAFIN));
		}
		if (filtros.get(FILTRO_ORIP) != null) {
			query.setParameter(10, filtros.get(FILTRO_ORIP));
		}
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public int contarPasosEjecutadosPor(Map<String, Object> filtros, int tipoConsulta, List<ValorEntrada> entrada, List<String> grupos) {
		StringBuilder consulta = this.obtenerConsultaPasosEjecutados(filtros, true, tipoConsulta, entrada, grupos);

		Query query = em.createNativeQuery(consulta.toString());

		aplicarParametrosPasosEjecutadosPor(query, filtros);

		return ((BigDecimal) query.getSingleResult()).intValue();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Proceso> buscarProcesosPor(Boolean activo, String filtro, String ordenadoPor, int limite, int buscarDesde) {
		StringBuilder consulta = new StringBuilder("SELECT t FROM Proceso t ");
		consulta.append("WHERE 1 = 1 and t.archivado = false ");
		if (activo != null) {
			consulta.append(AND_ACTIVO);
		}
		if (filtro != null && !filtro.isEmpty()) {
			consulta.append(AND_NOMBRE);
			consulta.append("OR OPERATOR('ToChar', t.id) LIKE :filtro ");
			consulta.append("OR LOWER(t.tipoProceso.nombre) LIKE LOWER(:filtro)) ");
		}
		if (ordenadoPor != null && !ordenadoPor.isEmpty()) {
			consulta.append("ORDER BY t." + ordenadoPor);
		}
		Query query = em.createQuery(consulta.toString());
		query.setHint(CACHE_STOREMODE, CacheStoreMode.REFRESH);

		if (buscarDesde > 0) {
			query.setFirstResult(buscarDesde);
		}
		if (limite > 0) {
			query.setMaxResults(limite);
		}
		if (activo != null) {
			query.setParameter(FILTRO_ACTIVO, activo);
		}
		if (filtro != null && !filtro.isEmpty()) {
			query.setParameter(FILTRO_MULTIFILTRO, "%" + filtro.replace("'", "\\'") + "%");
		}
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public int contarProcesosPor(Boolean activo, String filtro) {
		StringBuilder consulta = new StringBuilder("SELECT count(t) FROM Proceso t ");
		consulta.append("WHERE 1 = 1 and t.archivado = 0 ");
		if (activo != null) {
			consulta.append(AND_ACTIVO);
		}
		if (filtro != null && !filtro.isEmpty()) {
			consulta.append(AND_NOMBRE);
			consulta.append("OR OPERATOR('ToChar', t.id) LIKE :filtro ");
			consulta.append("OR LOWER(t.tipoProceso.nombre) LIKE LOWER(:filtro)) ");
		}
		Query query = em.createQuery(consulta.toString());
		if (activo != null) {
			query.setParameter(FILTRO_ACTIVO, activo);
		}
		if (filtro != null && !filtro.isEmpty()) {
			query.setParameter(FILTRO_MULTIFILTRO, "%" + filtro.replace("'", "\\'") + "%");
		}
		return ((Long) query.getSingleResult()).intValue();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Entrada> buscarEntradasActivasPorPaso(long pasoId) {
		Query query = em.createNamedQuery(Entrada.ENTRADA_ACTIVAS_POR_PASO);
		query.setParameter(1, pasoId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Regla> buscarReglasActivasPorPaso(long pasoId) {
		Query query = em.createNamedQuery(Regla.REGLA_ACTIVAS_POR_PASO);
		query.setParameter(1, pasoId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CondicionRegla> buscarCondicionReglaPorRegla(long reglaId) {
		Query query = em.createNamedQuery(CondicionRegla.CONDICIONREGLA_POR_REGLA);
		query.setParameter(1, reglaId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValorEntrada> buscarValorEntradaPorPasoEjecutado(long pasoEjecutadoId) {
		Query query = em.createNamedQuery(ValorEntrada.VALORENTRADA_POR_PASOEJECUTADO);
		query.setParameter(1, pasoEjecutadoId);
		return query.getResultList();
	}


	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ProcesoCategoria> buscarCategoriasPorProceso(long procesoId) {
		Query query = em.createNamedQuery(ProcesoCategoria.CATEGORIAS_POR_PROCESO);
		query.setParameter(FILTRO_PROCESOID, procesoId);
		return query.getResultList();
	}
	

	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	public long obtenerSecuenciaProceso() {
		long data = 1;
		StringBuilder sql = new StringBuilder("SELECT max(identificador) FROM SDB_PNG_CORRESPONDENCIA_PROCESO t ");
		Query query = em.createNativeQuery(sql.toString());

		List<Object> results = query.getResultList();
		if (results != null && !results.isEmpty() && results.get(0) != null) {
			if (results.get(0) instanceof Long) {
				data = (Long) results.get(0) + 1;
			} else if(results.get(0) instanceof BigDecimal) {
				data = ((BigDecimal) results.get(0)).longValue() + 1;
			}
			
		}
		return data;
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	public long obtenerIdentificadorSecuenciaEntrada() {
		long data = 1;
		StringBuilder sql = new StringBuilder("SELECT max(identificador) FROM SDB_PNG_CORRESPONDENCIA_SECUENCIA_ENTRADA t ");
		Query query = em.createNativeQuery(sql.toString());

		List<Object> results = query.getResultList();
		if (results != null && !results.isEmpty() && results.get(0) != null) {
			if (results.get(0) instanceof Long) {
				data = (Long) results.get(0) + 1;
			} else if(results.get(0) instanceof BigDecimal) {
				data = ((BigDecimal) results.get(0)).longValue() + 1;
			}
			
		}
		return data;
	}

	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	public List<ProcesoEjecutado> validarProcesosEjecutadosPorPeriodo(Proceso proceso) {
		List<ProcesoEjecutado> procesos = new ArrayList<>();
		Calendar date = Calendar.getInstance();

		if(proceso!=null) {
			StringBuilder sql = new StringBuilder("SELECT a.* FROM ProcesoEjecutado a ");
			sql.append("JOIN proceso b ON a.procesoId = b.id WHERE 1 = 1 ");
			if (proceso.getId() > 0) {
				sql.append("AND a.procesoId = :procesoId ");
			}
			
			String casttochar = "to_char(a.fechaCreacion, 'YYYYMM')";
			String sql2 = "";
			switch (proceso.getRestriccionTiempoEjecutado()) {
				case 1:
					sql2 = casttochar+" = " + date.get(Calendar.YEAR);
					break;
				case 2:
					int month = date.get(Calendar.MONTH) + 1;
					if (month < 7) {
						sql2 = casttochar +" >= " + date.get(Calendar.YEAR) + "01 AND "+casttochar+" <= " + date.get(Calendar.YEAR) + "06";
					} else {
						sql2 = casttochar +" >= " + date.get(Calendar.YEAR) + "07 AND "+casttochar+" <= " + date.get(Calendar.YEAR) + "12";
					}
					break;
				case 3:
					int quarter = (date.get(Calendar.MONTH) / 3) + 1;
					sql2 = "to_char(a.fechaCreacion, 'YYYYQ') = " + date.get(Calendar.YEAR) + quarter;
					break;
				case 4:
					month = date.get(Calendar.MONTH) + 1;
					String in = " IN (";
					if (month == 1 || month == 2) {
						sql2 = casttochar + in + date.get(Calendar.YEAR) + "01," + date.get(Calendar.YEAR) + "02)";
					} else if (month == 3 || month == 4) {
						sql2 = casttochar + in + date.get(Calendar.YEAR) + "03," + date.get(Calendar.YEAR) + "04)";
					} else if (month == 5 || month == 6) {
						sql2 = casttochar + in + date.get(Calendar.YEAR) + "05," + date.get(Calendar.YEAR) + "06)";
					} else if (month == 7 || month == 8) {
						sql2 = casttochar + in + date.get(Calendar.YEAR) + "07," + date.get(Calendar.YEAR) + "08)";
					} else if (month == 9 || month == 10) {
						sql2 = casttochar + in + date.get(Calendar.YEAR) + "09," + date.get(Calendar.YEAR) + "10)";
					} else if (month == 11 || month == 12) {
						sql2 = casttochar + in + date.get(Calendar.YEAR) + "11," + date.get(Calendar.YEAR) + "12)";
					}
					break;
				case 5:
					sql2 = casttochar +" = " + new SimpleDateFormat("yyyyMM").format(date.getTime());
					break;
				case 6:
					String fecha = new SimpleDateFormat("yyyyMM").format(date.getTime());
					if (date.get(Calendar.DAY_OF_MONTH) < 15) {
						sql2 = "to_char(a.fechaCreacion, 'YYYYMMDD') >= " + fecha + "01 AND to_char(a.fechaCreacion, 'YYYYMMDD') <= " + fecha + "15";
					} else {
						sql2 = "to_char(a.fechaCreacion, 'YYYYMMDD') <= " + fecha + "31 AND to_char(a.fechaCreacion, 'YYYYMMDD') >= " + fecha + "16";
					}
					break;
				case 7:
					sql2 = "to_char(a.fechaCreacion, 'YYYYWW') = " + new SimpleDateFormat("yyyyww").format(date.getTime());
					break;
				case 8:
					sql2 = "to_char(a.fechaCreacion, 'YYYYMMDD') = " + new SimpleDateFormat("yyyyMMdd").format(date.getTime());
					break;
				default:
					break;
			}
	
			if (!sql2.isEmpty()) {
				sql.append("AND "+sql2);
			}
			
			sql.append(" ORDER BY a.fechaCreacion DESC");
			sql.append(" LIMIT 1");
			
			Query query = em.createNativeQuery(sql.toString(), ProcesoEjecutado.class);
			if (proceso.getId() > 0) {
				query.setParameter(FILTRO_PROCESOID, proceso.getId());
			}
			procesos = query.getResultList();
		}
		
		return procesos;
	}
	
	
	
	/**
	 * {@inheritDoc} 
	 */
    public void guardarProceso(Proceso data, Long[] categoriasSeleccionadas, String usuarioActualId, String direccionIp){
		Calendar calendar = Calendar.getInstance();

		if(data.getJsonProcesoData()!=null) {
			data.setDatosAdicionales(JsonUtil.transformarAJson(data.getJsonProcesoData()));
		}

		if(data.getId()==0){
			data.setActivo(true);
			data.setFechaCreacion(calendar.getTime());
			data.setIdUsuarioCreacion(usuarioActualId);
			data.setIpCreacion(direccionIp);
			data.setIdentificador(obtenerSecuenciaProceso());
			persistir(data);
		} else{
			data.setFechaModificacion(calendar.getTime());
			data.setIdUsuarioModificacion(usuarioActualId);
			data.setIpModificacion(direccionIp);
			actualizar(data);
		}
		
		// Eliminar unchecked
		if(data.getCategorias()!=null) {
			for(ProcesoCategoria cat:data.getCategorias()){
				boolean ok = false;
				for(Long c:categoriasSeleccionadas){
					if(c.longValue()==cat.getId().getCategoriaId()){
						ok = true;
						break;
					}
				}
				if(!ok){
					eliminar(cat);
				}
			}
		}
		
		if(categoriasSeleccionadas!=null) {
			// Guardar nuevos
			for(Long c:categoriasSeleccionadas){
				boolean ok = false;
				for(ProcesoCategoria cat:data.getCategorias()){
					if(c==cat.getId().getCategoriaId()){
						ok = true;
						break;
					}
				}
				if(!ok){
					ProcesoCategoria cat = new ProcesoCategoria();
					ProcesoCategoriaPK pk = new ProcesoCategoriaPK();
					pk.setProcesoId(data.getId());
					Parametro categoria = new Parametro(c);
					pk.setCategoriaId(categoria.getId());
					cat.setId(pk);
					cat.setFechaCreacion(Calendar.getInstance().getTime());
					cat.setIdUsuarioCreacion(usuarioActualId);
					cat.setIpCreacion(direccionIp);
					persistir(cat);
				}
			}
		}
	}

	
	/**
	 * {@inheritDoc} 
	 */
	public void guardarSecuenciasEntrada(List<SecuenciaEntrada> secuenciaEntrada, String usuarioActualId, String direccionIp) {
		if(secuenciaEntrada!=null) {
			for(SecuenciaEntrada fs:secuenciaEntrada) {
				if(fs.getId()==0) {
					fs.setFechaCreacion(Calendar.getInstance().getTime());
					fs.setIdUsuarioCreacion(usuarioActualId);
					fs.setIpCreacion(direccionIp);
					fs.setIdentificador(obtenerIdentificadorSecuenciaEntrada());
					persistir(fs);
				} else {
					fs.setFechaModificacion(Calendar.getInstance().getTime());
					fs.setIdUsuarioModificacion(usuarioActualId);
					fs.setIpModificacion(direccionIp);
					actualizar(fs);
				}
			}
		}
	}
	
	
	/**
	 * {@inheritDoc} 
	 */
	public void guardarSecuenciasProceso(List<ProcesoSecuencia> secuenciaProceso, String usuarioActualId, String direccionIp) {
		if(secuenciaProceso!=null) {
			for(ProcesoSecuencia procesoseq:secuenciaProceso) {
				if(procesoseq.getId()==0) {
					procesoseq.setFechaCreacion(Calendar.getInstance().getTime());
					procesoseq.setIdUsuarioCreacion(usuarioActualId);
					procesoseq.setIpCreacion(direccionIp);
					persistir(procesoseq);
				} else {
					procesoseq.setFechaModificacion(Calendar.getInstance().getTime());
					procesoseq.setIdUsuarioModificacion(usuarioActualId);
					procesoseq.setIpModificacion(direccionIp);
					actualizar(procesoseq);
				}
			}
		}
	}

	
	/**
	 * {@inheritDoc} 
	 */
	public void guardarSecuenciaGlobal(List<SecuenciaGlobal> secuencias, String usuarioActualId, String direccionIp) {
		if(secuencias!=null) {
			for(SecuenciaGlobal secuencia:secuencias) {
				if(secuencia.getId()==0) {
					secuencia.setFechaCreacion(Calendar.getInstance().getTime());
					secuencia.setIdUsuarioCreacion(usuarioActualId);
					secuencia.setIpCreacion(direccionIp);
					persistir(secuencia);
				} else {
					secuencia.setFechaModificacion(Calendar.getInstance().getTime());
					secuencia.setIdUsuarioModificacion(usuarioActualId);
					secuencia.setIpModificacion(direccionIp);
					actualizar(secuencia);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc} 
	 */
    public void guardarPaso(Paso paso, Proceso proceso, int pasosListSize, String usuarioActualId, String direccionIp){
		Calendar calendar = Calendar.getInstance();
		
		if(paso.getJsonPasoData()!=null) {
			paso.setDatosAdicionales(JsonUtil.transformarAJson(paso.getJsonPasoData()));
		}

		if(paso.getId()==0){
			paso.setProceso(proceso);
			paso.setActivo(true);
			paso.setOrdenPaso(Long.valueOf(pasosListSize)+1);
			paso.setFechaCreacion(calendar.getTime());
			paso.setIdUsuarioCreacion(usuarioActualId);
			paso.setIpCreacion(direccionIp);
			persistir(paso);
		} else{
			paso.setFechaModificacion(calendar.getTime());
			paso.setIdUsuarioModificacion(usuarioActualId);
			paso.setIpModificacion(direccionIp);
			actualizar(paso);
		}

		// Verifica si se deshabilita el recordatorio anularlo en todas las instancias de paso
		if(paso.getEnviarRecordatorio()==0){
			String update = "update SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO set FECHA_SIGUIENTE_RECORDATORIO = null where ID_PASO = "+paso.getId();
			actualizarSQL(update);
		}
	}
    
    
	/**
	 * {@inheritDoc} 
	 */
    public void guardarEntrada(Entrada entrada, Paso paso, int entradasListSize, String usuarioActualId, String direccionIp){
		entrada.setOpciones(JsonUtil.transformarAJson(entrada.getOpcionesJson()));
		
		if(entrada.getEntradaAnidadaId()!=null && entrada.getEntradaAnidadaId()==0) {
			entrada.setEntradaAnidadaId(null);
			entrada.setEntradaAnidada(false);
		}
		
		if(!entrada.isEntradaAnidada()) {
			entrada.setEntradaAnidadaId(null);
		}
		
		Calendar calendar = Calendar.getInstance();
		if(entrada.getId()==0){
			entrada.setOrdenEntrada(entradasListSize+1);
			entrada.setPaso(paso);
			entrada.setActivo(true);
			entrada.setFechaCreacion(calendar.getTime());
			entrada.setIdUsuarioCreacion(usuarioActualId);
			entrada.setIpCreacion(direccionIp);
			persistir(entrada);
		} else{
			entrada.setFechaModificacion(calendar.getTime());
			entrada.setIdUsuarioModificacion(usuarioActualId);
			entrada.setIpModificacion(direccionIp);
			actualizar(entrada);
		}
	}
    
    
	/**
	 * {@inheritDoc} 
	 */
	public void eliminarEntrada(Entrada entrada, String usuarioActualId, String direccionIp){
		entrada.setActivo(false);
		entrada.setFechaModificacion(Calendar.getInstance().getTime());
		entrada.setIdUsuarioModificacion(usuarioActualId);
		entrada.setIpModificacion(direccionIp);
		actualizar(entrada);
		
		List<Regla> reglas = buscarReglasActivasPorPaso(entrada.getPaso().getId());

		if(reglas!=null){
			for(Regla c:reglas){
				c.setDetalles(buscarCondicionReglaPorRegla(c.getId()));
				Iterator<CondicionRegla> iter = c.getDetalles().iterator();
				while(iter.hasNext()){
					CondicionRegla d = iter.next();
					if(d.getEntrada().getId()==entrada.getId()){
						eliminar(d);
						iter.remove();
					}
				}

				if(c.getDetalles().isEmpty()){
					c.setActivo(false);
					c.setFechaModificacion(Calendar.getInstance().getTime());
					c.setIdUsuarioModificacion(usuarioActualId);
					c.setIpModificacion(direccionIp);
					actualizar(c);
				}
			}
		}
	}


	/**
	 * {@inheritDoc} 
	 */
	public void guardarRegla(Regla regla, Paso paso, String usuarioActualId, String direccionIp){
		Calendar calendar = Calendar.getInstance();
		if(regla.getId()==0){
			regla.setPaso(paso);
			regla.setActivo(true);
			regla.setFechaCreacion(calendar.getTime());
			regla.setIdUsuarioCreacion(usuarioActualId);
			regla.setIpCreacion(direccionIp);
			persistir(regla);
			for(CondicionRegla reglaRegla:regla.getDetalles()){
				if(reglaRegla.getId()==0l){
					reglaRegla.setRegla(regla);
					reglaRegla.setFechaCreacion(Calendar.getInstance().getTime());
					reglaRegla.setIdUsuarioCreacion(usuarioActualId);
					reglaRegla.setIpCreacion(direccionIp);
					persistir(reglaRegla);
				}
			}
		} else{
			regla.setFechaModificacion(calendar.getTime());
			regla.setIdUsuarioModificacion(usuarioActualId);
			regla.setIpModificacion(direccionIp);
			actualizar(regla);
			logger.info(regla.getSiguientePasoId());
			for(CondicionRegla condicionRegla:regla.getDetalles()){
				condicionRegla.setRegla(regla);
				if(condicionRegla.getId()==0l){
					condicionRegla.setFechaCreacion(Calendar.getInstance().getTime());
					condicionRegla.setIdUsuarioCreacion(usuarioActualId);
					condicionRegla.setIpCreacion(direccionIp);
					persistir(condicionRegla);
				} else{
					condicionRegla.setFechaModificacion(Calendar.getInstance().getTime());
					condicionRegla.setIdUsuarioModificacion(usuarioActualId);
					condicionRegla.setIpModificacion(direccionIp);
					actualizar(condicionRegla);
				}
			}
		}
	}
	

	/**
	 * {@inheritDoc} 
	 */
	public void guardarRestriccionesEntrada(List<RestriccionEntrada> restricciones, Entrada entrada, String usuarioActualId, String direccionIp){
		Calendar calendar = Calendar.getInstance();
		for(RestriccionEntrada restriccion:restricciones) {
			if(restriccion.getId()==0) {
				restriccion.setEntrada(entrada);
				restriccion.setFechaCreacion(calendar.getTime());
				restriccion.setIdUsuarioCreacion(usuarioActualId);
				restriccion.setIpCreacion(direccionIp);
				persistir(restriccion);
			} else {
				restriccion.setEntrada(entrada);
				restriccion.setFechaModificacion(calendar.getTime());
				restriccion.setIdUsuarioModificacion(usuarioActualId);
				restriccion.setIpModificacion(direccionIp);
				actualizar(restriccion);
			}
		}
	}
	
	
	/**
	 * {@inheritDoc} 
	 */
	@SuppressWarnings("unchecked")
	public List<String> obtenerSugerenciasRadicados(String texto, Map<String, Object> filtros, int tipoConsulta, List<String> grupos) {
		StringBuilder gruposString = estableceFiltroMultipleIN(grupos, false);

		StringBuilder consulta = new StringBuilder("SELECT distinct(secuencia_proceso) FROM SDB_ACC_CORRESPONDENCIA_PROCESO_EJECUTADO pe ");
		consulta.append("JOIN SDB_ACC_CORRESPONDENCIA_PASO_EJECUTADO ti ON ti.ID_PROCESO_EJECUTADO = pe.id ");
		consulta.append(WHERE1_1);
		consulta.append("AND pe.secuencia_proceso IS NOT NULL ");
		if (texto!=null && !texto.isEmpty()) {
			consulta.append("AND LOWER(pe.secuencia_proceso) LIKE LOWER('%"+texto+"%') ");
		}
		consultaPasosEjecutadosPorActivo(consulta, filtros.get(FILTRO_ACTIVO), "ti");
		consultaPorTipoConsulta(consulta, tipoConsulta, filtros, gruposString);
		if (filtros.get(FILTRO_ORIP) != null) {
			consulta.append("AND ti.id_orip_ejecucion = ?10 ");
		}
		
		consulta.append(" ORDER BY pe.id DESC");
		consulta.append(" FETCH NEXT 10 ROWS ONLY");
		
		Query query = em.createNativeQuery(consulta.toString());

		if(filtros.get(FILTRO_USUARIOASIGNADO)!=null) {
			query.setParameter(4, filtros.get(FILTRO_USUARIOASIGNADO));
		}
		if (filtros.get(FILTRO_ORIP) != null) {
			query.setParameter(10, filtros.get(FILTRO_ORIP));
		}
		return query.getResultList();
	}

}

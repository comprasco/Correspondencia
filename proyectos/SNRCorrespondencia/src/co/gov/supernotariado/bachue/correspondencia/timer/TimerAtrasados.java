package co.gov.supernotariado.bachue.correspondencia.timer;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposEstados;
import co.gov.supernotariado.bachue.correspondencia.ejb.api.TiposPeriodosPasos;
import co.gov.supernotariado.bachue.correspondencia.ejb.entity.PasoEjecutado;
import co.gov.supernotariado.bachue.correspondencia.ejb.integraciones.IntegracionCatalogos;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ParametrosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.negocio.ProcesosStatelessLocal;
import co.gov.supernotariado.bachue.correspondencia.ejb.util.NotificacionUtil;

/**
 * Timer que permite verificar las tareas atrasadas para marcarlas y enviar mensajes de recordatorios
 *
 */
@WebListener
public class TimerAtrasados implements ServletContextListener {
	private final Logger logger = Logger.getLogger(TimerAtrasados.class);

	/** Manejador de lógica de negocio de procesos */
	@EJB(name = "ProcesoBusiness")
	private ProcesosStatelessLocal procesoControl;

	/** Manejador de lógica de negocio de parámetros */
	@EJB(name = "ParametrosBusiness")
	private ParametrosStatelessLocal parametrosControl;

	/**
     * Programador de la tarea
     */
    private ScheduledExecutorService scheduler;
    
    /**
     * Tarea que se ejecuta cada x tiempo
     */
    private Runnable runnableJob = () -> {
		if(procesoControl!=null){
			try {
				IntegracionCatalogos integracionCatalogos = parametrosControl.obtenerIntegracionCatalogos();
				// Tareas vencidas
				List<PasoEjecutado> tasks = procesoControl.buscarPasosAtrasados(Calendar.getInstance().getTime());
				for(PasoEjecutado task:tasks){
					if(task.getPaso().getAvisoVencimiento()==1 || task.getPaso().getAvisoVencimiento()==3) {
						new NotificacionUtil(integracionCatalogos).enviarCorreoPasoAtrasado(task);
					} 
					if(task.getPaso().getAvisoVencimiento()==2 || task.getPaso().getAvisoVencimiento()==3) {
						new NotificacionUtil(integracionCatalogos).enviarSMSPasoAtrasado(task);
					}
					task.setFechaUltimoRecordatorio(Calendar.getInstance().getTime());
					task.setEstado(TiposEstados.DEMORADO.name());
					procesoControl.actualizarEntidad(task);
				}
				// Recordatorio tareas
				tasks = procesoControl.buscarRecordatoriosPasos(Calendar.getInstance().getTime());
				for(PasoEjecutado task:tasks){
					if(task.getPaso().getEnviarRecordatorio()==1 || task.getPaso().getEnviarRecordatorio()==3) {
						new NotificacionUtil(integracionCatalogos).enviarCorreoRecordatorioPaso(task);
					}
					if(task.getPaso().getEnviarRecordatorio()==2 || task.getPaso().getEnviarRecordatorio()==3) {
						new NotificacionUtil(integracionCatalogos).enviarSMSRecordatorioPaso(task);
					}
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(task.getFechaSiguienteRecordatorio());
					if(task.getPaso().getEnviarRecordatorio()==0){
						task.setFechaSiguienteRecordatorio(null);
					} else{
						// Agrega el tiempo para el proximo recordatorio
						// Si el tiempo del siguiente recordatorio es menor a la hora actual se recalcula el proximo recordatorio
						if(task.getPaso().getFrecuenciaRecordatorio()!=null && task.getPaso().getFrecuenciaRecordatorio()>0) {
							if(task.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.MINUTOS.name())){
								calendar.add(Calendar.MINUTE, task.getPaso().getFrecuenciaRecordatorio());
								if(calendar.getTime().before(Calendar.getInstance().getTime())){
									calendar.setTime(Calendar.getInstance().getTime());
									calendar.add(Calendar.MINUTE, task.getPaso().getFrecuenciaRecordatorio());
								}
								task.setFechaSiguienteRecordatorio(calendar.getTime());
							} else if(task.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.HORAS.name())){
								calendar.add(Calendar.HOUR_OF_DAY, task.getPaso().getFrecuenciaRecordatorio());
								if(calendar.getTime().before(Calendar.getInstance().getTime())){
									calendar.setTime(Calendar.getInstance().getTime());
									calendar.add(Calendar.HOUR_OF_DAY, task.getPaso().getFrecuenciaRecordatorio());
								}
								task.setFechaSiguienteRecordatorio(calendar.getTime());
							} else if(task.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.DIAS.name())){
								calendar.add(Calendar.DAY_OF_YEAR, task.getPaso().getFrecuenciaRecordatorio());
								if(calendar.getTime().before(Calendar.getInstance().getTime())){
									calendar.setTime(Calendar.getInstance().getTime());
									calendar.add(Calendar.DAY_OF_YEAR, task.getPaso().getFrecuenciaRecordatorio());
								}
								task.setFechaSiguienteRecordatorio(calendar.getTime());
							} else if(task.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.SEMANAS.name())){
								calendar.add(Calendar.WEEK_OF_YEAR, task.getPaso().getFrecuenciaRecordatorio());
								if(calendar.getTime().before(Calendar.getInstance().getTime())){
									calendar.setTime(Calendar.getInstance().getTime());
									calendar.add(Calendar.WEEK_OF_YEAR, task.getPaso().getFrecuenciaRecordatorio());
								}
								task.setFechaSiguienteRecordatorio(calendar.getTime());
							} else if(task.getPaso().getFrecuenciaRecordatorioPeriodo().equals(TiposPeriodosPasos.MESES.name())){
								calendar.add(Calendar.MONTH, task.getPaso().getFrecuenciaRecordatorio());
								if(calendar.getTime().before(Calendar.getInstance().getTime())){
									calendar.setTime(Calendar.getInstance().getTime());
									calendar.add(Calendar.MONTH, task.getPaso().getFrecuenciaRecordatorio());
								}
								task.setFechaSiguienteRecordatorio(calendar.getTime());
							} else{
								task.setFechaSiguienteRecordatorio(null);
							}
						} else {
							task.setFechaSiguienteRecordatorio(null);
						}
					}
					procesoControl.actualizarEntidad(task);
				}
			} catch (Exception e) {
				logger.info("TimerAtrasados... Error", e);
			}
		}
	};


    /**
     * Ejecuta el timer cada minuto
     * @param event Evento del contexto del Servlet
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
		logger.info("TimerAtrasados contextInitialized");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(runnableJob, 1, 1, TimeUnit.DAYS);
    }

    /**
     * Destruye el timer
     * @param event Evento del contexto del Servlet
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
		logger.info("TimerAtrasados contextDestroyed");
		if(scheduler!=null) {
			scheduler.shutdownNow();
		}
    }

}

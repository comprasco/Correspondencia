package co.gov.supernotariado.bachue.correspondencia.ejb.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.gov.supernotariado.bachue.correspondencia.ejb.to.GrupoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.UsuarioTO;

/**
 * Guarda datos adicionales para la configuración de los pasos
 *
 */
public class JsonPasoData implements Serializable {
	/** Identificador serializable */
	private static final long serialVersionUID = 1L;

	/** Indica que este paso solamente se guardará por web service */
	private boolean permitirWS = false;

	/** Indica que este paso unifica una división de pasos en el flujo */
	private boolean unificarPasosProceso = false;

	/** Indica si se permite asignar a varios usuarios en paralelo */
	private boolean permitirParalelo = false;

	/** Indica si se autoasigna a usuario que ejecutó el paso anteriores */
	private long asignarUsuarioEspecial = 0;

	/** Indica si se muestran los usuarios para seleccionar cuando se selecciona un grupo */
	private boolean mostrarUsuariosGrupos = false;
	
	/** Limita los grupos mostrado siguiente paso según los grupos en los que esté el usuario actual */
	private boolean limitarGruposSegunUsuarioActual = false;

	/** Si elusuario actual está en el grupo del siguiente paso, se oculta para su selección */
	private boolean ocultarUsuarioActualSiguientePaso = false;

	/** A cuantas columnas debe mostrarse el formulario dinamico */
	private int columnasFormulario = 2;

	/** Indica si el paso requiere el campo de comentarios */
	private boolean mostrarComentarios = true;
	
	/** Indica si se muestra información del próximo paso */
	private boolean mostrarProximoPaso = true;

	/** Indica si se oculta el combo de seleccion de usuario cuando ya está fijado el usuario */
	private boolean ocultarSeleccionUsuarios = false;

	/** Indica si se oculta el combo de seleccion de grupos cuando ya está fijado el grupo */
	private boolean ocultarSeleccionGrupos = false;

	/** Usuarios seleccionados que pueden ejecutar el paso */
	private List<Long> usuariosSeleccionados;

	/** Grupos seleccionados que pueden ejecutar el paso */
	private List<Long> gruposSeleccionados;
	
	/** Lista de usuarios seleccionados a mostrar en la configuración del paso */
	private transient List<UsuarioTO> usuariosSeleccionadosMostrar = new ArrayList<>();

	/** Lista de grupos seleccionados a mostrar en la configuración del paso */
	private transient List<GrupoTO> gruposSeleccionadosMostrar = new ArrayList<>();

	/**
	 * Obtiene el valor del atributo permitirWS
	 * @return El valor del atributo permitirWS
	 */
	public boolean isPermitirWS() {
		return permitirWS;
	}

	/**
	 * Establece el valor del atributo permitirWS
	 * @param permitirWS con el valor del atributo permitirWS a establecer
	 */
	public void setPermitirWS(boolean permitirWS) {
		this.permitirWS = permitirWS;
	}

	/**
	 * Obtiene el valor del atributo unificarPasosProceso
	 * @return El valor del atributo unificarPasosProceso
	 */
	public boolean isUnificarPasosProceso() {
		return unificarPasosProceso;
	}

	/**
	 * Establece el valor del atributo unificarPasosProceso
	 * @param unificarPasosProceso con el valor del atributo unificarPasosProceso a establecer
	 */
	public void setUnificarPasosProceso(boolean unificarPasosProceso) {
		this.unificarPasosProceso = unificarPasosProceso;
	}

	/**
	 * Obtiene el valor del atributo asignarUsuarioEspecial
	 * @return El valor del atributo asignarUsuarioEspecial
	 */
	public long getAsignarUsuarioEspecial() {
		return asignarUsuarioEspecial;
	}

	/**
	 * Establece el valor del atributo asignarUsuarioEspecial
	 * @param asignarUsuarioEspecial con el valor del atributo asignarUsuarioEspecial a establecer
	 */
	public void setAsignarUsuarioEspecial(long asignarUsuarioEspecial) {
		this.asignarUsuarioEspecial = asignarUsuarioEspecial;
	}

	/**
	 * Obtiene el valor del atributo mostrarUsuariosGrupos
	 * @return El valor del atributo mostrarUsuariosGrupos
	 */
	public boolean isMostrarUsuariosGrupos() {
		return mostrarUsuariosGrupos;
	}

	/**
	 * Establece el valor del atributo mostrarUsuariosGrupos
	 * @param mostrarUsuariosGrupos con el valor del atributo mostrarUsuariosGrupos a establecer
	 */
	public void setMostrarUsuariosGrupos(boolean mostrarUsuariosGrupos) {
		this.mostrarUsuariosGrupos = mostrarUsuariosGrupos;
	}

	/**
	 * Obtiene el valor del atributo limitarGruposSegunUsuarioActual
	 * @return El valor del atributo limitarGruposSegunUsuarioActual
	 */
	public boolean isLimitarGruposSegunUsuarioActual() {
		return limitarGruposSegunUsuarioActual;
	}

	/**
	 * Establece el valor del atributo limitarGruposSegunUsuarioActual
	 * @param limitarGruposSegunUsuarioActual con el valor del atributo limitarGruposSegunUsuarioActual a establecer
	 */
	public void setLimitarGruposSegunUsuarioActual(boolean limitarGruposSegunUsuarioActual) {
		this.limitarGruposSegunUsuarioActual = limitarGruposSegunUsuarioActual;
	}

	/**
	 * Obtiene el valor del atributo ocultarUsuarioActualSiguientePaso
	 * @return El valor del atributo ocultarUsuarioActualSiguientePaso
	 */
	public boolean isOcultarUsuarioActualSiguientePaso() {
		return ocultarUsuarioActualSiguientePaso;
	}

	/**
	 * Establece el valor del atributo ocultarUsuarioActualSiguientePaso
	 * @param ocultarUsuarioActualSiguientePaso con el valor del atributo ocultarUsuarioActualSiguientePaso a establecer
	 */
	public void setOcultarUsuarioActualSiguientePaso(boolean ocultarUsuarioActualSiguientePaso) {
		this.ocultarUsuarioActualSiguientePaso = ocultarUsuarioActualSiguientePaso;
	}

	/**
	 * Obtiene el valor del atributo columnasFormulario
	 * @return El valor del atributo columnasFormulario
	 */
	public int getColumnasFormulario() {
		return columnasFormulario;
	}

	/**
	 * Establece el valor del atributo columnasFormulario
	 * @param columnasFormulario con el valor del atributo columnasFormulario a establecer
	 */
	public void setColumnasFormulario(int columnasFormulario) {
		this.columnasFormulario = columnasFormulario;
	}

	/**
	 * Obtiene el valor del atributo mostrarComentarios
	 * @return El valor del atributo mostrarComentarios
	 */
	public boolean isMostrarComentarios() {
		return mostrarComentarios;
	}

	/**
	 * Establece el valor del atributo mostrarComentarios
	 * @param mostrarComentarios con el valor del atributo mostrarComentarios a establecer
	 */
	public void setMostrarComentarios(boolean mostrarComentarios) {
		this.mostrarComentarios = mostrarComentarios;
	}

	/**
	 * Obtiene el valor del atributo mostrarProximoPaso
	 * @return El valor del atributo mostrarProximoPaso
	 */
	public boolean isMostrarProximoPaso() {
		return mostrarProximoPaso;
	}

	/**
	 * Establece el valor del atributo mostrarProximoPaso
	 * @param mostrarProximoPaso con el valor del atributo mostrarProximoPaso a establecer
	 */
	public void setMostrarProximoPaso(boolean mostrarProximoPaso) {
		this.mostrarProximoPaso = mostrarProximoPaso;
	}

	/**
	 * Obtiene el valor del atributo ocultarSeleccionUsuarios
	 * @return El valor del atributo ocultarSeleccionUsuarios
	 */
	public boolean isOcultarSeleccionUsuarios() {
		return ocultarSeleccionUsuarios;
	}

	/**
	 * Establece el valor del atributo ocultarSeleccionUsuarios
	 * @param ocultarSeleccionUsuarios con el valor del atributo ocultarSeleccionUsuarios a establecer
	 */
	public void setOcultarSeleccionUsuarios(boolean ocultarSeleccionUsuarios) {
		this.ocultarSeleccionUsuarios = ocultarSeleccionUsuarios;
	}

	/**
	 * Obtiene el valor del atributo ocultarSeleccionGrupos
	 * @return El valor del atributo ocultarSeleccionGrupos
	 */
	public boolean isOcultarSeleccionGrupos() {
		return ocultarSeleccionGrupos;
	}

	/**
	 * Establece el valor del atributo ocultarSeleccionGrupos
	 * @param ocultarSeleccionGrupos con el valor del atributo ocultarSeleccionGrupos a establecer
	 */
	public void setOcultarSeleccionGrupos(boolean ocultarSeleccionGrupos) {
		this.ocultarSeleccionGrupos = ocultarSeleccionGrupos;
	}

	/**
	 * Obtiene el valor del atributo usuariosSeleccionados
	 * @return El valor del atributo usuariosSeleccionados
	 */
	public List<Long> getUsuariosSeleccionados() {
		return usuariosSeleccionados;
	}

	/**
	 * Establece el valor del atributo usuariosSeleccionados
	 * @param usuariosSeleccionados con el valor del atributo usuariosSeleccionados a establecer
	 */
	public void setUsuariosSeleccionados(List<Long> usuariosSeleccionados) {
		this.usuariosSeleccionados = usuariosSeleccionados;
	}

	/**
	 * Obtiene el valor del atributo gruposSeleccionados
	 * @return El valor del atributo gruposSeleccionados
	 */
	public List<Long> getGruposSeleccionados() {
		return gruposSeleccionados;
	}

	/**
	 * Establece el valor del atributo gruposSeleccionados
	 * @param gruposSeleccionados con el valor del atributo gruposSeleccionados a establecer
	 */
	public void setGruposSeleccionados(List<Long> gruposSeleccionados) {
		this.gruposSeleccionados = gruposSeleccionados;
	}

	/**
	 * Obtiene el valor del atributo usuariosSeleccionadosMostrar
	 * @return El valor del atributo usuariosSeleccionadosMostrar
	 */
	public List<UsuarioTO> getUsuariosSeleccionadosMostrar() {
		return usuariosSeleccionadosMostrar;
	}

	/**
	 * Establece el valor del atributo usuariosSeleccionadosMostrar
	 * @param usuariosSeleccionadosMostrar con el valor del atributo usuariosSeleccionadosMostrar a establecer
	 */
	public void setUsuariosSeleccionadosMostrar(List<UsuarioTO> usuariosSeleccionadosMostrar) {
		this.usuariosSeleccionadosMostrar = usuariosSeleccionadosMostrar;
	}

	/**
	 * Obtiene el valor del atributo gruposSeleccionadosMostrar
	 * @return El valor del atributo gruposSeleccionadosMostrar
	 */
	public List<GrupoTO> getGruposSeleccionadosMostrar() {
		return gruposSeleccionadosMostrar;
	}

	/**
	 * Establece el valor del atributo gruposSeleccionadosMostrar
	 * @param gruposSeleccionadosMostrar con el valor del atributo gruposSeleccionadosMostrar a establecer
	 */
	public void setGruposSeleccionadosMostrar(List<GrupoTO> gruposSeleccionadosMostrar) {
		this.gruposSeleccionadosMostrar = gruposSeleccionadosMostrar;
	}

	/**
	 * Obtiene el valor del atributo permitirParalelo
	 * @return El valor del atributo permitirParalelo
	 */
	public boolean isPermitirParalelo() {
		return permitirParalelo;
	}

	/**
	 * Establece el valor del atributo permitirParalelo
	 * @param permitirParalelo con el valor del atributo permitirParalelo a establecer
	 */
	public void setPermitirParalelo(boolean permitirParalelo) {
		this.permitirParalelo = permitirParalelo;
	}

}

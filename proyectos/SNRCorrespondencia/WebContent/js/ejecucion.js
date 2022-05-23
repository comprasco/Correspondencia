jQuery(document).ready(function() {

});

PrimeFaces.locales['es_ES'] = { closeText: 'Cerrar', prevText: 'Anterior', nextText: 'Siguiente', monthNames: ['Enero','Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'], monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun','Jul','Ago','Sep','Oct','Nov','Dic'], dayNames: ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'], dayNamesShort: ['Dom','Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'], dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sa'], weekHeader: 'Semana', firstDay: 1, isRTL: false, showMonthAfterYear: false, yearSuffix: '', timeOnlyTitle: 'Sólo hora', timeText: 'Tiempo', hourText: 'Hora', minuteText: 'Minuto', secondText: 'Segundo', currentText: 'Fecha actual', ampm: false, month: 'Mes', week: 'Semana', day: 'Día', allDayText : 'Todo el día' };


function verOracleWCCVisor(url) {
	var popUp1 = window.open(url,'_blank');
	if (popUp1 == null) {  
		mostrarAlerta('Por favor desactive su "bloqueador de ventanas emergentes" y después intente nuevamente!');
	} else {  
		popUp1.focus();
	}												
}


function verOracleCapture(url) {
	var popUp1 = window.open(url,'_self');
	if (popUp1 == null) {  
		mostrarAlerta('Por favor desactive su "bloqueador de ventanas emergentes" y después intente nuevamente!');
	} else {  
		popUp1.focus();
	}												
}


function descargarArchivo(tipo) {
	var popUp1 = window.open('/SNRCorrespondencia/Descargar'+'?t='+tipo,'_blank');
	if (popUp1 == null) {  
		mostrarAlerta('Por favor desactive su "bloqueador de ventanas emergentes" y después intente nuevamente!');
	} else {  
		popUp1.focus();
	}												
}

function previsualizarImagen(id) {
	jQuery.magnificPopup.open({
		items: {
		      src: '/SNRCorrespondencia/Download?f='+id
		},
		type: 'image',
		closeOnContentClick: true
	});
}

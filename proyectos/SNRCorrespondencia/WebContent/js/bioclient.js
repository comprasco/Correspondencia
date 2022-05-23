jQuery(document).ready(function() {

});


var connection = null;

function connect(tramaOperacion) {

	var trama = tramaOperacion;
	console.log(trama);
	
	// verificar si la conexión está abierta o asignada
	if (connection == null) {
		console.log('CON No asignada aún');
	} else { // asignada
		connection.close(3001, "inicio de petición");
		connection = null;
	}

	connection = new WebSocket('ws://localhost:1104/');

	connection.onopen = function () { // Conexión establecida
		connection.send(trama); // <-- enviar la petición de la operación
	}

	connection.onmessage = function (message) { // <- que hacer con la respuesta
		var response = JSON.parse(message.data);
		console.log(response.event + ' -> ' +response.response);
		if (response.event === 'enroll') { // respuesta de una operación enroll
			if (response.response === 'success') {
				// que hacer cuando el enrolamiento es EXITOSO
			} else {
				// que hacer cuando el enrolamiento es FALLIDO
			}
		} else if (response.event === 'verify') { // respuesta de una operación verify
			if (response.response === 'success') {
				jQuery("[id*='boton_inicializa']").click();
			} else {
				console.log("El segundo factor de autenticación falló.");
				// Pruebas
				jQuery("[id*='boton_inicializa']").click();
				// --
			}

		} else if (response.event === 'reset') { // respuesta de una operación reset
			if (response.response === 'success') {
				// que hacer cuando el cambio de 2º factor es EXITOSO
			} else {
				// que hacer cuando el cambio de 2º factor es FALLIDO
			}
		} else if (response.event === 'sign') { // respuesta de una operación sign
			if (response.response === 'success') {
				// que hacer cuando la captura de la firma es EXITOSA
			} else {
				// que hacer cuando la captura de la firma es FALIDA
			}
		} else if (response.event === 'pdf') { // respuesta de una operación pdf
			if (response.response === 'success') {
				// que hacer cuando la impresión es EXITOSA
			} else {
				// que hacer cuando la impresión es FALIDA
			}
		}
		
		connection.close(3002, 'Fin operación');
	}
	
	connection.onclose = () => {
		// que hacer cuando el servidor cierre la conexión
	}
	
	connection.onerror = function(error) {
		console.log('[Error en la comunicación con Bioclient]'+ error);
		PF('dialogoCargando').hide();
		// mostrarAlerta('Error en la comunicación con Bioclient');
		alert('Error en la comunicación con Bioclient');
	};
}

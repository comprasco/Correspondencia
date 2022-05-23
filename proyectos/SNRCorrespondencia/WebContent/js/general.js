jQuery(document).ready(function() {
	jQuery('#sidebarCollapse').on('click', function(){
		jQuery('#sidebar').toggleClass('active');
		jQuery(this).toggleClass('active');
	});
});


function validateInputNumber() {
    var e = window.event || event;
    var key = e.which || e.keyCode;
    ok = false;
    // console.log(key);

    if (!e.shiftKey && !e.altKey && !e.ctrlKey &&
    	// numbers   
        key >= 48 && key <= 57
        // comma, period and minus, . on keypad
        //key == 190 || key == 188 || key == 109 || key == 110 ||
        // Backspace and Tab and Enter
        // key == 8 || //key == 9 || key == 13 ||
        // Home and End
        // key == 35 || key == 36 ||
        // left and right arrows
        // key == 37 || key == 39 ||
        // Del and Ins
        //key == 46 || key == 45
        ){
    	ok = true;
    }
    
    if(!ok){
       	e.returnValue = false;
       	if (e.preventDefault) {
       		e.preventDefault();
       	}
    }
}


function mostrarAlerta(contenido){
	jQuery("[id*='dialogo_alerta_texto']").html(contenido);
	PF('dialogo_alerta').show();
}
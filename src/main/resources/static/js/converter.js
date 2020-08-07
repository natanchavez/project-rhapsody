let $goToTopButton = $('#go-to-top-btn');

/**
 * Estas funciones se ejecutarán únicamente cuando la página ya ha terminado de cargarse.
 */
$(document).ready(function () {
	/**
	 * Se limpia la lista de archivos PDF cargados por el usuario.
	 */
	$("#multiple-files").val("");
	
	/**
	 * Muestra u oculta el botón que permite ir desde la parte inferior de la pagina de nuevo a la parte superior.
	 */
	$(window).scroll(function () {
		if ($(this).scrollTop() > 50) {
			$('#go-to-top-btn').fadeIn();
		} else {
			$('#go-to-top-btn').fadeOut();
		}
	});
	
	/**
	 * Regresa a la parte superior de la pagina por medio de presionar el botón correspondiente.
	 */
	$goToTopButton.click(function () {
		$('body,html').animate({scrollTop: 0}, 800);
	});
})

/**
 * Cuando el usuario ha seleccionado archivos PDF para cargar actualiza el texto que indica el numero de archivos PDF
 * que actualmente se encuentran seleccionados.
 */
$("#multiple-files").on("change", function () {
	let numSelectedFiles = $(this)[0].files.length;
	$("#multiple-files-label").html("archivos seleccionados: " + numSelectedFiles);
})

/**
 * cuando el usuario inicia el proceso para generar sus archivos se muestra en pantalla una animación de carga, para
 * así hacerle saber que este proceso puede demorar un poco por lo cual se le solicita amablemente que espere unos
 * momentos.
 */
$("#generate-btn-student, #generate-btn-visitor").click(function () {
	let numSelectedFiles = $("#multiple-files")[0].files.length;
	
	if (numSelectedFiles > 0 || this.id === 'generate-btn-visitor') {
		$('#overlay').fadeIn();
	}
});

/**
 * cuando el proceso de generar los archivos ha terminado y se esta a punto de abandonar la pagina de inicio se
 * desactivara la animación de espera que antes se había mostrado.
 */
$(window).on("unload", function () {
	$('#overlay').fadeOut();
})


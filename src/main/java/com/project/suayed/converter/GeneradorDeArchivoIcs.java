package com.project.suayed.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Contiene la funcionalidad necesaria para generar un archivo de tipo iCalendar con extensión .ics
 */
public class GeneradorDeArchivoIcs {
	private final String rutaCarpetaIcal;
	private final String claveAsignatura;
	private final String nombreAsignatura;
	private final List<String> listaDescripcionTareas;
	private final List<String> listaPorcentajesTareas;
	private final List<String> listaFechaDiaActual;
	private final List<String> listaFechaDiaSiguiente;
	private String descripcionCompletaArchivoICS;
	
	/**
	 * Se encarga de inicializar la clase GeneradorDeArchivoIcs y recibe tres objetos los cuales contienen toda la
	 * información extraída de los archivos PDF, la cual será utilizada para ser unida en un archivo .ics para ser
	 * importado como un calendario.
	 *
	 * @param rutaGuardar         contiene la ruta de la carpeta en donde se guardaran los archivos iCal generados.
	 * @param ubicadorDeDatos     contiene la información de la asignatura, así como las tareas a entregar.
	 * @param formateadorDeFechas contiene la información de las fechas de las tareas a entregar.
	 */
	public GeneradorDeArchivoIcs(String rutaGuardar, UbicadorDeDatos ubicadorDeDatos,
								 FormateadorDeFechas formateadorDeFechas) {
		this.rutaCarpetaIcal = rutaGuardar;
		
		this.claveAsignatura = ubicadorDeDatos.getClaveAsignatura();
		this.nombreAsignatura = ubicadorDeDatos.getNombreAsignatura();
		this.listaDescripcionTareas = ubicadorDeDatos.getListaDescripcionTareas();
		this.listaPorcentajesTareas = ubicadorDeDatos.getListaPorcentajesTareas();
		
		this.listaFechaDiaActual = formateadorDeFechas.getListaFechaDiaActual();
		this.listaFechaDiaSiguiente = formateadorDeFechas.getListaFechaDiaSiguiente();
	}
	
	public String getDescripcionCompletaArchivoICS() {
		return descripcionCompletaArchivoICS;
	}
	
	public void setDescripcionCompletaArchivoICS(String descripcionCompletaArchivoICS) {
		this.descripcionCompletaArchivoICS = descripcionCompletaArchivoICS;
	}
	
	/**
	 * Se encarga de unir los datos de las tareas en una sola cadena de texto, la cual será concatenada con el esqueleto
	 * básico del archivo .ics
	 */
	public void unirDatosConFormatoICS() {
		StringBuilder informacionUnida;
		long identificadorUnicoDelArchivo = System.nanoTime();
		String estampaDeTiempo = "20200101T000000";
		String encabezadoICS = "" +
				"BEGIN:VCALENDAR\r\n" +
				"VERSION:2.0\r\n" +
				"PRODID:Prototype 1.0 alpha\r\n" +
				"X-WR-TIMEZONE:America/Mexico_City";
		String inicioCuerpoICS = "\r\nBEGIN:VEVENT";
		String inicioFechaTareaICS = "\r\nDTSTART;VALUE=DATE:";
		String finalFechaTareaICS = "\r\nDTEND;VALUE=DATE:";
		String estampaDeTiempoICS = "\r\nDTSTAMP:";
		String idUnicoICS = "\r\nUID:";
		String descripcionTareaICS = "\r\nDESCRIPTION:";
		String tituloTareaICS = "\r\nSUMMARY:";
		String finalCuerpoICS = "\r\nEND:VEVENT";
		String piePaginaICS = "\r\nEND:VCALENDAR";
		
		informacionUnida = new StringBuilder(encabezadoICS);
		
		for (int i = 0; i < this.listaDescripcionTareas.size(); i++) {
			informacionUnida.append(inicioCuerpoICS)
					.append(inicioFechaTareaICS).append(this.listaFechaDiaActual.get(i))
					.append(finalFechaTareaICS).append(this.listaFechaDiaSiguiente.get(i))
					.append(estampaDeTiempoICS).append(estampaDeTiempo)
					.append(idUnicoICS).append(identificadorUnicoDelArchivo + i)
					.append(descripcionTareaICS).append(this.listaDescripcionTareas.get(i))
					.append("\\n\\nPonderacion: ").append(this.listaPorcentajesTareas.get(i))
					.append(tituloTareaICS).append("Tarea: ").append(i + 1).append("/")
					.append(this.listaFechaDiaActual.size()).append(" - ")
					.append(this.nombreAsignatura).append(" / ")
					.append(this.claveAsignatura).append(finalCuerpoICS);
		}
		
		informacionUnida.append(piePaginaICS);
		
		this.descripcionCompletaArchivoICS = informacionUnida.toString();
	}
	
	/**
	 * Se encarga de tomar la cadena de texto del esqueleto .ics con todos los datos de las tareas y los guarda en un
	 * nuevo archivo con formato .ics, si el archivo ya existe lo único que hará será sobrescribirlo.
	 */
	public void guardarInfoEnArchivoICS() {
		
		try {
			FileWriter writer = new FileWriter(this.rutaCarpetaIcal + this.nombreAsignatura + ".ics");
			writer.write(this.descripcionCompletaArchivoICS);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Se encarga de escapar el carácter de “nueva línea” para ser utilizado en el archivo iCalendar.
	 */
	public void escaparNuevaLinea() {
		this.listaDescripcionTareas.replaceAll(descripcion ->
				descripcion.replace("\n", "\\n"));
	}
}

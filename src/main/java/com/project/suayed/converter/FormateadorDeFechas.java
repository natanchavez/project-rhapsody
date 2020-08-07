package com.project.suayed.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la funcionalidad para tomar una fecha y representarla en un formato diferente.
 */
public class FormateadorDeFechas {
	private List<String> listaFechasTareas;
	private List<String[]> listaDiaMesAnyo;
	private List<String[]> listaAnyoMesDia;
	private List<String> listaFechaDiaActual;
	private List<String> listaFechaDiaSiguiente;
	private long numSemanasEnSemestre;
	
	/**
	 * Constructor que inicializa el objeto "FormateadorDeFechas, recibe como parámetro una lista las fechas de las
	 * tareas a entregar de un cierto archivo PDF perteneciente a una asignatura.
	 *
	 * @param listaFechasTareas lista con fechas de entrega de tareas de una asignatura.
	 */
	public FormateadorDeFechas(List<String> listaFechasTareas) {
		this.listaFechasTareas = listaFechasTareas;
	}
	
	public long getNumSemanasEnSemestre() {
		return numSemanasEnSemestre;
	}
	
	public void setNumSemanasEnSemestre(long numSemanasEnSemestre) {
		this.numSemanasEnSemestre = numSemanasEnSemestre;
	}
	
	public List<String> getListaFechasTareas() {
		return listaFechasTareas;
	}
	
	public void setListaFechasTareas(List<String> listaFechasTareas) {
		this.listaFechasTareas = listaFechasTareas;
	}
	
	public List<String[]> getListaDiaMesAnyo() {
		return listaDiaMesAnyo;
	}
	
	public void setListaDiaMesAnyo(List<String[]> listaDiaMesAnyo) {
		this.listaDiaMesAnyo = listaDiaMesAnyo;
	}
	
	public List<String[]> getListaAnyoMesDia() {
		return listaAnyoMesDia;
	}
	
	public void setListaAnyoMesDia(List<String[]> listaAnyoMesDia) {
		this.listaAnyoMesDia = listaAnyoMesDia;
	}
	
	public List<String> getListaFechaDiaActual() {
		return listaFechaDiaActual;
	}
	
	public void setListaFechaDiaActual(List<String> listaFechaDiaActual) {
		this.listaFechaDiaActual = listaFechaDiaActual;
	}
	
	public List<String> getListaFechaDiaSiguiente() {
		return listaFechaDiaSiguiente;
	}
	
	public void setListaFechaDiaSiguiente(List<String> listaFechaDiaSiguiente) {
		this.listaFechaDiaSiguiente = listaFechaDiaSiguiente;
	}
	
	/**
	 * Se encarga de recibir una lista con las fechas de las tareas en forma de texto y separa los valores de cada fecha
	 * en día, mes y año.
	 */
	public void separarDiaMesAnyo() {
		this.listaDiaMesAnyo = this.listaFechasTareas.stream()
				.map(fechaEnTexto -> fechaEnTexto.split("\\sde\\s")).collect(Collectors.toList());
	}
	
	/**
	 * Se encarga de tomar la lista con las fechas en formato día, mes y año y la convierte a año, mes y día.
	 */
	public void acomodarAnyoMesDia() {
		this.listaAnyoMesDia = new ArrayList<>();
		
		for (String[] fechaAcomodada : this.listaDiaMesAnyo) {
			String dia = fechaAcomodada[0];
			String mes = fechaAcomodada[1];
			String anyo = fechaAcomodada[2];
			String[] anyoMesDia = {anyo, mes, dia};
			this.listaAnyoMesDia.add(anyoMesDia);
		}
	}
	
	/**
	 * Se encarga de revisar la lista de año, mes y día para reemplazar el mes en palabra por el valor numérico
	 * correspondiente.
	 */
	public void convertirMesANumero() {
		for (String[] fechaActual : this.listaAnyoMesDia) {
			String mesLetra = fechaActual[1];
			
			switch (mesLetra) {
				case "enero":
					fechaActual[1] = "01";
					break;
				case "febrero":
					fechaActual[1] = "02";
					break;
				case "marzo":
					fechaActual[1] = "03";
					break;
				case "abril":
					fechaActual[1] = "04";
					break;
				case "mayo":
					fechaActual[1] = "05";
					break;
				case "junio":
					fechaActual[1] = "06";
					break;
				case "julio":
					fechaActual[1] = "07";
					break;
				case "agosto":
					fechaActual[1] = "08";
					break;
				case "septiembre":
					fechaActual[1] = "09";
					break;
				case "octubre":
					fechaActual[1] = "10";
					break;
				case "noviembre":
					fechaActual[1] = "11";
					break;
				case "diciembre":
					fechaActual[1] = "12";
					break;
				default:
					throw new IllegalStateException("El nombre del mes es invalido: " + mesLetra);
			}
		}
	}
	
	/**
	 * Se encarga de tomar la lista con las fechas en formato año, mes y día para tomar esos valores y unirlos en una
	 * cadena de texto la cual será guardada en una nueva lista.
	 */
	public void unirFechaNumerica() {
		this.listaFechaDiaActual = new ArrayList<>();
		
		for (String[] fechaActual : this.listaAnyoMesDia) {
			String formatoyyyyMMdd = fechaActual[0] + fechaActual[1] + fechaActual[2];
			this.listaFechaDiaActual.add(formatoyyyyMMdd);
		}
		
	}
	
	/**
	 * Se encarga de tomar la lista con las fechas en formato año, mes y día para encontrar la fecha correspondiente al
	 * día siguiente (según el calendario) de todas las fechas.
	 */
	public void encontrarDiaSiguienteDeFecha() {
		this.listaFechaDiaSiguiente = new ArrayList<>();
		
		for (String fechaActual : this.listaFechaDiaActual) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			Calendar calendar = Calendar.getInstance();
			
			try {
				calendar.setTime(simpleDateFormat.parse(fechaActual));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			this.listaFechaDiaSiguiente.add(simpleDateFormat.format(calendar.getTime()));
		}
	}
	
	/**
	 * Se encarga de encontrar el numero de semanas que hay entre dos fechas especifica.
	 */
	public void encontrarNumeroDeSemanasRango() {
		String primeraTareaFecha = this.listaFechaDiaActual.get(0);
		String ultimaTareaFecha = this.listaFechaDiaActual.get(this.listaFechaDiaActual.size() - 1);
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		
		LocalDate primeraFecha = LocalDate.parse(primeraTareaFecha, dateTimeFormatter);
		LocalDate ultimaFecha = LocalDate.parse(ultimaTareaFecha, dateTimeFormatter);
		
		this.numSemanasEnSemestre = ChronoUnit.WEEKS.between(primeraFecha, ultimaFecha) + 1;
	}
}

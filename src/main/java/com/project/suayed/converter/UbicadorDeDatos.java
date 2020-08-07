package com.project.suayed.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.project.suayed.constants.Routes.SUBJECTS_LIST_ROUTE;

/**
 * Contiene la funcionalidad necesaria para buscar en el texto del archivo PDF los datos necesarios para poder construir
 * completamente el archivo .ics el cual contendrá todas las tareas por fecha de las materias a cursar en el presente
 * ciclo escolar.
 */
public class UbicadorDeDatos {
	private final File rutaArchivoPdfActual;
	private String textoCompletoAsignatura;
	private String nombreLicenciatura;
	private String claveAsignatura;
	private Map<String, String> listaAsignaturasMapa;
	private String nombreAsignatura;
	private String nombreAsesor;
	private String grupo;
	private String textoSoloAreaTareas;
	private List<String> listaFechasTareas;
	private List<String> listaDescripcionConPorcentajeTareas;
	private List<String> listaDescripcionTareas;
	private List<String> listaDescripcionRefinadaTareas;
	private List<String> listaNumeroUnidadTareas;
	private List<String> listaPorcentajesTareas;
	private List<String> listaNombreUnidad;
	private List<String> listaTipoDeActividad;
	
	/**
	 * Constructor que inicializa el objeto "UbicadorDeDatos", recibe como parámetro la ruta del documento PDF en turno,
	 * al cual se le extraerá la información necesaria para ser usada más adelante.
	 *
	 * @param rutaArchivoPdfActual la ruta al archivo pdf en turno
	 */
	public UbicadorDeDatos(File rutaArchivoPdfActual) {
		this.rutaArchivoPdfActual = rutaArchivoPdfActual;
	}
	
	public String getNombreLicenciatura() {
		return nombreLicenciatura;
	}
	
	public List<String> getListaDescripcionRefinadaTareas() {
		return listaDescripcionRefinadaTareas;
	}
	
	public List<String> getListaNombreUnidad() {
		return listaNombreUnidad;
	}
	
	public List<String> getListaTipoDeActividad() {
		return listaTipoDeActividad;
	}
	
	public List<String> getListaNumeroUnidadTareas() {
		return listaNumeroUnidadTareas;
	}
	
	public String getGrupo() {
		return grupo;
	}
	
	public String getNombreAsesor() {
		return nombreAsesor;
	}
	
	public List<String> getListaPorcentajesTareas() {
		return listaPorcentajesTareas;
	}
	
	public List<String> getListaDescripcionTareas() {
		return listaDescripcionTareas;
	}
	
	public List<String> getListaFechasTareas() {
		return listaFechasTareas;
	}
	
	public String getNombreAsignatura() {
		return nombreAsignatura;
	}
	
	public String getClaveAsignatura() {
		return claveAsignatura;
	}
	
	/**
	 * Se encarga de abrir un archivo PDF, leer su texto para extraerlo completo y guardarlo en una variable, lo
	 * anterior se logra mediante el uso de una biblioteca externa llamada "PDFBox".
	 */
	public void extraerTextoPDF() {
		
		try {
			PDDocument pdDocument = PDDocument.load(this.rutaArchivoPdfActual);
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			this.textoCompletoAsignatura = pdfTextStripper.getText(pdDocument);
			pdDocument.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Se encarga de revisar el texto completo extraído del archivo PDF y elimina todos los saltos de línea y se deshace
	 * de todos los espacios duplicados innecesarios.
	 */
	public void eliminarEspaciosInnecesarios() {
		this.textoCompletoAsignatura = this.textoCompletoAsignatura.replaceAll("( ){2,}", "$1");
		this.textoCompletoAsignatura =
				this.textoCompletoAsignatura.replaceAll("\\s?\\r(\\n)\\s?", "$1");
	}
	
	/**
	 * Se encarga de buscar en el texto completo y encontrar la clave de la asignatura esto mediante el uso de
	 * expresiones regulares.
	 */
	public void ubicarClaveAsignatura() {
		String regexEncontrarClave = "Datos.de.la.asignatura.Nombre.+?Clave.(\\d+).Grupo";
		
		Pattern pattern = Pattern.compile(regexEncontrarClave, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(textoCompletoAsignatura);
		
		while (matcher.find()) {
			this.claveAsignatura = matcher.group(1);
		}
	}
	
	/**
	 * Se encarga de tomar el archivo con las claves y nombres de todas las asignaturas del SUAYED y guarda esos datos
	 * en un hashmap para después poder buscar el nombre exacto de una asignatura únicamente proporcionando la clave de
	 * esta.
	 */
	public void cargarListaAsignatuas() {
		Path rutaArchivoAsignaturas = Paths.get(SUBJECTS_LIST_ROUTE);
		
		try {
			this.listaAsignaturasMapa = Files.lines(rutaArchivoAsignaturas)
					.filter(linea -> linea.matches("^\\d+----.*?$"))
					.collect(Collectors.toMap(
							clave -> clave.split("----")[0], asignatura -> asignatura.split("----")[1]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Se encarga de buscar en una lista con todas las asignaturas el nombre exacto de una materia en base a la clave de
	 * esta, la cual ya debió haber sido encontrada previamente en el documento PDF.
	 */
	public void obtenerNombreAsignatura() {
		this.nombreAsignatura = this.listaAsignaturasMapa.get(this.claveAsignatura);
	}
	
	/**
	 * Se encarga de buscar el nombre del asesor a cargo de la asignatura en cuestión.
	 */
	public void extraerNombreAsesor() {
		String regexEncontrarNombreAsesor = "(?<=II\\..Datos.del.asesor.Nombre.)(.+?)(?=.Correo)";
		
		Pattern pattern = Pattern.compile(regexEncontrarNombreAsesor, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(textoCompletoAsignatura);
		
		while (matcher.find()) {
			this.nombreAsesor = matcher.group(1);
		}
	}
	
	/**
	 * Se encarga de tomar una cadena con varias palabras separadas por un espacio y convierte la cadena a minúsculas
	 * con únicamente la primera letra de cada palabra en mayúsculas, ideal para poner en mayúscula la primera letra de
	 * cada palabra en el nombre de una persona.
	 */
	public void primeraLetraPalabrasEnMayuscula() {
		String[] nombreAsesorSeparado = this.nombreAsesor.toLowerCase().split("\\s");
		StringBuilder nombreUnido = new StringBuilder();
		
		for (String parabraActual : nombreAsesorSeparado) {
			String primeraLetraEnMayuscula = String.valueOf(parabraActual.charAt(0)).toUpperCase();
			nombreUnido.append(parabraActual.replaceAll("^(\\w)", primeraLetraEnMayuscula));
			nombreUnido.append(" ");
		}
		
		this.nombreAsesor = String.valueOf(nombreUnido).trim();
	}
	
	/**
	 * Se encarga de buscar el número de grupo de la asignatura en cuestión.
	 */
	public void extraerGrupo() {
		String regexEncontrarGrupo = "(Clave.\\d+.Grupo.)(\\d+)(?=.Modalidad)";
		
		Pattern pattern = Pattern.compile(regexEncontrarGrupo, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(textoCompletoAsignatura);
		
		while (matcher.find()) {
			this.grupo = matcher.group(2);
		}
	}
	
	/**
	 * Se encarga de ubicar el área de texto correspondiente a la información de las tareas para una asignatura, lo
	 * anterior se logra mediante el uso de expresiones regulares para ubicar el inicio y el fin de la sección deseada.
	 */
	public void delimitarTextoAreaTareas() {
		String regex = "(CALENDARIO.DE.ACTIVIDADES.+?Ponderacio.?n)(.+?)(VII\\..Sistema.de.evaluaci)";
		
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(this.textoCompletoAsignatura);
		
		while (matcher.find()) {
			this.textoSoloAreaTareas = matcher.group(2);
		}
	}
	
	/**
	 * Se encarga de encontrar todas las fechas para las tareas por medio del uso de expresiones regulares, una vez
	 * ubicadas las fechas se almacenan en una lista para poder ser utilizadas posteriormente.
	 */
	public void extraerFechasTareas() {
		this.listaFechasTareas = new ArrayList<>();
		
		String regexFechaTarea = "(\\d{2}.de.\\w+.de.\\d{4})";
		Pattern pattern = Pattern.compile(regexFechaTarea, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(this.textoSoloAreaTareas);
		
		while (matcher.find()) {
			this.listaFechasTareas.add(matcher.group(1));
		}
	}
	
	/**
	 * Se encarga de guardar en una lista el texto de la descripción de cada tarea, pero incluye también el porcentaje
	 * asociado a cada actividad, por lo cual aún se deben separar ambos datos en diferentes variables.
	 */
	public void delimitarDescripcionConPorcentajeTareas() {
		this.listaDescripcionConPorcentajeTareas =
				Pattern.compile(".\\d{2}.de.\\w+.de.\\d{4}.", Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
						.splitAsStream(this.textoSoloAreaTareas).collect(Collectors.toList());
		
		this.listaDescripcionConPorcentajeTareas.removeIf(elemento -> elemento == null ||
				"".equals(elemento) || elemento.matches("(?s)^.?\\d+.de.\\d+.?$"));
	}
	
	/**
	 * Se encarga de extraer a una lista las descripciones de cada tarea de una asignatura.
	 */
	public void extraerDescripcionesTareas() {
		this.listaDescripcionTareas = new ArrayList<>();
		String regexDescripcionTarea = "^(.+?).(\\d+.%).?(\\d+.de.\\d+.?)?$";
		Pattern pattern = Pattern.compile(regexDescripcionTarea, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		
		for (String descripcionConPorcentajeActual : this.listaDescripcionConPorcentajeTareas) {
			Matcher matcher = pattern.matcher(descripcionConPorcentajeActual);
			while (matcher.find()) {
				this.listaDescripcionTareas.add(matcher.group(1));
			}
		}
	}
	
	/**
	 * Se encarga de ubicar el número de unidad correspondiente a cada tarea para todas las asignaturas.
	 */
	public void extraerTareasNumeroUnidad() {
		this.listaNumeroUnidadTareas = new ArrayList<>();
		String regexEncontrarNumeroUnidad = "(?<=^UNIDAD.)(\\d+)(?=.?:)";
		
		for (String descripcionActual : this.listaDescripcionTareas) {
			Pattern pattern = Pattern.
					compile(regexEncontrarNumeroUnidad, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(descripcionActual);
			
			while (matcher.find()) {
				this.listaNumeroUnidadTareas.add(matcher.group(1));
			}
		}
	}
	
	/**
	 * Se encarga de extraer a una lista los porcentajes de cada tarea de una asignatura.
	 */
	public void extraerPorcentajesTareas() {
		this.listaPorcentajesTareas = new ArrayList<>();
		String regexPorcentajeTarea = "^(.+?).(\\d+.%).?(\\d+.de.\\d+.?)?$";
		Pattern pattern = Pattern.compile(regexPorcentajeTarea, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		
		for (String descripcionConPorcentajeActual : this.listaDescripcionConPorcentajeTareas) {
			Matcher matcher = pattern.matcher(descripcionConPorcentajeActual);
			while (matcher.find()) {
				this.listaPorcentajesTareas.add(matcher.group(2));
			}
		}
	}
	
	/**
	 * Se encarga de extraer y guardar en una lista los nombres de cada unidad correspondientes a cada tarea.
	 */
	public void obtenerTituloUnidad() {
		this.listaNombreUnidad = new ArrayList<>();
		String regexNombreUnidad = "^UNIDAD.\\d+:.?([A-ZÁ-Ú].+?).(Actividad.\\d+|" +
				"Act\\..lo.que.aprendí|Act\\..complementaria|Act\\..inicial|Act\\..lo.que.sé|" +
				"Act\\..de.aprendizaje|Cuestionario.de.reforzamiento|Foros)";
		Pattern pattern = Pattern.compile(regexNombreUnidad, Pattern.DOTALL);
		
		for (String descripcionActual : this.listaDescripcionTareas) {
			Matcher matcher = pattern.matcher(descripcionActual);
			while (matcher.find()) {
				this.listaNombreUnidad.add(matcher.group(1));
			}
		}
	}
	
	/**
	 * Extrae y guarda en una lista el tipo de actividad de cada tarea a entregar.
	 */
	public void obtenerTipoDeActividad() {
		this.listaTipoDeActividad = new ArrayList<>();
		String regexTipoDeUnidad = "^UNIDAD.\\d+:.?([A-ZÁ-Ú].+?).(Actividad.\\d+|" +
				"Act\\..lo.que.aprendí|Act\\..complementaria|Act\\..inicial|Act\\..lo.que.sé|" +
				"Act\\..de.aprendizaje|Cuestionario.de.reforzamiento|Foros)";
		Pattern pattern = Pattern.compile(regexTipoDeUnidad, Pattern.DOTALL);
		
		for (String descripcionActual : this.listaDescripcionTareas) {
			Matcher matcher = pattern.matcher(descripcionActual);
			
			while (matcher.find()) {
				this.listaTipoDeActividad.add(matcher.group(2));
			}
		}
	}
	
	/**
	 * Se encarga de ubicar y extraer exclusivamente el texto de la descripción de cada tarea.
	 */
	public void extraerExclusivamenteDescripcionesTareas() {
		this.listaDescripcionRefinadaTareas = new ArrayList<>();
		
		String regexTipoDeUnidad = "^UNIDAD.\\d+:.?([A-ZÁ-Ú].+?).(Actividad.\\d+|" +
				"Act\\..lo.que.aprendí|Act\\..complementaria|Act\\..inicial|Act\\..lo.que.sé|" +
				"Act\\..de.aprendizaje|Cuestionario.de.reforzamiento|Foros).(.+?)$";
		Pattern pattern = Pattern.compile(regexTipoDeUnidad, Pattern.DOTALL);
		
		for (String descripcionActual : this.listaDescripcionTareas) {
			Matcher matcher = pattern.matcher(descripcionActual);
			
			while (matcher.find()) {
				this.listaDescripcionRefinadaTareas.add(matcher.group(3));
			}
		}
	}
	
	/**
	 * Se encarga de tomar los datos previamente recabados y sustituye el símbolo de salto de línea por simples
	 * espacios, esto debido a que no es necesario mantener dichos saltos de línea para los archivos a generar y
	 * únicamente era necesario conservarlos para mantener la descripción de las tareas con una presentación adecuada.
	 */
	public void removerSaltosDeLinea() {
		this.nombreAsesor = this.nombreAsesor.replaceAll("\n", " ");
		this.nombreLicenciatura = this.nombreLicenciatura.replaceAll("\n", " ");
		this.listaFechasTareas.replaceAll(fecha -> fecha.replaceAll("\n", " "));
		this.listaPorcentajesTareas.replaceAll(porcentaje -> porcentaje.replaceAll("\n", " "));
		this.listaNombreUnidad.replaceAll(nombreUnidad -> nombreUnidad.replaceAll("\n", " "));
		this.listaTipoDeActividad.replaceAll(tipoActividad -> tipoActividad.replaceAll("\n", " "));
	}
	
	/**
	 * Se encarga de ubicar y extraer únicamente el nombre de la licenciatura.
	 */
	public void ubicarNombreLicenciatura() {
		String regexEncontrarLicenciatura =
				"(Grado.o.Licenciatura.)(Licenciatura.en.[a-zA-ZÁ-Úá-ú]+)(.II\\..Datos.del.asesor)";
		
		Pattern pattern = Pattern.compile(regexEncontrarLicenciatura, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(textoCompletoAsignatura);
		
		while (matcher.find()) {
			this.nombreLicenciatura = matcher.group(2);
		}
	}
}

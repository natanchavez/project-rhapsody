package com.project.suayed.converter;

import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.suayed.constants.Routes.WORD_TEMPLATE_ROUTE;

/**
 * Se hace uso de una biblioteca externa llamada "docx4j" la cual nos permite realizar modificaciones a archivos de
 * Microsoft office, en este caso en particular se tienen distintos métodos para tomar una plantilla de Word e insertar
 * datos obtenidos del documento PDF para insertarlos y así poder crear un archivo para cada tarea a entregar de cada
 * asignatura con la información correspondiente, tal como profesor, fecha de entrega, descripción de la tarea, etc.
 */
public class GeneradorDePlantillasWord {
	private final String rutaCarpetaWord;
	private final String nombreLicenciatura;
	private final String nombreAsignatura;
	private final String nombreAsesor;
	private final String grupo;
	private final List<String> listaNumeroUnidadTareas;
	private final List<String[]> listaDiaMesAnyo;
	private final List<String> listaDescripcionTareas;
	private final List<String> listaNombreUnidad;
	private final List<String> listaTipoDeActividad;
	private Map<Integer, WordprocessingMLPackage> plantillasDeWord;
	
	/**
	 * Inicializa el objeto GeneradorDePlantillasWord y recibe tres objetos diferentes los cuales contienen información
	 * previamente recolectada y necesaria para el funcionamiento de los métodos en esta clase.
	 *
	 * @param rutaGuardar         contiene la ruta en la que se guardaran los archivos de Word generados.
	 * @param ubicadorDeDatos     contiene datos tales como asignatura, tareas, profesor, etc.
	 * @param formateadorDeFechas contiene las fechas de entrega de todas las tareas.
	 */
	public GeneradorDePlantillasWord(
			String rutaGuardar, UbicadorDeDatos ubicadorDeDatos, FormateadorDeFechas formateadorDeFechas) {
		
		this.rutaCarpetaWord = rutaGuardar;
		this.nombreLicenciatura = ubicadorDeDatos.getNombreLicenciatura();
		this.nombreAsignatura = ubicadorDeDatos.getNombreAsignatura();
		this.nombreAsesor = ubicadorDeDatos.getNombreAsesor();
		this.grupo = ubicadorDeDatos.getGrupo();
		this.listaNumeroUnidadTareas = ubicadorDeDatos.getListaNumeroUnidadTareas();
		this.listaDiaMesAnyo = formateadorDeFechas.getListaDiaMesAnyo();
		this.listaDescripcionTareas = ubicadorDeDatos.getListaDescripcionTareas();
		this.listaNombreUnidad = ubicadorDeDatos.getListaNombreUnidad();
		this.listaTipoDeActividad = ubicadorDeDatos.getListaTipoDeActividad();
	}
	
	/**
	 * Se encarga de tomar todas las descripciones de las tareas y realiza un reemplazo de ciertos símbolos, esto debido
	 * a que si están presentes tales caracteres crean conflicto al momento de ser ingresados en el archivo de Word,
	 * como caso particular están los símbolos "<" y ">" los cuales causan problemas ya que se introducen en un archivo
	 * XML el cual hace uso de manera extensiva de los símbolos antes mencionados.
	 */
	public void reemplazarCaracteresConflictivos() {
		this.listaDescripcionTareas.replaceAll(descripcion ->
				descripcion.replace("<", "&lt;").replace(">", "&gt;"));
	}
	
	/**
	 * Realiza el reemplazo de los datos necesarios en el archivo de Word para una tarea en específico, así mismo
	 * también guarda el archivo resultante en la subcarpeta previamente creada para tal fin.
	 */
	public void generarPlantillaParaCadaTarea() {
		this.plantillasDeWord = new HashMap<>();
		
		for (int i = 0; i < this.listaDescripcionTareas.size(); i++) {
			
			try {
				WordprocessingMLPackage plantillaDeWord = WordprocessingMLPackage.load(new File(WORD_TEMPLATE_ROUTE));
				HashMap<String, String> mapaDatosParaIngresar = new HashMap<>();
				VariablePrepare.prepare(plantillaDeWord);
				mapaDatosParaIngresar.put("licenciatura", this.nombreLicenciatura);
				mapaDatosParaIngresar.put("asignatura", this.nombreAsignatura);
				mapaDatosParaIngresar.put("asesor", this.nombreAsesor);
				mapaDatosParaIngresar.put("grupo", this.grupo);
				mapaDatosParaIngresar.put("dia", this.listaDiaMesAnyo.get(i)[0]);
				mapaDatosParaIngresar.put("mes", this.listaDiaMesAnyo.get(i)[1]);
				mapaDatosParaIngresar.put("anyo", this.listaDiaMesAnyo.get(i)[2]);
				mapaDatosParaIngresar.put("unidadNum", this.listaNumeroUnidadTareas.get(i));
				mapaDatosParaIngresar.put("nombreUnidad", this.listaNombreUnidad.get(i));
				mapaDatosParaIngresar.put("tipoActividad", this.listaTipoDeActividad.get(i));
				mapaDatosParaIngresar.put("instrucciones", this.listaDescripcionTareas.get(i));
				plantillaDeWord.getMainDocumentPart().variableReplace(mapaDatosParaIngresar);
				this.plantillasDeWord.put(i + 1, plantillaDeWord);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Se encarga de tomar el documento de Word generado con todos los datos de las tareas y los guarda en un nuevo
	 * archivo con formato .docx, si el archivo ya existe lo único que hará será sobrescribirlo.
	 */
	public void guardarInfoEnArchivoWord() {
		int cantidadDigitosFormato = String.valueOf(this.plantillasDeWord.size()).length();
		
		for (int i = 1; i <= this.plantillasDeWord.size(); i++) {
			String numeroDeTareas = String.format("%0" + cantidadDigitosFormato + "d", i);
			
			try {
				File directorioTareas = new File(this.rutaCarpetaWord + this.nombreAsignatura);
				directorioTareas.mkdirs();
				this.plantillasDeWord.get(i).save(
						new File(directorioTareas + "/tarea " + numeroDeTareas + ".docx"));
			} catch (Docx4JException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Se encarga de convertir el carácter de salto de línea en uno compatible con el formato que el documento de Word
	 * utiliza internamente.
	 */
	public void convertirSaltoDeLinea() {
		this.listaDescripcionTareas.replaceAll(descripcion ->
				descripcion.replace("\n", "</w:t><w:br/><w:t>"));
	}
}

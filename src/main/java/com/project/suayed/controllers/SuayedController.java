package com.project.suayed.controllers;

import com.project.suayed.services.FileHandlerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.suayed.constants.Routes.*;

/**
 * Controlador de la aplicación, contiene la funcionalidad necesaria para recibir las peticiones del navegador y
 * regresara la vista al archivo HTML correspondiente.
 */
@Controller
public class SuayedController {
	
	private final FileHandlerService fileHandlerService;
	
	public SuayedController(FileHandlerService fileHandlerService) {
		this.fileHandlerService = fileHandlerService;
		this.fileHandlerService.eliminarFolderConArchivos(STORAGE_ROOT_FOLDER);
	}
	
	@RequestMapping("/")
	public String landingPage() {
		return "redirect:/home/";
	}
	
	/**
	 * Página principal, contiene la interfaz para cargar los archivos PDF e iniciar el proceso de generación de
	 * archivos.
	 *
	 * @return La patina principal.
	 */
	@RequestMapping("/home")
	public String homePage() {
		return "home";
	}
	
	/**
	 * Recibe una petición POST del usuario para subir los archivos PDF y empezar a generar los archivos deseados.
	 *
	 * @param useExampleFile Indica si la aplicación esta siendo probada por un visitante o no.
	 * @param files          Uno o múltiples archivos PDF subidos por el usuario.
	 * @param chosenFormat   El tipo de formato de los archivos a generar.
	 * @return Redirecciona a la página en la cual se listarán los archivos que fueron generados.
	 */
	@PostMapping("/home")
	public String savePdfFiles(@RequestParam(required = false, value = "useExampleFile") boolean useExampleFile,
							   @RequestParam("files") MultipartFile[] files,
							   @RequestParam("format") String chosenFormat) {
		boolean successfulUpload;
		File[] archivosPDF;
		
		if (useExampleFile) {
			successfulUpload = fileHandlerService.saveExampleFile();
		} else {
			successfulUpload = fileHandlerService.saveChosenFiles(files);
		}
		
		if (successfulUpload) {
			fileHandlerService.eliminarFolderConArchivos(GENERATED_FILES_FODER);
			fileHandlerService.crearFolder(GENERATED_FILES_FODER);
			archivosPDF = fileHandlerService.listUploadedFiles(PDF_UPLOAD_FOLDER);
			fileHandlerService.convertToChosenFormat(chosenFormat, archivosPDF);
			
			return "redirect:/home/" + chosenFormat;
		} else {
			return "redirect:/home/";
		}
	}
	
	/**
	 * Página que enlista los archivos que fueron generados, aquí se da la opción de descargarlos en un formato
	 * comprimido.
	 *
	 * @param format El tipo de formato de los archivos que fueron generados.
	 * @param model  Objeto modelo con el cual se puede enviar al front-end la lista de los archivos generados.
	 * @return La página de descarga de los archivos generados.
	 */
	@RequestMapping("/home/{format:.+}")
	public String downloadGeneratedFiles(@PathVariable("format") String format, Model model) {
		File[] archivos;
		archivos = fileHandlerService.listUploadedFiles(GENERATED_FILES_FODER);
		model.addAttribute("archivos", archivos);
		
		if (format.equals("word")) {
			for (File homeworks : archivos) {
				List<String> files1 = Arrays.stream(Objects.requireNonNull(homeworks.listFiles()))
						.map(File::getName).collect(Collectors.toList());
				
				model.addAttribute(homeworks.getName(), files1);
			}
		}
		model.addAttribute("format", format);
		
		return "download";
	}
	
	/**
	 * Recibe la petición del usuario de descargar los archivos, la procesa y le envía el archivo comprimido.
	 *
	 * @param response Contiene información referente a la petición HTTP del usuario.
	 * @param format   El formato de los archivos que fueron generados.
	 */
	@RequestMapping("/home/{format:.+}/download")
	public void downloadCompressedFiles(HttpServletResponse response, @PathVariable("format") String format) {
		fileHandlerService.compressFiles(format);
		fileHandlerService.respondToDownloadRequest(response, format);
	}
	
	/**
	 * Pagina que contiene las instrucciones detalladas de como utilizar esta aplicación.
	 *
	 * @return La página con todas las instrucciones.
	 */
	@RequestMapping("/instructions")
	public String instructionsPage() {
		return "instructions";
	}
	
	/**
	 * Pagina que contiene una sección con las respuestas a las preguntas mas comunes acerca del uso de esta
	 * aplicación.
	 *
	 * @return La página con la sección de preguntas frecuentes.
	 */
	@RequestMapping("/faq")
	public String faqPage() {
		return "faq";
	}
	
	/**
	 * Pagina que contiene la sección con el estudio de caso referente a esta aplicación.
	 *
	 * @return La página con el texto del estudio de caso.
	 */
	@RequestMapping("/case-study")
	public String caseStudyPage() {
		return "case-study";
	}
	
	/**
	 * Página que contiene un listado de las tecnologías y bibliotecas utilizadas durante la realización de esta
	 * aplicación.
	 *
	 * @return La página con la sección de “acerca de”.
	 */
	@RequestMapping("/about")
	public String aboutPage() {
		return "about";
	}
}
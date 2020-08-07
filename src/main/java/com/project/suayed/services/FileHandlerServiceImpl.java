package com.project.suayed.services;

import com.project.suayed.converter.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.project.suayed.constants.Routes.*;

/**
 * Implementación de la interfaz de servicio, contiene la lógica necesaria para generar los archivos deseados, para
 * comprimir, así como también para crear o modificar carpetas.
 */
@Service
public class FileHandlerServiceImpl implements FileHandlerService {
	
	/**
	 * Se encarga de recibir uno o múltiples archivos en formato PDF y crea un directorio en el cual los almacenará.
	 *
	 * @param files Uno o varios archivos en formato PDF.
	 * @return Verdadero o falso dependiendo de si los archivos PDF recibidos pudieron ser guardados o no.
	 */
	@Override
	public boolean saveChosenFiles(MultipartFile[] files) {
		if (files.length == 1 && Objects.equals(files[0].getOriginalFilename(), "")) {
			return false;
		}
		
		eliminarFolderConArchivos(PDF_UPLOAD_FOLDER);
		crearFolder(PDF_UPLOAD_FOLDER);
		
		for (MultipartFile file : files) {
			try {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(PDF_UPLOAD_FOLDER + file.getOriginalFilename());
				Files.write(path, bytes);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**
	 * Recibe la ubicación de un directorio y regresa una lista de todos los archivos en dicho folder.
	 *
	 * @param uploadFolder La ruta del folder del cual se requiere listas los archivos que contiene.
	 * @return Una lista de los archivos que se encuentran dentro del folder especificado.
	 */
	@Override
	public File[] listUploadedFiles(String uploadFolder) {
		return new File(uploadFolder).listFiles();
	}
	
	/**
	 * Funciona como coordinador, recibe la lista de los archivos PDF a procesar, así como también el formato a generar
	 * y llama a los métodos necesarios para procesar la información en los planes de trabajo y así generar los archivos
	 * deseados.
	 *
	 * @param chosenFormat El tipo de archivos que se van a generar a partir de los documentos PDF.
	 * @param archivosPDF  El listado de todos los archivos PDF previamente subidos por el usuario.
	 */
	@Override
	public void convertToChosenFormat(String chosenFormat, File[] archivosPDF) {
		UbicadorDeDatos ubicadorDeDatos;
		FormateadorDeFechas formateadorDeFechas;
		GeneradorDeArchivoIcs generadorDeArchivoIcs;
		GeneradorDePlantillasWord generadorDePlantillasWord;
		GeneradorDeArchivoTxt generadorDeArchivoTxt;
		int archivosPdfRestantes;
		List<InformacionTareas> listaDeTareas;
		
		listaDeTareas = new ArrayList<>();
		archivosPdfRestantes = archivosPDF.length;
		
		for (File archivoPdfActual : archivosPDF) {
			ubicadorDeDatos = new UbicadorDeDatos(archivoPdfActual);
			ubicadorDeDatos.extraerTextoPDF();
			ubicadorDeDatos.eliminarEspaciosInnecesarios();
			
			ubicadorDeDatos.ubicarNombreLicenciatura();
			
			ubicadorDeDatos.ubicarClaveAsignatura();
			ubicadorDeDatos.cargarListaAsignatuas();
			ubicadorDeDatos.obtenerNombreAsignatura();
			ubicadorDeDatos.extraerNombreAsesor();
			ubicadorDeDatos.primeraLetraPalabrasEnMayuscula();
			ubicadorDeDatos.extraerGrupo();
			ubicadorDeDatos.delimitarTextoAreaTareas();
			ubicadorDeDatos.extraerFechasTareas();
			ubicadorDeDatos.delimitarDescripcionConPorcentajeTareas();
			ubicadorDeDatos.extraerDescripcionesTareas();
			ubicadorDeDatos.extraerExclusivamenteDescripcionesTareas();
			ubicadorDeDatos.extraerTareasNumeroUnidad();
			ubicadorDeDatos.extraerPorcentajesTareas();
			ubicadorDeDatos.obtenerTituloUnidad();
			ubicadorDeDatos.obtenerTipoDeActividad();
			ubicadorDeDatos.removerSaltosDeLinea();
			formateadorDeFechas = new FormateadorDeFechas(ubicadorDeDatos.getListaFechasTareas());
			formateadorDeFechas.separarDiaMesAnyo();
			formateadorDeFechas.acomodarAnyoMesDia();
			formateadorDeFechas.convertirMesANumero();
			formateadorDeFechas.unirFechaNumerica();
			formateadorDeFechas.encontrarDiaSiguienteDeFecha();
			
			switch (chosenFormat) {
				case "calendar":
					generadorDeArchivoIcs = new GeneradorDeArchivoIcs(GENERATED_FILES_FODER, ubicadorDeDatos,
							formateadorDeFechas);
					generadorDeArchivoIcs.escaparNuevaLinea();
					generadorDeArchivoIcs.unirDatosConFormatoICS();
					generadorDeArchivoIcs.guardarInfoEnArchivoICS();
					break;
				case "word":
					generadorDePlantillasWord = new GeneradorDePlantillasWord(GENERATED_FILES_FODER, ubicadorDeDatos,
							formateadorDeFechas);
					generadorDePlantillasWord.reemplazarCaracteresConflictivos();
					generadorDePlantillasWord.convertirSaltoDeLinea();
					generadorDePlantillasWord.generarPlantillaParaCadaTarea();
					generadorDePlantillasWord.guardarInfoEnArchivoWord();
					break;
				case "text":
					generadorDeArchivoTxt = new GeneradorDeArchivoTxt(GENERATED_FILES_FODER, ubicadorDeDatos,
							formateadorDeFechas);
					generadorDeArchivoTxt.acumularDatosDeTareas();
					listaDeTareas.addAll(generadorDeArchivoTxt.getListaDeTareas());
					
					if (--archivosPdfRestantes == 0) {
						generadorDeArchivoTxt.setListaDeTareas(listaDeTareas);
						generadorDeArchivoTxt.ordenarTareasPorFecha();
						generadorDeArchivoTxt.encontrarInicioFinSemestre();
						generadorDeArchivoTxt.obtenerDiasDelSemestre();
						generadorDeArchivoTxt.unirDatosConFormatoParaArchivoTxt();
						generadorDeArchivoTxt.guardarInfoEnArchivoTxt();
					}
					break;
			}
		}
	}
	
	/**
	 * Toma los archivos generados y los comprime en un archivo único, lo cual facilita la descarga de múltiples
	 * archivos por el usuario.
	 *
	 * @param format Es el formato elegido por el usuario de los archivos a generar, aquí se utiliza para ser utilizado
	 *               como nombre en el archivo comprimido.
	 */
	@Override
	public void compressFiles(String format) {
		String compressedFileRoute;
		
		eliminarFolderConArchivos(COMPRESSED_FILES_FOLDER);
		crearFolder(COMPRESSED_FILES_FOLDER);
		
		compressedFileRoute = COMPRESSED_FILES_FOLDER + format + ".zip";
		
		try {
			FileOutputStream fos = new FileOutputStream(compressedFileRoute);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			File fileToZip = new File(GENERATED_FILES_FODER);
			
			compressFile(fileToZip, fileToZip.getName(), zipOut);
			zipOut.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Se ejecuta cuando el usuario solicita la descarga de los archivos generados en formato comprimido, se ubica el
	 * archivo y se le envía al usuario para que lo descargue.
	 *
	 * @param response Contiene información referente a la respuesta del HTTP.
	 * @param format   El tipo de archivos generados a partir de los documentos PDF.
	 */
	@Override
	public void respondToDownloadRequest(HttpServletResponse response, String format) {
		File[] files = listUploadedFiles(COMPRESSED_FILES_FOLDER);
		
		File file = new File(files[0].getPath());
		if (file.exists()) {
			
			//get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				//unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}
			
			response.setContentType(mimeType);

            /*
              In a regular HTTP response, the Content-Disposition response header is a
              header indicating if the content is expected to be displayed inline in the
              browser, that is, as a Web page or as part of a Web page, or as an
              attachment, that is downloaded and saved locally.

             */

            /*
              Here we have mentioned it to show inline
             */
			response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
			
			//Here we have mentioned it to show as attachment
			//response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));
			
			response.setContentLength((int) file.length());
			
			InputStream inputStream;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(file));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Elimina el un folder especifico con todos los archivos que contiene.
	 *
	 * @param folderRoute La ruta del folder a eliminar.
	 */
	@Override
	public void eliminarFolderConArchivos(String folderRoute) {
		
		boolean storageFolderExist = Files.exists(Paths.get(folderRoute));
		
		if (storageFolderExist) {
			try {
				Files.walk(Paths.get(folderRoute))
						.sorted(Comparator.reverseOrder())
						.map(Path::toFile)
						.forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Recibe una ruta para un folder que aun no existe y lo crea.
	 *
	 * @param folderRoute La ruta del folder que aun no existe y se desea crear.
	 */
	@Override
	public void crearFolder(String folderRoute) {
		Path path = Paths.get(folderRoute);
		
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Se toma el plan de trabajo de ejemplo, utilizado para probar la aplicación y se guarda en el folder destinado
	 * para generar los archivos deseados.
	 *
	 * @return Verdadero o falso, dependiendo de si fue posible copiar el archivo de ejemplo a la ruta donde se
	 * almacenan los archivos PDF que serán procesados.
	 */
	@Override
	public boolean saveExampleFile() {
		eliminarFolderConArchivos(PDF_UPLOAD_FOLDER);
		crearFolder(PDF_UPLOAD_FOLDER);
		
		Path src = Paths.get(EXAMPLE_PDF_ROUTE);
		Path dest = Paths.get(PDF_UPLOAD_FOLDER + EXAMPLE_PDF_FILENAME);
		try {
			Files.copy(src, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Auxilia al método de comprimir archivos, se encarga de añadir un nuevo elemento al archivo comprimido, de esta
	 * manera también se pueden agregar carpetas al archivo comprimido.
	 *
	 * @param fileToZip El elemento que será agregado al archivo comprimido.
	 * @param fileName  Nombre del elemento que será agregado al archivo comprimido.
	 * @param zipOut    El archivo comprimido que se está generando.
	 * @throws IOException En caso de que el elemento a agregar al archivo comprimido no pueda ser ubicado, ya sea
	 *                     porque no existe o porque ya no se encuentra disponible.
	 */
	private void compressFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		
		if (fileToZip.isHidden()) {
			return;
		}
		
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
			}
			zipOut.closeEntry();
			
			File[] children = fileToZip.listFiles();
			
			for (File childFile : Objects.requireNonNull(children)) {
				compressFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
}

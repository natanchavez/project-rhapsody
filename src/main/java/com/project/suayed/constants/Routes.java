package com.project.suayed.constants;

/**
 * Contiene las rutas utilizadas por esta aplicacion, de esta manera si se requiere modificar una ruta entonces
 * simplemente se puede realizar el cambio aquí y el cambio se verá reflejado en todos los lugares que utilicen dicha
 * ruta.
 */
public final class Routes {

    public static final String STORAGE_ROOT_FOLDER = "./suayedFiles/";

    public static final String PDF_UPLOAD_FOLDER = "./suayedFiles/pdfFiles/";
    public static final String COMPRESSED_FILES_FOLDER = "./suayedFiles/compressedFiles/";
    public static final String GENERATED_FILES_FODER = "./suayedFiles/generatedFiles/";

    public static final String WORD_TEMPLATE_ROUTE = "./resource/plantilla Word/plantilla.docx";
    public static final String SUBJECTS_LIST_ROUTE = "./resource/lista de asignaturas/asignaturas.txt";
    public static final String EXAMPLE_PDF_ROUTE = "./src/main/resources/static/pdf/example.pdf";
    public static final String EXAMPLE_PDF_FILENAME = "example.pdf";

    private Routes() {
    }
}
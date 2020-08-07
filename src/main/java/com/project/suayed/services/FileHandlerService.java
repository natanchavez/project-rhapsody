package com.project.suayed.services;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface FileHandlerService {

    boolean saveChosenFiles(MultipartFile[] files);

    File[] listUploadedFiles(String uploadFolder);

    void convertToChosenFormat(String chosenFormat, File[] archivosPDF);

    void compressFiles(String format);

    void respondToDownloadRequest(HttpServletResponse response, String format);

    void eliminarFolderConArchivos(String storageRootFolder);

    void crearFolder(String pdfUploadFolder);
    
    boolean saveExampleFile();
}

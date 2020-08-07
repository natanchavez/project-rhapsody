package com.project.suayed.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

public class GeneradorDeArchivoTxt {
    private final String rutaCarpetaTxt;
    private final List<String> listaPorcentajesTareas;
    private final List<String> listaFechaDiaActual;
    private final List<String> listaNumeroUnidadTareas;
    private final List<String> listaDescripcionRefinadaTareas;
    private final List<String> listaNombreUnidad;
    private final List<String> listaTipoDeActividad;
    private final String claveAsignatura;
    private final String nombreAsignatura;

    private final StringBuilder descripcionCompletaArchivoTxt = new StringBuilder();
    private List<InformacionTareas> listaDeTareas = new ArrayList<>();
    private String fechaInicioSemestre;
    private String fechaFinalSemestre;
    private List<String> listaFechasDelSemestre;


    public GeneradorDeArchivoTxt(String rutaGuardar, UbicadorDeDatos ubicadorDeDatos, FormateadorDeFechas formateadorDeFechas) {
        this.rutaCarpetaTxt = rutaGuardar;
        this.claveAsignatura = ubicadorDeDatos.getClaveAsignatura();
        this.listaPorcentajesTareas = ubicadorDeDatos.getListaPorcentajesTareas();
        this.listaFechaDiaActual = formateadorDeFechas.getListaFechaDiaActual();
        this.nombreAsignatura = ubicadorDeDatos.getNombreAsignatura();
        this.listaNumeroUnidadTareas = ubicadorDeDatos.getListaNumeroUnidadTareas();
        this.listaDescripcionRefinadaTareas = ubicadorDeDatos.getListaDescripcionRefinadaTareas();
        this.listaNombreUnidad = ubicadorDeDatos.getListaNombreUnidad();
        this.listaTipoDeActividad = ubicadorDeDatos.getListaTipoDeActividad();
    }

    public List<InformacionTareas> getListaDeTareas() {
        return listaDeTareas;
    }

    public void setListaDeTareas(List<InformacionTareas> listaDeTareas) {
        this.listaDeTareas = new ArrayList<>();
        this.listaDeTareas = listaDeTareas;
    }

    public void acumularDatosDeTareas() {
        for (int i = 0; i < this.listaFechaDiaActual.size(); i++) {
            InformacionTareas informacionTareas = new InformacionTareas();

            String fechaActual = this.listaFechaDiaActual.get(i);
            String numeroUnidad = this.listaNumeroUnidadTareas.get(i);
            String tituloUnidad = this.listaNombreUnidad.get(i);
            String tipoTarea = this.listaTipoDeActividad.get(i);
            String descripcionTarea = this.listaDescripcionRefinadaTareas.get(i);
            String porcentajeTarea = this.listaPorcentajesTareas.get(i);

            informacionTareas.setNombreAsignatura(this.nombreAsignatura);
            informacionTareas.setClaveAsignatura(this.claveAsignatura);
            informacionTareas.setFechaEntregaTarea(fechaActual);
            informacionTareas.setNumeroUnidad(numeroUnidad);
            informacionTareas.setTituloUnidad(tituloUnidad);
            informacionTareas.setTipoTarea(tipoTarea);
            informacionTareas.setDescripcionTarea(descripcionTarea);
            informacionTareas.setPorcentajeTarea(porcentajeTarea);

            listaDeTareas.add(informacionTareas);
        }
    }

    public void ordenarTareasPorFecha() {
        Comparator<InformacionTareas> compararPorFechaDeEntrega =
                Comparator.comparing(informacionTareas -> Integer.parseInt(informacionTareas.getFechaEntregaTarea()));
        this.listaDeTareas.sort(compararPorFechaDeEntrega);
    }

    public void encontrarInicioFinSemestre() {
        String fechaPrimeraTarea = this.listaDeTareas.get(0).getFechaEntregaTarea();
        String fechaUltimaTarea = this.listaDeTareas.get(this.listaDeTareas.size() - 1).getFechaEntregaTarea();

        this.fechaInicioSemestre = obtenerInicioYFinDeSemana(fechaPrimeraTarea);
        this.fechaFinalSemestre = obtenerInicioYFinDeSemana(fechaUltimaTarea);
    }

    public void obtenerDiasDelSemestre() {
        this.listaFechasDelSemestre = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate fechaInicial = LocalDate.parse(String.valueOf(fechaInicioSemestre), dateTimeFormatter);
        LocalDate fechaDiaSiguiente = LocalDate.parse(String.valueOf(fechaFinalSemestre), dateTimeFormatter);

        while (!fechaInicial.isAfter(fechaDiaSiguiente)) {
            String refactorizar = String.valueOf(fechaInicial).replaceAll("-", "");
            this.listaFechasDelSemestre.add(refactorizar);
            fechaInicial = fechaInicial.plusDays(1);
        }
    }

    public void unirDatosConFormatoParaArchivoTxt() {
        int semanaActual = 1;
        String inicioDeSemana = "domingo";

        for (String fechaActual : this.listaFechasDelSemestre) {
            int[] indicesDeResultados = IntStream.range(0, this.listaDeTareas.size())
                    .filter(tarea -> this.listaDeTareas.get(tarea).getFechaEntregaTarea().equals(fechaActual))
                    .toArray();

            if (obtenerDiaDeLaSemana(fechaActual).equals(inicioDeSemana)) {
                this.descripcionCompletaArchivoTxt.append("Semana ")
                        .append(semanaActual)
                        .append(" del: [").append(convertirFechaFormatoTexto(fechaActual))
                        .append("] al: [").append(convertirFechaFormatoTexto(obtenerFinalSemana(fechaActual)))
                        .append("] {");

                semanaActual++;
            }

            this.descripcionCompletaArchivoTxt.append("\r\n\t")
                    .append(obtenerDiaDeLaSemana(fechaActual))
                    .append(" (")
                    .append(convertirFechaFormatoTexto(fechaActual))
                    .append(") {");

            for (Object indiceDeTarea : indicesDeResultados) {
                this.descripcionCompletaArchivoTxt.append("\r\n\r\n\t\t-------->        0        <--------")
                        .append("\r\n\t\t\t")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getNombreAsignatura())
                        .append(" / ")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getClaveAsignatura())
                        .append("\r\n\t\t\t")
                        .append(convertirFechaFormatoTexto(this.listaDeTareas.get((Integer) indiceDeTarea).getFechaEntregaTarea()))
                        .append("\r\n\t\t\t")
                        .append("Unidad ")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getNumeroUnidad())
                        .append(": ")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getTituloUnidad())
                        .append("\r\n\t\t\t")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getTipoTarea())
                        .append("\r\n\t\t\t\t")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getDescripcionTarea().replaceAll("\\n", "\n\t\t\t\t"))
                        .append("\r\n\t\t\t")
                        .append(this.listaDeTareas.get((Integer) indiceDeTarea).getPorcentajeTarea());
            }

            this.descripcionCompletaArchivoTxt.append("\r\n\t}");

            if (obtenerDiaDeLaSemana(fechaActual).equals("sabado")) {
                this.descripcionCompletaArchivoTxt.append("\r\n}\r\n");
            }
        }
    }

    public void guardarInfoEnArchivoTxt() {

        try {
            FileWriter writer = new FileWriter(this.rutaCarpetaTxt + "tareas del semestre.txt");
            writer.write(String.valueOf(this.descripcionCompletaArchivoTxt));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String obtenerDiaDeLaSemana(String fechaActual) {
        String nombreDelDia;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(simpleDateFormat.parse(fechaActual));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int diaDeLaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        switch (diaDeLaSemana) {
            case 1:
                nombreDelDia = "domingo";
                break;
            case 2:
                nombreDelDia = "lunes";
                break;
            case 3:
                nombreDelDia = "martes";
                break;
            case 4:
                nombreDelDia = "miercoles";
                break;
            case 5:
                nombreDelDia = "jueves";
                break;
            case 6:
                nombreDelDia = "viernes";
                break;
            case 7:
                nombreDelDia = "sabado";
                break;
            default:
                throw new IllegalStateException("valor inesperado: " + diaDeLaSemana);
        }

        return nombreDelDia;
    }

    public String obtenerFinalSemana(String fechaInicioSemana) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        try {
            calendar.setTime(simpleDateFormat.parse(fechaInicioSemana));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.add(Calendar.DAY_OF_MONTH, 6);

        return simpleDateFormat.format(calendar.getTime());
    }

    private String obtenerInicioYFinDeSemana(String fechaTarea) {
        boolean esPrimeraTarea = fechaTarea.equals(this.listaDeTareas.get(0).getFechaEntregaTarea());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        try {
            calendar.setTime(simpleDateFormat.parse(fechaTarea));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int numeroAnyo = calendar.get(Calendar.YEAR);
        int numeroSemana = calendar.get(Calendar.WEEK_OF_YEAR);

        calendar.clear();

        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.YEAR, numeroAnyo);
        calendar.set(Calendar.WEEK_OF_YEAR, numeroSemana);

        if (esPrimeraTarea) {
            calendar.set(Calendar.DAY_OF_WEEK, 1);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, 7);
        }

        return simpleDateFormat.format(calendar.getTime());
    }

    public String convertirFechaFormatoTexto(String fecha) {
        Locale fechaEspanyolMex = new Locale("es", "MX");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatoFechaTexto = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", fechaEspanyolMex);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        try {
            calendar.setTime(simpleDateFormat.parse(fecha));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formatoFechaTexto.format(calendar.getTime());
    }
}

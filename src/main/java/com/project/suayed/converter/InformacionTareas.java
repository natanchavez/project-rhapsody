package com.project.suayed.converter;

/**
 * Este objeto almacena toda la información referente a una tarea específica.
 */
public class InformacionTareas {
	private String nombreAsignatura;
	private String claveAsignatura;
	private String fechaEntregaTarea;
	private String numeroUnidad;
	private String tituloUnidad;
	private String tipoTarea;
	private String descripcionTarea;
	private String porcentajeTarea;
	
	public String getNombreAsignatura() {
		return nombreAsignatura;
	}
	
	public void setNombreAsignatura(String nombreAsignatura) {
		this.nombreAsignatura = nombreAsignatura;
	}
	
	public String getClaveAsignatura() {
		return claveAsignatura;
	}
	
	public void setClaveAsignatura(String claveAsignatura) {
		this.claveAsignatura = claveAsignatura;
	}
	
	public String getFechaEntregaTarea() {
		return fechaEntregaTarea;
	}
	
	public void setFechaEntregaTarea(String fechaEntregaTarea) {
		this.fechaEntregaTarea = fechaEntregaTarea;
	}
	
	public String getNumeroUnidad() {
		return numeroUnidad;
	}
	
	public void setNumeroUnidad(String numeroUnidad) {
		this.numeroUnidad = numeroUnidad;
	}
	
	public String getTituloUnidad() {
		return tituloUnidad;
	}
	
	public void setTituloUnidad(String tituloUnidad) {
		this.tituloUnidad = tituloUnidad;
	}
	
	public String getTipoTarea() {
		return tipoTarea;
	}
	
	public void setTipoTarea(String tipoTarea) {
		this.tipoTarea = tipoTarea;
	}
	
	public String getDescripcionTarea() {
		return descripcionTarea;
	}
	
	public void setDescripcionTarea(String descripcionTarea) {
		this.descripcionTarea = descripcionTarea;
	}
	
	public String getPorcentajeTarea() {
		return porcentajeTarea;
	}
	
	public void setPorcentajeTarea(String porcentajeTarea) {
		this.porcentajeTarea = porcentajeTarea;
	}
}

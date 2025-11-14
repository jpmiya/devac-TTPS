package org.example.devac.dto;

import org.example.devac.models.EstadoMascota;
import java.time.LocalDate;

public class MascotaRequest {
    private Long duenoId;
    private String nombre;
    private String tamaño;
    private String color;
    private LocalDate fechaDePerdida;
    private EstadoMascota estado;
    private String foto;
    private String coordenadas;
    private String descripcion;
    private String tipo;
    private String raza;

    // Constructor vacío
    public MascotaRequest() {
    }

    // Getters
    public Long getDuenoId() {
        return duenoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTamaño() {
        return tamaño;
    }

    public String getColor() {
        return color;
    }

    public LocalDate getFechaDePerdida() {
        return fechaDePerdida;
    }

    public EstadoMascota getEstado() {
        return estado;
    }

    public String getFoto() {
        return foto;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRaza() {
        return raza;
    }

    // Setters
    public void setDuenoId(Long duenoId) {
        this.duenoId = duenoId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setFechaDePerdida(LocalDate fechaDePerdida) {
        this.fechaDePerdida = fechaDePerdida;
    }

    public void setEstado(EstadoMascota estado) {
        this.estado = estado;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }
}

package org.example.devac.dto;

import org.example.devac.models.EstadoMascota;
import java.time.LocalDate;

public class MascotaResponse {
    private Long id;
    private Long duenoId;

    private String nombre;
    private String tipo;
    private String raza;
    private String tamanio; // <-- sin ñ
    private String color;

    private LocalDate fechaDePerdida;
    private EstadoMascota estado;

    private String coordenadas;
    private String descripcion;
    private String fotoUrl;

    public MascotaResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDuenoId() { return duenoId; }
    public void setDuenoId(Long duenoId) { this.duenoId = duenoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDate getFechaDePerdida() { return fechaDePerdida; }
    public void setFechaDePerdida(LocalDate fechaDePerdida) { this.fechaDePerdida = fechaDePerdida; }

    public EstadoMascota getEstado() { return estado; }
    public void setEstado(EstadoMascota estado) { this.estado = estado; }

    public String getCoordenadas() { return coordenadas; }
    public void setCoordenadas(String coordenadas) { this.coordenadas = coordenadas; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}

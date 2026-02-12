package org.example.devac.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class AvistamientoRequest {
    private Long usuarioId;
    private Long mascotaId;
    private String fecha;
    private String foto;
    private String coordenadas;
    @JsonAlias({"lat", "latitude"})
    private Double latitud;
    @JsonAlias({"lng", "lon", "long", "longitude"})
    private Double longitud;
    private String comentario;

    // Constructor vacío
    public AvistamientoRequest() {
    }

    // Getters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFoto() {
        return foto;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public String getComentario() {
        return comentario;
    }

    // Setters
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

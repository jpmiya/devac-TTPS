package org.example.devac.dto;

public class AvistamientoRequest {
    private Long usuarioId;
    private Long mascotaId;
    private String fecha;
    private String foto;
    private String coordenadas;
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

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

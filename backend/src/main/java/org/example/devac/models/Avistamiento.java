package org.example.devac.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"usuario", "mascota"})
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AVISTAMIENTO_ID")
    private Long id;
    private String fecha;
    private String foto; // arreglo de bytes
    private String coordenadas;
    private String comentario;
    private String barrio;
    private String ciudad;
    // que no se permita un Avistamiento q no tiene un usuario ni una mascota
    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "MASCOTA_ID", nullable = false)
    private Mascota mascota;

    // Constructor
    public Avistamiento(Usuario usuario,Mascota mascota, String fecha, String foto, String coordenadas, String comentario) {
        this.fecha = fecha;
        this.foto = foto;
        this.coordenadas = coordenadas;
        this.comentario = comentario;
        this.mascota = mascota;
        this.usuario = usuario;
    }

    public Avistamiento() {}

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

    public Usuario getUsuario() {
        return usuario;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Long getId() {
        return id;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
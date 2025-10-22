package org.example.devac.models;


import jakarta.persistence.*;

@Entity
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AVISTAMIENTO_ID")
    private Long id;
    private String fecha;
    private String foto; // arreglo de bytes
    private String coordenadas;
    private String comentario;
    @ManyToOne
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "MASCOTA_ID")
    private Mascota mascota;

    // Constructor
    public Avistamiento(String fecha, String foto, String coordenadas, String comentario) {
        this.fecha = fecha;
        this.foto = foto;
        this.coordenadas = coordenadas;
        this.comentario = comentario;
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

}
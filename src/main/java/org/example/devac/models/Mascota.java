package org.example.devac.models;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;


@Entity
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MASCOTA_ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "USUARIO_ID")
    private Usuario dueno;
    private String nombre;
    private String tamaño;
    private String color;
    private LocalDate fecha_de_perdida;
    private EstadoMascota estado;
    private String foto; // URL de la foto
    private String coordenadas;
    private String descripcion;
    @OneToMany(mappedBy = "mascota", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Avistamiento> avistamientos;
    private String tipo;
    private String raza;


    // Constructor por defecto
    public Mascota() {
    }

    
    public EstadoMascota getEstado() {
        return estado;
    }

    public void setEstado(EstadoMascota estado) {
        this.estado = estado;
    }

    public void agregar_avistamiento(Avistamiento avistamiento) {

    }

    public List<Avistamiento> getAvistamientos() {
        return avistamientos;
    }
    
    public String getNombre() {
        return this.nombre;
    }

    public String getTamaño() {
        return this.tamaño;
    }

    public String getColor() {
        return this.color;
    }

    public LocalDate getFechaDePerdida() {
        return this.fecha_de_perdida;
    }

    public String getFoto() {
        return this.foto;
    }

    public String getCoordenadas() {
        return this.coordenadas;
    }

    public String getDescripcion() {
        return this.descripcion;
    }
}

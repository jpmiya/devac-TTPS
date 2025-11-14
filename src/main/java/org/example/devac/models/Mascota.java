package org.example.devac.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;


@Entity
@JsonIgnoreProperties({"avistamientos"})
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


    public void setDueno(Usuario dueno) {
        this.dueno = dueno;
    }

    public List<Avistamiento> getAvistamientos() {
        return avistamientos;
    }
    
    public Long getId() {
        return id;
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

    public String getTipo() {
        return this.tipo;
    }

    public String getRaza() {
        return this.raza;
    }

    public Usuario getDueno() {
        return this.dueno;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setFechaDePerdida(LocalDate fecha) {
        this.fecha_de_perdida = fecha;
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

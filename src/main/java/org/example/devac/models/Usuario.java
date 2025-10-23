package org.example.devac.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USUARIO_ID")
    private Long id;
    private String nombreYApellido;
    private String mail;
    private String password;
    private String telefono;
    private String barrio;
    private String ciudad;
    private int posicion;
    private int puntos;
    private RolEnum rol;
    private int casosEnZona;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Medalla> medallas;
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Avistamiento> avistamientos;

    // Constructor
    public Usuario(String nombreYApellido, String mail, String password, String telefono,
                   String barrio, String ciudad, int posicion, int puntos, int casosEnZona) {
        this.nombreYApellido = nombreYApellido;
        this.mail = mail;
        this.password = password;
        this.telefono = telefono;
        this.barrio = barrio;
        this.ciudad = ciudad;
        this.posicion = posicion;
        this.puntos = puntos;
        this.casosEnZona = casosEnZona;
        this.medallas = new ArrayList<>();
        this.avistamientos = new ArrayList<>();
    }

    public Usuario() {

    }


    public void crearAvistamiento(String nombreMascota, String coordenadas, String foto, Date fecha, String comentario) {
        // Por implementar
    }

    public void sumarPuntos(int cantidad) {
        // Por implementar
    }

    public void restarPuntos(int cantidad) {
        // Por implementar
    }

    public void crearMascotaPerdida(Mascota mascota) {
        // Por implementar
    }

    public void obtenerMedalla(String medalla) {
        // Por implementar
    }

    public void mascotaEncontrada(Mascota mascota) {
        // Por implementar
    }

    public void adoptarMascota(Mascota mascota) {
        // Por implementar
    }

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getCasosEnZona() {
        return casosEnZona;
    }

    public long getId() { return id; }

    public List<Medalla> getMedallas() {
        return medallas;
    }

    public List<Avistamiento> getAvistamientos() {
        return avistamientos;
    }

    // Setters necesarios
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setMail(String mail) {this.mail = mail;}

    public void setNombreYApellido(String nombreYApellido){this.nombreYApellido = nombreYApellido;}

}

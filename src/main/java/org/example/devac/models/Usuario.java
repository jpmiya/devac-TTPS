package org.example.devac.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@JsonIgnoreProperties({"avistamientos", "mascotas"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USUARIO_ID")
    private Long id;
    private String nombreYApellido;
    @Column(unique = true, nullable = false)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    @OneToMany(mappedBy = "dueno", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Mascota> mascotas;

    // Constructor
    public Usuario(String nombreYApellido, String email, String password, String telefono,
                   String barrio, String ciudad, int posicion, int puntos, int casosEnZona) {
        this.nombreYApellido = nombreYApellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.barrio = barrio;
        this.ciudad = ciudad;
        this.posicion = posicion;
        this.puntos = puntos;
        this.casosEnZona = casosEnZona;
        this.medallas = new ArrayList<>();
        this.avistamientos = new ArrayList<>();
        this.mascotas = new ArrayList<>();
    }

    public Usuario() {
    }

    public Mascota agregarMascota(Mascota mascota) {
        this.mascotas.add(mascota);
        return mascota;
    }

    public void eliminarMascota(Mascota mascota) {
        this.mascotas.remove(mascota);
    }

    public void sumarPuntos(int cantidad) {
        // Por implementar
    }

    public void restarPuntos(int cantidad) {
        // Por implementar
    }

    public void crearMascotaPerdida(Mascota mascota) {

    }

    public void obtenerMedalla(String medalla) {
        // Por implementar
    }

    public void adoptarMascota(Mascota mascota) {
        // Por implementar
    }

    // ====================
    // Getters
    // ====================

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public String getEmail() {
        return email;
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

    public long getId() {
        return id;
    }

    public List<Medalla> getMedallas() {
        return medallas;
    }

    public List<Avistamiento> getAvistamientos() {
        return avistamientos;
    }

    public RolEnum getRol() {
        return rol;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    // ====================
    // Setters
    // ====================

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNombreYApellido(String nombreYApellido) {
        this.nombreYApellido = nombreYApellido;
    }

    public void setPassword(String hashed) {
        this.password = hashed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setCasosEnZona(int casosEnZona) {
        this.casosEnZona = casosEnZona;
    }

    public void setMedallas(List<Medalla> medallas) {
        this.medallas = medallas;
    }

    public void setAvistamientos(List<Avistamiento> avistamientos) {
        this.avistamientos = avistamientos;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    public void setRol(RolEnum rol) {
        this.rol = rol;
    }

    // builder

    public static class Builder {
        private String nombreYApellido;
        private String email;
        private String password;
        private String telefono;
        private String barrio;
        private String ciudad;
        private int posicion = 0;
        private int puntos = 0;
        private int casosEnZona = 0;
        private RolEnum rol = RolEnum.USUARIO;

        public Builder nombreYApellido(String nombreYApellido) {
            this.nombreYApellido = nombreYApellido;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public Builder barrio(String barrio) {
            this.barrio = barrio;
            return this;
        }

        public Builder ciudad(String ciudad) {
            this.ciudad = ciudad;
            return this;
        }

        public Builder posicion(int posicion) {
            this.posicion = posicion;
            return this;
        }

        public Builder puntos(int puntos) {
            this.puntos = puntos;
            return this;
        }

        public Builder casosEnZona(int casosEnZona) {
            this.casosEnZona = casosEnZona;
            return this;
        }

        public Builder rol(RolEnum rol) {
            this.rol = rol;
            return this;
        }

        public Usuario build() {
            // Validaciones
            validateRequired(email, "Email");
            validateRequired(password, "Password");
            validateRequired(nombreYApellido, "Nombre y Apellido");

            Usuario usuario = new Usuario();
            usuario.nombreYApellido = this.nombreYApellido;
            usuario.email = this.email;
            usuario.password = this.password;
            usuario.telefono = this.telefono;
            usuario.barrio = this.barrio;
            usuario.ciudad = this.ciudad;
            usuario.posicion = this.posicion;
            usuario.puntos = this.puntos;
            usuario.casosEnZona = this.casosEnZona;
            usuario.rol = this.rol;
            usuario.medallas = new ArrayList<>();
            usuario.avistamientos = new ArrayList<>();
            usuario.mascotas = new ArrayList<>();
            return usuario;
        }
    }

    // ====================
    // Métodos de Validación (reutilizables)
    // ====================

    private static void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(fieldName + " es requerido y no puede estar vacío");
        }
    }
}

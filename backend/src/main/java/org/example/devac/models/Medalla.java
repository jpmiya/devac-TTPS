package org.example.devac.models;

public enum Medalla {
    RESCATISTA_NIVEL_1("Rescatista nivel 1: pocas mascotas reportadas/ayudadas"),
    RESCATISTA_NIVEL_2("Rescatista nivel 2: cantidad intermedia de mascotas reportadas/ayudadas"),
    RESCATISTA_NIVEL_3("Rescatista nivel 3: muchas mascotas reportadas/ayudadas"),
    HEROE_DEL_BARRIO("Héroe del barrio: ayuda en más de X casos de su zona"),
    ANGEL_GUARDIAN("Ángel guardián: mantiene varias mascotas en tránsito hasta adopción/reencuentro"),
    NUEVO_TUTOR("Nuevo Tutor: persona que adoptó una mascota recientemente");

    private final String descripcion;

    Medalla(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
    

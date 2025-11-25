package org.example.devac.services;

import org.example.devac.models.Usuario;

public class UserEditerService {




    public void edit(String nombre,String email,String telefono, String barrio, String ciudad, Integer posicion, Usuario existente){
        this.setNombreYApellidoNullPattern(nombre, existente);
        this.setEmailNullPattern(email, existente);
        this.setTelefonoNullPattern(telefono, existente);
        this.setBarrioNullPattern(barrio, existente);
        this.setCiudadNullPattern(ciudad, existente);
        this.setPosicionNullPattern(posicion, existente); //no estoy segurod e q este vaya
    }


    private String setNombreYApellidoNullPattern(String nombre, Usuario existente){
        if (nombre == null) return existente.getNombreYApellido();
        existente.setNombreYApellido(nombre);
        return existente.getNombreYApellido();
    }

    private String setEmailNullPattern(String email, Usuario existente){
        if (email == null) return existente.getEmail();
        existente.setNombreYApellido(email);
        return existente.getNombreYApellido();
    }

    private String setTelefonoNullPattern(String telefono, Usuario existente){
        if (telefono == null) return existente.getTelefono();
        existente.setTelefono(telefono);
        return existente.getTelefono();
    }

    private String setBarrioNullPattern(String barrio, Usuario existente){
        if (barrio == null) return existente.getBarrio();
        existente.setBarrio(barrio);
        return existente.getBarrio();
    }

    private String setCiudadNullPattern(String ciudad, Usuario existente){
        if (ciudad == null) return existente.getCiudad();
        existente.setCiudad(ciudad);
        return existente.getCiudad();
    }

    // acá asumo que posición es numérico; ajustá tipos después como quieras
    private int setPosicionNullPattern(Integer posicion, Usuario existente){
        if (posicion == null) return existente.getPosicion();
        existente.setPosicion(posicion);
        return existente.getPosicion();
    }




}

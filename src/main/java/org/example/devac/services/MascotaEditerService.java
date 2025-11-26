package org.example.devac.services;

import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.repositories.MascotaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//no se si es correcto que esto sea un service, chequear despues
@Service
public class MascotaEditerService {

    @Autowired
    MascotaRepo mascotaRepository;

    public Mascota edit(Long id, Mascota cambios) {
        Mascota existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Mascota no encontrada con ID: " + id));

        // Relación dueño
        if (cambios.getDueno() != null) {
            existente.setDueno(cambios.getDueno());
        }

        if (cambios.getNombre() != null) {
            existente.setNombre(cambios.getNombre());
        }

        if (cambios.getTamaño() != null) {
            existente.setTamaño(cambios.getTamaño());
        }

        if (cambios.getColor() != null) {
            existente.setColor(cambios.getColor());
        }

        if (cambios.getFecha_de_perdida() != null) {
            existente.setFecha_de_perdida(cambios.getFecha_de_perdida());
        }

        if (cambios.getEstado() != null) {
            existente.setEstado(cambios.getEstado());
        }

        if (cambios.getFoto() != null) {
            existente.setFoto(cambios.getFoto());
        }

        if (cambios.getCoordenadas() != null) {
            existente.setCoordenadas(cambios.getCoordenadas());
        }

        if (cambios.getDescripcion() != null) {
            existente.setDescripcion(cambios.getDescripcion());
        }

        if (cambios.getAvistamientos() != null) {
            existente.setAvistamientos(cambios.getAvistamientos());
        }

        if (cambios.getTipo() != null) {
            existente.setTipo(cambios.getTipo());
        }

        if (cambios.getRaza() != null) {
            existente.setRaza(cambios.getRaza());
        }

        return mascotaRepository.save(existente);
    }
}

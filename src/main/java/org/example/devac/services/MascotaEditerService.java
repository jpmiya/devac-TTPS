package org.example.devac.services;

import jakarta.ws.rs.NotFoundException;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.repositories.MascotaRepo;
import org.example.devac.utils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//no se si es correcto que esto sea un service, chequear despues
@Service
public class MascotaEditerService {

    @Autowired
    MascotaRepo mascotaRepository;

    public Mascota edit(Long id, Mascota cambios) {
        Mascota existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mascota no encontrada con ID: " + id));

        String [] ignorar = PropertyUtils.getNullPropertyNames(cambios);
        BeanUtils.copyProperties(cambios, existente, ignorar);

        return mascotaRepository.save(existente);
    }
}

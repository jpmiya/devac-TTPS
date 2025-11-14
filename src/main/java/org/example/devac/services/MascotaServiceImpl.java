package org.example.devac.services;

import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.example.devac.repositories.MascotaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    MascotaRepo mascotaRepo;

    @Override
    public Mascota registrar(Mascota mascota) {
        return mascotaRepo.save(mascota);
    }

    @Override
    public Mascota editar(Mascota mascota) {
        return mascotaRepo.save(mascota);
    }

    public void eliminar(Mascota mascota) {
        mascotaRepo.delete(mascota);
    }

    public List<Mascota> getAllLost() {
        List<Mascota> perdidos = mascotaRepo.findAllByEstado(EstadoMascota.PERDIDO_AJENO);
        List<Mascota> perdidosMios = mascotaRepo.findAllByEstado(EstadoMascota.PERDIDO_PROPIO);
        perdidos.addAll(perdidosMios);
        return perdidos;
    }




}

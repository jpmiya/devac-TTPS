package org.example.devac.services;

import org.example.devac.dto.MascotaRequest;
import org.example.devac.models.Mascota;

import java.util.List;
import java.util.Map;

public interface MascotaService {
    Mascota registrar(MascotaRequest request);
    Mascota editar(Mascota mascota);
    void eliminar(Mascota mascota);
    List<Mascota> findAllLost();
    //prueba
}

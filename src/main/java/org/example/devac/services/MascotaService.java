package org.example.devac.services;

import org.example.devac.models.Mascota;

import java.util.List;
import java.util.Map;

public interface MascotaService {
    Mascota registrar(Mascota mascota);
    Mascota editar(Mascota mascota);
    void eliminar(Mascota mascota);
    List<Mascota> findAllLost();
    //prueba
}

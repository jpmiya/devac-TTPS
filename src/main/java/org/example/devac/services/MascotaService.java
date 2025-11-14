package org.example.devac.services;

import org.example.devac.models.Mascota;
import java.util.Map;

public interface MascotaService {
    Mascota registrar(Mascota mascota);
    Mascota editar(Mascota mascota);
    void eliminar(Mascota mascota);
    //prueba
}

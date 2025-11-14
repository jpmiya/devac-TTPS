package org.example.devac.DAOs;

import java.util.List;

public interface MascotaDAO<T> extends GenericDAO<T> {
    // Obtener mascotas por ID de usuario
    List<T> getByUsuarioId(Long usuarioId);
}

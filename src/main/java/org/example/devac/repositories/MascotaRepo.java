package org.example.devac.repositories;

import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MascotaRepo extends  JpaRepository<Mascota,Long>{
    Optional<Mascota> findById(long id);
    Optional<Mascota> findByNombre(String nombre);
    Optional<Mascota> findByEstado(EstadoMascota estado);
}


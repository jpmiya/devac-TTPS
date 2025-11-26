package org.example.devac.repositories;

import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface MascotaRepo extends  JpaRepository<Mascota,Long>{
    Optional<Mascota> findById(long id);
    List<Mascota> findAllByDueno(Long idUsuario); //chequear si anda asi o hay q implementar
    Optional<Mascota> findByNombre(String nombre);
    List<Mascota> findAllByEstado(EstadoMascota estado);
}


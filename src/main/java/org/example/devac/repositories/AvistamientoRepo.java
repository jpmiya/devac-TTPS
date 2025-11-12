package org.example.devac.repositories;

import org.example.devac.models.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AvistamientoRepo  extends  JpaRepository<Avistamiento,Long>{
    Optional<Avistamiento> findById(long id);
    Optional<Avistamiento> findByFecha(Date fecha);
}

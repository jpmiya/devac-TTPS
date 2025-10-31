package org.example.devac.repositories;

import org.example.devac.models.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvistamientoRepo  extends  JpaRepository<Avistamiento,Long>{
}

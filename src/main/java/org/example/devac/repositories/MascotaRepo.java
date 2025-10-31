package org.example.devac.repositories;

import org.example.devac.models.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepo extends  JpaRepository<Mascota,Long>{
}


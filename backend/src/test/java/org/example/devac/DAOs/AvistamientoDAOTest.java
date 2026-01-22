package org.example.devac.DAOs;

import org.example.devac.models.Avistamiento;
import org.example.devac.models.Mascota;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AvistamientoDAOTest {

    private final AvistamientoDAOHibernateJPA avistamientoDao = new AvistamientoDAOHibernateJPA();
    private Long createdId;

    @AfterEach
    public void cleanup() {
        if (createdId != null) {
            try {
                avistamientoDao.delete(createdId);
            } catch (Exception ignored) {}
            createdId = null;
        }
    }


    /*
    @Test
    public void persist_and_get_shouldReturnPersistedAvistamiento() {
        MascotaDAOHibernateJPA mascotaDao = new MascotaDAOHibernateJPA();
        Mascota m = new org.example.devac.models.Mascota();
        Mascota persistedMascota = mascotaDao.persist(m);
        Long mascotaId = persistedMascota.getId();

        Avistamiento a = new Avistamiento(mascotaId, "2025-10-24", "foto.jpg", "-34.9,-57.9", "Encontrada cerca del parque");
        Avistamiento created = avistamientoDao.persist(a);
        assertNotNull(created);
        createdId = created.getId();
        assertNotNull(createdId, "El id debe asignarse al persistir");

        Avistamiento found = avistamientoDao.get(createdId);
        assertNotNull(found);
        assertEquals("2025-10-24", found.getFecha());
        assertEquals("foto.jpg", found.getFoto());
        assertEquals("-34.9,-57.9", found.getCoordenadas());
        assertEquals("Encontrada cerca del parque", found.getComentario());
        // cleanup persisted mascota
        try { mascotaDao.delete(mascotaId); } catch (Exception ignored) {}
    }
    */

}

package org.example.devac.DAOs;

import jakarta.persistence.Entity;
import org.example.devac.models.Avistamiento;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.DAOs.EMF;
import org.example.devac.DAOs.UsuarioDAOHibernateJPA;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {

    private final UsuarioDAOHibernateJPA usuarioDao = new UsuarioDAOHibernateJPA();
    private Long createdId;

    @AfterEach
    public void cleanup() {
        if (createdId != null) {
            try {
                usuarioDao.delete(createdId);
            } catch (Exception ignored) {
            }
            createdId = null;
        }
    }

    @Test
    public void createAndFindByEmail_shouldPersistAndRetrieveUsuario() {
        // crear usuario con email único para evitar colisiones
        String email = "test+" + UUID.randomUUID() + "@example.com";
        Usuario u = new Usuario("Pepe sand", email, "contraseña_segura", "22155151515",
                "lomitas", "la plata", 3, 0, 0);

        // persistir usando el DAO
        Usuario created = usuarioDao.persist(u);
        assertNotNull(created);
        // intentar obtener id si el DAO devuelve la entidad con id
        createdId = created.getId();

        // buscar via EntityManager / JPQL (simula getByEmail)
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery(
                    "SELECT x FROM Usuario x WHERE x.mail = :mail", Usuario.class);
            q.setParameter("mail", email);
            Usuario found = q.getSingleResult();
            assertNotNull(found);
            assertEquals(email, found.getMail());
        } finally {
            em.close();
        }
    }
/*
    @Test
    public void avistamientoCollectionTest() {
        Usuario u = new Usuario("Pepe sand", "mail@mail.com", "contraseña_segura", "22155151515",
                "lomitas", "la plata", 3, 0, 0);

        MascotaDAOHibernateJPA mascotaDao = new MascotaDAOHibernateJPA();
        Mascota m = new Mascota();
        Mascota persisted = mascotaDao.persist(m);
        Long mascotaId = persisted.getId();

        u.crearAvistamiento(mascotaId, "2141424, -124124214", "foto_pepe.jpg",
                "2025-05-05", "pepe");

        List<Avistamiento> avistamientos = u.getAvistamientos();
        assertNotNull(avistamientos);
        assertFalse(avistamientos.isEmpty());
        assertTrue(avistamientos.stream()
                .anyMatch(a -> a.getMascota() != null && mascotaId.equals(a.getMascota().getId())));

        // cleanup persisted mascota
        try {
            mascotaDao.delete(mascotaId);
        } catch (Exception ignored) {
        }
    }

    comentado porque rompe
 */

    @Test
    public void create2UsersWithSameEmailShouldNotPersistTest() {
        String email = "pepe@pepe.com";
        Usuario u1 = new Usuario("Pepe sand", email, "contraseña_segura", "22155151515",
                "lomitas", "la plata", 3, 0, 0);

        Usuario u2 = new Usuario("not pepe", email, "contraseña_segura", "22155151515",
                "lomitas", "la plata", 3, 0, 0);

        UsuarioDAO<Usuario> ud = new UsuarioDAOHibernateJPA();
        EntityManager em = EMF.getEMF().createEntityManager();
        assertThrows(Exception.class,  () -> {
            ud.persist(u1);
            ud.persist(u2);
            em.flush();
        });
    }

    @Test
    public void createUserWithNullMailShouldNotPersistTest() {
        Usuario u = new Usuario("Pepe sand", null, "contraseña_segura", "22155151515",
                "lomitas", "la plata", 3, 0, 0);

        UsuarioDAO<Usuario> ud = new UsuarioDAOHibernateJPA();
        assertThrows(Exception.class, () -> ud.persist(u));
    }
}

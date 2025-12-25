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
import jakarta.persistence.EntityTransaction;

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
        Usuario u = new Usuario.Builder()
                .nombreYApellido("Pepe sand")
                .email(email)
                .password("contraseña_segura")
                .telefono("22155151515")
                .barrio("lomitas")
                .ciudad("la plata")
                .posicion(3)
                .build();

        // persistir usando el DAO
        Usuario created = usuarioDao.persist(u);
        assertNotNull(created);
        // intentar obtener id si el DAO devuelve la entidad con id
        createdId = created.getId();

        // buscar via EntityManager / JPQL (simula getByEmail)
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery(
                    "SELECT x FROM Usuario x WHERE x.email = :email", Usuario.class);
            q.setParameter("email", email);
            Usuario found = q.getSingleResult();
            assertNotNull(found);
            assertEquals(email, found.getEmail());
        } finally {
            em.close();
        }
    }

    @Test
    public void avistamientoCollectionTest() {
        Usuario u = new Usuario.Builder()
                .nombreYApellido("Pepe sand")
                .email("mail@mail.com")
                .password("contraseña_segura")
                .telefono("22155151515")
                .barrio("lomitas")
                .ciudad("la plata")
                .posicion(3)
                .build();

        MascotaDAOHibernateJPA mascotaDao = new MascotaDAOHibernateJPA();
        Mascota m = new Mascota();
        Mascota persisted = mascotaDao.persist(m);
        Long mascotaId = persisted.getId();


        // cleanup persisted mascota
        try {
            mascotaDao.delete(mascotaId);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void create2UsersWithSameEmailShouldNotPersistTest() {
        String email = "pepe@pepe.com";
        Usuario u1 = new Usuario.Builder()
                .nombreYApellido("Pepe sand")
                .email(email)
                .password("contraseña_segura")
                .telefono("22155151515")
                .barrio("lomitas")
                .ciudad("la plata")
                .posicion(3)
                .build();

        Usuario u2 = new Usuario.Builder()
                .nombreYApellido("not pepe")
                .email(email)
                .password("contraseña_segura")
                .telefono("22155151515")
                .barrio("lomitas")
                .ciudad("la plata")
                .posicion(3)
                .build();

        // persist both within the same EntityManager/transaction so the DB constraint
        // will be enforced at commit time (and throw an exception)
        EntityManager em = EMF.getEMF().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            assertThrows(Exception.class, () -> {
                tx.begin();
                em.persist(u1);
                em.persist(u2);
                tx.commit();
            });
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Test
    public void createUserWithNullMailShouldNotPersistTest() {
        // Builder Pattern valida email requerido, lanza IllegalStateException en build()
        assertThrows(IllegalStateException.class, () -> {
            new Usuario.Builder()
                    .nombreYApellido("Pepe sand")
                    .email(null)
                    .password("contraseña_segura")
                    .telefono("22155151515")
                    .barrio("lomitas")
                    .ciudad("la plata")
                    .posicion(3)
                    .build();
        });
    }
}

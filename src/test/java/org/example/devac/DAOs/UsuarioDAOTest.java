package org.example.devac.DAOs;

import org.example.devac.models.Usuario;
import org.example.devac.DAOs.EMF;
import org.example.devac.DAOs.UsuarioDAOHibernateJPA;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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
            } catch (Exception ignored) {}
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
}
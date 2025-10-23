package org.example.devac.DAOs;

import org.example.devac.models.Usuario;
import org.junit.jupiter.api.*;
import jakarta.persistence.*;

import static org.junit.jupiter.api.Assertions.*;

public class GenericDAOTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;

    private GenericDAOHibernateJPA<Usuario> genericDao;
    private Long createdId;

    @BeforeAll
    static void initEMF() {
        emf = Persistence.createEntityManagerFactory("unlp-test");
    }

    @AfterAll
    static void closeEMF() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();

        genericDao = new GenericDAOHibernateJPA<>(Usuario.class);
    }

    @AfterEach
    void tearDown() {
        if (tx != null && tx.isActive()) tx.rollback();
        if (em != null && em.isOpen()) em.close();
        createdId = null;
    }

    @Test
    void persist_and_findById() {
        Usuario u = new Usuario("Nicolás", "nico@mail.com", "contraseñasegura", "2251515",
                "los hornos", "la plata", 3, 0, 0);
        genericDao.persist(u);
        em.flush();
        createdId = u.getId();


        Usuario found = genericDao.get(createdId);
        assertNotNull(found);
        assertEquals("Nicolás", found.getNombreYApellido());

        tx.commit();
    }

    @Test
    void update_entity() {
        // Arrange
        Usuario u = new Usuario();
        u.setNombreYApellido("nicolas not beiserman");
        u.setMail("nico@mail.com");
        genericDao.persist(u);
        em.flush();
        Long id = u.getId();

        // Act
        u.setNombreYApellido("Nicolás Beiserman");
        genericDao.update(u);
        em.flush();

        // Assert
        Usuario updated = genericDao.get(id);
        assertEquals("Nicolás Beiserman", updated.getNombreYApellido());

        tx.commit();
    }

    @Test
    void delete_entity() {
        Usuario u = new Usuario();
        u.setNombreYApellido("B");
        u.setMail("b@mail.com");
        genericDao.persist(u);
        em.flush();
        Long id = u.getId();

        genericDao.delete(id);
        em.flush();

        Usuario gone = genericDao.get(id);
        assertNull(gone, "Después de delete, findById debe devolver null");

        tx.commit();
    }

    @Test
    void findAll_returns_items() {
        Usuario u1 = new Usuario();
        u1.setNombreYApellido("A");
        u1.setMail("a@mail.com");
        Usuario u2 = new Usuario();
        u2.setNombreYApellido("B");
        u2.setMail("b@mail.com");

        genericDao.persist(u1);
        genericDao.persist(u2);
        em.flush();

        assertTrue(genericDao.getAll("asc").size() >= 2);
        tx.commit();
    }
}

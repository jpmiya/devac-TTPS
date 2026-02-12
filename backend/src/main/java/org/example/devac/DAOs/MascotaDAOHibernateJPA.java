package org.example.devac.DAOs;

import jakarta.persistence.EntityManager;
import org.example.devac.models.Mascota;
import org.example.devac.models.EstadoMascota;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MascotaDAOHibernateJPA extends GenericDAOHibernateJPA<Mascota> implements MascotaDAO<Mascota> {

    public MascotaDAOHibernateJPA() {
        super(Mascota.class);
    }

    // ejemplo de método específico: buscar por nombre
    public List<Mascota> getByNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM " + getPersistentClass().getSimpleName() + " m WHERE m.nombre = :nombre", Mascota.class)
                    .setParameter("nombre", nombre)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mascota> getByUsuarioId(Long usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM " + getPersistentClass().getSimpleName() + " m WHERE m.dueno.id = :usuarioId", Mascota.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mascota> findAllLostWithDueno() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT m FROM Mascota m " +
                                    "JOIN FETCH m.dueno d " +
                                    "WHERE m.estado IN (:estadoPropio, :estadoAjeno)",
                            Mascota.class)
                    .setParameter("estadoPropio", EstadoMascota.PERDIDO_PROPIO)
                    .setParameter("estadoAjeno", EstadoMascota.PERDIDO_AJENO)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mascota> findAllAdoptedWithDueno() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT m FROM Mascota m " +
                                    "JOIN FETCH m.dueno d " +
                                    "WHERE m.estado = :estadoAdoptado",
                            Mascota.class)
                    .setParameter("estadoAdoptado", EstadoMascota.ADOPTADO)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

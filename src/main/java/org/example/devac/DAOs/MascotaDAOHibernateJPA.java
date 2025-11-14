package org.example.devac.DAOs;

import jakarta.persistence.EntityManager;
import org.example.devac.models.Mascota;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MascotaDAOHibernateJPA extends GenericDAOHibernateJPA<Mascota> implements MascotaDAO<Mascota> {

    public MascotaDAOHibernateJPA() {
        super(Mascota.class);
    }

    // ejemplo de método específico: buscar por nombre
    public List<Mascota> getByNombre(String nombre) {
        EntityManager em = EMF.getEMF().createEntityManager();
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
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            return em.createQuery("SELECT m FROM " + getPersistentClass().getSimpleName() + " m WHERE m.dueno.id = :usuarioId", Mascota.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

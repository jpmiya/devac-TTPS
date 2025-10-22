package org.example.devac.DAOs;

import jakarta.persistence.EntityManager;
import org.example.devac.models.Avistamiento;

import java.util.List;

public class AvistamientoDAOHibernateJPA extends GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO<Avistamiento> {

    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }

    public List<Avistamiento> getByUsuarioId(Long usuarioId) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            return em.createQuery("SELECT a FROM " + getPersistentClass().getSimpleName() + " a WHERE a.usuario.id = :uid", Avistamiento.class)
                    .setParameter("uid", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

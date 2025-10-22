package org.example.devac.DAOs;

import jakarta.persistence.EntityManager;
import org.example.devac.models.Usuario;

public class UsuarioDAOHibernateJPA extends GenericDAOHibernateJPA
        implements UsuarioDAO{


    public UsuarioDAOHibernateJPA(){
        super(Usuario.class);
    }


    public Usuario getByMail(String mail) {
        EntityManager em = EMF.getEMF().createEntityManager();
        Usuario usr;
        try {
            usr = (Usuario) em.createQuery("SELECT m FROM " +
                            this.getPersistentClass().getSimpleName() + " m WHERE m.email = :email")
                    .setParameter("email", mail).getSingleResult();
        } catch (Exception e) {
            usr = null;
        } finally {
            em.close();
        }
        return usr;
    }

}

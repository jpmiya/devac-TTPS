package org.example.devac.DAOs;

import jakarta.persistence.EntityManager;
import org.example.devac.models.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAOHibernateJPA extends GenericDAOHibernateJPA<Usuario>
    implements UsuarioDAO<Usuario>{


    public UsuarioDAOHibernateJPA(){
        super(Usuario.class);
    }


    public Usuario getByMail(String mail) {
        //get the entity Manager
        EntityManager em = EMF.getEMF().createEntityManager();
        Usuario usr;
        try {
            //we get the user from the database
            usr = em.createQuery("SELECT m FROM " +
                            this.getPersistentClass().getSimpleName() + " m WHERE m.email = :mail", Usuario.class)
                    .setParameter("mail", mail)
                    .getSingleResult();
        } catch (Exception e) {
            usr = null;
        } finally {
            em.close();
        }
        return usr;
    }

}

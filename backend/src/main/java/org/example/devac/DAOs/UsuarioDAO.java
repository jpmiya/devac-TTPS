package org.example.devac.DAOs;

public interface UsuarioDAO<T> extends GenericDAO<T> {
    public T getByMail(String mail);

}

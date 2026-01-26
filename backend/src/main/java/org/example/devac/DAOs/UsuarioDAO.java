package org.example.devac.DAOs;

import org.example.devac.models.Usuario;

public interface UsuarioDAO<T> extends GenericDAO<T> {
    public T getByMail(String mail);
    public Usuario getById(Long idUsuario);

}

package org.example.devac.services;

import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioDAO<Usuario> usuarioDAO;
    @Autowired
    MascotaService mascotaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(Usuario usuario) {
        String hashed = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashed);
        return usuarioDAO.persist(usuario);
    }

    @Override
    public Usuario editar(Long id,Usuario usuario) {
       return usuarioDAO.update(usuario);
    }

    @Override
    public boolean login(String email, String password) {
        // Buscar por email usando getByMail del DAO
        Usuario usuario = usuarioDAO.getByMail(email);
        
        if (usuario == null) {
            return false;
        }

        // Comparar la contraseña ingresada (en texto plano) con el hash guardado
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    public Usuario registrarMascota(Mascota mascota, Long idUsuario) {
       Usuario usuario = usuarioDAO.get(idUsuario);
       if (usuario == null) {
           throw new RuntimeException("Usuario no encontrado");
       }
       usuario.agregarMascota(mascota);
       return usuarioDAO.update(usuario);
    }

    public Usuario editarMascota(Mascota mascota, Long idUsuario) {
        Usuario usuario = usuarioDAO.get(idUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        mascota.setDueno(usuario);
        mascotaService.editar(mascota);
        return usuario;
    }

    public Usuario eliminarMascota(Mascota mascota,Long idUsuario) {
        Usuario usuario = usuarioDAO.get(idUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuario.eliminarMascota(mascota);
        mascotaService.eliminar(mascota);
        return usuarioDAO.update(usuario);
    }



}

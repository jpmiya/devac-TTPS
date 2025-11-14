package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioDAO<Usuario> usuarioDAO;
    
    @Autowired
    MascotaDAO<Mascota> mascotaDAO;
    
    @Autowired
    MascotaService mascotaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(Usuario usuario) {
        // Validar que no exista otro usuario con el mismo email
        Usuario existente = usuarioDAO.getByMail(usuario.getEmail());
        if (existente != null) {
            throw new BadRequestException("Ya existe un usuario registrado con el email: " + usuario.getEmail());
        }
        
        String hashed = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashed);
        return usuarioDAO.persist(usuario);
    }

    @Override
    public Usuario editar(Long id, Usuario usuario) {
        // Buscar el usuario existente
        Usuario existente = usuarioDAO.get(id);
        if (existente == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + id);
        }
        
        // Actualizar los campos (manteniendo el ID original)
        existente.setNombreYApellido(usuario.getNombreYApellido());
        existente.setEmail(usuario.getEmail());
        existente.setTelefono(usuario.getTelefono());
        existente.setBarrio(usuario.getBarrio());
        existente.setCiudad(usuario.getCiudad());
        existente.setPosicion(usuario.getPosicion());
        
        // Solo actualizar la contraseña si viene una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(usuario.getPassword());
            existente.setPassword(hashed);
        }
        
        return usuarioDAO.update(existente);
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
           throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
       }
       
       // Asignar el dueño a la mascota
       mascota.setDueno(usuario);
       
       // Persistir la mascota directamente con el DAO
       mascotaDAO.persist(mascota);
       
       // Retornar el usuario
       return usuario;
    }

    public Usuario editarMascota(Mascota mascota, Long idUsuario) {
        Usuario usuario = usuarioDAO.get(idUsuario);
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }
        mascota.setDueno(usuario);
        mascotaService.editar(mascota);
        return usuario;
    }

    public Usuario eliminarMascota(Mascota mascota, Long idUsuario) {
        Usuario usuario = usuarioDAO.get(idUsuario);
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        // Verificar que la mascota existe
        Mascota mascotaCompleta = mascotaDAO.get(mascota.getId());
        if (mascotaCompleta == null) {
            throw new BadRequestException("Mascota no encontrada con ID: " + mascota.getId());
        }
        
        // Verificar que la mascota pertenece al usuario
        if (mascotaCompleta.getDueno().getId() != idUsuario) {
            throw new BadRequestException("La mascota no pertenece a este usuario");
        }
        
        // Eliminar la mascota usando el ID (más seguro que pasar la entidad)
        mascotaDAO.delete(mascota.getId());
        
        return usuario;
    }

    @Override
    public List<Mascota> getMascotasDeUsuario(Long idUsuario) {
        Usuario usuario = usuarioDAO.get(idUsuario);
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        return mascotaDAO.getByUsuarioId(idUsuario);
    }

}

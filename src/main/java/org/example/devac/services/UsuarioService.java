package org.example.devac.services;

import org.example.devac.dto.UsuarioRegisterDTO;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UsuarioService {
    Usuario registrar(UsuarioRegisterDTO usuario);
    Usuario editar(Long id,Usuario usuario);
    boolean login(String email, String password);
    Mascota registrarMascota(Mascota mascota, Long idUsuario);
    Mascota editarMascota(Long idMascota, Long idUsuario);
    Usuario eliminarMascota(Long idMascota,Long idUsuario);
    List<Mascota> getMascotasDeUsuario(Long idUsuario);
}

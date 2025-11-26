package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.models.RolEnum;
import org.example.devac.models.Usuario;
import org.example.devac.repositories.MascotaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.devac.dto.UsuarioRegisterDTO;
import org.example.devac.repositories.UsuarioRepo;
import org.example.devac.services.UserEditerService;

import java.util.ArrayList;
import java.util.List;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    
    @Autowired
    MascotaService mascotaService;

    @Autowired
    UsuarioRepo usuarioRepository;

    @Autowired
    MascotaRepo mascotaRepository;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(UsuarioRegisterDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException(
                    "Ya existe un usuario registrado con el email: " + dto.getEmail()
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNombreYApellido(dto.getNombreYApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setBarrio(dto.getBarrio());
        usuario.setCiudad(dto.getCiudad());


        usuario.setPosicion(0);
        usuario.setPuntos(0);
        usuario.setRol(RolEnum.USUARIO);
        usuario.setCasosEnZona(0);
        usuario.setMedallas(new ArrayList<>());
        usuario.setAvistamientos(new ArrayList<>());
        usuario.setMascotas(new ArrayList<>());

        String hashed = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(hashed);

        return usuarioRepository.save(usuario);
    }



    //esta funcion esta mal corregir despues
    @Override
    public Usuario editar(Long id, Usuario usuario1) {
        // despues implemento  try{
        Usuario usuario = usuarioRepository.findById(usuario1.getId()).get();
        Usuario existente = usuarioRepository.findById(usuario.getId()).get();
        // } catch Exception ex
        UserEditerService userEditerService = new UserEditerService();
        // Buscar el usuario existente
        if (existente == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + id);
        }


        // Actualizar los campos (manteniendo el ID original)
        userEditerService.edit(usuario.getNombreYApellido(),usuario.getEmail(),usuario.getTelefono(),usuario.getBarrio(),usuario.getCiudad(),usuario.getPosicion(),existente);
        
        // Solo actualizar la contraseña si viene una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(usuario.getPassword());
            existente.setPassword(hashed);
        }
        
        return usuarioRepository.save(usuario); // PROBAR
    }

    @Override
    public boolean login(String email, String password) {
        // Buscar por email usando getByMail del DAO
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado con el email: " + email));

        // Comparar la contraseña ingresada (en texto plano) con el hash guardado
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    public Mascota registrarMascota(Mascota mascota, Long idUsuario) {
       Usuario usuario = usuarioRepository.findById(idUsuario).get();
       if (usuario == null) {
           throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
       }
       
       // Asignar el dueño a la mascota
       mascota.setDueno(usuario);
       
       mascotaRepository.save(mascota);
       
       // Retornar el usuario
       return mascota;
    }

    public Mascota editarMascota(Long idMascota, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).get();
        Mascota mascota = mascotaRepository.findById(idMascota).get();
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }
        mascotaService.editar(mascota);
        return mascota;
    }

    public Usuario eliminarMascota(Long idMascota, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).get();
        Mascota mascota = mascotaRepository.findById(idMascota).get();
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }
        if (mascota == null) {
            throw new BadRequestException("Mascota no encontrada con ID: " + mascota.getId());
        }
        
        // Verificar que la mascota pertenece al usuario
        if (mascota.getDueno().getId() != idUsuario) {
            throw new BadRequestException("La mascota no pertenece a este usuario");
        }
        // Eliminar la mascota usando el ID (más seguro que pasar la entidad)
        mascotaRepository.delete(mascota);
        
        return usuario;
    }

    @Override
    public List<Mascota> getMascotasDeUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).get();
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        return mascotaRepository.findAllByDueno(idUsuario);
    }

}

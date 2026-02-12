package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.UsuarioRegisterDTO;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.models.RolEnum;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioDAO<Usuario> usuarioDAO;

    @Autowired
    private MascotaDAO<Mascota> mascotaDAO;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UserEditerService userEditerService;

    @Autowired
    private GeorefService georefService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(UsuarioRegisterDTO usuarioDTO) {
        // Validar que no exista otro usuario con el mismo email
        Usuario existente = usuarioDAO.getByMail(usuarioDTO.getEmail());
        if (existente != null) {
            throw new BadRequestException("Ya existe un usuario registrado con el email: " + usuarioDTO.getEmail());
        }

        // Validar contraseña
        String rawPassword = usuarioDTO.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new BadRequestException("La contraseña no puede estar vacía");
        }

        // Resolver barrio y ciudad desde coordenadas usando Georef
        String barrio = usuarioDTO.getBarrio();
        String ciudad = usuarioDTO.getCiudad();
        if (usuarioDTO.getCoordenadas() != null && !usuarioDTO.getCoordenadas().isBlank()) {
            GeorefService.UbicacionResult ubicacion = georefService.resolverUbicacion(usuarioDTO.getCoordenadas());
            if (ubicacion != null) {
                barrio = ubicacion.getBarrio();
                ciudad = ubicacion.getCiudad();
            }
        }

        Usuario usuario = new Usuario.Builder()
                .nombreYApellido(usuarioDTO.getNombreYApellido())
                .email(usuarioDTO.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .telefono(usuarioDTO.getTelefono())
                .barrio(barrio)
                .ciudad(ciudad)
                .posicion(0)
                .puntos(0)
                .casosEnZona(0)
                .rol(RolEnum.USUARIO)
                .build();

        return usuarioDAO.persist(usuario);
    }

    public Usuario findById(Long idUsuario) {
        return this.usuarioDAO.getById(idUsuario);
    }

    @Override
    public Usuario login(String email, String password) {
        Usuario usr = usuarioDAO.getByMail(email);
        if (usr == null) {
            return null;
        }
        if (passwordEncoder.matches(password, usr.getPassword())){
            return usr;
        } else {
            return null;
        }
    }

    @Override
    public Usuario editar(Long id, Usuario usuario) {
        return userEditerService.edit(id, usuario);
    }

    @Override
    public Mascota editarMascota(Long idMascota, Long usuarioId) {
        // Validar que el usuario existe y es dueño de la mascota
        Mascota mascotaExistente = validarPropiedadMascota(idMascota, usuarioId);

        // delego la edicion a mascotaservice
        return mascotaService.editar(mascotaExistente);
    }

    @Override
    public Usuario eliminarMascota(Long idMascota, Long idUsuario) {
        // Validar que el usuario existe y es dueño de la mascota
        validarPropiedadMascota(idMascota, idUsuario);
        mascotaDAO.delete(idMascota);

        return usuarioDAO.get(idUsuario);
    }



    private Mascota validarPropiedadMascota(Long idMascota, Long idUsuario) {
        Usuario usuario = usuarioDAO.get(idUsuario);
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado con ID: " + idUsuario);
        }

        Mascota mascota = mascotaDAO.get(idMascota);
        if (mascota == null) {
            throw new BadRequestException("Mascota no encontrada con ID: " + idMascota);
        }

        if (!mascota.isDueno(idUsuario)) {
            throw new BadRequestException("La mascota no pertenece a este usuario");
        }

        return mascota;
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
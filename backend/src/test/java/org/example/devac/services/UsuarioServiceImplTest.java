package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.UsuarioRegisterDTO;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.models.RolEnum;
import org.example.devac.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests para UsuarioServiceImpl con Mockito
 * Valida: registrar, login, editar, mascota operations
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioDAO<Usuario> usuarioDAO;

    @Mock
    private MascotaDAO<Mascota> mascotaDAO;

    @Mock
    private MascotaService mascotaService;

    @Mock
    private UserEditerService userEditerService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioRegisterDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioRegisterDTO();
        usuarioDTO.setNombreYApellido("Juan Pérez");
        usuarioDTO.setEmail("juan@example.com");
        usuarioDTO.setPassword("password123");
        usuarioDTO.setTelefono("123456789");
        usuarioDTO.setBarrio("Centro");
        usuarioDTO.setCiudad("La Plata");

        usuario = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("hashedPassword")
                .telefono("123456789")
                .barrio("Centro")
                .ciudad("La Plata")
                .build();
    }

    @Test
    void registrar_withValidDTO_shouldPersistUsuario() {
        when(usuarioDAO.getByMail(anyString())).thenReturn(null);
        when(usuarioDAO.persist(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.registrar(usuarioDTO);

        assertNotNull(result);
        verify(usuarioDAO).getByMail("juan@example.com");
        verify(usuarioDAO).persist(any(Usuario.class));
    }

    @Test
    void registrar_withExistingEmail_shouldThrowBadRequestException() {
        when(usuarioDAO.getByMail(anyString())).thenReturn(usuario);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.registrar(usuarioDTO)
        );

        assertTrue(exception.getMessage().contains("Ya existe un usuario"));
        assertTrue(exception.getMessage().contains("juan@example.com"));
        verify(usuarioDAO).getByMail("juan@example.com");
        verify(usuarioDAO, never()).persist(any());
    }

    @Test
    void registrar_withNullPassword_shouldThrowBadRequestException() {
        usuarioDTO.setPassword(null);
        when(usuarioDAO.getByMail(anyString())).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.registrar(usuarioDTO)
        );

        assertTrue(exception.getMessage().contains("contraseña"));
        verify(usuarioDAO, never()).persist(any());
    }

    @Test
    void registrar_withEmptyPassword_shouldThrowBadRequestException() {
        usuarioDTO.setPassword("");
        when(usuarioDAO.getByMail(anyString())).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.registrar(usuarioDTO)
        );

        assertTrue(exception.getMessage().contains("contraseña"));
        verify(usuarioDAO, never()).persist(any());
    }

    @Test
    void registrar_shouldSetDefaultValues() {
        when(usuarioDAO.getByMail(anyString())).thenReturn(null);
        when(usuarioDAO.persist(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            assertEquals(0, u.getPosicion());
            assertEquals(0, u.getPuntos());
            assertEquals(0, u.getCasosEnZona());
            assertEquals(RolEnum.USUARIO, u.getRol());
            return u;
        });

        usuarioService.registrar(usuarioDTO);

        verify(usuarioDAO).persist(any(Usuario.class));
    }

    @Test
    void registrar_shouldEncodePassword() {
        when(usuarioDAO.getByMail(anyString())).thenReturn(null);
        when(usuarioDAO.persist(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            assertNotEquals("password123", u.getPassword()); // Password debe estar hasheado
            assertTrue(u.getPassword().startsWith("$2a$")); // BCrypt format
            return u;
        });

        usuarioService.registrar(usuarioDTO);

        verify(usuarioDAO).persist(any(Usuario.class));
    }

    @Test
    void login_withValidCredentials_shouldReturnTrue() {
        String rawPassword = "password123";
        String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        Usuario usuarioConHash = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password(hashedPassword)
                .build();

        when(usuarioDAO.getByMail("juan@example.com")).thenReturn(usuarioConHash);

        Usuario result = usuarioService.login("juan@example.com", rawPassword);

        assertEquals(usuarioConHash, result);
        verify(usuarioDAO).getByMail("juan@example.com");
    }

    @Test
    void login_withInvalidPassword_shouldReturnNull() {
        String hashedPassword = new BCryptPasswordEncoder().encode("password123");
        Usuario usuarioConHash = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password(hashedPassword)
                .build();

        when(usuarioDAO.getByMail("juan@example.com")).thenReturn(usuarioConHash);

        Usuario result = usuarioService.login("juan@example.com", "wrongPassword");

        assertNull(result);
    }

    @Test
    void login_withNonExistentUser_shouldReturnNull() {
        when(usuarioDAO.getByMail("nonexistent@example.com")).thenReturn(null);

        Usuario result = usuarioService.login("nonexistent@example.com", "password123");

        assertNull(result);
    }

    @Test
    void editar_shouldDelegateToUserEditerService() {
        Usuario cambios = new Usuario();
        Usuario editado = new Usuario.Builder()
                .nombreYApellido("Juan Pérez Editado")
                .email("juan@example.com")
                .password("hashedPassword")
                .build();

        when(userEditerService.edit(1L, cambios)).thenReturn(editado);

        Usuario result = usuarioService.editar(1L, cambios);

        assertEquals("Juan Pérez Editado", result.getNombreYApellido());
        verify(userEditerService).edit(1L, cambios);
    }

    @Test
    void registrarMascota_withValidUsuario_shouldPersistMascota() {
        Mascota mascota = new Mascota.Builder()
                .dueno(usuario)
                .nombre("Firulais")
                .tipo("Perro")
                .build();

        when(usuarioDAO.get(1L)).thenReturn(usuario);
        when(mascotaDAO.persist(mascota)).thenReturn(mascota);

        Mascota result = usuarioService.registrarMascota(mascota, 1L);

        assertNotNull(result);
        assertEquals(usuario, result.getDueno());
        verify(usuarioDAO).get(1L);
        verify(mascotaDAO).persist(mascota);
    }

    @Test
    void registrarMascota_withNonExistentUsuario_shouldThrowBadRequestException() {
        Mascota mascota = new Mascota.Builder()
                .dueno(usuario)
                .nombre("Firulais")
                .tipo("Perro")
                .build();

        when(usuarioDAO.get(999L)).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.registrarMascota(mascota, 999L)
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioDAO).get(999L);
        verify(mascotaDAO, never()).persist(any());
    }

    @Test
    void editarMascota_withValidOwnership_shouldDelegateToMascotaService() {
        usuario.setId(1L);
        Mascota mascota = new Mascota.Builder()
                .dueno(usuario)
                .nombre("Firulais")
                .tipo("Perro")
                .build();
        mascota.setId(10L);

        when(usuarioDAO.get(1L)).thenReturn(usuario);
        when(mascotaDAO.get(10L)).thenReturn(mascota);
        when(mascotaService.editar(mascota)).thenReturn(mascota);

        Mascota result = usuarioService.editarMascota(10L, 1L);

        assertNotNull(result);
        verify(mascotaService).editar(mascota);
    }

    @Test
    void editarMascota_withInvalidOwnership_shouldThrowBadRequestException() {
        usuario.setId(1L);
        Usuario otroUsuario = new Usuario.Builder()
                .nombreYApellido("Otro Usuario")
                .email("otro@example.com")
                .password("password")
                .build();
        otroUsuario.setId(2L);

        Mascota mascotaDeOtro = new Mascota.Builder()
                .dueno(otroUsuario)
                .nombre("Firulais")
                .tipo("Perro")
                .build();
        mascotaDeOtro.setId(10L);

        when(usuarioDAO.get(1L)).thenReturn(usuario);
        when(mascotaDAO.get(10L)).thenReturn(mascotaDeOtro);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.editarMascota(10L, 1L)
        );

        assertTrue(exception.getMessage().contains("no pertenece"));
        verify(mascotaService, never()).editar(any());
    }

    @Test
    void eliminarMascota_withValidOwnership_shouldCallMascotaDAODelete() {
        usuario.setId(10L); // Match with stub below
        Mascota mascota = new Mascota.Builder()
                .dueno(usuario)
                .nombre("Firulais")
                .tipo("Perro")
                .build();
        mascota.setId(10L);

        when(usuarioDAO.get(10L)).thenReturn(usuario); // Changed from 1L to 10L
        when(mascotaDAO.get(10L)).thenReturn(mascota);
        doNothing().when(mascotaDAO).delete(10L);

        assertDoesNotThrow(() -> usuarioService.eliminarMascota(10L, 10L));

        verify(mascotaDAO).delete(10L);
    }

    @Test
    void eliminarMascota_withInvalidOwnership_shouldThrowBadRequestException() {
        usuario.setId(2L); // Changed to 2L for invalid ownership scenario
        Usuario otroUsuario = new Usuario.Builder()
                .nombreYApellido("Otro Usuario")
                .email("otro@example.com")
                .password("password")
                .build();
        otroUsuario.setId(1L);

        Mascota mascotaDeOtro = new Mascota.Builder()
                .dueno(otroUsuario) // Owned by usuario with id=1
                .nombre("Firulais")
                .tipo("Perro")
                .build();
        mascotaDeOtro.setId(10L);

        when(usuarioDAO.get(2L)).thenReturn(usuario); // Current user trying to delete
        when(mascotaDAO.get(10L)).thenReturn(mascotaDeOtro);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.eliminarMascota(10L, 2L)
        );

        assertTrue(exception.getMessage().contains("no pertenece"));
        verify(mascotaService, never()).eliminar(any());
    }

    @Test
    void eliminarMascota_withNonExistentMascota_shouldThrowBadRequestException() {
        usuario.setId(10L); // Match with stub below
        when(usuarioDAO.get(10L)).thenReturn(usuario);
        when(mascotaDAO.get(999L)).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> usuarioService.eliminarMascota(999L, 10L)
        );

        assertTrue(exception.getMessage().contains("Mascota no encontrada"));
    }
}

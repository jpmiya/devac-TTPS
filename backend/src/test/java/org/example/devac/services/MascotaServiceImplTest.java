package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.MascotaRequest;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests para MascotaServiceImpl con Mockito
 * Valida: registrar con Builder, editar, eliminar, findAllLost
 */
@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private UsuarioDAO<Usuario> usuarioDAO;

    @Mock
    private MascotaDAO<Mascota> mascotaDAO;

    @Mock
    private MascotaEditerService mascotaEditerService;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Usuario dueno;
    private MascotaRequest mascotaRequest;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        dueno = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .build();
        dueno.setId(1L);

        mascotaRequest = new MascotaRequest();
        mascotaRequest.setDuenoId(1L);
        mascotaRequest.setNombre("Firulais");
        mascotaRequest.setTipo("Perro");
        mascotaRequest.setTamaño("Grande");
        mascotaRequest.setColor("Marrón");
        mascotaRequest.setRaza("Labrador");
        mascotaRequest.setDescripcion("Perro muy amigable");

        mascota = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .tamaño("Grande")
                .color("Marrón")
                .raza("Labrador")
                .descripcion("Perro muy amigable")
                .build();
        mascota.setId(10L);
    }

    @Test
    void registrar_withValidRequest_shouldPersistMascota() {
        when(usuarioDAO.get(1L)).thenReturn(dueno);
        when(mascotaDAO.persist(any(Mascota.class))).thenReturn(mascota);

        Mascota result = mascotaService.registrar(mascotaRequest);

        assertNotNull(result);
        assertEquals("Firulais", result.getNombre());
        assertEquals("Perro", result.getTipo());
        verify(usuarioDAO).get(1L);
        verify(mascotaDAO).persist(any(Mascota.class));
    }

    @Test
    void registrar_withNonExistentDueno_shouldThrowBadRequestException() {
        when(usuarioDAO.get(999L)).thenReturn(null);
        mascotaRequest.setDuenoId(999L);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> mascotaService.registrar(mascotaRequest)
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioDAO).get(999L);
        verify(mascotaDAO, never()).persist(any());
    }

    @Test
    void registrar_withoutEstado_shouldSetDefaultPerdidoPropio() {
        when(usuarioDAO.get(1L)).thenReturn(dueno);
        when(mascotaDAO.persist(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota m = invocation.getArgument(0);
            assertEquals(EstadoMascota.PERDIDO_PROPIO, m.getEstado());
            return m;
        });

        mascotaService.registrar(mascotaRequest);

        verify(mascotaDAO).persist(any(Mascota.class));
    }

    @Test
    void registrar_withCustomEstado_shouldUseProvidedEstado() {
        mascotaRequest.setEstado(EstadoMascota.PERDIDO_AJENO);
        when(usuarioDAO.get(1L)).thenReturn(dueno);
        when(mascotaDAO.persist(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota m = invocation.getArgument(0);
            assertEquals(EstadoMascota.PERDIDO_AJENO, m.getEstado());
            return m;
        });

        mascotaService.registrar(mascotaRequest);

        verify(mascotaDAO).persist(any(Mascota.class));
    }

    @Test
    void registrar_shouldSetAllFieldsFromRequest() {
        LocalDate fecha = LocalDate.of(2024, 12, 1);
        mascotaRequest.setFechaDePerdida(fecha);
        mascotaRequest.setCoordenadas("-34.9214,-57.9544");

        when(usuarioDAO.get(1L)).thenReturn(dueno);
        when(mascotaDAO.persist(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota m = invocation.getArgument(0);
            assertEquals(dueno, m.getDueno());
            assertEquals("Firulais", m.getNombre());
            assertEquals("Grande", m.getTamaño());
            assertEquals("Marrón", m.getColor());
            assertEquals(fecha, m.getFechaDePerdida());
            assertEquals("-34.9214,-57.9544", m.getCoordenadas());
            assertEquals("Perro muy amigable", m.getDescripcion());
            assertEquals("Perro", m.getTipo());
            assertEquals("Labrador", m.getRaza());
            return m;
        });

        mascotaService.registrar(mascotaRequest);

        verify(mascotaDAO).persist(any(Mascota.class));
    }

    @Test
    void editar_shouldDelegateToMascotaEditerService() {
        Mascota cambios = new Mascota();
        cambios.setId(10L);
        cambios.setNombre("Firulais Editado");

        Mascota editado = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais Editado")
                .tipo("Perro")
                .build();

        when(mascotaEditerService.edit(10L, cambios)).thenReturn(editado);

        Mascota result = mascotaService.editar(cambios);

        assertEquals("Firulais Editado", result.getNombre());
        verify(mascotaEditerService).edit(10L, cambios);
    }

    @Test
    void eliminar_shouldCallDAODeleteWithId() {
        mascota.setId(10L);
        doNothing().when(mascotaDAO).delete(10L);

        assertDoesNotThrow(() -> mascotaService.eliminar(mascota));

        verify(mascotaDAO).delete(10L);
    }

    @Test
    void findAllLost_shouldReturnOnlyLostMascotas() {
        Mascota perdidoPropio = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .estado(EstadoMascota.PERDIDO_PROPIO)
                .build();

        Mascota perdidoAjeno = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Michi")
                .tipo("Gato")
                .estado(EstadoMascota.PERDIDO_AJENO)
                .build();

        Mascota recuperado = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Bobby")
                .tipo("Perro")
                .estado(EstadoMascota.RECUPERADO)
                .build();

        List<Mascota> todas = Arrays.asList(perdidoPropio, perdidoAjeno, recuperado);
        when(mascotaDAO.getAll("id")).thenReturn(todas);

        List<Mascota> result = mascotaService.findAllLost();

        assertEquals(2, result.size());
        assertTrue(result.contains(perdidoPropio));
        assertTrue(result.contains(perdidoAjeno));
        assertFalse(result.contains(recuperado));
        verify(mascotaDAO).getAll("id");
    }

    @Test
    void findAllLost_withNoLostMascotas_shouldReturnEmptyList() {
        Mascota recuperado1 = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Bobby")
                .tipo("Perro")
                .estado(EstadoMascota.RECUPERADO)
                .build();

        Mascota recuperado2 = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Michi")
                .tipo("Gato")
                .estado(EstadoMascota.RECUPERADO)
                .build();

        List<Mascota> todas = Arrays.asList(recuperado1, recuperado2);
        when(mascotaDAO.getAll("id")).thenReturn(todas);

        List<Mascota> result = mascotaService.findAllLost();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllLost_withEmptyDatabase_shouldReturnEmptyList() {
        when(mascotaDAO.getAll("id")).thenReturn(Arrays.asList());

        List<Mascota> result = mascotaService.findAllLost();

        assertTrue(result.isEmpty());
    }
}

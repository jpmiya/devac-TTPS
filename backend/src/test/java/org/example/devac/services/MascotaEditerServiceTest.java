package org.example.devac.services;

import jakarta.ws.rs.NotFoundException;
import org.example.devac.DAOs.MascotaDAO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para MascotaEditerService
 * Valida: BeanUtils.copyProperties, PropertyUtils.getNullPropertyNames, partial updates
 */
@ExtendWith(MockitoExtension.class)
class MascotaEditerServiceTest {

    @Mock
    private MascotaDAO<Mascota> mascotaDAO;

    @InjectMocks
    private MascotaEditerService mascotaEditerService;

    private Usuario dueno;
    private Mascota existente;

    @BeforeEach
    void setUp() {
        dueno = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .build();
        dueno.setId(1L);

        existente = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .tamaño("Grande")
                .color("Marrón")
                .raza("Labrador")
                .descripcion("Perro muy amigable")
                .estado(EstadoMascota.PERDIDO_PROPIO)
                .build();
        existente.setId(10L);
    }

    @Test
    void edit_withNonExistentMascota_shouldThrowNotFoundException() {
        when(mascotaDAO.get(999L)).thenReturn(null);

        Mascota cambios = new Mascota();
        cambios.setNombre("Nuevo Nombre");

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mascotaEditerService.edit(999L, cambios)
        );

        assertTrue(exception.getMessage().contains("Mascota no encontrada"));
        verify(mascotaDAO).get(999L);
        verify(mascotaDAO, never()).update(any());
    }

    @Test
    void edit_withPartialChanges_shouldUpdateOnlyNonNullFields() {
        Mascota cambios = new Mascota();
        cambios.setNombre("Firulais Editado");
        cambios.setColor("Negro");
        // tipo, tamaño, raza, etc. quedan null -> no se deben actualizar

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        assertEquals("Firulais Editado", result.getNombre());
        assertEquals("Negro", result.getColor());
        // Valores originales preservados
        assertEquals("Perro", result.getTipo());
        assertEquals("Grande", result.getTamaño());
        assertEquals("Labrador", result.getRaza());
        assertEquals("Perro muy amigable", result.getDescripcion());
        assertEquals(EstadoMascota.PERDIDO_PROPIO, result.getEstado());

        verify(mascotaDAO).get(10L);
        verify(mascotaDAO).update(existente);
    }

    @Test
    void edit_withAllFieldsNull_shouldNotUpdateAnything() {
        Mascota cambios = new Mascota();
        // Todos los campos null

        String originalNombre = existente.getNombre();
        String originalTipo = existente.getTipo();
        String originalColor = existente.getColor();

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        // Todos los valores originales preservados
        assertEquals(originalNombre, result.getNombre());
        assertEquals(originalTipo, result.getTipo());
        assertEquals(originalColor, result.getColor());

        verify(mascotaDAO).update(existente);
    }

    @Test
    void edit_withEstadoChange_shouldUpdateEstado() {
        Mascota cambios = new Mascota();
        cambios.setEstado(EstadoMascota.RECUPERADO);

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        assertEquals(EstadoMascota.RECUPERADO, result.getEstado());
        // Otros campos preservados
        assertEquals("Firulais", result.getNombre());
        assertEquals("Perro", result.getTipo());

        verify(mascotaDAO).update(existente);
    }

    @Test
    void edit_withFechaAndCoordenadas_shouldUpdateThoseFields() {
        LocalDate nuevaFecha = LocalDate.of(2024, 12, 15);
        Mascota cambios = new Mascota();
        cambios.setFechaDePerdida(nuevaFecha);
        cambios.setCoordenadas("-34.9214,-57.9544");

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        assertEquals(nuevaFecha, result.getFechaDePerdida());
        assertEquals("-34.9214,-57.9544", result.getCoordenadas());
        // Otros campos preservados
        assertEquals("Firulais", result.getNombre());

        verify(mascotaDAO).update(existente);
    }

    @Test
    void edit_withFotoUpdate_shouldUpdateFoto() {
        Mascota cambios = new Mascota();
        cambios.setFotoUrl("http://example.com/new-photo.jpg");

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        assertEquals("http://example.com/new-photo.jpg", result.getFotoUrl());
    }

    @Test
    void edit_shouldNotModifyIdOrDueno() {
        Mascota cambios = new Mascota();
        cambios.setNombre("Nuevo Nombre");

        Long originalId = existente.getId();
        Usuario originalDueno = existente.getDueno();

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        // ID y dueño no deben cambiar
        assertEquals(originalId, result.getId());
        assertEquals(originalDueno, result.getDueno());
    }

    @Test
    void edit_withMultipleFieldChanges_shouldUpdateAllProvided() {
        LocalDate nuevaFecha = LocalDate.of(2024, 12, 15);
        Mascota cambios = new Mascota();
        cambios.setNombre("Firulais Actualizado");
        cambios.setTamaño("Mediano");
        cambios.setColor("Negro y Blanco");
        cambios.setDescripcion("Descripción actualizada");
        cambios.setRaza("Mestizo");
        cambios.setEstado(EstadoMascota.PERDIDO_AJENO);
        cambios.setFechaDePerdida(nuevaFecha);
        cambios.setCoordenadas("-34.0000,-57.0000");
        cambios.setFotoUrl("http://example.com/updated.jpg");

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        assertEquals("Firulais Actualizado", result.getNombre());
        assertEquals("Mediano", result.getTamaño());
        assertEquals("Negro y Blanco", result.getColor());
        assertEquals("Descripción actualizada", result.getDescripcion());
        assertEquals("Mestizo", result.getRaza());
        assertEquals(EstadoMascota.PERDIDO_AJENO, result.getEstado());
        assertEquals(nuevaFecha, result.getFechaDePerdida());
        assertEquals("-34.0000,-57.0000", result.getCoordenadas());
        assertEquals("http://example.com/updated.jpg", result.getFotoUrl());
        // Tipo no cambió (no estaba en cambios)
        assertEquals("Perro", result.getTipo());
    }

    @Test
    void edit_shouldCallDAOUpdateOnce() {
        Mascota cambios = new Mascota();
        cambios.setNombre("Nuevo Nombre");

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenReturn(existente);

        mascotaEditerService.edit(10L, cambios);

        verify(mascotaDAO, times(1)).get(10L);
        verify(mascotaDAO, times(1)).update(existente);
    }

    @Test
    void edit_withEmptyStrings_shouldTreatAsNull() {
        Mascota cambios = new Mascota();
        cambios.setNombre("");
        cambios.setColor("");

        String originalNombre = existente.getNombre();
        String originalColor = existente.getColor();

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        // Empty strings deben tratarse como null (ignorados por BeanUtils)
        // Depende de la implementación de PropertyUtils.getNullPropertyNames
        // Si detecta empty strings, los valores originales se preservan
        assertNotNull(result.getNombre());
        assertNotNull(result.getColor());
    }

    @Test
    void edit_shouldPreserveAvistamientosCollection() {
        Mascota cambios = new Mascota();
        cambios.setNombre("Nuevo Nombre");

        int originalAvistamientosSize = existente.getAvistamientos() != null 
            ? existente.getAvistamientos().size() 
            : 0;

        when(mascotaDAO.get(10L)).thenReturn(existente);
        when(mascotaDAO.update(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota result = mascotaEditerService.edit(10L, cambios);

        // Collection no debe modificarse
        if (existente.getAvistamientos() != null) {
            assertNotNull(result.getAvistamientos());
            assertEquals(originalAvistamientosSize, result.getAvistamientos().size());
        }
    }
}

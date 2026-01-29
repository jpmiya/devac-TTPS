package org.example.devac.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Mascota.Builder Pattern
 * Valida: construcción exitosa, valores por defecto, validaciones fail-fast, sobrecarga de métodos
 */
class MascotaBuilderTest {

    private Usuario dueno;

    @BeforeEach
    void setUp() {
        // Crear un dueño válido para los tests
        dueno = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .build();
    }

    @Test
    void build_withAllRequiredFields_shouldCreateMascota() {
        Mascota mascota = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .build();

        assertNotNull(mascota);
        assertEquals(dueno, mascota.getDueno());
        assertEquals("Firulais", mascota.getNombre());
        assertEquals("Perro", mascota.getTipo());
    }

    @Test
    void build_withDefaultEstado_shouldSetPerdidoPropio() {
        Mascota mascota = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .build();

        assertEquals(EstadoMascota.PERDIDO_PROPIO, mascota.getEstado());
    }

    @Test
    void build_withCustomEstado_shouldOverrideDefault() {
        Mascota mascota = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .estado(EstadoMascota.RECUPERADO)
                .build();

        assertEquals(EstadoMascota.RECUPERADO, mascota.getEstado());
    }

    @Test
    void build_withAllOptionalFields_shouldSetAllProperties() {
        LocalDate fecha = LocalDate.of(2024, 12, 1);

        Mascota mascota = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .tamaño("Grande")
                .color("Marrón")
                .fechaDePerdida(fecha)
                .fotoUrl("http://example.com/photo.jpg")
                .coordenadas("-34.9214,-57.9544")
                .descripcion("Perro muy amigable")
                .raza("Labrador")
                .estado(EstadoMascota.PERDIDO_AJENO)
                .build();

        assertEquals("Grande", mascota.getTamaño());
        assertEquals("Marrón", mascota.getColor());
        assertEquals(fecha, mascota.getFechaDePerdida());
        assertEquals("http://example.com/photo.jpg", mascota.getFotoUrl());
        assertEquals("-34.9214,-57.9544", mascota.getCoordenadas());
        assertEquals("Perro muy amigable", mascota.getDescripcion());
        assertEquals("Labrador", mascota.getRaza());
        assertEquals(EstadoMascota.PERDIDO_AJENO, mascota.getEstado());
    }

    @Test
    void build_withoutDueno_shouldThrowIllegalStateException() {
        Mascota.Builder builder = new Mascota.Builder()
                .nombre("Firulais")
                .tipo("Perro");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Dueño"));
        assertTrue(exception.getMessage().contains("requerido"));
    }

    @Test
    void build_withoutNombre_shouldThrowIllegalStateException() {
        Mascota.Builder builder = new Mascota.Builder()
                .dueno(dueno)
                .tipo("Perro");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Nombre"));
    }

    @Test
    void build_withEmptyNombre_shouldThrowIllegalStateException() {
        Mascota.Builder builder = new Mascota.Builder()
                .dueno(dueno)
                .nombre("   ")
                .tipo("Perro");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Nombre"));
    }

    @Test
    void build_withoutTipo_shouldThrowIllegalStateException() {
        Mascota.Builder builder = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Tipo"));
    }

    @Test
    void build_withEmptyTipo_shouldThrowIllegalStateException() {
        Mascota.Builder builder = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Tipo"));
    }

    @Test
    void builder_shouldSupportFluentInterface() {
        Mascota.Builder builder = new Mascota.Builder();

        assertSame(builder, builder.dueno(dueno));
        assertSame(builder, builder.nombre("Firulais"));
        assertSame(builder, builder.tipo("Perro"));
        assertSame(builder, builder.tamaño("Grande"));
        assertSame(builder, builder.color("Marrón"));
        assertSame(builder, builder.fechaDePerdida(LocalDate.now()));
        assertSame(builder, builder.fotoUrl("url"));
        assertSame(builder, builder.coordenadas("coords"));
        assertSame(builder, builder.descripcion("desc"));
        assertSame(builder, builder.raza("Labrador"));
        assertSame(builder, builder.estado(EstadoMascota.RECUPERADO));
    }

    @Test
    void build_multipleInstances_shouldBeIndependent() {
        Mascota mascota1 = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais")
                .tipo("Perro")
                .build();

        Mascota mascota2 = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Michi")
                .tipo("Gato")
                .build();

        assertNotEquals(mascota1.getNombre(), mascota2.getNombre());
        assertNotEquals(mascota1.getTipo(), mascota2.getTipo());
    }

    @Test
    void validateRequired_overloading_shouldWorkForBothObjectAndString() {
        // Test validación de Object (dueno = null)
        Mascota.Builder builderNullDueno = new Mascota.Builder()
                .nombre("Firulais")
                .tipo("Perro");

        assertThrows(IllegalStateException.class, builderNullDueno::build);

        // Test validación de String (nombre vacío)
        Mascota.Builder builderEmptyNombre = new Mascota.Builder()
                .dueno(dueno)
                .nombre("")
                .tipo("Perro");

        assertThrows(IllegalStateException.class, builderEmptyNombre::build);

        // Test validación de String (tipo null)
        Mascota.Builder builderNullTipo = new Mascota.Builder()
                .dueno(dueno)
                .nombre("Firulais");

        assertThrows(IllegalStateException.class, builderNullTipo::build);
    }
}

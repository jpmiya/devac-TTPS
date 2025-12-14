package org.example.devac.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Usuario.Builder Pattern
 * Valida: construcción exitosa, valores por defecto, validaciones fail-fast
 */
class UsuarioBuilderTest {

    @Test
    void build_withAllRequiredFields_shouldCreateUsuario() {
        Usuario usuario = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .telefono("123456789")
                .barrio("Centro")
                .ciudad("La Plata")
                .build();

        assertNotNull(usuario);
        assertEquals("Juan Pérez", usuario.getNombreYApellido());
        assertEquals("juan@example.com", usuario.getEmail());
        assertEquals("password123", usuario.getPassword());
        assertEquals("123456789", usuario.getTelefono());
        assertEquals("Centro", usuario.getBarrio());
        assertEquals("La Plata", usuario.getCiudad());
    }

    @Test
    void build_withDefaultValues_shouldSetCorrectDefaults() {
        Usuario usuario = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .build();

        // Validar valores por defecto
        assertEquals(0, usuario.getPosicion());
        assertEquals(0, usuario.getPuntos());
        assertEquals(0, usuario.getCasosEnZona());
        assertEquals(RolEnum.USUARIO, usuario.getRol());
        assertNotNull(usuario.getMedallas());
        assertTrue(usuario.getMedallas().isEmpty());
    }

    @Test
    void build_withCustomValues_shouldOverrideDefaults() {
        Usuario usuario = new Usuario.Builder()
                .nombreYApellido("Admin User")
                .email("admin@example.com")
                .password("admin123")
                .posicion(1)
                .puntos(100)
                .casosEnZona(5)
                .rol(RolEnum.ADMIN)
                .build();

        assertEquals(1, usuario.getPosicion());
        assertEquals(100, usuario.getPuntos());
        assertEquals(5, usuario.getCasosEnZona());
        assertEquals(RolEnum.ADMIN, usuario.getRol());
    }

    @Test
    void build_withoutEmail_shouldThrowIllegalStateException() {
        Usuario.Builder builder = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .password("password123");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Email"));
        assertTrue(exception.getMessage().contains("requerido"));
    }

    @Test
    void build_withEmptyEmail_shouldThrowIllegalStateException() {
        Usuario.Builder builder = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("   ")
                .password("password123");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    void build_withoutPassword_shouldThrowIllegalStateException() {
        Usuario.Builder builder = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Password"));
    }

    @Test
    void build_withEmptyPassword_shouldThrowIllegalStateException() {
        Usuario.Builder builder = new Usuario.Builder()
                .nombreYApellido("Juan Pérez")
                .email("juan@example.com")
                .password("");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Password"));
    }

    @Test
    void build_withoutNombreYApellido_shouldThrowIllegalStateException() {
        Usuario.Builder builder = new Usuario.Builder()
                .email("juan@example.com")
                .password("password123");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Nombre y Apellido"));
    }

    @Test
    void build_withEmptyNombreYApellido_shouldThrowIllegalStateException() {
        Usuario.Builder builder = new Usuario.Builder()
                .nombreYApellido("  ")
                .email("juan@example.com")
                .password("password123");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                builder::build
        );

        assertTrue(exception.getMessage().contains("Nombre y Apellido"));
    }

    @Test
    void builder_shouldSupportFluentInterface() {
        // Validar que cada método devuelve el Builder (fluent API)
        Usuario.Builder builder = new Usuario.Builder();

        assertSame(builder, builder.nombreYApellido("Juan"));
        assertSame(builder, builder.email("juan@example.com"));
        assertSame(builder, builder.password("pass123"));
        assertSame(builder, builder.telefono("123"));
        assertSame(builder, builder.barrio("Centro"));
        assertSame(builder, builder.ciudad("La Plata"));
        assertSame(builder, builder.posicion(1));
        assertSame(builder, builder.puntos(10));
        assertSame(builder, builder.casosEnZona(2));
        assertSame(builder, builder.rol(RolEnum.ADMIN));
    }

    @Test
    void build_multipleInstances_shouldBeIndependent() {
        Usuario.Builder builder = new Usuario.Builder();

        Usuario usuario1 = builder
                .nombreYApellido("User 1")
                .email("user1@example.com")
                .password("pass1")
                .build();

        // Crear nueva instancia del builder para evitar estado compartido
        Usuario usuario2 = new Usuario.Builder()
                .nombreYApellido("User 2")
                .email("user2@example.com")
                .password("pass2")
                .build();

        assertNotEquals(usuario1.getNombreYApellido(), usuario2.getNombreYApellido());
        assertNotEquals(usuario1.getEmail(), usuario2.getEmail());
    }
}

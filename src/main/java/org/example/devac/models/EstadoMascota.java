package org.example.devac.models;

/**
 * Enum que representa los posibles estados de una mascota y una breve nota
 * sobre quién puede asignar cada estado:
 *
 * PERDIDO_PROPIO - seteado por un usuario registrado cuando pierde la mascota.
 * PERDIDO_AJENO  - seteado por un usuario registrado cuando encuentra una mascota no reportada como perdida.
 * RECUPERADO     - seteado por el usuario creador al recuperar una mascota en estado perdido.
 * ADOPTADO       - seteado por el usuario creador sobre una mascota ajena en estado perdido.
 */
public enum EstadoMascota {
    PERDIDO_PROPIO,
    PERDIDO_AJENO,
    RECUPERADO,
    ADOPTADO
}

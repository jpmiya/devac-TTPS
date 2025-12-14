package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.MascotaRequest;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private UsuarioDAO<Usuario> usuarioDAO;

    @Autowired
    private MascotaDAO<Mascota> mascotaDAO;

    @Autowired
    MascotaEditerService mascotaEditerService;


    @Override
    public Mascota registrar(MascotaRequest request) {
        // Buscar el dueño por ID
        Usuario dueno = usuarioDAO.get(request.getDuenoId());
        if (dueno == null) {
            throw new BadRequestException("Usuario no encontrado");
        }

        // Determinar estado
        EstadoMascota estado = request.getEstado() != null 
            ? request.getEstado() 
            : EstadoMascota.PERDIDO_PROPIO;

        // Crear mascota usando Builder Pattern
        Mascota mascota = new Mascota.Builder()
                .dueno(dueno)
                .nombre(request.getNombre())
                .tamaño(request.getTamaño())
                .color(request.getColor())
                .fechaDePerdida(request.getFechaDePerdida())
                .foto(request.getFoto())
                .coordenadas(request.getCoordenadas())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .raza(request.getRaza())
                .estado(estado)
                .build();

        return mascotaDAO.persist(mascota);
    }

    @Override
    public Mascota editar(Mascota mascota) {
        return mascotaEditerService.edit(mascota.getId(),mascota);
    }

    public void eliminar(Mascota mascota) {
        mascotaDAO.delete(mascota.getId());
    }

    public List<Mascota> findAllLost() {
        // Obtener todas las mascotas y filtrar por estado perdido
        List<Mascota> todas = mascotaDAO.getAll("id");
        return todas.stream()
            .filter(m -> m.getEstado() == EstadoMascota.PERDIDO_AJENO || 
                        m.getEstado() == EstadoMascota.PERDIDO_PROPIO)
            .collect(Collectors.toList());
    }

}

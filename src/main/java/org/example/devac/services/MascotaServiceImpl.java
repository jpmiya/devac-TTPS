package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.MascotaRequest;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.repositories.MascotaRepo;
import org.example.devac.repositories.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    UsuarioRepo usuarioRepository;

    @Autowired
    MascotaRepo mascotaRepository;

    @Autowired
    MascotaEditerService mascotaEditerService;


    @Override
    public Mascota registrar(MascotaRequest request) {
        // Buscar el dueño por ID
        Usuario dueno = usuarioRepository.findById(request.getDuenoId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        // Crear la mascota
        Mascota mascota = new Mascota();
        mascota.setDueno(dueno);
        mascota.setNombre(request.getNombre());
        mascota.setTamaño(request.getTamaño());
        mascota.setColor(request.getColor());
        mascota.setFechaDePerdida(request.getFechaDePerdida());
        mascota.setFoto(request.getFoto());
        mascota.setCoordenadas(request.getCoordenadas());
        mascota.setDescripcion(request.getDescripcion());
        mascota.setTipo(request.getTipo());
        mascota.setRaza(request.getRaza());
        
        // Establecer estado (usar el del request o por defecto PERDIDO_PROPIO)
        if (request.getEstado() != null) {
            mascota.setEstado(request.getEstado());
        } else {
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
        }

        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota editar(Mascota mascota) {
        return mascotaEditerService.edit(mascota.getId(),mascota);
    }

    public void eliminar(Mascota mascota) {
        mascotaRepository.delete(mascota);
    }

    public List<Mascota> findAllLost() {
        // Obtener todas las mascotas y filtrar por estado perdido
        List<Mascota> todas = mascotaRepository.findAll();
        return todas.stream()
            .filter(m -> m.getEstado() == EstadoMascota.PERDIDO_AJENO || 
                        m.getEstado() == EstadoMascota.PERDIDO_PROPIO)
            .collect(Collectors.toList());
    }

}

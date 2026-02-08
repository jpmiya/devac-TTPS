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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    
    @Autowired
    private MinioService minioService;


    @Override
    public Mascota registrar(MascotaRequest request) {
        return registrar(request, null);
    }
    
    @Transactional
    public Mascota registrar(MascotaRequest request, MultipartFile foto) {
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
                .coordenadas(request.getCoordenadas())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .raza(request.getRaza())
                .estado(estado)
                .build();

    
        Mascota mascotaGuardada = mascotaDAO.persist(mascota);
        
        String uploadedFileName = null;
        try {
            // Subir foto si existe
            if (foto != null && !foto.isEmpty()) {
                uploadedFileName = minioService.uploadFile(foto, mascotaGuardada.getId());
                String fileUrl = minioService.getFileUrl(uploadedFileName);
                mascotaGuardada.setFotoUrl(fileUrl);
                mascotaDAO.update(mascotaGuardada);
            }
            
            return mascotaGuardada;
            
        } catch (Exception e) {
            // Si falló después de subir la foto, eliminarla de MinIO
            if (uploadedFileName != null) {
                try {
                    minioService.deleteFile(uploadedFileName);
                } catch (Exception deleteEx) {
                    // Log el error pero no fallar por esto
                    System.err.println("Error al eliminar foto huérfana de MinIO: " + deleteEx.getMessage());
                }
            }
            // Re-lanzar la excepción para que @Transactional haga rollback de la BD
            throw e;
        }
    }
    
    // Sobrecarga: registrar con Mascota directamente
    @Transactional
    public Mascota registrar(Mascota mascota, Long duenoId) {
        Usuario dueno = usuarioDAO.get(duenoId);
        if (dueno == null) {
            throw new BadRequestException("Usuario no encontrado");
        }
        
        mascota.setDueno(dueno);
        Mascota mascotaGuardada = mascotaDAO.persist(mascota);
        
        // Agregar a la lista del usuario
        dueno.agregarMascota(mascotaGuardada);
        usuarioDAO.update(dueno);
        
        return mascotaGuardada;
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



    @Override
    public Optional<Mascota> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(mascotaDAO.get(id));
    }

    @Override
    public Mascota buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("ID inválido");
        }

        Mascota m = mascotaDAO.get(id);
        if (m == null) {
            // si tenés NotFoundException, mejor usar esa.
            throw new BadRequestException("Mascota no encontrada");
        }

        return m;
    }


}

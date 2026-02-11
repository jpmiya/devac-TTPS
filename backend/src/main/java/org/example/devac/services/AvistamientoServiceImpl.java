package org.example.devac.services;


import org.example.devac.DAOs.AvistamientoDAO;
import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.AvistamientoRequest;
import org.example.devac.models.Avistamiento;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.example.devac.exceptions.BadRequestException;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {
    @Autowired
    AvistamientoDAO<Avistamiento> avistamientoDAO;

    @Autowired
    UsuarioDAO<Usuario> usuarioDAO;

    @Autowired
    MascotaDAO<Mascota> mascotaDAO;

    @Autowired private MinioService minioService;


    @Transactional
    public Avistamiento createAvistamiento(AvistamientoRequest request, MultipartFile foto) {
        Usuario usuario = usuarioDAO.get(request.getUsuarioId());
        if (usuario == null) throw new BadRequestException("Usuario no encontrado");

        Mascota mascota = mascotaDAO.get(request.getMascotaId());
        if (mascota == null) throw new BadRequestException("Mascota no encontrada");

        Avistamiento av = new Avistamiento(
                usuario,
                mascota,
                request.getFecha(),
                null, // fotoUrl después
                request.getCoordenadas(),
                request.getComentario()
        );

        Avistamiento guardado = avistamientoDAO.persist(av);

        String uploaded = null;
        try {
            if (foto != null && !foto.isEmpty()) {
                uploaded = minioService.uploadFile(foto, guardado.getId()); // o "avistamientos/"
                String url = minioService.getFileUrl(uploaded);
                guardado.setFotoUrl(url);
                avistamientoDAO.update(guardado);
            }
            return guardado;
        } catch (Exception e) {
            if (uploaded != null) {
                try { minioService.deleteFile(uploaded); } catch (Exception ignore) {}
            }
            throw e;
        }
    }


    @Override
    public List<Avistamiento> getAvistamientos(){
        return avistamientoDAO.getAll("id");
    }

}

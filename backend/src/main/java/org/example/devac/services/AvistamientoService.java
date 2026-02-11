package org.example.devac.services;

import org.example.devac.dto.AvistamientoRequest;
import org.example.devac.models.Avistamiento;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvistamientoService {
    public Avistamiento createAvistamiento(AvistamientoRequest request, MultipartFile foto);

    public List<Avistamiento> getAvistamientos();

}

package org.example.devac.services;

import org.example.devac.dto.AvistamientoRequest;
import org.example.devac.models.Avistamiento;

import java.util.List;

public interface AvistamientoService {
    public Avistamiento createAvistamiento(AvistamientoRequest request);
    public List<Avistamiento> getAvistamientos();

}

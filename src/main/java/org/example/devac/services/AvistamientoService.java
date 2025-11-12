package org.example.devac.services;

import org.example.devac.models.Avistamiento;

import java.util.List;

public interface AvistamientoService {
    public Avistamiento createAvistamiento(Avistamiento avistamiento);
    public List<Avistamiento> getAvistamientos();
}

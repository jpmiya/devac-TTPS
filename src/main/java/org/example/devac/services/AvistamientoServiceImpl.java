package org.example.devac.services;


import org.example.devac.models.Avistamiento;
import org.example.devac.repositories.AvistamientoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {
    @Autowired
    AvistamientoRepo avistamientoRepo;

    @Override
    public Avistamiento createAvistamiento(Avistamiento avistamiento) {
        return avistamientoRepo.save(avistamiento);
    }

    @Override
    public List<Avistamiento> getAvistamientos(){
        return avistamientoRepo.findAll();

    }














}

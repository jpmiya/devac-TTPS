package org.example.devac.services;


import org.example.devac.DAOs.AvistamientoDAO;
import org.example.devac.models.Avistamiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {
    @Autowired
    AvistamientoDAO<Avistamiento> avistamientoDAO;

    @Override
    public Avistamiento createAvistamiento(Avistamiento avistamiento) {
        return avistamientoDAO.persist(avistamiento);
    }

    @Override
    public List<Avistamiento> getAvistamientos(){
        return avistamientoDAO.getAll("id");
    }

}

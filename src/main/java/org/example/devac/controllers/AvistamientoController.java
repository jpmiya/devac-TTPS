package org.example.devac.controllers;


import org.example.devac.dto.AvistamientoRequest;
import org.example.devac.models.Avistamiento;
import org.example.devac.models.Mascota;
import org.example.devac.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.devac.services.AvistamientoService;

import java.util.List;

@RestController
@RequestMapping("/avistamiento")
public class AvistamientoController {

    @Autowired
    private AvistamientoService avistamientoService;

    @PostMapping("/create")
    public ResponseEntity<Avistamiento> create(@RequestBody AvistamientoRequest request) {
        return ResponseEntity.ok(avistamientoService.createAvistamiento(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Avistamiento>> getAvistamientos() {
        return ResponseEntity.ok(avistamientoService.getAvistamientos());
    }

}

package org.example.devac.controllers;

import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mascota")
public class MascotaController {
    @Autowired
    private MascotaService mascotaService;

    @PostMapping("/register")
    public ResponseEntity<Mascota> registrar(@RequestBody Mascota mascota) {
        return ResponseEntity.ok(mascotaService.registrar(mascota));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mascota> editar(@RequestBody Mascota mascota) {
        return ResponseEntity.ok(mascotaService.editar(mascota));
    }
}

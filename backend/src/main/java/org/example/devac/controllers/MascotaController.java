package org.example.devac.controllers;

import org.example.devac.dto.MascotaRequest;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mascota")
public class MascotaController {
    @Autowired
    private MascotaService mascotaService;

    @PostMapping("/register")
    public ResponseEntity<Mascota> registrar(
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return ResponseEntity.ok(mascotaService.registrar(request, foto));
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Mascota> editar(@RequestBody Mascota mascota) {
        return ResponseEntity.ok(mascotaService.editar(mascota));
    }

    @GetMapping("/findAllLost")
    public ResponseEntity<List<Mascota>> findAllLost() {
        return ResponseEntity.ok(mascotaService.findAllLost());
    }
}

package org.example.devac.controllers;

import org.example.devac.dto.MascotaRequest;
import org.springframework.http.MediaType;
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

    @PostMapping(value="/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrar(
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value="foto", required=false) MultipartFile foto) {

        try {
            return ResponseEntity.ok(mascotaService.registrar(request, foto));
        } catch (Exception e) {
            e.printStackTrace(); // <- esto lo vas a ver en docker logs
            return ResponseEntity.status(500).body(e.toString());
        }
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

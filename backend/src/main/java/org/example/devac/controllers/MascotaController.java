package org.example.devac.controllers;

import org.example.devac.dto.MascotaRequest;
import org.springframework.http.MediaType;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.example.devac.dto.MascotaResponse;


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


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editar(
            @PathVariable("id") Long id,
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            HttpServletRequest httpRequest
    ) {
        try {
            Long meId = (Long) httpRequest.getAttribute("USER_ID");
            if (meId == null) {
                return ResponseEntity.status(401).body("No logueado");
            }

            Mascota updated = mascotaService.editar(id, request, foto, meId);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.toString());
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> getById(@PathVariable("id") Long id) {
        Mascota m = mascotaService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(m));
    }




    @GetMapping("/findAllLost")
    public ResponseEntity<List<Mascota>> findAllLost() {
        return ResponseEntity.ok(mascotaService.findAllLost());
    }




    private MascotaResponse toResponse(Mascota m) {
        MascotaResponse r = new MascotaResponse();
        r.setId(m.getId());
        r.setDuenoId(m.getDueno() != null ? m.getDueno().getId() : null);
        r.setNombre(m.getNombre());
        r.setTipo(m.getTipo());
        r.setRaza(m.getRaza());
        r.setTamanio(m.getTamaño());
        r.setColor(m.getColor());
        r.setFechaDePerdida(m.getFechaDePerdida());
        r.setEstado(m.getEstado());
        r.setCoordenadas(m.getCoordenadas());
        r.setDescripcion(m.getDescripcion());
        r.setFotoUrl(m.getFotoUrl());
        return r;
    }
}

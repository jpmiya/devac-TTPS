package org.example.devac.controllers;

import org.example.devac.dto.MascotaRequest;
import org.springframework.http.MediaType;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
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
            HttpSession session
    ) {
        try {
            // 1) usuario logueado
            Long meId = (Long) session.getAttribute("USER_ID");

            if (meId == null) {
                return ResponseEntity.status(401).body("No logueado");
            }

            // 2) mascota existente
            Mascota actual = mascotaService.buscarPorId(id);

            // 3) validar dueño
            if (actual.getDueno() == null || actual.getDueno().getId() == null) {
                return ResponseEntity.status(500).body("Mascota sin dueño (datos corruptos)");
            }
            if (!actual.getDueno().getId().equals(meId)) {
                return ResponseEntity.status(403).body("No sos el dueño");
            }

            // 4) construir mascota actualizada (sin tocar dueño)
            Mascota mascota = new Mascota.Builder()
                    .dueno(actual.getDueno())
                    .nombre(request.getNombre())
                    .tipo(request.getTipo())
                    .raza(request.getRaza())
                    .tamaño(request.getTamaño())          // si tu MascotaRequest se llama getTamaño() así
                    .color(request.getColor())
                    .fechaDePerdida(request.getFechaDePerdida())
                    .coordenadas(request.getCoordenadas())
                    .descripcion(request.getDescripcion())
                    .estado(request.getEstado())
                    .build();

            // CLAVE: el id SIEMPRE del path
            mascota.setId(id);

            // 5) editar en BD (tu service hace el update)
            Mascota updated = mascotaService.editar(mascota);

            // 6) si vino foto, acá lo ideal es delegarlo al service
            // Si ya lo tenés armado en registrar(), creá un método editarConFoto(...) similar.
            if (foto != null && !foto.isEmpty()) {
                // EJEMPLO (si creás un método en el service):
                // updated = mascotaService.editarConFoto(id, mascota, foto);

                // Si todavía no lo tenés, dejalo así y lo agregamos después.
                System.out.println("Foto recibida: " + foto.getOriginalFilename() + " (" + foto.getSize() + " bytes)");
            }

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

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
            @PathVariable Long id,
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            HttpSession session
    ) {
        try {
            // 1) usuario logueado
            Usuario me = (Usuario) session.getAttribute("usuario");
            if (me == null) return ResponseEntity.status(401).build();

            // 2) mascota existente
            Mascota actual = mascotaService.buscarPorId(id);

            // 3) validar dueño
            if (actual.getDueno() == null || !actual.getDueno().getId().equals(me.getId())) {
                return ResponseEntity.status(403).build();
            }

            // 4) mapear request -> Mascota (usando el existente como base)
            Mascota mascota = new Mascota.Builder()
                    .dueno(actual.getDueno())                 // NO se toca
                    .nombre(request.getNombre())
                    .tipo(request.getTipo())
                    .raza(request.getRaza())
                    .tamaño(request.getTamaño())              // ojo: tu request parece usar "Tamaño"
                    .color(request.getColor())
                    .fechaDePerdida(request.getFechaDePerdida())
                    .coordenadas(request.getCoordenadas())
                    .descripcion(request.getDescripcion())
                    .estado(request.getEstado())
                    .build();

            mascota.setId(id); // CLAVE: el id viene del path, no del body

            // 5) editar en BD
            Mascota updated = mascotaService.editar(mascota);

            // 6) si vino foto, la subís y actualizás fotoUrl (si tu service ya lo hace, mejor moverlo ahí)
            if (foto != null && !foto.isEmpty()) {
                // si querés, acá llamás a un método del service tipo editarFoto(id, foto)
                // por ahora, si no tenés método, dejalo sin foto y lo agregamos después
            }

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.toString());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> getById(@PathVariable Long id) {
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

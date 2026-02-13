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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.devac.exceptions.BadRequestException;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mascota")
public class MascotaController {
    private final MascotaService mascotaService;
    private final ObjectMapper objectMapper;

    public MascotaController(MascotaService mascotaService, ObjectMapper objectMapper) {
        this.mascotaService = mascotaService;
        this.objectMapper = objectMapper;
    }

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
            @RequestPart("mascota") String mascotaJson,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            HttpServletRequest request
    ) {
        System.out.println("ENTRE AL PUT editar id=" + id);

        // 0) Validación rápida
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID inválido");
        }
        if (mascotaJson == null || mascotaJson.isBlank()) {
            return ResponseEntity.badRequest().body("Parte 'mascota' vacía o inexistente");
        }

        // 1) Parse JSON -> DTO
        final MascotaRequest requestDto;
        try {
            System.out.println("[PUT] mascotaJson length=" + mascotaJson.length());
            System.out.println("[PUT] mascotaJson head=" + mascotaJson.substring(0, Math.min(200, mascotaJson.length())));
            requestDto = objectMapper.readValue(mascotaJson, MascotaRequest.class);
        } catch (JsonProcessingException e) {
            System.err.println("[PUT] ERROR parseando JSON 'mascota': " + e.getMessage());
            return ResponseEntity.badRequest().body("JSON inválido en parte 'mascota': " + e.getOriginalMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error leyendo parte 'mascota': " + e);
        }

        try {
            // 2) Auth por JWT (desde request attribute)
            Long meId = (Long) request.getAttribute("USER_ID");
            if (meId == null) return ResponseEntity.status(401).body("No logueado");

            // 3) Buscar mascota actual
            Mascota actual = mascotaService.buscarPorId(id); // si esto ya lanza excepción, perfecto
            if (actual == null) return ResponseEntity.status(404).body("Mascota no existe");

            if (actual.getDueno() == null || actual.getDueno().getId() == null) {
                return ResponseEntity.status(500).body("Mascota sin dueño");
            }

            // 4) Permisos
            if (!actual.getDueno().getId().equals(meId)) {
                return ResponseEntity.status(403).body("No sos el dueño");
            }

            // 5) Logs de multipart
            System.out.println("=== PUT /mascota/" + id + " ===");
            System.out.println("USER_ID=" + meId);
            System.out.println("foto null? " + (foto == null));
            if (foto != null) {
                System.out.println("foto empty? " + foto.isEmpty());
                System.out.println("foto name=" + foto.getOriginalFilename());
                System.out.println("foto size=" + foto.getSize());
                System.out.println("foto contentType=" + foto.getContentType());
            }

            // 6) Construir “parcial” manteniendo dueño + id
            Mascota mascotaActualizada = new Mascota.Builder()
                    .dueno(actual.getDueno())
                    .nombre(requestDto.getNombre())
                    .tipo(requestDto.getTipo())
                    .raza(requestDto.getRaza())
                    .tamaño(requestDto.getTamaño())
                    .color(requestDto.getColor())
                    .fechaDePerdida(requestDto.getFechaDePerdida())
                    .coordenadas(requestDto.getCoordenadas() != null ? requestDto.getCoordenadas() : actual.getCoordenadas())
                    .descripcion(requestDto.getDescripcion())
                    .estado(requestDto.getEstado() != null ? requestDto.getEstado() : actual.getEstado())
                    .build();

            mascotaActualizada.setId(id);

            // 7) Service (sin cast)
            Mascota updated = mascotaService.editarConFoto(id, mascotaActualizada, foto);

            return ResponseEntity.ok(updated);

        } catch (BadRequestException e) {
            // tus validaciones de negocio
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error editando mascota: " + e);
        }
    }




    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> getById(@PathVariable("id") Long id) {
        Mascota m = mascotaService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(m));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<MascotaResponse>> getAll() {
        List<Mascota> todas = mascotaService.findAll();
        List<MascotaResponse> resp = todas.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/findAllLost")
    public ResponseEntity<List<MascotaResponse>> getAllLost() {
        List<Mascota> perdidas = mascotaService.findAllLost();
        List<MascotaResponse> resp = perdidas.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/findAllAdopted")
    public ResponseEntity<List<MascotaResponse>> getAllAdopted() {
        List<Mascota> adoptadas = mascotaService.findAllAdopted();
        List<MascotaResponse> resp = adoptadas.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(resp);
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

        if (m.getDueno() != null) {
            MascotaResponse.DuenoResumen dueno = new MascotaResponse.DuenoResumen();
            dueno.setTelefono(m.getDueno().getTelefono());
            dueno.setBarrio(m.getDueno().getBarrio());
            dueno.setCiudad(m.getDueno().getCiudad());
            dueno.setCoordenadas(m.getDueno().getCoordenadas());
            r.setDueno(dueno);
        }

        return r;
    }
}

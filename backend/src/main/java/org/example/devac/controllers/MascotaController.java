package org.example.devac.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.devac.dto.MascotaRequest;
import org.example.devac.dto.MascotaResponse;
import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.services.MascotaService;
import org.example.devac.utils.JwtUtils;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrar(
            @RequestPart("mascota") MascotaRequest request,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        // si mascotaService.registrar() tira excepción -> GlobalExceptionHandler la maneja
        return ResponseEntity.ok(mascotaService.registrar(request, foto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editar(
            @PathVariable("id") Long id,
            @RequestPart("mascota") String mascotaJson,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @CookieValue(value = "jwt", required = false) String token
    ) {
        // ===== auth (control de flujo, no excepción) =====
        Long meId = extractMeIdOr401(token);
        if (meId == null) return ResponseEntity.status(401).body("No autorizado");

        // ===== validaciones rápidas (podés tirarlas como BadRequestException) =====
        if (id == null || id <= 0) throw new BadRequestException("ID inválido");
        if (mascotaJson == null || mascotaJson.isBlank()) throw new BadRequestException("Parte 'mascota' vacía o inexistente");

        // ===== parse JSON =====
        final MascotaRequest request;
        try {
            request = objectMapper.readValue(mascotaJson, MascotaRequest.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("JSON inválido en parte 'mascota': " + e.getOriginalMessage());
        }

        // ===== buscar y permisos =====
        Mascota actual = mascotaService.buscarPorId(id);
        if (actual == null) return ResponseEntity.status(404).body("Mascota no existe");
        if (actual.getDueno() == null || actual.getDueno().getId() == null) {
            // esto sí es inconsistencia del servidor -> mejor lanzar RuntimeException
            throw new IllegalStateException("Mascota sin dueño");
        }
        if (!actual.getDueno().getId().equals(meId)) {
            return ResponseEntity.status(403).body("No sos el dueño");
        }

        // ===== construir update =====
        Mascota mascotaActualizada = new Mascota.Builder()
                .dueno(actual.getDueno())
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .raza(request.getRaza())
                .tamaño(request.getTamaño())
                .color(request.getColor())
                .fechaDePerdida(request.getFechaDePerdida())
                .coordenadas(request.getCoordenadas())
                .descripcion(request.getDescripcion())
                .estado(request.getEstado() != null ? request.getEstado() : actual.getEstado())
                .build();

        mascotaActualizada.setId(id);

        Mascota updated = mascotaService.editarConFoto(id, mascotaActualizada, foto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> getById(@PathVariable("id") Long id) {
        Mascota m = mascotaService.buscarPorId(id);
        if (m == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(toResponse(m));
    }

    @GetMapping("/findAllLost")
    public ResponseEntity<List<MascotaResponse>> getAllLost() {
        List<Mascota> perdidas = mascotaService.findAllLost();
        List<MascotaResponse> resp = perdidas.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @PathVariable("id") Long id,
            @CookieValue(value = "jwt", required = false) String token
    ) {
        Long meId = extractMeIdOr401(token);
        if (meId == null) return ResponseEntity.status(401).body("No autorizado");

        Mascota actual = mascotaService.buscarPorId(id);
        if (actual == null) return ResponseEntity.status(404).body("Mascota no existe");
        if (actual.getDueno() == null || actual.getDueno().getId() == null) {
            throw new IllegalStateException("Mascota sin dueño");
        }
        if (!actual.getDueno().getId().equals(meId)) {
            return ResponseEntity.status(403).body("No sos el dueño");
        }

        mascotaService.eliminar(actual);
        return ResponseEntity.ok().build();
    }

    // ===== helpers =====

    private Long extractMeIdOr401(String token) {
        if (token == null || token.isBlank()) return null;
        if (!JwtUtils.validateToken(token)) return null;
        return JwtUtils.extractUserId(token);
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

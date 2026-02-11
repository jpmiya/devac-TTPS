package org.example.devac.controllers;


import org.example.devac.dto.AvistamientoRequest;
import org.example.devac.models.Avistamiento;
import org.example.devac.models.Mascota;
import org.example.devac.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.devac.services.AvistamientoService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.devac.dto.AvistamientoListDto;




import java.util.List;

@RestController
@RequestMapping("/avistamiento")
public class AvistamientoController {

    @Autowired
    private AvistamientoService avistamientoService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("avistamiento") String avistamientoJson,
            @RequestPart(value="foto", required=false) MultipartFile foto
    ) throws Exception {

        AvistamientoRequest req =
                objectMapper.readValue(avistamientoJson, AvistamientoRequest.class);

        return ResponseEntity.ok(avistamientoService.createAvistamiento(req, foto));
    }


    @GetMapping("/all")
    public ResponseEntity<List<AvistamientoListDto>> all() {
        var list = avistamientoService.getAvistamientos();

        List<AvistamientoListDto> dtos = list.stream()
                .map(a -> new AvistamientoListDto(
                        a.getId(),
                        a.getFecha(), // String
                        a.getCoordenadas(),
                        a.getComentario(),
                        a.getUsuario() != null ? a.getUsuario().getId() : null,
                        a.getUsuario() != null ? a.getUsuario().getNombreYApellido() : null,
                        a.getMascota() != null ? a.getMascota().getId() : null,
                        a.getMascota() != null ? a.getMascota().getNombre() : null,
                        a.getFotoUrl()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }


}

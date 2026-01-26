package org.example.devac.controllers;

import org.example.devac.exceptions.BadRequestException;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.devac.dto.UsuarioRegisterDTO;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> create(@RequestBody UsuarioRegisterDTO usuario) {
        return ResponseEntity.ok(usuarioService.registrar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Usuario usr = usuarioService.login(email, password);

        if (usr == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)   // 401
                    .body("Credenciales inválidas");
        }


        //si el service no devolvio null seguro que la password estaba correcta
        return ResponseEntity.ok("Login correcto");
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editUser(@PathVariable("id") Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.editar(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/createMascota")
    public ResponseEntity<?> registrarMascota(@RequestBody Mascota mascota, @PathVariable("id") Long id) {
       try{
           return ResponseEntity.ok(usuarioService.registrarMascota(mascota,id));
       } catch (BadRequestException e){
           return ResponseEntity.badRequest().body("Usuario no encontrado");
       }

    }

    @PutMapping("/{id}/editMascota/{idMascota}")
    public ResponseEntity<?> editarMascota(@PathVariable("idMascota") Long idMascota, @PathVariable("id") Long id) {
        return ResponseEntity.ok(usuarioService.editarMascota(idMascota,id));
    }

    @PostMapping("/{id}/deleteMascota/{idMascota}")
    public ResponseEntity<?> eliminarMascota(@PathVariable("idMascota") Long idMascota, @PathVariable("id") Long id) {
        //falta manejar las distintas excepciones
        return ResponseEntity.ok(usuarioService.eliminarMascota(idMascota,id));
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<Mascota>> getMascotasDeUsuario(@PathVariable("id") Long id) {
        return ResponseEntity.ok(usuarioService.getMascotasDeUsuario(id));
    }
}

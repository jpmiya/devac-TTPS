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
import org.example.devac.dto.LoginRequest;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        try {
            Usuario u = usuarioService.login(req.getEmail(), req.getPassword());

            if (u == null) {
                return ResponseEntity.status(401).body("Credenciales inválidas");
            }

            // ✅ UNA sola key para todo el sistema
            session.setAttribute("USER_ID", u.getId());

            System.out.println("LOGIN OK sessionId=" + session.getId()
                    + " USER_ID=" + session.getAttribute("USER_ID"));

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.toString());
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout");
    }



    @GetMapping("/me")
    public ResponseEntity<Usuario> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).build();

        Usuario usuario = usuarioService.findById(userId);
        if (usuario == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(usuario);
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

    @PutMapping("/{id}/mascotas/{idMascota}")
    public ResponseEntity<?> editarMascota(@PathVariable("idMascota") Long idMascota, @PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(usuarioService.editarMascota(idMascota, id));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/mascotas/{idMascota}")
    public ResponseEntity<?> eliminarMascota(@PathVariable("idMascota") Long idMascota, @PathVariable("id") Long id) {
        try {
            usuarioService.eliminarMascota(idMascota, id);
            return ResponseEntity.ok().build();
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<Mascota>> getMascotasDeUsuario(@PathVariable("id") Long id) {
        return ResponseEntity.ok(usuarioService.getMascotasDeUsuario(id));
    }
}

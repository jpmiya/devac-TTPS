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
import org.example.devac.utils.JwtUtils;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


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
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        try {
            Usuario u = usuarioService.login(req.getEmail(), req.getPassword());

            if (u == null) {
                return ResponseEntity.status(401).body("Credenciales inválidas");
            }

            String token = JwtUtils.generateToken(u.getId());
            response.addCookie(JwtUtils.createCookie(token));

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.toString());
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // false en dev, true en prod
        cookie.setPath("/");
        cookie.setMaxAge(0); // Eliminar cookie
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout");
    }



    @GetMapping("/me")
    public ResponseEntity<Usuario> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("USER_ID");
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


    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<Mascota>> getMascotasDeUsuario(@PathVariable("id") Long id) {
        return ResponseEntity.ok(usuarioService.getMascotasDeUsuario(id));
    }

}

package org.example.devac.controllers;

import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.example.devac.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.registrar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        return ResponseEntity.ok(usuarioService.login(email,password));
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<Usuario> editUser(@PathVariable Long id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.editar(id,usuario));
    }

    @PostMapping("/createMascota/{id}")
    public ResponseEntity<Usuario> registrarMascota(@RequestBody Mascota mascota,@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.registrarMascota(mascota,id));
    }

    @PostMapping("/editMascota/{id}")
    public ResponseEntity<Usuario> editarMascota(@RequestBody Mascota mascota,@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.editarMascota(mascota,id));
    }

    @PostMapping("/deleteMascota/{id}")
    public ResponseEntity<Usuario> eliminarMascota(@RequestBody Mascota mascota,@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.eliminarMascota(mascota,id));
    }
}

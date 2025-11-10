package org.example.devac.controllers;

import org.example.devac.models.Usuario;
import org.example.devac.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.registrar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody String email, @RequestBody String password) {
        return ResponseEntity.ok(usuarioService.login(email,password));
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<Usuario> editUser(@PathVariable Long id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.editar(id,usuario));
    }

}

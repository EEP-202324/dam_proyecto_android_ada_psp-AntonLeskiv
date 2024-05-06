package com.eep.tickets.controllers;

import com.eep.tickets.models.AuthenticationResponse;
import com.eep.tickets.models.Role;
import com.eep.tickets.models.User;
import com.eep.tickets.services.AuthenticationService;
import com.eep.tickets.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        boolean isAuthenticated = authenticationService.authenticate(email, password);

        if (isAuthenticated) {
            Role role = userService.getByEmail(email).getRole();
            AuthenticationResponse response = new AuthenticationResponse(true, "Autenticación exitosa.", role.toString() );
            return ResponseEntity.ok().body(response);
        } else {
            AuthenticationResponse response = new AuthenticationResponse(false, "Credenciales inválidas");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password"); // Asumimos que recibimos la contraseña en texto plano para hashearla en el servidor.

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse(false, "Email y contraseña son requeridos"));
        }

        // Comprobar si el usuario ya existe
        User existingUser = userService.getByEmail(email);
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthenticationResponse(false, "El email ya está registrado"));
        }

        // Hashear la contraseña
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordHash = encoder.encode(password);

        // Crear y guardar el nuevo usuario
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash);
        newUser.setRole(Role.USER);
        // Establecer otros campos necesarios para el nuevo usuario aquí.

        userService.create(newUser);

        // Después de registrar al usuario, podrías autenticarlo y devolver una respuesta exitosa.
        AuthenticationResponse response = new AuthenticationResponse(true, "Registro exitoso", newUser.getRole().toString());
        return ResponseEntity.ok().body(response);
    }

}

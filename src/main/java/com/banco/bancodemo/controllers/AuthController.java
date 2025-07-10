package com.banco.bancodemo.controllers;

import com.banco.bancodemo.config.JwtTokenProvider;
import com.banco.bancodemo.dto.*;
import com.banco.bancodemo.dto.request.LoginRequest;
import com.banco.bancodemo.dto.request.RegisterRequest;
import com.banco.bancodemo.dto.response.JwtAuthResponse;
import com.banco.bancodemo.entity.TitularCuenta;
import com.banco.bancodemo.services.TitularCuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "API para registro y autenticación de usuarios")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TitularCuentaService titularCuentaService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Intento de login para email: {}", loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Obtener información del usuario
            TitularCuenta titular = titularCuentaService.buscarPorEmail(loginRequest.getEmail());

            // Crear respuesta con token y datos del usuario
            LoginResponse response = new LoginResponse();
            response.setToken(jwt);

            UserInfo userInfo = new UserInfo();
            userInfo.setId(titular.getId());
            userInfo.setEmail(titular.getEmail());
            userInfo.setNombre(titular.getNombre());
            response.setUser(userInfo);

            log.info("Login exitoso para email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en login para email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("Error al iniciar sesión: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario")
    public ResponseEntity<ApiResponseDTO<TitularCuentaDTO>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Intento de registro para email: {}", registerRequest.getEmail());
        log.debug("Datos de registro recibidos: {}", registerRequest);

        try {
            TitularCuentaDTO titularDTO = TitularCuentaDTO.builder()
                    .nombre(registerRequest.getNombre())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .dni(registerRequest.getDni())
                    .direccion(registerRequest.getDireccion())
                    .telefono(registerRequest.getTelefono())
                    .build();

            TitularCuentaDTO createdTitular = titularCuentaService.crearTitular(titularDTO);

            log.info("Usuario registrado exitosamente con email: {}", registerRequest.getEmail());
            return ResponseEntity.ok(ApiResponseDTO.success(
                    createdTitular,
                    "Usuario registrado exitosamente"
            ));
        } catch (Exception e) {
            log.error("Error al registrar usuario con email: {}", registerRequest.getEmail(), e);
            throw e;
        }
    }

    // Clases internas para las respuestas
    public static class LoginResponse {
        private String token;
        private UserInfo user;

        // Getters y setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UserInfo getUser() { return user; }
        public void setUser(UserInfo user) { this.user = user; }
    }

    public static class UserInfo {
        private Long id;
        private String email;
        private String nombre;

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
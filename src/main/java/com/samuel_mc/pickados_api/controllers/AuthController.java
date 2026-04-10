package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.AuthRequestDTO;
import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.RegisterTipsterRequestDTO;
import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.dto.RequestPasswordResetDTO;
import com.samuel_mc.pickados_api.dto.ResetPasswordRequestDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.service.PasswordResetService;
import com.samuel_mc.pickados_api.service.facade.UserRegistrationFacade;
import com.samuel_mc.pickados_api.service.EmailVerificationService;
import com.samuel_mc.pickados_api.util.JwtUtil;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRegistrationFacade userRegistrationFacade;
    private final JwtUtil jwtUtil;
    private final ResponseUtils responseUtils;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthenticationManager authenticationManager, UserRegistrationFacade userRegistrationFacade,
            JwtUtil jwtUtil, ResponseUtils responseUtils, EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.userRegistrationFacade = userRegistrationFacade;
        this.jwtUtil = jwtUtil;
        this.responseUtils = responseUtils;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT junto con sus datos básicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"username\":\"samuel\",\"role\":\"ROLE_USER\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Usuario o contraseña no válidos")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDTO req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("userId", userDetails.getId());
            resp.put("username", userDetails.getUsername());
            resp.put("role",
                    userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(""));
            return ResponseEntity.ok(resp);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña no válidos");
        }
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en la plataforma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    })
    @PostMapping("/register-user")
    public ResponseEntity<GenericResponseDTO<String>> registerUser(@RequestBody @Valid RegisterUserRequestDTO req) {
        this.userRegistrationFacade.processRegistration(req);
        return this.responseUtils.generateSuccessResponse(null);
    }

    @Operation(summary = "Registrar tipster", description = "Registra un nuevo tipster en la plataforma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipster registrado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    })
    @PostMapping("/register-tipster")
    public ResponseEntity<GenericResponseDTO<String>> registerTipster(
            @RequestBody @Valid RegisterTipsterRequestDTO req) {
        this.userRegistrationFacade.processRegistration(req);
        return this.responseUtils.generateSuccessResponse(null);
    }

    @Operation(summary = "Verificar correo electrónico", description = "Verifica el correo mediante un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo verificado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado", content = @Content)
    })
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            emailVerificationService.verifyEmail(token);
            return ResponseEntity.ok(Map.of("message", "Correo verificado exitosamente"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @Operation(summary = "Solicitar actualización de contraseña", description = "Envía un correo con un enlace temporal para actualizar la contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud procesada correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping("/request-password-reset")
    public ResponseEntity<GenericResponseDTO<String>> requestPasswordReset(
            @RequestBody @Valid RequestPasswordResetDTO req) {
        passwordResetService.requestPasswordReset(req.getEmail());
        return responseUtils.generateSuccessResponse(
                "Si el correo existe, recibirás instrucciones para actualizar tu contraseña");
    }

    @Operation(summary = "Actualizar contraseña", description = "Actualiza la contraseña usando un token temporal enviado por correo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Token inválido o datos incorrectos", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO req) {
        try {
            passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
            return responseUtils.generateSuccessResponse("Contraseña actualizada correctamente");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}

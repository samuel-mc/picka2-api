package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.AuthRequestDTO;
import com.samuel_mc.pickados_api.dto.AuthAvailabilityResponseDTO;
import com.samuel_mc.pickados_api.dto.AuthSessionResponseDTO;
import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.RegisterAdminRequestDTO;
import com.samuel_mc.pickados_api.dto.RegisterTipsterRequestDTO;
import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.dto.RequestPasswordResetDTO;
import com.samuel_mc.pickados_api.dto.ResetPasswordRequestDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.service.PasswordResetService;
import com.samuel_mc.pickados_api.service.AuthRateLimitService;
import com.samuel_mc.pickados_api.service.facade.UserRegistrationFacade;
import com.samuel_mc.pickados_api.service.EmailVerificationService;
import com.samuel_mc.pickados_api.repository.UserRepository;
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
import org.springframework.http.ResponseCookie;
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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y registro de cuentas")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRegistrationFacade userRegistrationFacade;
    private final JwtUtil jwtUtil;
    private final ResponseUtils responseUtils;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final AuthRateLimitService authRateLimitService;
    private final UserRepository userRepository;
    private final String authCookieName;
    private final boolean authCookieSecure;
    private final String authCookieSameSite;
    private final long jwtExpirationMs;

    public AuthController(AuthenticationManager authenticationManager, UserRegistrationFacade userRegistrationFacade,
            JwtUtil jwtUtil, ResponseUtils responseUtils, EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService, AuthRateLimitService authRateLimitService,
            UserRepository userRepository,
            @Value("${app.auth.cookie-name:picka2_auth}") String authCookieName,
            @Value("${app.auth.cookie-secure:false}") boolean authCookieSecure,
            @Value("${app.auth.cookie-same-site:Lax}") String authCookieSameSite,
            @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.userRegistrationFacade = userRegistrationFacade;
        this.jwtUtil = jwtUtil;
        this.responseUtils = responseUtils;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.authRateLimitService = authRateLimitService;
        this.userRepository = userRepository;
        this.authCookieName = authCookieName;
        this.authCookieSecure = authCookieSecure;
        this.authCookieSameSite = authCookieSameSite;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica la cuenta y devuelve un token JWT junto con sus datos básicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"username\":\"samuel\",\"role\":\"ROLE_TIPSTER\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Usuario o contraseña no válidos")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDTO req, HttpServletRequest request) {
        String rateLimitKey = buildLoginRateLimitKey(req, request);
        if (authRateLimitService.isLoginBlocked(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Demasiados intentos fallidos. Intenta nuevamente más tarde."));
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername().trim(), req.getPassword()));
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            authRateLimitService.clearLoginFailures(rateLimitKey);
            return ResponseEntity.ok()
                    .header("Set-Cookie", buildAuthCookie(token).toString())
                    .body(buildAuthSessionResponse(userDetails));
        } catch (AuthenticationException ex) {
            authRateLimitService.recordLoginFailure(rateLimitKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña no válidos");
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(@org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        return ResponseEntity.ok(buildAuthSessionResponse(principal));
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponseDTO<String>> logout() {
        return ResponseEntity.ok()
                .header("Set-Cookie", buildExpiredAuthCookie().toString())
                .body(GenericResponseDTO.<String>builder()
                        .success(true)
                        .code("SUCCESS")
                        .message("Sesión cerrada")
                        .data(null)
                        .timestamp(java.time.LocalDateTime.now())
                        .build());
    }

    @Operation(summary = "Registrar usuario interno", description = "Alias legado para registrar un administrador interno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador registrado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    })
    @PostMapping("/register-user")
    public ResponseEntity<GenericResponseDTO<String>> registerUser(@RequestBody @Valid RegisterUserRequestDTO req) {
        this.userRegistrationFacade.processRegistration(req);
        return this.responseUtils.generateSuccessResponse(null);
    }

    @Operation(summary = "Registrar admin", description = "Registra un nuevo usuario interno administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador registrado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    })
    @PostMapping("/register-admin")
    public ResponseEntity<GenericResponseDTO<String>> registerAdmin(@RequestBody @Valid RegisterAdminRequestDTO req) {
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

    @GetMapping("/availability")
    public ResponseEntity<GenericResponseDTO<AuthAvailabilityResponseDTO>> checkAvailability(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email
    ) {
        String normalizedUsername = username == null ? null : username.trim().toLowerCase(Locale.ROOT);
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);

        if ((normalizedUsername == null || normalizedUsername.isBlank())
                && (normalizedEmail == null || normalizedEmail.isBlank())) {
            return ResponseEntity.badRequest().body(
                    GenericResponseDTO.<AuthAvailabilityResponseDTO>builder()
                            .success(false)
                            .code("BAD_REQUEST")
                            .message("Debes enviar username o email para validar disponibilidad")
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }

        AuthAvailabilityResponseDTO dto = new AuthAvailabilityResponseDTO(
                normalizedUsername == null || normalizedUsername.isBlank()
                        ? null
                        : !userRepository.existsByUsername(normalizedUsername),
                normalizedEmail == null || normalizedEmail.isBlank()
                        ? null
                        : !userRepository.existsByEmail(normalizedEmail)
        );

        return responseUtils.generateSuccessResponse(dto);
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

    private String buildLoginRateLimitKey(AuthRequestDTO req, HttpServletRequest request) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim().toLowerCase(Locale.ROOT);
        String ip = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr().trim();
        return username + "|" + ip;
    }

    private AuthSessionResponseDTO buildAuthSessionResponse(CustomUserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
        return new AuthSessionResponseDTO(userDetails.getId(), userDetails.getUsername(), role);
    }

    private ResponseCookie buildAuthCookie(String token) {
        return ResponseCookie.from(authCookieName, token)
                .httpOnly(true)
                .secure(authCookieSecure)
                .sameSite(authCookieSameSite)
                .path("/")
                .maxAge(Duration.ofMillis(jwtExpirationMs))
                .build();
    }

    private ResponseCookie buildExpiredAuthCookie() {
        return ResponseCookie.from(authCookieName, "")
                .httpOnly(true)
                .secure(authCookieSecure)
                .sameSite(authCookieSameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}

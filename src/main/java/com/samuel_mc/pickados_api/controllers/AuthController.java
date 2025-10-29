package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.AuthRequest;
import com.samuel_mc.pickados_api.dto.GenericResponse;
import com.samuel_mc.pickados_api.dto.RegisterRequest;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.exception.GenericException;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.service.AuthService;
import com.samuel_mc.pickados_api.util.JwtUtil;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final ResponseUtils responseUtils;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService,
                          JwtUtil jwtUtil, ResponseUtils responseUtils) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.responseUtils = responseUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("username", userDetails.getUsername());
            resp.put("role", userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(""));
            return ResponseEntity.ok(resp);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña inválidos");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse<String>> register(@RequestBody RegisterRequest req) {
        this.authService.userRegister(req);
        return this.responseUtils.generateSuccessResponse(null);
    }
}

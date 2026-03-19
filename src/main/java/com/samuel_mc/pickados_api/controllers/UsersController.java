package com.samuel_mc.pickados_api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.dto.mappers.UserMapper;
import com.samuel_mc.pickados_api.dto.UserResponseDTO;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admins")
    public List<UserResponseDTO> getAdmins() {
        return userRepository.findByRole_Name("ADMIN").stream()
                .map(UserMapper.INSTANCIA::userEntityToUserResponseDTO)
                .collect(Collectors.toList());
    }
}

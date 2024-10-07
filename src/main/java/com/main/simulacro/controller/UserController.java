package com.main.simulacro.controller;

import com.main.simulacro.dto.request.AuthRequest;
import com.main.simulacro.dto.request.UserCreateDTO;
import com.main.simulacro.dto.response.AuthResponse;
import com.main.simulacro.dto.response.UserResponseDTO;
import com.main.simulacro.entity.User;
import com.main.simulacro.service.EmailService;
import com.main.simulacro.service.UserService;
import com.main.simulacro.util.CustomUserDetails;
import com.main.simulacro.util.Role;
import com.main.simulacro.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private EmailService emailService;


    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserCreateDTO userCreateDTO) {
        // Crear un nuevo usuario
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(userCreateDTO.getPassword());
        user.setEmail(userCreateDTO.getEmail());

        // Asignar el rol desde el DTO
        if (userCreateDTO.getRole() == null) {
            user.setRole(Role.USER); // Asignar rol USER por defecto si no se especifica
        } else {
            user.setRole(userCreateDTO.getRole());
        }

        // Crear el usuario a través del servicio
        User createdUser = userService.createUser(user);

        // Construir la respuesta
        UserResponseDTO responseUserDTO = new UserResponseDTO();
        responseUserDTO.setId(createdUser.getId());
        responseUserDTO.setUsername(createdUser.getUsername());
        responseUserDTO.setRole(createdUser.getRole().name());

        return ResponseEntity.ok(responseUserDTO);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            // Autenticación
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generar el token JWT
            String token = jwtTokenProvider.generateToken(userDetails.getUsername(), ((CustomUserDetails) userDetails).getRole());

            // Retornar el token en la respuesta
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Credenciales incorrectas"));
        }
    }

    @GetMapping("/sendEmail")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        emailService.sendSimpleEmail(to, subject, text);
        return "Correo enviado con éxito!";
    }


}

package com.main.simulacro.service;

import com.main.simulacro.entity.User;
import com.main.simulacro.repository.UserRepository;
import com.main.simulacro.util.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService { // Implementa UserDetailsService

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUsByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Valida si el email tiene un formato correcto (puedes usar una expresión regular)
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    // Valida la fortaleza de la contraseña (ejemplo básico, puedes añadir más reglas)
    private boolean isStrongPassword(String password) {
        return password.matches(".*[A-Z].*") && // Al menos una mayúscula
                password.matches(".*[a-z].*") && // Al menos una minúscula
                password.matches(".*\\d.*") &&   // Al menos un número
                password.matches(".*[!@#$%^&*].*"); // Al menos un carácter especial
    }


    public User createUser(User userCreateDTO) {
        // Crear un nuevo usuario y establecer sus atributos
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(userCreateDTO.getPassword());
        user.setRole(userCreateDTO.getRole()); // Establecer el rol desde el DTO

        // Validaciones
        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }

        // Validar si el usuario ya existe
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El usuario con ese email ya existe.");
        }

        // Validación de la contraseña
        if (!StringUtils.hasText(user.getPassword()) || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        if (!isStrongPassword(user.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe ser más fuerte.");
        }

        // Encriptar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Intentar guardar el usuario en la base de datos
        try {
            return userRepository.save(user);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Error al guardar el usuario: " + e.getMessage());
        }
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }

    // Implementación del método loadUserByUsername de UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Construir CustomUserDetails con rol
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(), // Usar el rol del usuario
                getAuthorities(user) // Obtener las autoridades
        );
    }
    // Método para obtener las autoridades del usuario
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority(user.getRole().name())); // Asumiendo que role es un enum
    }
}

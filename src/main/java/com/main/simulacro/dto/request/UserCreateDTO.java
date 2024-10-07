package com.main.simulacro.dto.request;


import com.main.simulacro.util.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private Role role ;
}

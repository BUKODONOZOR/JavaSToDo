package com.main.simulacro.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {

    private String username;
    private String email;
    private String password;

}

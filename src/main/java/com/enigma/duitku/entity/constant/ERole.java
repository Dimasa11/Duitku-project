package com.enigma.duitku.entity.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum ERole {
    ROLE_USER,
    ROLE_ADMIN;

    public static ERole get(String value) {
        for (ERole eRole : ERole.values()) {
            if(eRole.name().equalsIgnoreCase(value)) return eRole;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found");
    }
}

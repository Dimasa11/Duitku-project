package com.enigma.duitku.controller;

import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.LoginResponse;
import com.enigma.duitku.model.response.RegisterResponse;
import com.enigma.duitku.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register/user")
    public ResponseEntity<?> registerUsers(@RequestBody AuthRequest authRequest)throws UserException {
        RegisterResponse register = authService.registerUsers(authRequest);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully registered user")
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping(path = "/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest authRequest) throws UserException{
        RegisterResponse register = authService.registerAdmin(authRequest);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully registered admin")
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        LoginResponse responseLogin = authService.login(authRequest);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Login")
                .data(responseLogin)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}

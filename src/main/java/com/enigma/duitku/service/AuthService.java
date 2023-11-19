package com.enigma.duitku.service;


import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.LoginResponse;
import com.enigma.duitku.model.response.RegisterResponse;

public interface AuthService {

    RegisterResponse registerUsers(AuthRequest authRequest) throws UserException;
    RegisterResponse registerAdmin(AuthRequest authRequest) throws UserException;
    LoginResponse login(AuthRequest request);
}

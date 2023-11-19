package com.enigma.duitku.controller;


import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.LoginResponse;
import com.enigma.duitku.model.response.RegisterResponse;
import com.enigma.duitku.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void testRegisterUsers() throws Exception {

        AuthRequest authRequest = AuthRequest.builder()
                .email("dim3214@gmail.com")
                .password("apaya")
                .name("dimas")
                .address("jalan terus")
                .mobileNumber("08953245345")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .email("dim3214@gmail.com")
                .mobileNumber("08953245345")
                .build();


        when(authService.registerUsers(any(AuthRequest.class))).thenReturn(registerResponse);


        mockMvc.perform(post("/api/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Successfully registered user"))
                .andExpect(jsonPath("$.data.email").value("dim3214@gmail.com"))
                .andExpect(jsonPath("$.data.mobileNumber").value("08953245345"));


        verify(authService, times(1)).registerUsers(any(AuthRequest.class));
    }

    @Test
    public void testRegisterAdmin() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .email("dim3214@gmail.com")
                .password("apaya")
                .name("dimas")
                .address("jalan terus")
                .mobileNumber("08953245345")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .email("dim3214@gmail.com")
                .mobileNumber("08953245345")
                .build();


        when(authService.registerAdmin(any(AuthRequest.class))).thenReturn(registerResponse);


        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Successfully registered admin"))
                .andExpect(jsonPath("$.data.email").value("dim3214@gmail.com"))
                .andExpect(jsonPath("$.data.mobileNumber").value("08953245345"));

        verify(authService, times(1)).registerAdmin(any(AuthRequest.class));
    }

    @Test
    void testLogin() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .email("dim5435@gmail.com")
                .password("rahasia")
                .build();

        List<String> roles = List.of("ROLE_USER");
        LoginResponse loginResponse = LoginResponse.builder()
                .mobileNumber("089653543546")
                .roles(roles)
                .token("tooken")
                .build();


        when(authService.login(any(AuthRequest.class))).thenReturn(loginResponse);


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Successfully Login"))
                .andExpect(jsonPath("$.data.mobileNumber").value("089653543546"))
                .andExpect(jsonPath("$.data.roles").isArray())
                .andExpect(jsonPath("$.data.token").value("tooken"));


        verify(authService, times(1)).login(any(AuthRequest.class));
    }


}

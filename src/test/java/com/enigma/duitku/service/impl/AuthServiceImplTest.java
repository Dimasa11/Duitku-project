package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.*;
import com.enigma.duitku.entity.constant.ERole;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.LoginResponse;
import com.enigma.duitku.model.response.RegisterResponse;
import com.enigma.duitku.repository.UserCredentialRepository;
import com.enigma.duitku.security.BCryptUtils;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.RoleService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@SpringBootTest
class AuthServiceImplTest {
    private AuthServiceImpl authServiceImpl;
    private UserCredentialRepository userCredentialRepository;
    private BCryptUtils bCryptUtils;
    private UserService userService;
    private RoleService roleService;
    private ValidationUtil validationUtil;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        userCredentialRepository = Mockito.mock(UserCredentialRepository.class);
        bCryptUtils = Mockito.mock(BCryptUtils.class);
        userService = Mockito.mock(UserService.class);
        roleService = Mockito.mock(RoleService.class);
        validationUtil = Mockito.mock(ValidationUtil.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtUtils = Mockito.mock(JwtUtils.class);
        authServiceImpl = new AuthServiceImpl(userCredentialRepository, bCryptUtils, userService, roleService, validationUtil, authenticationManager, jwtUtils);
    }

    @Test
    public void registerUsers_shouldSucceed() throws UserException {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("dim4211@gmail.com");
        authRequest.setPassword("apakahkamutau");
        authRequest.setName("dimdoang");
        authRequest.setAddress("jalan-jalan teros");
        authRequest.setMobileNumber("08954353434");

        Role role = Role.builder()
                .role(ERole.ROLE_USER)
                .build();

        UserCredential credential = UserCredential.builder()
                .mobileNumber(authRequest.getMobileNumber())
                .password(bCryptUtils.hashPassword(authRequest.getPassword()))
                .roles(List.of(role))
                .build();

        User user = User.builder()
                .name(authRequest.getName())
                .mobileNumber(authRequest.getMobileNumber())
                .email(authRequest.getEmail())
                .userCredential(credential)
                .build();

        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);

        when(roleService.getOrSave(ERole.ROLE_USER)).thenReturn(role);
        when(userCredentialRepository.saveAndFlush(credential)).thenReturn(credential);
        when(userService.create(user)).thenReturn(user);


        RegisterResponse registerResponse = authServiceImpl.registerUsers(authRequest);


        assertEquals(authRequest.getMobileNumber(), registerResponse.getMobileNumber());
        assertEquals(wallet.getBalance(), registerResponse.getBalance());
        assertEquals(authRequest.getEmail(), registerResponse.getEmail());


        verify(roleService, times(1)).getOrSave(ERole.ROLE_USER);
        verify(userCredentialRepository, times(1)).saveAndFlush(any(UserCredential.class));
        verify(userService, times(1)).create(any(User.class));

    }

    @Test
    void testRegisterUsersConflict() throws UserException {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("dim4211@gmail.com");
        authRequest.setPassword("apakahkamutau");
        authRequest.setName("dimdoang");


        Role role = new Role();
        role.setRole(ERole.ROLE_USER);

        when(roleService.getOrSave(ERole.ROLE_USER)).thenReturn(new Role());
        when(bCryptUtils.hashPassword(any())).thenReturn("rahasia");
        when(userCredentialRepository.saveAndFlush(any(UserCredential.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));


        ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class,
                () -> authServiceImpl.registerUsers(authRequest)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("user already exists", exception.getReason());

        verify(roleService,times(1)).getOrSave(ERole.ROLE_USER);
        verify(bCryptUtils,times(1)).hashPassword(any());
        verify(userCredentialRepository,times(1)).saveAndFlush(any(UserCredential.class));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void registerAdmin_shouldSucceed() throws UserException {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setMobileNumber("08987564767");
        authRequest.setPassword("rahasia");
        authRequest.setName("dimdoang");

        Role role = Role.builder()
                .role(ERole.ROLE_ADMIN)
                .build();

        String hashedPassword = bCryptUtils.hashPassword(authRequest.getPassword());

        UserCredential credential = UserCredential.builder()
                .mobileNumber(authRequest.getMobileNumber())
                .password(hashedPassword)
                .roles(List.of(role))
                .build();

        when(roleService.getOrSave(ERole.ROLE_ADMIN)).thenReturn(role);
        when(userCredentialRepository.saveAndFlush(any(UserCredential.class))).thenReturn(credential);

        RegisterResponse registerResponse = authServiceImpl.registerAdmin(authRequest);

        assertEquals(authRequest.getMobileNumber(), registerResponse.getMobileNumber());

        verify(roleService, times(1)).getOrSave(ERole.ROLE_ADMIN);
        verify(userCredentialRepository, times(1)).saveAndFlush(any(UserCredential.class));
        verify(userService, times(1)).create(any(Admin.class));
    }

    @Test
    void testRegisterAdminWithConflict() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("dim4211@gmail.com");
        authRequest.setPassword("apakahkamutau");
        authRequest.setName("dimdoang");


        Role role = new Role();
        role.setRole(ERole.ROLE_ADMIN);

        when(roleService.getOrSave(ERole.ROLE_ADMIN)).thenReturn(role);
        when(bCryptUtils.hashPassword(authRequest.getPassword())).thenReturn(authRequest.getPassword());
        when(userCredentialRepository.saveAndFlush(any(UserCredential.class))).thenThrow(DataIntegrityViolationException.class);

        ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class, () -> {
            authServiceImpl.registerAdmin(authRequest);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("admin already exists", exception.getReason());

        verify(roleService, times(1)).getOrSave(ERole.ROLE_ADMIN);
        verify(bCryptUtils, times(1)).hashPassword(authRequest.getPassword());
        verify(userCredentialRepository, times(1)).saveAndFlush(any(UserCredential.class));
    }

    @Test
    public void login_shouldSucceed() {
        AuthRequest request = AuthRequest.builder()
                .mobileNumber("08946437457")
                .password("gatau")
                .build();

        Authentication authentication = mock(Authentication.class);
        UserDetailImpl userDetails = mock(UserDetailImpl.class);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(() -> "ROLE_USER");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getMobileNumber()).thenReturn(request.getMobileNumber());
        when(userDetails.getAuthorities()).thenAnswer(invocation -> authorities);
        when(jwtUtils.generateToken(request.getMobileNumber())).thenReturn("generatedToken");

        LoginResponse loginResponse = authServiceImpl.login(request);

        assertEquals(request.getMobileNumber(), loginResponse.getMobileNumber());
        assertEquals(Collections.singletonList("ROLE_USER"), loginResponse.getRoles());
        assertEquals("generatedToken", loginResponse.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateToken(request.getMobileNumber());
    }
}

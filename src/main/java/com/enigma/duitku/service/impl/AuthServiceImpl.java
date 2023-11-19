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
import com.enigma.duitku.service.AuthService;
import com.enigma.duitku.service.RoleService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final BCryptUtils bCryptUtils;
    private final UserService userService;
    private final RoleService roleService;

    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse registerUsers(AuthRequest authRequest) throws UserException {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_USER);
            UserCredential credential= UserCredential.builder()
                    .mobileNumber(authRequest.getMobileNumber())
                    .password(bCryptUtils.hashPassword(authRequest.getPassword()))
                    .roles(List.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(credential);

            User user = User.builder()
                    .name(authRequest.getName())
                    .mobileNumber(authRequest.getMobileNumber())
                    .email(authRequest.getEmail())
                    .userCredential(credential)
                    .build();
            userService.create(user);

            Wallet wallet = new Wallet();
            wallet.setBalance(0.0);

            return RegisterResponse.builder()
                    .mobileNumber(user.getMobileNumber())
                    .balance(wallet.getBalance())
                    .email(user.getEmail())
                    .build();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already exists");

        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse registerAdmin(AuthRequest authRequest)throws UserException {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_ADMIN);
            UserCredential credential= UserCredential.builder()
                    .mobileNumber(authRequest.getMobileNumber())
                    .password(bCryptUtils.hashPassword(authRequest.getPassword()))
                    .roles(List.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(credential);

            Admin admin = Admin.builder()
                    .name(authRequest.getName())
                    .mobileNumber(authRequest.getMobileNumber())
                    .userCredential(credential)
                    .build();
            userService.create(admin);

            Wallet wallet = new Wallet();
            wallet.setBalance(0.0);

            return RegisterResponse.builder()
                    .mobileNumber(credential.getMobileNumber())
                    .balance(wallet.getBalance())
                    .build();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "admin already exists");
        }
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getMobileNumber(),
                request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String token = jwtUtils.generateToken(userDetails.getMobileNumber());
        return LoginResponse.builder()
                .mobileNumber(userDetails.getMobileNumber())
                .roles(roles)
                .token(token)
                .build();
    }

}

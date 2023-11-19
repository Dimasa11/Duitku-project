package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.UserCredential;
import com.enigma.duitku.entity.UserDetailImpl;
import com.enigma.duitku.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuitkuUserServiceImpl implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        UserCredential userCredential = userCredentialRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->new UsernameNotFoundException("user not found"));
        List<SimpleGrantedAuthority> grantedAuthorities = userCredential.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole().name())).collect(Collectors.toList());

        return UserDetailImpl.builder()
                .mobileNumber(userCredential.getMobileNumber())
                .password(userCredential.getPassword())
                .authorities(grantedAuthorities)
                .build();
    }
}

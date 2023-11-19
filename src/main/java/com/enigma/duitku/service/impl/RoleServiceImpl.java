package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Role;
import com.enigma.duitku.entity.constant.ERole;
import com.enigma.duitku.repository.RoleRepository;
import com.enigma.duitku.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getOrSave(ERole role) {
        return roleRepository.findByRole(role).orElseGet(()-> roleRepository.save(Role.builder()
                .role(role)
                .build()));
    }
}

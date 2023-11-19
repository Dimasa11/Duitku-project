package com.enigma.duitku.service;

import com.enigma.duitku.entity.Role;
import com.enigma.duitku.entity.constant.ERole;

public interface RoleService {
    Role getOrSave(ERole role);
}

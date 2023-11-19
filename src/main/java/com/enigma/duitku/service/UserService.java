package com.enigma.duitku.service;

import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User create(User  user) throws UserException;
    Admin create (Admin admin) throws UserException;
    User getById(String id);
    Page<UserResponse> getAll(Integer page, Integer size);
    User update(User user);
    void deleteById(String id);

}

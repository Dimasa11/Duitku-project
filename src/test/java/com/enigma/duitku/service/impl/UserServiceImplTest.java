package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.repository.AdminRepository;
import com.enigma.duitku.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;



    @Test
    public void createUser_shouldSucceed() throws UserException {
        User user = new User();
        user.setMobileNumber("08954353434");
        user.setEmail("dim4211@gmail.com");

        when(userRepository.findById(user.getMobileNumber())).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        User createdUser = userServiceImpl.create(user);

        assertNotNull(createdUser);
        assertEquals(user.getMobileNumber(), createdUser.getMobileNumber());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertNotNull(createdUser.getWallet());
        assertEquals(0.0, createdUser.getWallet().getBalance());

        verify(userRepository, times(1)).findById(user.getMobileNumber());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    public void createUser_shouldThrowExceptionIfUserFailed() {
        User user = new User();
        user.setMobileNumber("08954353434");

        when(userRepository.findById(user.getMobileNumber())).thenReturn(Optional.of(user));

        assertThrows(UserException.class, () -> userServiceImpl.create(user));

        verify(userRepository, times(1)).findById(user.getMobileNumber());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void createAdmin_shouldSucceed() throws UserException {
        // Create a mock Admin
        Admin admin = new Admin();
        admin.setMobileNumber("08954353434");

        // Mocking behavior to return an empty Optional when findById is called
        when(adminRepository.findById(admin.getMobileNumber())).thenReturn(Optional.empty());

        // Mocking the saveAndFlush method to return the same admin
        when(adminRepository.saveAndFlush(any(Admin.class))).thenReturn(admin);

        // Call the create method
        Admin createdAdmin = userServiceImpl.create(admin);

        // Assertions
        assertNotNull(createdAdmin);
        assertEquals(admin.getMobileNumber(), createdAdmin.getMobileNumber());
        assertNotNull(createdAdmin.getWallet());
        assertEquals(0.0, createdAdmin.getWallet().getBalance());

        // Verify that the findById and saveAndFlush methods were called
        verify(adminRepository, times(1)).findById(admin.getMobileNumber());
        verify(adminRepository, times(1)).saveAndFlush(any(Admin.class));
    }

    @Test
    public void createAdmin_shouldThrowExceptionIfAdminExists() {
        // Create a mock Admin
        Admin admin = new Admin();
        admin.setMobileNumber("08954353434");

        // Mocking behavior to return a non-empty Optional when findById is called
        when(adminRepository.findById(admin.getMobileNumber())).thenReturn(Optional.of(admin));

        // Call the create method and expect a UserException
        assertThrows(UserException.class, () -> userServiceImpl.create(admin));

        // Verify that the findById method was called
        verify(adminRepository, times(1)).findById(admin.getMobileNumber());
        // Verify that the saveAndFlush method was not called
        verify(adminRepository, never()).saveAndFlush(any(Admin.class));
    }

    @Test
    public void getUserById_shouldReturnUser() {
        // Create a mock User
        User user = new User();


        // Mocking behavior to return the user when findById is called
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        // Call the getById method
        User retrievedUser = userServiceImpl.getById("userId123");

        // Assertions
        assertEquals(user.getId(), retrievedUser.getId());

        // Verify that the findById method was called
        verify(userRepository, times(1)).findById("userId123");
    }

    @Test
    public void getUserById_shouldThrowExceptionWhenUserNotFound() {
        // Mocking behavior to return an empty Optional when findById is called
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // Call the getById method and expect a ResponseStatusException
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userServiceImpl.getById("nonExistentUserId"));

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        // Verify that the findById method was called
        verify(userRepository, times(1)).findById("nonExistentUserId");
    }

    @Test
    void getAll() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }
}
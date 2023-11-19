package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.response.UserResponse;
import com.enigma.duitku.repository.AdminRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AdminRepository adminRepository;

    @Override
    public User create(User user) throws UserException {
        try {
            // TODO 1 Getting Optional User by Mobile Number
            Optional<User> findUser = userRepository.findById(user.getMobileNumber());

            // TODO 2 Checking if User with the Mobile Number already exists
            if(findUser.isEmpty()) {

                // TODO 3 Creating a new Wallet for the User
                Wallet wallet = new Wallet();
                wallet.setBalance(0.0);
                wallet.setId(user.getMobileNumber());
                user.setWallet(wallet);

                // TODO 4 Saving and Flushing the User to the Repository
                User addUser=userRepository.saveAndFlush(user);

                // TODO 5 Checking if User is successfully added
                if(addUser !=null) {
                    return addUser;
                } else {
                    throw new UserException("Sorry, sign up unsuccessfull!");
                }

            } else {
                throw new UserException("User already Registered With The Mobile Number: " +  user.getMobileNumber());
            }

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already used");
        }
    }

    @Override
    public Admin create(Admin admin) throws UserException {
        // TODO 1 Getting Optional Admin by Mobile Number
        Optional<Admin> findUser = adminRepository.findById(admin.getMobileNumber());
        // TODO 2 Checking if User with the Mobile Number already exists
        if(findUser.isEmpty()) {

            // TODO 3 Creating a new Wallet for the User
            Wallet wallet = new Wallet();
            wallet.setBalance(0.0);
            wallet.setId(admin.getMobileNumber());
            admin.setWallet(wallet);

            // TODO 4 Saving and Flushing the User to the Repository
            Admin addAdmin=adminRepository.saveAndFlush(admin);

            // TODO 5 Checking if User is successfully added
            if(addAdmin!=null) {
                return addAdmin;
            } else {
                throw new UserException("Sorry, sign up unsuccessfull!");
            }

        } else {
            throw new UserException("Admin already Registered With The Mobile Number: " +  admin.getMobileNumber());
        }
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<UserResponse> getAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users.getContent()) {
            UserResponse userResponse = new UserResponse(user.getMobileNumber(), user.getName(), user.getEmail());
            userResponses.add(userResponse);
        }
        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }

    @Override
    public User update(User user) {
        User currentUser= getById(user.getMobileNumber());
        if (currentUser!= null) {
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User by Number " + user.getMobileNumber() + " No Found. Unable to update.");
        }
    }

    @Override
    public void deleteById(String id) {
        User user= getById(id);
        userRepository.delete(user);
    }
}

package com.enigma.duitku.controller;

import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.PagingResponse;
import com.enigma.duitku.model.response.UserResponse;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createNewCustomer(@RequestBody User user) throws UserException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<User>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully create new user")
                        .data(userService.create(user))
                        .build());
    }

    // Admin
    @GetMapping
    public ResponseEntity<?> getAllUser(
            @RequestParam(name = "page", required = false, defaultValue = "0")Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5")Integer size
    ) {
        Page<UserResponse> userResponses = userService.getAll(page, size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(userResponses.getTotalPages())
                .size(size)
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get all user")
                        .data(userResponses.getContent())
                        .paging(pagingResponse)
                        .build());
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getUserId(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<User>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get user by id")
                        .data(userService.getById(id))
                        .build());
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<User>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully update user")
                        .data(userService.update(user))
                        .build());
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteById(id);
        User user = new User();
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<String>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully delete user")
                        .data(String.valueOf(user))
                        .build());
    }
}

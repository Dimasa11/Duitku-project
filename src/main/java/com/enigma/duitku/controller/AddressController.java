package com.enigma.duitku.controller;

import com.enigma.duitku.entity.Address;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.PagingResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/add")
    public ResponseEntity<?> addAddress(@RequestBody AddressRequest addressRequest, HttpServletRequest httpServletRequest) throws UserException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            if(jwtToken != null) {
                AddressResponse addressResponse = addressService.addAddress(addressRequest, jwtToken);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .data(addressResponse)
                                .message("Successfully created address")
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.CONFLICT.value())
                            .message("Failed add address " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAddress(@RequestBody Address address, HttpServletRequest httpServletRequest) throws UserException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            if(jwtToken != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.<Address>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully update customer")
                                .data(addressService.updateAddress(address, jwtToken))
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed update address" + e.getMessage())
                            .build());
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAddressId(@PathVariable String id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.<Address>builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Successfully get customer by id")
                            .data(addressService.getAddressId(id))
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed get id" + e.getMessage())
                            .build());
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAddress(@RequestParam String id, HttpServletRequest httpServletRequest ) throws UserException {
        try{
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            if(jwtToken != null) {
                addressService.removeAddress(id, jwtToken);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message(String.valueOf("Sucessfully delete address"))
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Delete Failed " + e.getMessage())
                            .build());
        }
    }

    // Admin
    @GetMapping("/allAddresses")
    public ResponseEntity<?> viewAllAddress(
            @RequestParam(name = "page", required = false, defaultValue = "0")Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5")Integer size
    ){

        try {
            Page<AddressResponse> addressResponses = addressService.viewAllAddress(page, size);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .currentPage(page)
                    .totalPage(addressResponses.getTotalPages())
                    .size(size)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("View all successfull")
                            .data(addressResponses.getContent())
                            .paging(pagingResponse)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("View all address " + e.getMessage())
                            .build());
        }
    }

}

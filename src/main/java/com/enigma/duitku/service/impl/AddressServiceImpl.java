package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Address;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.ConflictException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import com.enigma.duitku.repository.AddressRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.AddressService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final JwtUtils jwtUtils;

    private final UserService userService;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse addAddress(AddressRequest addressRequest, String token) throws UserException {

        // TODO 1 : Extract the user ID from the JWT token using jwtUtils.
        String loggedInUserId = jwtUtils.extractUserId(token);

        // TODO 2 : Retrieve the user object from the userService based on the extracted user ID.
        User user = userService.getById(loggedInUserId);

        // TODO 3 : Check if the user object is not null.
        if(user != null) {
            // TODO 4 : Check if the user already has an address. If yes, throw a ConflictException.
            if (user.getAddress() != null) {
                throw new ConflictException("User already has an address");
            }

            // TODO 5 : Create a new Address object and set its properties based on the provided addressRequest.
            Address address = new Address();
            address.setState(addressRequest.getState());
            address.setCity(addressRequest.getCity());
            address.setStreetName(addressRequest.getStreetName());
            address.setPostalCode(addressRequest.getPostalCode());

            // TODO 6 : Set the user's address to the newly created address.
            user.setAddress(address);

            // TODO 7: Save the new address to the addressRepository.
            addressRepository.save(address);

            // TODO 8 : Save the updated user object to the userRepository.
            userRepository.save(user);

            // TODO 9 : Build and return an AddressResponse based on the user's address.
            return AddressResponse.builder()
                    .state(user.getAddress().getState())
                    .city(user.getAddress().getCity())
                    .streetName(user.getAddress().getStreetName())
                    .postalCode(user.getAddress().getPostalCode())
                    .build();
        } else {

            // TODO 10 : If the user object is null, throw a UserException indicating the need to log in.
            throw new UserException("Please Login in!");
        }
    }

    @Override
    public Address updateAddress(Address address, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            Address currentAddress = getAddressId(address.getId());
            return addressRepository.save(address);
        } else {
            throw new UserException("Please Login in!");
        }
    }

    @Override
    public Address getAddressId(String id) {
            return addressRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address id not found"));
    }

    @Override
    public void removeAddress(String id, String token) throws UserException{
        // TODO 1 : Extract the user ID from the JWT token using jwtUtils.
        String loggedInUserId = jwtUtils.extractUserId(token);

        // TODO 2 : Retrieve the user object from the userService based on the extracted user ID.
        User userValidate = userService.getById(loggedInUserId);

        if(userValidate != null) {
            // TODO 3 : Retrieving and Deleting Addresses
            Address address = addressRepository.findById(id).orElse(null);

            if(address != null) {
                // TODO 4 : Removes the address from the user object
                userValidate.setAddress(null);

                // TODO : Removes the address entity from the repository
                addressRepository.delete(address);
            }
        } else {
            throw new UserException("Please Login in!");
        }
    }

    // Admin
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<AddressResponse> viewAllAddress(Integer page, Integer size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Address> addresses = addressRepository.findAll(pageable);
            List<AddressResponse> addressResponses = new ArrayList<>();
            for (Address address : addresses.getContent()) {
                AddressResponse addressResponse = AddressResponse.builder()
                        .city(address.getCity())
                        .postalCode(address.getPostalCode())
                        .state(address.getState())
                        .streetName(address.getStreetName())
                        .build();
                addressResponses.add(addressResponse);
            }
            return new PageImpl<>(addressResponses, pageable, addresses.getTotalElements());
    }

}

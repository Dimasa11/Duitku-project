package com.enigma.duitku.service;

import com.enigma.duitku.entity.Address;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import org.springframework.data.domain.Page;

public interface AddressService {

    AddressResponse addAddress(AddressRequest addressRequest, String token) throws UserException;
    Address updateAddress(Address address, String token) throws UserException;
    Address getAddressId(String id);
    void removeAddress(String id, String token) throws UserException;

    // Admin
    Page<AddressResponse> viewAllAddress(Integer page, Integer size) ;
}

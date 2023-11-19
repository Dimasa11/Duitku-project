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
import com.enigma.duitku.service.UserService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AddressServiceImplTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void addAddress_shouldSucceed() throws UserException {

        String userId = "08964363436";
        String token = "validToken";
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setState("tunjungan");
        addressRequest.setCity("Semarang");
        addressRequest.setStreetName("Jalan kita bersama");
        addressRequest.setPostalCode("59483");

        User user = new User();
        user.setMobileNumber(userId);

        Address address = new Address();
        address.setState(addressRequest.getState());
        address.setCity(addressRequest.getCity());
        address.setStreetName(addressRequest.getStreetName());
        address.setPostalCode(addressRequest.getPostalCode());


        when(jwtUtils.extractUserId(token)).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressResponse addressResponse = addressService.addAddress(addressRequest, token);

        assertNotNull(addressResponse);
        assertEquals(addressRequest.getState(), addressResponse.getState());
        assertEquals(addressRequest.getCity(), addressResponse.getCity());
        assertEquals(addressRequest.getStreetName(), addressResponse.getStreetName());
        assertEquals(addressRequest.getPostalCode(), addressResponse.getPostalCode());


        verify(jwtUtils, times(1)).extractUserId(token);
        verify(userService, times(1)).getById(userId);
        verify(userRepository, times(1)).save(user);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void updateAddress_shouldSucceed() throws UserException {
        String userId = "08964363436";
        String token = "validToken";
        String addressId = "33625ga5";

        Address addressToUpdate = new Address();
        addressToUpdate.setId(addressId);
        addressToUpdate.setState("tunjungan");
        addressToUpdate.setCity("Semarang");
        addressToUpdate.setStreetName("Jalan kita bersama");
        addressToUpdate.setPostalCode("59483");

        User dummyUser = new User();
        dummyUser.setMobileNumber(userId);

        Address currentAddressInDatabase = new Address();
        currentAddressInDatabase.setId(addressId);
        currentAddressInDatabase.setState("ngaglik");
        currentAddressInDatabase.setCity("purwodadi");
        currentAddressInDatabase.setStreetName("jln. megamendung");
        currentAddressInDatabase.setPostalCode("54321");
        
        when(jwtUtils.extractUserId(token)).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(dummyUser);
        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.of(currentAddressInDatabase));
        when(addressRepository.save(any(Address.class))).thenReturn(addressToUpdate);

        Address updatedAddress = addressService.updateAddress(addressToUpdate, token);

        assertNotNull(updatedAddress);
        assertEquals(addressToUpdate.getId(), updatedAddress.getId());
        assertEquals(addressToUpdate.getState(), updatedAddress.getState());
        assertEquals(addressToUpdate.getCity(), updatedAddress.getCity());
        assertEquals(addressToUpdate.getStreetName(), updatedAddress.getStreetName());
        assertEquals(addressToUpdate.getPostalCode(), updatedAddress.getPostalCode());

        verify(jwtUtils, times(1)).extractUserId(token);
        verify(userService, times(1)).getById(userId);
        verify(addressRepository, times(1)).findById(addressId);
        verify(addressRepository, times(1)).save(any(Address.class));
    }


    @Test
    void getAddressId_shouldReturnAddress() {
        String addressId = "432435";

        Address dummyAddress = new Address();
        dummyAddress.setId(addressId);
        dummyAddress.setState("bringinharjo");
        dummyAddress.setCity("bandung");
        dummyAddress.setStreetName("jlan maju");
        dummyAddress.setPostalCode("42425");

        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.of(dummyAddress));

        Address resultAddress = addressService.getAddressId(addressId);

        assertEquals(dummyAddress.getId(), resultAddress.getId());
        assertEquals(dummyAddress.getState(), resultAddress.getState());
        assertEquals(dummyAddress.getCity(), resultAddress.getCity());
        assertEquals(dummyAddress.getStreetName(), resultAddress.getStreetName());
        assertEquals(dummyAddress.getPostalCode(), resultAddress.getPostalCode());

        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    public void testRemoveAddress() throws UserException {
        String userId = "45345345";
        String addressId = "454356";
        String token = "validToken";

        User loggedInUser = new User();
        loggedInUser.setMobileNumber(userId);

        Address addressToRemove = new Address();
        addressToRemove.setId(addressId);

        when(jwtUtils.extractUserId(token)).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(loggedInUser);
        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.ofNullable(addressToRemove));


        addressService.removeAddress(addressId, token);

        verify(addressRepository, times(1)).delete(addressToRemove);

    }

    @Test
    public void testViewAllAddress() {
        Integer page = 0;
        Integer size = 10;

        Pageable pageable = PageRequest.of(page, size);

        List<Address> dummyAddressList = new ArrayList<>();
        Address address1 = new Address();
        address1.setCity("jakarta");
        address1.setPostalCode("52424");
        address1.setState("klapagading");
        address1.setStreetName("jln. sudirman");

        Address address2 = new Address();
        address2.setCity("malang");
        address2.setPostalCode("51952");
        address2.setState("winginharjo");
        address2.setStreetName("jln. gatot subroto");

        dummyAddressList.add(address1);
        dummyAddressList.add(address2);
        Page<Address> mockPage = new PageImpl<>(dummyAddressList, pageable, dummyAddressList.size());

        when(addressRepository.findAll(pageable)).thenReturn(mockPage);


        Page<AddressResponse> resultPage = addressService.viewAllAddress(page, size);


        verify(addressRepository, times(1)).findAll(pageable);

        assertEquals(mockPage.getTotalElements(), resultPage.getTotalElements(),
                "Total elements should match");

    }
}
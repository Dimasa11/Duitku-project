package com.enigma.duitku.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AddressRequest {

    private String streetName;
    private String city;
    private String state;
    private String postalCode;
    private String id;

}

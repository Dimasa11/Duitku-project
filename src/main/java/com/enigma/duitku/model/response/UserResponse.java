package com.enigma.duitku.model.response;

import com.enigma.duitku.entity.Address;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {
    private String email;
    private String mobileNumber;
    private String name;
}

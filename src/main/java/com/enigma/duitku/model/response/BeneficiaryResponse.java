package com.enigma.duitku.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BeneficiaryResponse {

    private String mobileNumber;
    private String name;
    private String accountNo;
    private String bankName;
    private String errors;

}

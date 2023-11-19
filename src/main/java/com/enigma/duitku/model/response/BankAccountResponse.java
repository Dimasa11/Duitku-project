package com.enigma.duitku.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BankAccountResponse {
    private String mobileNumber;
    private String bankName;
    private Double balance;
    private String accountNo;
    private String errors;
}

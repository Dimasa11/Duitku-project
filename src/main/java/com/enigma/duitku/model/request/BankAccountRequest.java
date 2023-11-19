package com.enigma.duitku.model.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BankAccountRequest {

    private String mobileNumber;
    private String bankName;
    private Double balance;
    private String accountNo;
    private Double amount;
    private String receiver;
}

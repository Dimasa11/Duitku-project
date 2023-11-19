package com.enigma.duitku.model.request;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest  {

    private String mobileNumber;
    private String receiver;
    private String description;
    private String transactionType;
    private String bankName;
    private Double amount;
    private String userId;
    private String beneficiaryMobileNumber;
    private String billType;
    private String targetMobileNumber;
}

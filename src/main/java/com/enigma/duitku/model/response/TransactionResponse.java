package com.enigma.duitku.model.response;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransactionResponse {

    private String receiver;
    private String description;
    private String transactionType;
    private Double amount;
    private String errors;
    private Double balance;
}

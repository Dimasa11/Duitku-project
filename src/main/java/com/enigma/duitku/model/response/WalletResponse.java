package com.enigma.duitku.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class WalletResponse {

    private String targetMobileNumber;
    private Double amount;
    private String description;
}

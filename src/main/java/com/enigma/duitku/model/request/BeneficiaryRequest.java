package com.enigma.duitku.model.request;

import com.enigma.duitku.entity.Beneficiary;
import com.enigma.duitku.entity.Wallet;
import lombok.*;

import javax.persistence.Column;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BeneficiaryRequest {

    private String mobileNumber;
    private String name;
    private String accountNo;
    private String bankName;
    private List<Beneficiary> listOfBeneficiaries;
}

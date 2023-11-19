package com.enigma.duitku.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_beneficiary")
@Builder(toBuilder = true)
public class Beneficiary {

    @Id
    @GenericGenerator(strategy = "uuid2", name= "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "beneficiary_id")
    private String beneficiaryId;

    @Column(name= "mobile_number", length = 12, nullable = false, unique = true)
    @Size(min = 10, message = "Mobile number must be at least 10 characters long")
    private String mobileNumber;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(name="account_no", length = 15, unique = true)
    private String accountNo;

    @Column(name= "bank_name", length = 15, nullable = false)
    private String bankName;


    @Column(name = "status", length = 10, nullable = false)
    private String status;
}

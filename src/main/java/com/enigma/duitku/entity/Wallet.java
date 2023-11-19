package com.enigma.duitku.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_wallet")
@Builder(toBuilder = true)
public class Wallet {

    @Id
    @Column(name="wallet_id")
    private String id;

    @Column(nullable = false)
    private Double balance;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Transaction> listOfTransactions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Beneficiary> listOfBeneficiaries = new ArrayList<>();

    @ElementCollection
    private List<Bill> listofBills = new ArrayList<>();
}

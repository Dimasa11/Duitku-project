package com.enigma.duitku.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_transaction")
@Builder(toBuilder = true)
public class Transaction {

    @Id
    @GenericGenerator(strategy = "uuid2", name= "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    @Column(name = "local_date", nullable = false)
    private LocalDateTime localDate;

    @Column(name = "wallet_id", length = 50)
    private String walletId;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 30)
    private String description;

    @Column(nullable = false, length = 50)
    private String receiver;

    @Column(nullable = false, length = 50)
    private String type;
}

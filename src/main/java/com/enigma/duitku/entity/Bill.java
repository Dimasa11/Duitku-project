package com.enigma.duitku.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Bill {

    @Column(name = "consumer_no")
    private String consumerNo;

    @Column(name= "bill_type")
    private String billType;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 30)
    private String description;

    @Column(length = 20, nullable = false)
    private String receiver;

    @Column(name = "payment_date_time")
    private LocalDateTime paymentDateTime;

}

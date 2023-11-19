package com.enigma.duitku.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "m_address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Address {

    @Id
    @GenericGenerator(strategy = "uuid2", name= "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    @Column(name="street_name", length = 20)
    private String streetName;

    @Column(length = 15)
    private String city;

    @Column(length = 15)
    private String state;

    @Column(length = 6)
    private String postalCode;

}

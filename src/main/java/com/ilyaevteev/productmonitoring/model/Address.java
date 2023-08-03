package com.ilyaevteev.productmonitoring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "addresses")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "house")
    private String house;

    @Column(name = "postcode")
    private String postcode;

    @OneToMany(mappedBy = "address",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Store> stores;
}

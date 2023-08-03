package com.ilyaevteev.productmonitoring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "stores")
@Data
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "store",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<StoreProductPrice> storeProductPrices;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
}

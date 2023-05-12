package com.ilyaevteev.productmonitoring.model.auth;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;
}

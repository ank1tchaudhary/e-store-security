package com.estoresecurity.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private boolean isLoggedIn;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Collection<Role> roles=new ArrayList<>();

//    public User(User user) {
//        this.id=user.getId();
//        this.firstName=user.getFirstName();
//        this.lastName=user.getLastName();
//        this.username=user.getUsername();
//        this.password=user.getPassword();
//        this.isActive=user.isActive();
//        this.roles=user.getRoles();
//    }
}

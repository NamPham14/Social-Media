package com.social_media.identityservice.domain;

import com.social_media.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String password;
    private String email;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;


}

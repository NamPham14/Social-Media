package com.social_media.identityservice.domain.model.user.aggregate;


import com.social_media.identityservice.domain.shared.valueobject.UserId;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import com.social_media.identityservice.domain.model.user.exception.InvalidUserIdentityException;
import com.social_media.identityservice.domain.shared.valueobject.UserStatus;
import lombok.*;



import java.util.Set;

@Getter
public class User {
    private UserId id;
    private String username;
    private String password;
    private String email;
    private Set<RoleId> roles; // Reference by ID
    private UserStatus status;


    private User() {}

    public static User reconstruct(UserId id, String username, String password, String email, Set<RoleId> roles,UserStatus status) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.email = email;
        user.roles = roles;
        user.status = status;
        return user;
    }

    public static User register(String username, String email, String encodedPassword, Set<RoleId> roles) {
        if (username == null || username.length() < 4) {
            throw new InvalidUserIdentityException("Username must be at least 4 characters");
        }

        User user = new User();
        user.id = UserId.generate();
        user.username = username;
        user.email = email;
        user.password = encodedPassword;
        user.roles = roles;
        user.status = UserStatus.ACTIVE;

        return user;
    }

    public void banAccount(){
        this.status = UserStatus.BANNED;
    }

}

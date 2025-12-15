package com.technokratos.model;

import com.technokratos.dto.enums.Role;
import lombok.*;

import java.util.UUID;

//@Getter
//@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserEntity {
    private UUID uuid;
    private String username;
    private String email;
    private String password;
    private Role role;

    @Override
    public String toString() {
        return "UserEntity {username='%s', email='%s'}".formatted(username, email);
    }
}
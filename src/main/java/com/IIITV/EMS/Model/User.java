package com.IIITV.EMS.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    @JsonIgnore
    private String password;

    private Role role;

    private UserStatus status;
}
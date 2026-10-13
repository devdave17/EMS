
package com.IIITV.EMS.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
            message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8,
            message = "Password must contain at least 8 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Status is required")
    private UserStatus status;
}

package sum25.studentcode.backend.modules.Auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sum25.studentcode.backend.model.Role;

@Getter
@Setter
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "role is required")
    private Role role;
}
package sum25.studentcode.backend.modules.Auth.dto.request;

import lombok.*;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
}
package sum25.studentcode.backend.modules.Auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AuthResponse {
    String token;
    UserResponse user;
    String message;
}

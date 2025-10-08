package sum25.studentcode.backend.modules.Auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginJwtRequest {
    private String token;
}

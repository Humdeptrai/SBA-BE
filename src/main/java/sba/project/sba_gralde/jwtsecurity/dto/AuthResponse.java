package sba.project.sba_gralde.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
}

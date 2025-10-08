package sba.project.sba_gralde.jwtsecurity.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sba.project.sba_gralde.jwtsecurity.dto.AuthResponse;
import sba.project.sba_gralde.jwtsecurity.dto.LoginRequest;
import sba.project.sba_gralde.jwtsecurity.dto.RegisterRequest;
import sba.project.sba_gralde.jwtsecurity.entity.RefreshToken;
import sba.project.sba_gralde.jwtsecurity.service.AuthService;
import sba.project.sba_gralde.jwtsecurity.service.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
         authService.register(req);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");
        if (requestRefreshToken == null) {
            return ResponseEntity.badRequest().body("refreshToken is required");
        }

        return authService.findByToken(requestRefreshToken)
                .map(authService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user);
                    return ResponseEntity.ok(Map.of(
                            "accessToken", newAccessToken,
                            "refreshToken", requestRefreshToken,
                            "tokenType", "Bearer"
                    ));
                })
                .orElseGet(() -> ResponseEntity
                        .status(403)
                        .body(Map.of("error", "Refresh token not found")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");
        if (requestRefreshToken == null) return ResponseEntity.badRequest().body("refreshToken is required");

        return authService.findByToken(requestRefreshToken)
                .map(rt -> {
                    authService.deleteByUser(rt.getUser());
                    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
                }).orElseGet(() -> ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Invalid refresh token")));
    }
}

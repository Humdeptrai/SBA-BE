package sum25.studentcode.backend.modules.Auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.dto.request.LoginRequest;
import sum25.studentcode.backend.modules.Auth.dto.request.RegisterRequest;
import sum25.studentcode.backend.modules.Auth.dto.response.AuthResponse;
import sum25.studentcode.backend.modules.Auth.dto.response.UserResponse;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.security.jwt.JwtUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        // Register new user
        User user = userService.register(request);

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate JWT token
        String jwt = jwtUtils.generateToken(user);

        // Get user response
        UserResponse userResponse = userService.getUserByUsername(request.getUsername());

        // Build response

        return AuthResponse.builder()
                .token(jwt)
                .user(userResponse)
                .message("User registered successfully")
                .build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        // 1. Xác thực username/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Lấy user trực tiếp từ database, KHÔNG gọi getCurrentUser()
        User user = userService.getUserEntityByUsername(request.getUsername());

        // 3. Tạo JWT token
        String jwt = jwtUtils.generateToken(user);

        // 4. Lấy thông tin trả về
        UserResponse userResponse = userService.getUserByUsername(request.getUsername());

        return AuthResponse.builder()
                .token(jwt)
                .user(userResponse)
                .message("Login successful")
                .build();
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userService.getCurrentUserResponse();
    }
}

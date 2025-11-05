package sum25.studentcode.backend.modules.Auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.model.Role;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Wallet;
import sum25.studentcode.backend.modules.Auth.dto.request.LoginRequest;
import sum25.studentcode.backend.modules.Auth.dto.request.RegisterRequest;
import sum25.studentcode.backend.modules.Auth.dto.response.AuthResponse;
import sum25.studentcode.backend.modules.Auth.dto.response.UserResponse;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;
import sum25.studentcode.backend.security.jwt.JwtUtils;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final WalletRepository walletRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Register new user
            User user = userService.register(request);

            // Tạo wallet nếu role là TEACHER
            if (user.getRole() == Role.TEACHER) {
                Wallet wallet = Wallet.builder()
                        .user(user)
                        .balance(BigDecimal.valueOf(0000.00)) // Số dư ban đầu 1000 VND
                        .currency("VND")
                        .isActive(true)
                        .build();
                walletRepository.save(wallet);
            }

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
            AuthResponse response = AuthResponse.builder()
                    .token(jwt)
                    .user(userResponse)
                    .message("User registered successfully")
                    .build();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("Username already taken")) {
                AuthResponse errorResponse = AuthResponse.builder()
                        .message("Username already exists. Please choose a different username.")
                        .build();
                return ResponseEntity.status(409).body(errorResponse); // 409 Conflict
            }

            // Handle other registration errors
            AuthResponse errorResponse = AuthResponse.builder()
                    .message("Registration failed: " + e.getMessage())
                    .build();
            return ResponseEntity.status(400).body(errorResponse);
        }

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

package sum25.studentcode.backend.modules.Auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Role;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.dto.request.RegisterRequest;
import sum25.studentcode.backend.modules.Auth.dto.response.UserResponse;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Wallet.service.WalletService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.STUDENT)
                .build();
        // ✅ Tạo wallet mặc định cho user mới
        walletService.initializeNewWallet(user);

        return userRepository.save(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return convertToUserResponse(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserResponse getCurrentUserResponse() {
        User user = getCurrentUser();
        return convertToUserResponse(user);
    }

    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUsername(user.getUsername());
        response.setUserId(user.getUserId().toString());
        return response;
    }
}

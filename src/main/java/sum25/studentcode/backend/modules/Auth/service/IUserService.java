package sum25.studentcode.backend.modules.Auth.service;

import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.dto.request.RegisterRequest;
import sum25.studentcode.backend.modules.Auth.dto.response.UserResponse;
import java.util.List;

public interface IUserService {
    User register(RegisterRequest request);
    UserResponse getUserByUsername(String username);
    User getCurrentUser();
    UserResponse getCurrentUserResponse();
    User getUserEntityByUsername(String username);
    List<UserResponse> getAllUsers();
}

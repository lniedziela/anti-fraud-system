package antifraud.model.user;

import antifraud.model.user.dto.UserRequest;
import antifraud.model.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User map(UserRequest userRequest) {
        return new User(
                userRequest.getId(),
                userRequest.getName(),
                userRequest.getUsername(),
                userRequest.getPassword()
        );
    }

    public UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRole()
        );
    }
}

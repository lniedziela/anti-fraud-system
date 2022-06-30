package antifraud.controller;

import antifraud.model.role.dto.RoleRequest;
import antifraud.model.user.dto.UserLockRequest;
import antifraud.model.user.dto.UserRequest;
import antifraud.model.user.dto.UserResponse;
import antifraud.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse user(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "User with given username already exist!"));
    }

    @GetMapping("/list")
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPPORT"})
    public List<UserResponse> list() {
        return userService.listAll();
    }

    @DeleteMapping("/user/{username}")
    @Secured("ROLE_ADMINISTRATOR")
    public Map<String, String> delete(@PathVariable String username) {
        if (userService.deleteByUsername(username)) {
            return Map.of(
                    "username", username,
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with given name does not exist!"
            );
        }
    }

    @PutMapping("/role")
    @Secured("ROLE_ADMINISTRATOR")
    public UserResponse role(@RequestBody RoleRequest request) {
        return userService.changeRole(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/access")
    @Secured("ROLE_ADMINISTRATOR")
    public Map<String, String> lock(@RequestBody UserLockRequest request) {
        return userService.unlockUser(request);
    }
}

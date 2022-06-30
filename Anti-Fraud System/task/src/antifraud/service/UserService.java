package antifraud.service;

import antifraud.exception.InvalidRoleException;
import antifraud.exception.InvalidStatusException;
import antifraud.exception.TheSameRoleException;
import antifraud.model.exception.ExceptionDTO;
import antifraud.model.role.Role;
import antifraud.model.role.dto.RoleRequest;
import antifraud.model.user.User;
import antifraud.model.user.UserMapper;
import antifraud.model.user.dto.UserLockRequest;
import antifraud.model.user.dto.UserRequest;
import antifraud.model.user.dto.UserResponse;
import antifraud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static antifraud.model.role.Role.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found."));
    }

    @Transactional
    public Optional<UserResponse> createUser(UserRequest userRequest) {
        if (userRepository.existsByUsernameIgnoreCase(userRequest.getUsername())) {
            return Optional.empty();
        } else {
            var user = userMapper.map(userRequest);
            setUserDefaultRole(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            var savedUser = userRepository.save(user);
            return Optional.of(userMapper.map(savedUser));
        }
    }

    private void setUserDefaultRole(User user) {
        if (userRepository.count() == 0) {
            user.setRole(ADMINISTRATOR);
            user.setAccountNonLocked(true);
        } else {
            user.setRole(MERCHANT);
        }
    }

    public List<UserResponse> listAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .sorted(Comparator.comparing(UserResponse::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteByUsername(String username) {
        return userRepository.deleteByUsernameIgnoreCase(username) == 1;
    }

    @Transactional
    public Optional<UserResponse> changeRole(RoleRequest request) {
        var optionalUser = userRepository.findUserByUsernameIgnoreCase(request.getUsername());
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        var role = request.getRole();
        var user = optionalUser.get();
        validateRole(role, user);
        user.setRole(role);
        var savedUSer = userRepository.save(user);
        return Optional.of(userMapper.map(savedUSer));
    }

    private void validateRole(Role role, User user) {
        if (Objects.equals(user.getRole(), role)) {
//            throw new TheSameRoleException();
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (Objects.equals(role, ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(role, SUPPORT) && !Objects.equals(role, MERCHANT)) {
            throw new InvalidRoleException();
        }
    }

    @Transactional
    public Map<String, String> unlockUser(UserLockRequest request) {
        var operation = request.getOperation();
        var username = request.getUsername();

        var user = userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found."));

        if (Objects.equals(operation, "LOCK")) {
            user.setAccountNonLocked(false);
            return Map.of("status", "User " + username + " locked!");
        } else if (Objects.equals(operation, "UNLOCK")) {
            user.setAccountNonLocked(true);
            return Map.of("status", "User " + username + " unlocked!");
        } else {
            throw new InvalidStatusException();
        }
    }

    @ExceptionHandler({InvalidStatusException.class, InvalidRoleException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ExceptionDTO handleInvalidRequestException(RuntimeException e) {
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler(TheSameRoleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ExceptionDTO handleTheSameRoleException(RuntimeException e) {
        return new ExceptionDTO(e.getMessage());
    }
}

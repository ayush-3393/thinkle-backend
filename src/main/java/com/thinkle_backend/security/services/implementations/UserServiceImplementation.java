package com.thinkle_backend.security.services.implementations;

import com.thinkle_backend.models.ThinkleUsers;
import com.thinkle_backend.repositories.ThinkleUsersRepository;
import com.thinkle_backend.security.dtos.AuthResponse;
import com.thinkle_backend.security.dtos.LoginRequest;
import com.thinkle_backend.security.dtos.SignUpRequest;
import com.thinkle_backend.security.dtos.UserInfo;
import com.thinkle_backend.security.exceptions.DuplicateResourceException;
import com.thinkle_backend.security.exceptions.ResourceNotFoundException;
import com.thinkle_backend.security.exceptions.UnauthorizedException;
import com.thinkle_backend.security.services.JwtService;
import com.thinkle_backend.security.services.UserService;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserService {

    private final ThinkleUsersRepository thinkleUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    // Password validation pattern (at least 8 chars, one uppercase, one lowercase, one digit)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"
    );

    /**
     * Handles user registration with comprehensive validation and error handling.
     *
     * @param signUpRequest The registration request containing user details
     * @return AuthResponse with token and user information upon successful registration
     * @throws DuplicateResourceException if user with email already exists
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public AuthResponse handleSignup(SignUpRequest signUpRequest) {
        log.info("Processing signup request for email: {}", signUpRequest.getEmail());

        try {
            // Validate input data
            validateSignUpRequest(signUpRequest);

            // Check if user already exists
            checkUserExists(signUpRequest.getEmail(), signUpRequest.getUsername());

            // Create and save new user
            ThinkleUsers newUser = createThinkleUser(signUpRequest);
            ThinkleUsers savedUser = saveUser(newUser);

            // Generate JWT token
            String token = jwtService.generateToken(savedUser.getEmail());

            // Create user info DTO
            UserInfo userInfo = mapToUserInfo(savedUser);

            log.info("User registered successfully with ID: {}", savedUser.getId());
            return new AuthResponse(token, "Registration successful", userInfo);

        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation during signup", e);
            throw new DuplicateResourceException("User with this email or username already exists");
        } catch (Exception e) {
            log.error("Unexpected error during signup for email: {}", signUpRequest.getEmail(), e);
            throw new RuntimeException("Registration failed. Please try again later.", e);
        }

    }

    /**
     * Handles user login with authentication and proper error handling.
     *
     * @param loginRequest The login request containing email and password
     * @return AuthResponse with token and user information upon successful login
     * @throws UnauthorizedException if authentication fails
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse handleLogin(LoginRequest loginRequest) {
        log.info("Processing login request for email: {}", loginRequest.getEmail());

        try {
            // Validate input data
            validateLoginRequest(loginRequest);

            // Authenticate user
            Authentication authentication = authenticateUser(loginRequest);

            if (!authentication.isAuthenticated()) {
                log.warn("Authentication failed for email: {}", loginRequest.getEmail());
                throw new UnauthorizedException("Authentication failed");
            }

            // Retrieve user from database
            ThinkleUsers user = findUserByEmail(loginRequest.getEmail());

            // Generate JWT token
            String token = jwtService.generateToken(user.getEmail());

            // Create user info DTO
            UserInfo userInfo = mapToUserInfo(user);

            log.info("User logged in successfully: {}", user.getEmail());
            return new AuthResponse(token, "Login successful", userInfo);

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {}", loginRequest.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        } catch (DisabledException e) {
            log.warn("Account disabled for email: {}", loginRequest.getEmail());
            throw new UnauthorizedException("Account is disabled. Please contact support.");
        } catch (LockedException e) {
            log.warn("Account locked for email: {}", loginRequest.getEmail());
            throw new UnauthorizedException("Account is locked. Please contact support.");
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", loginRequest.getEmail(), e);
            throw new RuntimeException("Login failed. Please try again later.", e);
        }
    }

    /**
     * Validates the signup request data.
     *
     * @param request The signup request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSignUpRequest(SignUpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Signup request cannot be null");
        }

        // Validate username
        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (!request.getUsername().matches("^[a-zA-Z0-9_.-]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, dots, hyphens and underscores");
        }

        // Validate email
        validateEmail(request.getEmail());

        // Validate password
        validatePassword(request.getPassword());
    }

    /**
     * Validates the login request data.
     *
     * @param request The login request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }

        validateEmail(request.getEmail());

        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    /**
     * Validates email format.
     *
     * @param email The email to validate
     * @throws IllegalArgumentException if email is invalid
     */
    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
        if (!EMAIL_PATTERN.matcher(email.toLowerCase()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates password strength.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException if password is weak
     */
    private void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
            );
        }
    }

    /**
     * Checks if a user with the given email or username already exists.
     * Uses the existing repository method for email check and manual validation for duplicates.
     *
     * @param email The email to check
     * @param username The username to check
     * @throws DuplicateResourceException if user exists
     */
    private void checkUserExists(String email, String username) {
        // Check if user exists by email (more efficient with existsByEmail)
        if (thinkleUsersRepository.existsByEmail(email.toLowerCase())) {
            log.warn("Attempt to register with existing email: {}", email);
            throw new DuplicateResourceException("User with this email already exists");
        }

        // Check if user exists by username
        if (thinkleUsersRepository.existsByUsername(username.trim())) {
            log.warn("Attempt to register with existing username: {}", username);
            throw new DuplicateResourceException("User with this username already exists");
        }

        log.debug("Email and username availability check passed for: {} / {}", email, username);
    }

    /**
     * Creates a new ThinkleUsers entity from signup request.
     *
     * @param request The signup request
     * @return The created ThinkleUsers entity
     */
    private ThinkleUsers createThinkleUser(SignUpRequest request) {
        ThinkleUsers user = new ThinkleUsers();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return user;
    }

    /**
     * Saves the user to the database with error handling.
     *
     * @param user The user to save
     * @return The saved user
     * @throws RuntimeException if save operation fails
     */
    private ThinkleUsers saveUser(ThinkleUsers user) {
        try {
            return thinkleUsersRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving user: {}", e.getMessage());

            // Check which constraint was violated
            String errorMessage = e.getMessage().toLowerCase();
            if (errorMessage.contains("email")) {
                throw new DuplicateResourceException("User with this email already exists");
            } else if (errorMessage.contains("username")) {
                throw new DuplicateResourceException("User with this username already exists");
            } else {
                throw new DuplicateResourceException("User with this email or username already exists");
            }
        } catch (Exception e) {
            log.error("Error saving user to database", e);
            throw new RuntimeException("Failed to create user account", e);
        }
    }

    /**
     * Authenticates the user using Spring Security.
     * Authentication is done using email as username (as defined in UserPrincipal).
     *
     * @param loginRequest The login request
     * @return The authentication result
     */
    private Authentication authenticateUser(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail().toLowerCase().trim(),
                        loginRequest.getPassword()
                )
        );
    }

    /**
     * Finds a user by email using the existing repository method.
     *
     * @param email The user's email
     * @return The found user
     * @throws ResourceNotFoundException if user not found
     */
    private ThinkleUsers findUserByEmail(String email) {
        return thinkleUsersRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });
    }

    /**
     * Maps a ThinkleUsers entity to UserInfo DTO.
     *
     * @param user The ThinkleUsers entity
     * @return The UserInfo DTO
     */
    private UserInfo mapToUserInfo(ThinkleUsers user) {
        return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    /**
     * Retrieves user information by email (utility method for potential future use).
     *
     * @param email The user's email
     * @return UserInfo DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(String email) {
        log.debug("Retrieving user info for email: {}", email);
        ThinkleUsers user = findUserByEmail(email);
        return mapToUserInfo(user);
    }

    /**
     * Checks if a user exists by email (utility method for potential future use).
     *
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userExistsByEmail(String email) {
        return thinkleUsersRepository.existsByEmail(email.toLowerCase());
    }

    /**
     * Checks if a user exists by username (utility method for potential future use).
     *
     * @param username The username to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userExistsByUsername(String username) {
        return thinkleUsersRepository.existsByUsername(username.trim());
    }
}

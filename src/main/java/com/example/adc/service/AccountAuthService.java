package com.example.adc.service;

import com.example.adc.dto.account.CreateAccountRequest;
import com.example.adc.dto.account.CreateAccountResponse;
import com.example.adc.dto.account.ErrorResponse;
import com.example.adc.dto.login.LoginRequest;
import com.example.adc.dto.login.LoginResponse;
import com.example.adc.error.API_ERRORS;
import com.example.adc.error.ApiError;
import com.example.adc.model.Session;
import com.example.adc.model.User;
import com.example.adc.repository.SessionRepository;
import com.example.adc.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

public class AccountAuthService {

    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "BOFFICER", "ADMIN");

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AccountAuthService() {
        this.userRepository = new UserRepository();
        this.sessionRepository = new SessionRepository();
    }

    public Object createAccount(CreateAccountRequest request) {
        if (request == null || request.input == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        CreateAccountRequest.CreateAccountInput input = request.input;

        if (isBlank(input.username) ||
                isBlank(input.password) ||
                isBlank(input.confirmation) ||
                isBlank(input.email) ||
                isBlank(input.phone) ||
                isBlank(input.address) ||
                isBlank(input.role)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        if (!input.password.equals(input.confirmation)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        if (!ALLOWED_ROLES.contains(input.role)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        if (userRepository.existsByUsername(input.username)) {
            return error(API_ERRORS.USER_ALREADY_EXISTS);
        }

        String passwordHash = BCrypt.hashpw(input.password, BCrypt.gensalt());

        User user = new User(
                UUID.randomUUID().toString(),
                input.username,
                passwordHash,
                input.email,
                input.phone,
                input.address,
                input.role
        );

        userRepository.save(user);

        CreateAccountResponse response = new CreateAccountResponse();
        response.status = "success";
        response.data = new CreateAccountResponse.AccountData();
        response.data.username = user.getUsername();
        response.data.role = user.getRole();

        return response;
    }

    public Object login(LoginRequest request) {
        if (request == null || request.input == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        LoginRequest.LoginInput input = request.input;

        if (isBlank(input.username) || isBlank(input.password)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        User user = userRepository.findByUsername(input.username);

        if (user == null) {
            return error(API_ERRORS.USER_NOT_FOUND);
        }

        boolean passwordMatches = BCrypt.checkpw(input.password, user.getPasswordHash());

        if (!passwordMatches) {
            return error(API_ERRORS.INVALID_CREDENTIALS);
        }

        String tokenId = UUID.randomUUID().toString();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(5, ChronoUnit.MINUTES);

        Session session = new Session(
                tokenId,
                user.getUserId(),
                user.getRole(),
                issuedAt.toString(),
                expiresAt.toString()
        );

        sessionRepository.save(session);

        LoginResponse response = new LoginResponse();
        response.status = "success";
        response.data = new LoginResponse.LoginData();
        response.data.token = new LoginResponse.TokenData();
        response.data.token.tokenId = session.getTokenId();
        response.data.token.userId = session.getUserId();
        response.data.token.role = session.getRole();
        response.data.token.issuedAt = session.getIssuedAt();
        response.data.token.expiresAt = session.getExpiresAt();

        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private ErrorResponse error(ApiError apiError) {
        ErrorResponse response = new ErrorResponse();
        response.status = apiError.getCode();
        response.data = apiError.getMessage();
        return response;
    }
}

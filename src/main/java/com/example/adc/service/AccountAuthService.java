package com.example.adc.service;

import com.example.adc.error.API_ERRORS;
import com.example.adc.error.ApiError;
import com.example.adc.model.Session;
import com.example.adc.model.User;
import com.example.adc.repository.SessionRepository;
import com.example.adc.repository.UserRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;


//*
// This class is the service for the account authentication service.
// It handles the business logic for the account authentication service.
// It is used to create a new account, login, show users, delete an account, modify an account, show authentication sessions, show user role, change user role, change user password, and logout.
// */
public class AccountAuthService {

    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "BOFFICER", "ADMIN");

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AccountAuthService() {
        this.userRepository = new UserRepository();
        this.sessionRepository = new SessionRepository();
    }

    public Response createAccount(JsonObject request) {
        JsonObject input = getObject(request, "input");
        if (request == null || input == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        String username = getString(input, "username");
        String password = getString(input, "password");
        String confirmation = getString(input, "confirmation");
        String phone = getString(input, "phone");
        String address = getString(input, "address");
        String role = getString(input, "role");

        if (isBlank(username) ||
                isBlank(password) ||
                isBlank(confirmation) ||
                isBlank(phone) ||
                isBlank(address) ||
                isBlank(role)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        if (!password.equals(confirmation)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        if (!ALLOWED_ROLES.contains(role)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        if (userRepository.existsByUsername(username)) {
            return error(API_ERRORS.USER_ALREADY_EXISTS);
        }

        if (!isValidEmail(username)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        User user = new User(username, password, phone, address, role);
        userRepository.save(user);

        return success(Json.createObjectBuilder()
                .add("username", user.getUsername())
                .add("role", user.getRole())
                .build());
    }

    public Response login(JsonObject request) {
        JsonObject input = getObject(request, "input");
        if (request == null || input == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        String username = getString(input, "username");
        String password = getString(input, "password");

        if (isBlank(username) || isBlank(password)) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return error(API_ERRORS.USER_NOT_FOUND);
        }

        if (!password.equals(user.getPassword())) {
            return error(API_ERRORS.INVALID_CREDENTIALS);
        }

        String tokenId = UUID.randomUUID().toString();
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 900;

        Session session = new Session(
                tokenId,
                user.getUsername(),
                user.getRole(),
                issuedAt,
                expiresAt
        );

        sessionRepository.save(session);

        JsonObject token = Json.createObjectBuilder()
                .add("tokenId", session.getTokenId())
                .add("username", session.getUsername())
                .add("role", session.getRole())
                .add("issuedAt", session.getIssuedAt())
                .add("expiresAt", session.getExpiresAt())
                .build();

        return success(Json.createObjectBuilder()
                .add("token", token)
                .build());
    }

    public Response showUsers(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String tokenId = getString(token, "tokenId");
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            if (!session.getRole().equals("BOFFICER") && !session.getRole().equals("ADMIN")) {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            JsonArrayBuilder users = Json.createArrayBuilder();
            for (User user : userRepository.findAll()) {
                users.add(Json.createObjectBuilder()
                        .add("username", user.getUsername())
                        .add("role", user.getRole())
                        .build());
            }

            return success(Json.createObjectBuilder()
                    .add("users", users.build())
                    .build());

        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response delete(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String tokenId = getString(token, "tokenId");
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            if (!session.getRole().equals("ADMIN")) {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            String username = getString(input, "username");
            if (isBlank(username)) {
                return error(API_ERRORS.INVALID_INPUT);
            }

            User userToDelete = userRepository.findByUsername(username);
            if (userToDelete == null) {
                return error(API_ERRORS.USER_NOT_FOUND);
            }

            userRepository.deleteByUsername(username);
            for (Session currentSession : sessionRepository.findAll()) {
                if (currentSession.getUsername().equals(username)) {
                    sessionRepository.delete(currentSession);
                }
            }

            return successMessage("Account deleted successfully");
        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response modifyAttribute(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String tokenId = getString(token, "tokenId");
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            String username = getString(input, "username");
            JsonObject attributes = getObject(input, "attributes");
            if (isBlank(username) || attributes == null) {
                return error(API_ERRORS.INVALID_INPUT);
            }

            String phone = getString(attributes, "phone");
            String address = getString(attributes, "address");
            boolean hasPhone = !isBlank(phone);
            boolean hasAddress = !isBlank(address);

            if (!hasPhone && !hasAddress) {
                return error(API_ERRORS.INVALID_INPUT);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return error(API_ERRORS.USER_NOT_FOUND);
            }

            String role = session.getRole();
            switch (role) {
                case "ADMIN":
                    break;
                case "BOFFICER":
                    if (user.getRole().equals("ADMIN")) {
                        return error(API_ERRORS.UNAUTHORIZED);
                    }
                    if (user.getRole().equals("BOFFICER") && !session.getUsername().equals(username)) {
                        return error(API_ERRORS.UNAUTHORIZED);
                    }
                    break;
                case "USER":
                    if (!session.getUsername().equals(username)) {
                        return error(API_ERRORS.UNAUTHORIZED);
                    }
                    break;
                default:
                    return error(API_ERRORS.UNAUTHORIZED);
            }

            if (hasPhone) {
                userRepository.updatePhone(username, phone);
            }
            if (hasAddress) {
                userRepository.updateAddress(username, address);
            }

            return successMessage("Updated successfully");
        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response showAuthSessions(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String tokenId = getString(token, "tokenId");
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            if (!session.getRole().equals("ADMIN")) {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            JsonArrayBuilder sessions = Json.createArrayBuilder();
            for (Session currentSession : sessionRepository.findAll()) {
                sessions.add(Json.createObjectBuilder()
                        .add("tokenId", currentSession.getTokenId())
                        .add("username", currentSession.getUsername())
                        .add("role", currentSession.getRole())
                        .add("expiresAt", currentSession.getExpiresAt())
                        .build());
            }

            return success(Json.createObjectBuilder()
                    .add("sessions", sessions.build())
                    .build());

        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response showUserRole(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String tokenId = getString(token, "tokenId");
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            if (!session.getRole().equals("ADMIN") && !session.getRole().equals("BOFFICER")) {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            String username = getString(input, "username");
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return error(API_ERRORS.USER_NOT_FOUND);
            }
            if (user.getUsername() == null) {
                return error(API_ERRORS.INVALID_INPUT);
            }

            return success(Json.createObjectBuilder()
                    .add("username", user.getUsername())
                    .add("role", user.getRole())
                    .build());

        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response changeUserRole(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String tokenId = getString(token, "tokenId");
            String username = getString(input, "username");
            String newRole = getString(input, "newRole");

            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }
            if (isBlank(username) || isBlank(newRole)) {
                return error(API_ERRORS.INVALID_INPUT);
            }
            if (!ALLOWED_ROLES.contains(newRole)) {
                return error(API_ERRORS.INVALID_INPUT);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            if (!session.getRole().equals("ADMIN")) {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return error(API_ERRORS.USER_NOT_FOUND);
            }

            userRepository.updateRole(username, newRole);
            for (Session currentSession : sessionRepository.findAll()) {
                if (currentSession.getUsername().equals(username)
                        && currentSession.getExpiresAt() > now) {
                    currentSession.setRole(newRole);
                    sessionRepository.save(currentSession);
                }
            }

            return successMessage("Role updated successfully");
        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response changeUserPassword(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String username = getString(input, "username");
            String oldPassword = getString(input, "oldPassword");
            String newPassword = getString(input, "newPassword");
            String tokenId = getString(token, "tokenId");

            if (isBlank(username) || isBlank(oldPassword) || isBlank(newPassword)) {
                return error(API_ERRORS.INVALID_INPUT);
            }
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return error(API_ERRORS.INVALID_CREDENTIALS);
            }

            if (!session.getUsername().equals(username)) {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            if (!oldPassword.equals(user.getPassword())) {
                return error(API_ERRORS.INVALID_CREDENTIALS);
            }

            userRepository.updatePassword(username, newPassword);
            return successMessage("Password changed successfully");
        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    public Response logout(JsonObject request) {
        JsonObject input = getObject(request, "input");
        JsonObject token = getObject(request, "token");
        if (request == null || input == null || token == null) {
            return error(API_ERRORS.INVALID_INPUT);
        }

        try {
            String username = getString(input, "username");
            String tokenId = getString(token, "tokenId");

            if (isBlank(username)) {
                return error(API_ERRORS.INVALID_INPUT);
            }
            if (isBlank(tokenId)) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            Session session = sessionRepository.findByTokenId(tokenId);
            if (session == null) {
                return error(API_ERRORS.INVALID_TOKEN);
            }

            long now = Instant.now().getEpochSecond();
            if (now > session.getExpiresAt()) {
                sessionRepository.delete(session);
                return error(API_ERRORS.TOKEN_EXPIRED);
            }

            String role = session.getRole();
            if (role.equals("ADMIN")) {
                if (username.equals(session.getUsername())) {
                    sessionRepository.delete(session);
                } else {
                    for (Session currentSession : sessionRepository.findAll()) {
                        if (currentSession.getUsername().equals(username)) {
                            sessionRepository.delete(currentSession);
                        }
                    }
                }
            } else if (session.getUsername().equals(username)) {
                sessionRepository.delete(session);
            } else {
                return error(API_ERRORS.UNAUTHORIZED);
            }

            return successMessage("Logout succesful");
        } catch (Exception e) {
            return error(API_ERRORS.FORBIDDEN);
        }
    }

    private boolean isValidEmail(String username) {
        if (username == null) {
            return false;
        }
    
        return username.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private JsonObject getObject(JsonObject source, String key) {
        if (source == null || key == null || !source.containsKey(key) || source.isNull(key)) {
            return null;
        }

        try {
            return source.getJsonObject(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    private String getString(JsonObject source, String key) {
        if (source == null || key == null || !source.containsKey(key) || source.isNull(key)) {
            return null;
        }

        try {
            return source.getString(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    private Response success(JsonObject data) {
        JsonObject body = Json.createObjectBuilder()
                .add("status", "success")
                .add("data", data)
                .build();
        return Response.ok(body).build();
    }

    private Response successMessage(String message) {
        JsonObject data = Json.createObjectBuilder()
                .add("message", message)
                .build();
        return success(data);
    }

    private Response error(ApiError apiError) {
        JsonObject body = Json.createObjectBuilder()
                .add("status", apiError.getCode())
                .add("data", apiError.getMessage())
                .build();
        return Response.ok(body).build();
    }
}

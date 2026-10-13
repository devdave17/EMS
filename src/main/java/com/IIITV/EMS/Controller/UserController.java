package com.IIITV.EMS.Controller;

import com.IIITV.EMS.Model.IdempotencyRecord;
import com.IIITV.EMS.Model.User;
import com.IIITV.EMS.Repository.IdempotencyRepository;
import com.IIITV.EMS.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final IdempotencyRepository idempotencyRepository;

    public UserController(UserService userService, IdempotencyRepository idempotencyRepository) {
        this.userService = userService;
        this.idempotencyRepository = idempotencyRepository;
    }


    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid
            @RequestBody User user,
            @RequestHeader(value = "Idempotency-Key", required = false)
            String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            User savedUser = userService.saveUser(user);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedUser.getId())
                    .toUri();

            return ResponseEntity.created(location).body(savedUser);
        }

        String requestHash;
        try {
            String requestData = user.getName() + "|" +
                    user.getEmail() + "|" +
                    user.getPassword() + "|" +
                    user.getRole() + "|" +
                    user.getStatus();

            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(requestData.getBytes(StandardCharsets.UTF_8));

            requestHash = HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not calculate request hash");
        }

        var existing = idempotencyRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            IdempotencyRecord record = existing.get();

            if (!requestHash.equals(record.getRequestHash())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency-Key reused with a different request");
            }

            User originalUser = userService.getUserById(record.getResourceId());

            return ResponseEntity.ok()
                    .header("Idempotency-Replayed", "true")
                    .body(originalUser);
        }

        User savedUser = userService.saveUser(user);

        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(idempotencyKey);
        record.setRequestHash(requestHash);
        record.setResourceId(savedUser.getId());
        record.setCreatedAt(LocalDateTime.now());

        idempotencyRepository.save(record);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedUser);
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        if (!acceptsJson(
                request.getHeader("Accept"))) {
            return ResponseEntity.status(406).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Valid
            @PathVariable String id,
            @RequestBody User user,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {

        User currentUser = userService.getUserById(id);

        String currentEtag = "\"" + Integer.toHexString(
                java.util.Objects.hash(
                        currentUser.getId(),
                        currentUser.getName(),
                        currentUser.getEmail(),
                        currentUser.getRole(),
                        currentUser.getStatus()
                )
        ) + "\"";

        if (ifMatch != null && !ifMatch.equals(currentEtag)) {
            return ResponseEntity.status(412)
                    .eTag(currentEtag)
                    .build();
        }

        User updatedUser = userService.updateUser(id, user);

        String updatedEtag = "\"" + Integer.toHexString(
                java.util.Objects.hash(
                        updatedUser.getId(),
                        updatedUser.getName(),
                        updatedUser.getEmail(),
                        updatedUser.getRole(),
                        updatedUser.getStatus()
                )
        ) + "\"";

        return ResponseEntity.ok()
                .eTag(updatedEtag)
                .body(updatedUser);
    }



    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable String id,
            @RequestHeader(value = "If-None-Match", required = false)
            String ifNoneMatch,
    HttpServletRequest request) {
        if (!acceptsJson(
                request.getHeader("Accept"))) {
            return ResponseEntity.status(406).build();
        }
        User user = userService.getUserById(id);

        String etag = "\"" + Integer.toHexString(
                java.util.Objects.hash(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus()
                )
        ) + "\"";

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(304)
                    .eTag(etag)
                    .build();
        }

        return ResponseEntity.ok()
                .eTag(etag)
                .header("Cache-Control", "private, no-cache")
                .body(user);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.noContent()
                .header("Allow", "GET, POST, PUT, DELETE, OPTIONS")
                .build();
    }

    private boolean acceptsJson(String accept) {
        return accept == null
                || accept.isBlank()
                || accept.contains("*/*")
                || accept.contains("application/json");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}

package com.roomate.app.controller;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResourceController {
    private final UserServiceImplementation userService;

    public ResourceController(UserServiceImplementation userService) {
        this.userService = userService;
    }

    @PutMapping("/additional_info")
    public ResponseEntity<?> addAdditionalInfo(@AuthenticationPrincipal Jwt jwt, @RequestBody UserEntity updatedDetails) {
        String userId = jwt.getSubject();
        UserEntity updateUser = userService.updateUserProfile(userId, updatedDetails);

        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @GetMapping("/profile-status")
    public ResponseEntity<Map<String, Boolean>> getProfileCompletionStatus(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        boolean isComplete = userService.isProfileCompleteInDatabase(auth0UserId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isComplete", isComplete);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/public_resource")
    public ResponseEntity<String> getPublicResource() {
        return ResponseEntity.ok("This is a public resource from Spring Boot.");
    }

    @GetMapping("/create_or_find_user")
    public ResponseEntity<String> createOrFindUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("first_name");
        String lastName = jwt.getClaimAsString("last_name");

        UserEntity currentUser = userService.findOrCreateUserByAuthId(userId, email, firstName, lastName);

        return ResponseEntity.ok("This is a secret resource accessed by user: " + firstName +
                " (Auth0 UID: " + userId + ", Local DB ID: " + currentUser.getId() + ", Email: " + email + ")");
    }

     @PutMapping("/profile")
     public ResponseEntity<UserEntity> updateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UserEntity updatedDetails) {
         String userId= jwt.getSubject();
         UserEntity updatedUser = userService.updateUserProfile(userId, updatedDetails);
         return ResponseEntity.ok(updatedUser);
     }

//    @PostMapping("/provision-user")
//    public ResponseEntity<UserEntity> provisionUser(@AuthenticationPrincipal Jwt jwt) {
//        String userId = jwt.getSubject();
//        String userEmail = jwt.getClaimAsString("email");
//        String firstName = jwt.getClaimAsString("given_name");
//        String lastName = jwt.getClaimAsString("family_name");
//
//        if (firstName == null && jwt.getClaimAsString("name") != null) {
//            String fullName = jwt.getClaimAsString("name");
//            String[] parts = fullName.split(" ", 2);
//            firstName = parts.length > 0 ? parts[0] : null;
//            lastName = parts.length > 1 ? parts[1] : null;
//        }
//
//
//        UserEntity currentUser = userService.findOrCreateUserByAuthId(userId, userEmail, firstName, lastName);
//        return new ResponseEntity<>(currentUser, HttpStatus.OK);
//    }

}
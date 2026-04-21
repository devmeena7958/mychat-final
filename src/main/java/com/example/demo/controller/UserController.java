package com.example.demo.controller;

import com.example.demo.Model.MessageType;
import com.example.demo.Model.Users;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.JwtService;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {

        if(userRepo.existsByRollNo(user.getRollNo())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "User with this roll number already exists"));
        }

        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Users user) {

        Users currUser = userRepo.findByRollNo(user.getRollNo());

        if(currUser != null && currUser.getPassword().equals(user.getPassword())){

            String token = jwtService.generateToken(currUser.getUserName());

            currUser.setStatus(MessageType.ONLINE);
            return ResponseEntity.ok(
                    Map.of(
                        "message", "Login successful",
                        "userName", currUser.getUserName(),
                        "token", token
            ));
        }
        else if(currUser != null){
             return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid password"));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "User not found"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAllByStatus(MessageType.ONLINE));
    }

}

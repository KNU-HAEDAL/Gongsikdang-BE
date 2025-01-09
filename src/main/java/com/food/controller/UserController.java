package com.food.controller;

import com.food.config.jwt.token.JwtUtil;
import com.food.dto.UserDTO;
import com.food.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "유저 로그인 및 회원가입 API")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "로그인", description = "유저 로그인 API. ID와 비밀번호를 통해 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        boolean isAuthenticated = userService.authenticateUser(userDTO);
        Map<String, String> response = new HashMap<>();

        if (isAuthenticated) {
            String token = jwtUtil.generateToken(userDTO.getId());
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }

    @Operation(summary = "회원가입", description = "유저 회원가입 API. ID와 비밀번호, 기타 정보를 통해 회원가입합니다.")
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserDTO userDTO) {
        boolean isRegistered = userService.registerUser(userDTO);
        Map<String, String> response = new HashMap<>();

        if (isRegistered) {
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Registration failed. User may already exist.");
            return ResponseEntity.status(400).body(response);
        }
    }

    @Operation(summary = "ID 중복 확인", description = "회원가입 시 ID의 중복 여부를 확인합니다.")
    @PostMapping("/checkDuplicateId")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateId(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        Map<String, Boolean> response = new HashMap<>();

        if (id == null || id.isEmpty()) {
            response.put("isDuplicate", false);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            boolean isDuplicate = userService.isIdDuplicated(id);
            response.put("isDuplicate", isDuplicate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("isDuplicate", false);
            return ResponseEntity.status(500).body(response);
        }
    }
}
package com.food.controller;

import com.food.config.jwt.token.JwtUtil;
import com.food.dto.UserDTO;
import com.food.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "유저 로그인 및 회원가입 API")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 생성자 주입 방식 사용
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "로그인",
            description = "유저 로그인 API. ID와 비밀번호를 통해 로그인합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"id\": \"hello\", \"password\": \"1234\" }")
                    )
            )
    )
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        if (userService.authenticateUser(userDTO)) {
            String token = jwtUtil.generateToken(userDTO.getId());
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Operation(
            summary = "회원가입",
            description = "유저 회원가입 API. ID와 비밀번호, 기타 정보를 통해 회원가입합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"id\": \"hello\", \"password\": \"1234\", \"name\": \"안뇽\" }")
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        System.out.println("🔥 회원가입 API 호출됨: " + userDTO);

        if (userService.registerUser(userDTO)) {
            response.put("message", "Registration successful");
            System.out.println("🔥 회원가입 성공!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Registration failed. User may already exist.");
            System.out.println("🔥 회원가입 실패! 예외 발생 아마 db");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(
            summary = "ID 중복 확인",
            description = "회원가입 시 ID의 중복 여부를 확인합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"id\": \"hello\"}")
                    )
            )
    )
    @PostMapping("/checkDuplicateId")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateId(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        Map<String, Boolean> response = new HashMap<>();

        if (id == null || id.isEmpty()) {
            response.put("isDuplicate", false);
            return ResponseEntity.badRequest().body(response);
        }

        boolean isDuplicate = userService.isIdDuplicated(id);
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }
}
